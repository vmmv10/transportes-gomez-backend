package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.EntregaAdapter;
import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import com.transporte_gomez.erp.repository.EntregaRepository;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.specification.EntregaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EntregaServices {

    private final OrdenServicioRepository ordenServicioRepository;
    private final EntregaRepository entregaRepository;
    private final EntregaAdapter entregaAdapter;
    private final RutaRepository rutaRepository;
    private final OrdenServicioService ordenServicioService;

    public Page<Entrega> getEntregas(Pageable pageable, EntregaFiltro filtro) {
        return entregaRepository.findAll(EntregaSpecification.conFiltros(filtro), pageable)
                .map(entregaAdapter::getEntrega);
    }

    public void crearEntregas(RutaEntity rutaEntity, List<OrdenServicio> ordenServicioList) {
        for (int i = 0; i < ordenServicioList.size(); i++) {
            OrdenServicio ordenServicio = ordenServicioList.get(i);
            OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(ordenServicio.getId())
                    .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada: " + ordenServicio.getId()));

            EntregaEntity entregaEntity = entregaAdapter.createEntrega(rutaEntity, ordenServicioEntity, i + 1);
            entregaRepository.save(entregaEntity);
        }

        // Actualizamos el estado enRuta una sola vez por cada orden
        for (OrdenServicio ordenServicio : ordenServicioList) {
            OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(ordenServicio.getId())
                    .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada: " + ordenServicio.getId()));
            ordenServicioEntity.setEnRuta(true);
            ordenServicioRepository.save(ordenServicioEntity);
        }

    }

    public void updateEntregas(RutaEntity rutaEntity, List<OrdenServicio> ordenServicioList) {
        for (int i = 0; i < ordenServicioList.size(); i++) {
            Optional<EntregaEntity> entregaEntity = entregaRepository.findByRuta_IdAndOrdenServicio_Id(rutaEntity.getId(), ordenServicioList.get(i).getId());
            if (entregaEntity.isPresent()) {
                // Actualizar entrega existente
                EntregaEntity existingEntrega = entregaEntity.get();
                existingEntrega.setOrden(i + 1);
                entregaRepository.save(existingEntrega);
            } else {
                // Crear nueva entrega si no existe
                OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(ordenServicioList.get(i).getId())
                        .orElseThrow(() -> new RuntimeException("Orden de servicio no encontrada"));
                EntregaEntity nuevaEntrega = entregaAdapter.createEntrega(rutaEntity, ordenServicioEntity, i + 1);
                entregaRepository.save(nuevaEntrega);
                ordenServicioEntity.setEnRuta(true);
                ordenServicioRepository.save(ordenServicioEntity);
            }
        }
    }

    public void deleteEntrega(Integer id) {
        entregaRepository.deleteById(id);
    }

    public void deleteEntregaByRutaAndOrden(Integer ruta, Long orden) {
        EntregaEntity entregaEntity = entregaRepository.findByRuta_IdAndOrdenServicio_Id(ruta, orden)
                .orElseThrow(() -> new RuntimeException("Entrega not found for ruta: " + ruta + " and orden: " + orden));
        entregaRepository.delete(entregaEntity);

        OrdenServicioEntity ordenServicioEntity = entregaEntity.getOrdenServicio();
        ordenServicioEntity.setEnRuta(false);
        ordenServicioRepository.save(ordenServicioEntity);
    }

    public List<Reporte> obtenerEntregasEntregadasPorMes(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.contarEntregasPorMesIncluyendoCeros(filtro.getEscuela());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],
                        ((Long) obj[1])
                ))
                .collect(Collectors.toList());
    }

    public List<Reporte> findTopEscuelasConMasEntregas(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.findTopEscuelasConMasEntregas(filtro.getEscuela(), filtro.getSize());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],
                        ((Long) obj[1])
                ))
                .collect(Collectors.toList());
    }

    public void entregar(Integer id, List<MultipartFile> files) {
        EntregaEntity entregaEntity = entregaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrega not found with id: " + id));

        if (entregaEntity.getEntregado()) {
            throw new RuntimeException("Entrega ya ha sido entregada con id: " + id);
        }

        entregaEntity.setEntregado(true);
        entregaEntity.setFecha(OffsetDateTime.now());
        entregaRepository.save(entregaEntity);

        OrdenServicioEntity ordenServicioEntity = entregaEntity.getOrdenServicio();
        ordenServicioEntity.setEnRuta(false);
        ZoneId zoneId = ZoneId.of("America/Santiago");
        LocalDate fecha = LocalDate.now(zoneId);
        Instant fechaChile = fecha.atStartOfDay(zoneId).toInstant();
        ordenServicioEntity.setFechaEntrega(fechaChile);
        ordenServicioEntity.setEntregado(true);
        if (files != null && !files.isEmpty()) {
            ordenServicioService.asignarImagen(files, ordenServicioEntity);
        }
        ordenServicioRepository.save(ordenServicioEntity);

        List<EntregaEntity> entregas = entregaRepository.findByRuta_Id(entregaEntity.getRuta().getId());
        Boolean sinEntregas = true;
        for (EntregaEntity entrega : entregas) {
            if (!entrega.getEntregado()) {
                sinEntregas = false;
                break;
            }
        }

        if (sinEntregas) {
            RutaEntity rutaEntity = rutaRepository.getReferenceById(entregaEntity.getRuta().getId());
            rutaEntity.setFin(Instant.now());
            rutaEntity.setEnTransito(false);
            rutaEntity.setEstado("FINALIZADA");

            rutaRepository.save(rutaEntity);
        }
    }

    public List<Reporte> obtenerEntregasEntregadasVsNoEntregadas(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.contarEntregasEntregadasVsNoEntregadas(filtro.getEscuela());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],      // estado: "Entregadas" o "No entregadas"
                        ((Long) obj[1])       // total
                ))
                .collect(Collectors.toList());
    }

    public List<Reporte> obtenerUltimasEntregas(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.ultimasEntregas(filtro.getEscuela(), filtro.getSize());

        return resultados.stream()
                .map(obj -> new Reporte(
                        obj[1] + " - " + obj[2] + " (" + obj[3] + ")", // fecha - escuela (estado)
                        ((Integer) obj[0]).longValue() // id entrega
                ))
                .collect(Collectors.toList());
    }


    public List<Reporte> obtenerEscuelasConPendientes(EntregaFiltro filtro) {
        List<Object[]> resultados = entregaRepository.escuelasConPendientes(filtro.getEscuela());

        return resultados.stream()
                .map(obj -> new Reporte(
                        (String) obj[0],
                        ((Long) obj[1])
                ))
                .collect(Collectors.toList());
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
        List<Object[]> result = entregaRepository.getEntregaStatsNative(filtro.getEscuela());
        EntregaDashboard entregaDashboard = new EntregaDashboard();
        System.out.println("EntregaDashboard resultado: " + Arrays.toString(result.get(0)));
        entregaDashboard.setEntregasHoy((Long) result.get(0)[3]);
        entregaDashboard.setEntregasRealizadas((Long) result.get(0)[1]);
        entregaDashboard.setEntregasPendientes((Long) result.get(0)[2]);
        entregaDashboard.setEntregasTotal((Long) result.get(0)[0]);

        return entregaDashboard;
    }
}
