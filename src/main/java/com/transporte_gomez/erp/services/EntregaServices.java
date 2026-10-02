package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.EntregaAdapter;
import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import com.transporte_gomez.erp.enums.EntregaEstado;
import com.transporte_gomez.erp.enums.SeguimientoTipo;
import com.transporte_gomez.erp.repository.EntregaRepository;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.specification.EntregaSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
@Slf4j
public class EntregaServices {

    private final OrdenServicioRepository ordenServicioRepository;
    private final EntregaRepository entregaRepository;
    private final EntregaAdapter entregaAdapter;
    private final RutaRepository rutaRepository;
    private final OrdenServicioService ordenServicioService;
    private final SeguimientoService seguimientoService;

    public Page<Entrega> getEntregas(Pageable pageable, EntregaFiltro filtro) {
        return entregaRepository.findAll(EntregaSpecification.conFiltros(filtro), pageable)
                .map(entregaAdapter::getEntrega);
    }

    @Transactional(readOnly = true)
    public byte[] generarExcel(EntregaFiltro filtro, Sort sort) {
        Sort orden = (sort == null || sort.isUnsorted()) ? Sort.by(Sort.Direction.DESC, "id") : sort;
        List<EntregaEntity> entregas = entregaRepository.findAll(EntregaSpecification.conFiltros(filtro), orden);

        ZoneId zonaChile = ZoneId.of("America/Santiago");
        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        DateTimeFormatter formatoFechaHora = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

        String[] columnas = {
                "ID Entrega", "Orden de Servicio", "Fecha Ruta", "Ruta", "Chofer", "Orden en Ruta",
                "Escuela", "RBD", "Comuna", "Categoría", "OC / Doc. Referencia", "Estado", "Fecha Entrega"
        };

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Entregas");

            Font fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            fontHeader.setColor(IndexedColors.WHITE.getIndex());

            CellStyle estiloHeader = workbook.createCellStyle();
            estiloHeader.setFont(fontHeader);
            estiloHeader.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
            estiloHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            estiloHeader.setAlignment(HorizontalAlignment.CENTER);
            estiloHeader.setBorderBottom(BorderStyle.THIN);

            Row header = sheet.createRow(0);
            for (int i = 0; i < columnas.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columnas[i]);
                cell.setCellStyle(estiloHeader);
            }

            int filaIdx = 1;
            for (EntregaEntity entrega : entregas) {
                Row row = sheet.createRow(filaIdx++);
                OrdenServicioEntity os = entrega.getOrdenServicio();
                RutaEntity ruta = entrega.getRuta();

                setCell(row, 0, entrega.getId());
                setCell(row, 1, os != null ? os.getId() : null);
                setCell(row, 2, ruta != null && ruta.getFecha() != null ? ruta.getFecha().format(formatoFecha) : null);
                setCell(row, 3, ruta != null ? ruta.getId() : null);
                setCell(row, 4, ruta != null && ruta.getChofer() != null ? nombreCompleto(ruta.getChofer().getNombre(), ruta.getChofer().getApellidos()) : null);
                setCell(row, 5, entrega.getOrden());
                setCell(row, 6, os != null && os.getEscuela() != null ? os.getEscuela().getNombre() : null);
                setCell(row, 7, os != null && os.getEscuela() != null ? os.getEscuela().getRbd() : null);
                setCell(row, 8, os != null && os.getEscuela() != null ? os.getEscuela().getComuna() : null);
                setCell(row, 9, os != null && os.getCategoria() != null ? os.getCategoria().getNombre() : null);
                setCell(row, 10, os != null ? os.getDocumentoReferencia() : null);
                setCell(row, 11, Boolean.TRUE.equals(entrega.getEntregado()) ? "Entregado" : "No Entregado");
                setCell(row, 12, entrega.getFecha() != null
                        ? entrega.getFecha().atZoneSameInstant(zonaChile).format(formatoFechaHora) : null);
            }

            sheet.setAutoFilter(new CellRangeAddress(0, Math.max(filaIdx - 1, 0), 0, columnas.length - 1));
            sheet.createFreezePane(0, 1);
            for (int i = 0; i < columnas.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (IOException e) {
            log.error("Error generando Excel de entregas", e);
            throw new RuntimeException("Error al generar el Excel de entregas", e);
        }
    }

    private void setCell(Row row, int col, Object valor) {
        Cell cell = row.createCell(col);
        if (valor == null) {
            cell.setBlank();
        } else if (valor instanceof Number n) {
            cell.setCellValue(n.doubleValue());
        } else {
            cell.setCellValue(valor.toString());
        }
    }

    private String nombreCompleto(String nombre, String apellidos) {
        String n = nombre != null ? nombre : "";
        String a = apellidos != null ? apellidos : "";
        return (n + " " + a).trim();
    }

    public void crearEntregas(RutaEntity rutaEntity, List<OrdenServicio> ordenServicioList) {
        for (int i = 0; i < ordenServicioList.size(); i++) {
            OrdenServicio ordenServicio = ordenServicioList.get(i);
            OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(ordenServicio.getId())
                    .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada: " + ordenServicio.getId()));

            entregaRepository.save(nuevaOReprogramada(rutaEntity, ordenServicioEntity, i + 1));
        }

        // Actualizamos el estado enRuta una sola vez por cada orden
        for (OrdenServicio ordenServicio : ordenServicioList) {
            OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(ordenServicio.getId())
                    .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada: " + ordenServicio.getId()));
            ordenServicioEntity.setEnRuta(true);
            ordenServicioRepository.save(ordenServicioEntity);
        }

    }

    @Transactional
    public void updateEntregas(RutaEntity rutaEntity, List<OrdenServicio> ordenServicioList) {
        Set<Long> ordenesSolicitadas = new HashSet<>();
        for (OrdenServicio ordenServicio : ordenServicioList) {
            ordenesSolicitadas.add(ordenServicio.getId());
        }

        List<EntregaEntity> entregasExistentes = entregaRepository.findByRuta_Id(rutaEntity.getId());
        for (EntregaEntity entregaExistente : entregasExistentes) {
            Long ordenId = entregaExistente.getOrdenServicio().getId();
            if (!ordenesSolicitadas.contains(ordenId)) {
                desasociarEntrega(entregaExistente);
                entregaRepository.delete(entregaExistente);
            }
        }

        for (int i = 0; i < ordenServicioList.size(); i++) {
            Optional<EntregaEntity> entregaEntity = entregaRepository.findByRuta_IdAndOrdenServicio_Id(rutaEntity.getId(), ordenServicioList.get(i).getId());
            OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(ordenServicioList.get(i).getId())
                    .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada"));
            if (entregaEntity.isPresent()) {
                // Actualizar entrega existente
                EntregaEntity existingEntrega = entregaEntity.get();
                existingEntrega.setOrden(i + 1);
                entregaRepository.save(existingEntrega);
            } else {
                // Crear nueva entrega si no existe (o traer la que no se pudo entregar en otra ruta)
                entregaRepository.save(nuevaOReprogramada(rutaEntity, ordenServicioEntity, i + 1));
            }
            ordenServicioEntity.setEnRuta(true);
            ordenServicioRepository.save(ordenServicioEntity);
        }
    }

    public void deleteEntrega(Integer id) {
        entregaRepository.deleteById(id);
    }

    @Transactional
    public void deleteEntregaByRutaAndOrden(Integer ruta, Long orden) {
        EntregaEntity entregaEntity = entregaRepository.findByRuta_IdAndOrdenServicio_Id(ruta, orden)
                .orElseThrow(() -> new RuntimeException("Entrega not found for ruta: " + ruta + " and orden: " + orden));

        desasociarEntrega(entregaEntity);
        entregaRepository.delete(entregaEntity);
    }

    public List<Reporte> obtenerEntregasEntregadasPorMes(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.contarEntregasPorMesIncluyendoCeros(filtro.getEscuela());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],
                        ((Long) obj[1])
                ))
                .toList();
    }

    public List<Reporte> findTopEscuelasConMasEntregas(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.findTopEscuelasConMasEntregas(filtro.getEscuela(), filtro.getSize());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],
                        ((Long) obj[1])
                ))
                .toList();
    }

    @Transactional
    public void entregar(Integer id, List<MultipartFile> files) {
        entregar(id, files, null, null);
    }

    @Transactional
    public void entregar(Integer id, List<MultipartFile> files, BigDecimal latitud, BigDecimal longitud) {
        log.info("Entregando entrega con id: {}", id);
        EntregaEntity entregaEntity = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada con ID: " + id));

        if (Boolean.TRUE.equals(entregaEntity.getEntregado())) {
            throw new IllegalArgumentException("La entrega " + id + " ya fue registrada como entregada");
        }

        entregaEntity.setEntregado(true);
        entregaEntity.setEstado(EntregaEstado.ENTREGADO);
        entregaEntity.setMotivo(null);
        entregaEntity.setIntentos(valor(entregaEntity.getIntentos()) + 1);
        entregaEntity.setLatitud(latitud);
        entregaEntity.setLongitud(longitud);
        entregaEntity.setFecha(OffsetDateTime.now());
        entregaRepository.save(entregaEntity);
        seguimientoService.registrar(entregaEntity, SeguimientoTipo.ENTREGADO, "Entregado", latitud, longitud, null);
        log.info("Entrega con id: {}", id);
        OrdenServicioEntity ordenServicioEntity = entregaEntity.getOrdenServicio();
        ordenServicioEntity.setEnRuta(false);
        ZoneId zoneId = ZoneId.of("America/Santiago");
        LocalDate fecha = LocalDate.now(zoneId);
        Instant fechaChile = fecha.atStartOfDay(zoneId).toInstant();
        ordenServicioEntity.setFechaEntrega(fechaChile);
        ordenServicioEntity.setEntregado(true);
        if (files != null && !files.isEmpty()) {
            log.info("Entregando imagen con id: {}", id);
            ordenServicioService.asignarImagen(files, ordenServicioEntity);
        }
        ordenServicioRepository.save(ordenServicioEntity);

        revisarRuta(entregaEntity.getRuta().getId());
    }

    /**
     * El conductor no pudo entregar (cerrado, nadie para recibir...) o se lo rechazaron.
     * La orden queda libre para asignarla a otra ruta.
     */
    @Transactional
    public void noEntregado(Integer id, EntregaResultado resultado) {
        EntregaEntity entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada con ID: " + id));
        if (Boolean.TRUE.equals(entrega.getEntregado())) {
            throw new IllegalArgumentException("La entrega " + id + " ya fue registrada como entregada");
        }
        EntregaEstado estado = resultado.getEstado() == EntregaEstado.RECHAZADO ? EntregaEstado.RECHAZADO : EntregaEstado.NO_ENTREGADO;
        String motivo = resultado.getMotivo() != null ? resultado.getMotivo().trim() : "";
        if (motivo.isEmpty()) {
            throw new IllegalArgumentException("Indica el motivo por el que no se entregó");
        }
        entrega.setEstado(estado);
        entrega.setMotivo(motivo);
        entrega.setIntentos(valor(entrega.getIntentos()) + 1);
        entrega.setLatitud(resultado.getLatitud());
        entrega.setLongitud(resultado.getLongitud());
        entrega.setFecha(OffsetDateTime.now());
        entregaRepository.save(entrega);

        OrdenServicioEntity orden = entrega.getOrdenServicio();
        orden.setEnRuta(false);
        ordenServicioRepository.save(orden);

        seguimientoService.registrar(entrega, estado == EntregaEstado.RECHAZADO ? SeguimientoTipo.RECHAZADO : SeguimientoTipo.NO_ENTREGADO,
                motivo, resultado.getLatitud(), resultado.getLongitud(), null);
        revisarRuta(entrega.getRuta().getId());
    }

    /** El conductor está cerca del destino (lo envía la app una vez por entrega). */
    @Transactional
    public void proximidad(Integer id, EntregaResultado resultado) {
        EntregaEntity entrega = entregaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entrega no encontrada con ID: " + id));
        if (entrega.getEstado() != EntregaEstado.PENDIENTE || seguimientoService.yaRegistrado(entrega, SeguimientoTipo.CERCA_DESTINO)) {
            return;
        }
        seguimientoService.registrar(entrega, SeguimientoTipo.CERCA_DESTINO, "El conductor está cerca del destino",
                resultado.getLatitud(), resultado.getLongitud(), resultado.getDistanciaKm());
    }

    /** Eventos "en ruta" de las entregas pendientes, al comenzar la ruta. */
    @Transactional
    public void registrarInicioRuta(Integer rutaId) {
        for (EntregaEntity entrega : entregaRepository.findByRuta_Id(rutaId)) {
            if (entrega.getEstado() == EntregaEstado.PENDIENTE && !seguimientoService.yaRegistrado(entrega, SeguimientoTipo.EN_RUTA)) {
                seguimientoService.registrar(entrega, SeguimientoTipo.EN_RUTA, "La orden salió a reparto", null, null, null);
            }
        }
    }

    /**
     * Con la primera entrega resuelta la ruta queda en tránsito; cuando todas están resueltas
     * (entregadas o no) la ruta se finaliza. El conductor puede registrar después el odómetro de llegada.
     */
    private void revisarRuta(Integer rutaId) {
        List<EntregaEntity> entregas = entregaRepository.findByRuta_Id(rutaId);
        boolean todasResueltas = entregas.stream().allMatch(e -> e.getEstado() != null && e.getEstado().resuelta());
        RutaEntity rutaEntity = rutaRepository.getReferenceById(rutaId);
        if (rutaEntity.getInicio() == null) {
            rutaEntity.setInicio(Instant.now());
        }
        if (todasResueltas) {
            rutaEntity.setFin(Instant.now());
            rutaEntity.setEnTransito(false);
            rutaEntity.setEstado("FINALIZADA");
        } else {
            rutaEntity.setEnTransito(true);
            rutaEntity.setEstado("PENDIENTE");
        }
        rutaRepository.save(rutaEntity);
    }

    /**
     * Una orden tiene una sola entrega. Si ya tuvo una que no se pudo entregar en otra ruta,
     * se trae a esta ruta (queda pendiente y se conserva el número de intentos).
     */
    private EntregaEntity nuevaOReprogramada(RutaEntity rutaEntity, OrdenServicioEntity orden, Integer posicion) {
        Optional<EntregaEntity> anterior = entregaRepository.findByOrdenServicio_Id(orden.getId());
        if (anterior.isEmpty()) {
            return entregaAdapter.createEntrega(rutaEntity, orden, posicion);
        }
        EntregaEntity entrega = anterior.get();
        if (entrega.getEstado() == null || !entrega.getEstado().reprogramable()) {
            throw new IllegalArgumentException("La orden " + orden.getId() + " ya está en la ruta " + entrega.getRuta().getId());
        }
        Integer rutaAnterior = entrega.getRuta().getId();
        entrega.setRuta(rutaEntity);
        entrega.setOrden(posicion);
        entrega.setEstado(EntregaEstado.PENDIENTE);
        entrega.setEntregado(false);
        entrega.setMotivo(null);
        entrega.setLatitud(null);
        entrega.setLongitud(null);
        entrega.setFecha(null);
        EntregaEntity guardada = entregaRepository.save(entrega);
        seguimientoService.registrar(guardada, SeguimientoTipo.REPROGRAMADO,
                "Reprogramada desde la ruta " + rutaAnterior, null, null, null);
        return guardada;
    }

    private static int valor(Integer n) {
        return n != null ? n : 0;
    }

    public List<Reporte> obtenerEntregasEntregadasVsNoEntregadas(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.contarEntregasEntregadasVsNoEntregadas(filtro.getEscuela());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],      // estado: "Entregadas" o "No entregadas"
                        ((Long) obj[1])       // total
                ))
                .toList();
    }

    public List<Reporte> obtenerUltimasEntregas(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.ultimasEntregas(filtro.getEscuela(), filtro.getSize());

        return resultados.stream()
                .map(obj -> new Reporte(
                        obj[1] + " - " + obj[2] + " (" + obj[3] + ")", // fecha - escuela (estado)
                        ((Integer) obj[0]).longValue() // id entrega
                ))
                .toList();
    }


    public List<Reporte> obtenerEscuelasConPendientes(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.escuelasConPendientes(filtro.getEscuela());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],
                        ((Long) obj[1])
                ))
                .toList();
    }

    public Double obtenerPromedioDiario(EntregaFiltro filtro) {
        return entregaRepository.promedioEntregasDiarias(filtro.getEscuela());
    }

    public Long countEntregasParaHoyPorEscuela(EntregaFiltro filtro) {
        LocalDate hoy = LocalDate.now();
        LocalDate inicioDia = LocalDate.from(hoy.atStartOfDay());
        LocalDate finDia = LocalDate.from(hoy.atTime(LocalTime.MAX));
        return entregaRepository.countEntregasPorEscuelaEntreFechas(filtro.getEscuela(), inicioDia, finDia);
    }

    public EntregaDashboard getStats(EntregaFiltro filtro) {
        System.out.println("FILTRO: " + filtro);
        List<Object[]> result = entregaRepository.getEntregaStats(filtro.getEscuela(), filtro.getFecha(), filtro.getOc(), filtro.getCategoria());
        Long result2 = entregaRepository.getEntregasHoy(filtro.getEscuela(), filtro.getOc(), filtro.getCategoria());
        EntregaDashboard entregaDashboard = new EntregaDashboard();
        entregaDashboard.setEntregasRealizadas((Long) result.get(0)[1]);
        entregaDashboard.setEntregasPendientes((Long) result.get(0)[2]);
        entregaDashboard.setEntregasTotal((Long) result.get(0)[0]);
        entregaDashboard.setEntregasHoy(result2);

        return entregaDashboard;
    }

    @Transactional
    public void completarRuta(Integer id) {
        List<EntregaEntity> entregas = entregaRepository.findByRuta_Id(id);
        for (EntregaEntity entrega : entregas) {
            if (!entrega.getEntregado()) {
                entrega.setEntregado(true);
                entrega.setEstado(EntregaEstado.ENTREGADO);
                entrega.setIntentos(valor(entrega.getIntentos()) + 1);
                entrega.setFecha(OffsetDateTime.now());
                entregaRepository.save(entrega);

                OrdenServicioEntity ordenServicioEntity = entrega.getOrdenServicio();
                ordenServicioEntity.setEnRuta(false);
                ordenServicioEntity.setEntregado(true);
                ordenServicioRepository.save(ordenServicioEntity);
            }
        }
    }

    public List<Kpi> getKpis(EntregaFiltro filtro) {
        List<Kpi> kpis = new ArrayList<>();

        Kpi entregasATiempo = new Kpi();

        BigDecimal entregasATiempoValor = entregaRepository.getPromedioKPIEntregasATiempo(filtro.getEscuela(), filtro.getFecha(), filtro.getCategoria());

        entregasATiempo.setNombre("Entregas a Tiempo");
        entregasATiempo.setValor(entregasATiempoValor);
        entregasATiempo.setPorcentaje(entregasATiempoValor.divide(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP));
        entregasATiempo.setUnidad("%");

        kpis.add(entregasATiempo);

        Kpi quiebreStock = new Kpi();

        BigDecimal quiebreStockValor = entregaRepository.getPromedioKPIQuiebreStock(filtro.getEscuela(), filtro.getFecha(), filtro.getCategoria());
        quiebreStock.setNombre("Quiebre de Stock");
        quiebreStock.setValor(quiebreStockValor
                .setScale(2, RoundingMode.HALF_UP));
        quiebreStock.setPorcentaje(quiebreStockValor.divide(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP));
        quiebreStock.setUnidad("%");

        kpis.add(quiebreStock);

        Kpi tiempoRespuestaInterno = new Kpi();

        BigDecimal tiempoRespuestaInternoValor = entregaRepository.getPromedioTiempoRespuestaInterno(filtro.getEscuela(), filtro.getFecha(), filtro.getCategoria());

        if (tiempoRespuestaInternoValor == null) {
            tiempoRespuestaInternoValor = BigDecimal.ZERO;
        }
        tiempoRespuestaInterno.setNombre("Tiempo de Respuesta Interno");
        tiempoRespuestaInterno.setValor(tiempoRespuestaInternoValor
                .setScale(2, RoundingMode.HALF_UP));
        tiempoRespuestaInterno.setPorcentaje(tiempoRespuestaInternoValor.divide(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP));
        tiempoRespuestaInterno.setUnidad("Horas");

        kpis.add(tiempoRespuestaInterno);

        return kpis;
    }

    private void desasociarEntrega(EntregaEntity entregaEntity) {
        OrdenServicioEntity ordenServicioEntity = entregaEntity.getOrdenServicio();
        ordenServicioEntity.setEntrega(null);
        ordenServicioEntity.setEnRuta(false);
        ordenServicioRepository.save(ordenServicioEntity);
    }
}
