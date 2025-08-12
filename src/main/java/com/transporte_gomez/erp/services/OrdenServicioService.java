package com.transporte_gomez.erp.services;

import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.transporte_gomez.erp.adapter.OrdenServicioAdapter;
import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.entity.*;
import com.transporte_gomez.erp.enums.AuditoriaOperacion;
import com.transporte_gomez.erp.enums.Modulo;
import com.transporte_gomez.erp.exception.OrdenServicioException;
import com.transporte_gomez.erp.repository.*;
import com.transporte_gomez.erp.specification.OrdenServicioSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class OrdenServicioService {

    private final DocumentoService documentoService;
    private final UsuarioRepository usuarioRepository;
    private final OrdenServicioDetalleRepository ordenServicioDetalleRepository;
    @Value("${ruta.ordenes}")
    private String rutaOrdenes;

    private final OrdenServicioDetalleService ordenServicioDetalleService;
    private final OrdenServicioRepository ordenServicioRepository;
    private final OrdenServicioAdapter ordenServicioAdapter;
    private final AuditoriaService auditoriaService;
    private final ImagenService imagenService;

    public Page<OrdenServicio> getOrdenServicios(Pageable pageable, OrdenServicioFiltro filtro){
        return ordenServicioRepository.findAll(OrdenServicioSpecification.conFiltros(filtro), pageable)
                .map(entity -> ordenServicioAdapter.getOrdenServicio(entity, false));
    }

    public OrdenServicio getOrdenServicio(Long id) {
        return ordenServicioRepository.findById(id)
                .map(entity -> ordenServicioAdapter.getOrdenServicio(entity, true))
                .orElse(null);
    }

    public OrdenServicio createOrdenServicio(OrdenServicio ordenServicio, Usuario usuario) {
        OrdenServicioEntity ordenServicioEntitySave = ordenServicioRepository.save(ordenServicioAdapter.createOrdenServicio(ordenServicio));
        if (ordenServicio.getDetalles() != null && !ordenServicio.getDetalles().isEmpty()) {
            ordenServicioDetalleService.create(ordenServicio.getDetalles(), ordenServicioEntitySave);
        }
        auditoriaService.registrarAuditoria(AuditoriaOperacion.CREADO.getNombre(), Modulo.ORDEN_SERVICIO.getCodigo(), ordenServicioEntitySave.getId(), usuario.getId());
        if (ordenServicioEntitySave.getBodega() != null && ordenServicioEntitySave.getBodega() == 4L) {
            documentoService.asignarDocumento(ordenServicioEntitySave.getDocumento().getId(), true);
        }
        return ordenServicioAdapter.getOrdenServicio(ordenServicioEntitySave, true);
    }

    public OrdenServicio updateOrdenServicio(Long id, OrdenServicio ordenServicio, List<MultipartFile> files, Usuario usuario) {
        OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de servicio no encontrada con ID: " + id));

        if (ordenServicioEntity.getEntregado()) {
            if (files != null && !files.isEmpty()) {
                asignarImagen(files, ordenServicioEntity);
            }
            auditoriaService.registrarAuditoria(AuditoriaOperacion.ACTUALIZADO.getNombre(), Modulo.ORDEN_SERVICIO.getCodigo(), ordenServicioEntity.getId(), usuario.getId());
            return ordenServicioAdapter.getOrdenServicio(ordenServicioEntity, true);
        } else {
            OrdenServicioEntity updatedEntity = ordenServicioRepository.save(ordenServicioAdapter.updateOrdenServicio(ordenServicioEntity, ordenServicio));
            if (ordenServicio.getDetalles() != null && !ordenServicio.getDetalles().isEmpty()) {
                ordenServicioDetalleService.update(ordenServicio.getDetalles(), updatedEntity);
            }
            if (files != null && !files.isEmpty()) {
                asignarImagen(files, updatedEntity);
            }
            auditoriaService.registrarAuditoria(AuditoriaOperacion.ACTUALIZADO.getNombre(), Modulo.ORDEN_SERVICIO.getCodigo(), updatedEntity.getId(), usuario.getId());
            return ordenServicioAdapter.getOrdenServicio(updatedEntity, true);
        }
    }

    @Transactional
    public void deleteOrdenServicio(Long id) {
        OrdenServicioEntity ordenServicio = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de servicio no encontrada con ID: " + id));
        for (OrdenServicioDetalleEntity detalle : ordenServicio.getDetalles()) {
            ordenServicioDetalleService.delete(detalle.getId());
        }
        ordenServicioRepository.deleteById(id);
    }

    public void entregado(Long id) {
        OrdenServicioEntity ordenServicio = ordenServicioRepository.findById(id)
                .orElse(null);
        if (ordenServicio != null) {
            ordenServicio.setEntregado(true);
            ordenServicioRepository.save(ordenServicio);
        } else {
            throw new IllegalArgumentException("Orden de servicio no encontrada con ID: " + id);
        }
    }

    public ResponseEntity<byte[]> generarPdf(Long id) {
        OrdenServicio orden = getOrdenServicio(id);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD, Color.BLACK); /// azul
            Font titleFont2 = new Font(Font.HELVETICA, 13, Font.BOLD, Color.BLACK); /// azul
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY);
            Paragraph title = new Paragraph("Sociedad Comercial Gomez Velásquez Ltda", titleFont);
            Color color = new Color(220, 230, 241);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(5f);
            doc.add(title);

            Paragraph titleOs = new Paragraph("Orden de Servicio", titleFont2);
            titleOs.setAlignment(Element.ALIGN_CENTER);
            titleOs.setSpacingAfter(5f);
            doc.add(titleOs);

            PdfPTable tableOrden = new PdfPTable(2);
            tableOrden.setWidthPercentage(100);
            tableOrden.addCell(CeldaSinLineas("N°: " + orden.getId(), Element.ALIGN_LEFT));
            tableOrden.addCell(CeldaSinLineas("", Element.ALIGN_RIGHT));
            //tableOrden.addCell(CeldaSinLineas("Fecha Emisión: " + orden.getFecha().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), Element.ALIGN_RIGHT));
            doc.add(tableOrden);
            doc.add(new Paragraph(" "));

            PdfPTable tableclienteTitulo = new PdfPTable(1);
            tableclienteTitulo.setWidthPercentage(100);
            PdfPCell cell = new PdfPCell(new Phrase("Cliente", boldFont));
            cell.setBackgroundColor(color); // celeste claro
            cell.setPadding(4f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(Color.GRAY);
            tableclienteTitulo.addCell(cell);

            doc.add(tableclienteTitulo);
            PdfPTable tableCliente = new PdfPTable(2);
            tableCliente.setWidthPercentage(100);
            tableCliente.setWidths(new float[] { 1f, 2f });
            tableCliente.addCell(celda("RBD", Element.ALIGN_LEFT));
            tableCliente.addCell(celda(orden.getEscuela().getRbd(), Element.ALIGN_LEFT));
            tableCliente.addCell(celda("Comuna", Element.ALIGN_LEFT));
            tableCliente.addCell(celda(orden.getEscuela().getComuna(), Element.ALIGN_LEFT));
            tableCliente.addCell(celda("Nombre del Establecimiento", Element.ALIGN_LEFT));
            tableCliente.addCell(celda(orden.getEscuela().getNombre(), Element.ALIGN_LEFT));
            tableCliente.addCell(celda("Director(a)", Element.ALIGN_LEFT));
            tableCliente.addCell(celda(orden.getEscuela().getDirector(), Element.ALIGN_LEFT));
            tableCliente.addCell(celda("Dirección", Element.ALIGN_LEFT));
            tableCliente.addCell(celda(orden.getEscuela().getDireccion(), Element.ALIGN_LEFT));

            doc.add(tableCliente);
            doc.add(new Paragraph(" "));

            PdfPTable tableProveedorTitulo = new PdfPTable(1);
            tableProveedorTitulo.setWidthPercentage(100);
            PdfPCell cellTituloProveedor = new PdfPCell(new Phrase("Proveedor", boldFont));
            cellTituloProveedor.setBackgroundColor(color);
            cellTituloProveedor.setPadding(4f);
            cellTituloProveedor.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellTituloProveedor.setBorderColor(Color.GRAY);
            tableProveedorTitulo.addCell(cellTituloProveedor);

            doc.add(tableProveedorTitulo);
            PdfPTable tableProveedor = new PdfPTable(2);
            tableProveedor.setWidthPercentage(100);
            tableProveedor.addCell(celda("Razón Social", Element.ALIGN_LEFT));
            tableProveedor.addCell(celda("Sociedad Comercial Gomez Velásquez Ltda.", Element.ALIGN_LEFT));
            tableProveedor.addCell(celda("Rut", Element.ALIGN_LEFT));
            tableProveedor.addCell(celda("76.651.672-5", Element.ALIGN_LEFT));
            tableProveedor.addCell(celda("Dirección", Element.ALIGN_LEFT));
            tableProveedor.addCell(celda("Pasaje Los Coigues 109, Quemchi", Element.ALIGN_LEFT));
            tableProveedor.setWidths(new float[] { 1f, 4f });
            doc.add(tableProveedor);
            doc.add(new Paragraph(" "));

            PdfPTable tableInsumosTitulo = new PdfPTable(2);
            tableInsumosTitulo.setWidthPercentage(100);
            cell.setBackgroundColor(color); // celeste claro
            cell.setPadding(4f);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBorderColor(Color.GRAY);
            tableInsumosTitulo.addCell(tituloCelda("Insumo"));
            tableInsumosTitulo.addCell(tituloCelda("Cantidad"));
            tableInsumosTitulo.setWidths(new float[] { 5f, 1f });
            doc.add(tableInsumosTitulo);

            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[] { 5f, 1f });

            for (OrdenServicioDetalle d : orden.getDetalles()) {
                table.addCell(celda(d.getNombre(), Element.ALIGN_LEFT));
                table.addCell(celda(String.valueOf(d.getCantidad()), Element.ALIGN_CENTER));
            }

            doc.add(table);
            doc.add(new Paragraph(" "));

            PdfPTable tableObsTitulo = new PdfPTable(1);
            tableObsTitulo.setWidthPercentage(100);
            PdfPCell cellObsTitulo = new PdfPCell(new Phrase("Observaciones", boldFont));
            cellObsTitulo.setBackgroundColor(color);
            cellObsTitulo.setPadding(4f);
            cellObsTitulo.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellObsTitulo.setBorderColor(Color.GRAY);
            tableObsTitulo.addCell(cellObsTitulo);

            doc.add(tableObsTitulo);

            PdfPTable tableObs = new PdfPTable(1);
            tableObs.setWidthPercentage(100);
            tableObs.addCell(celdaTopLimpio(orden.getObservaciones(), Element.ALIGN_LEFT));
            doc.add(tableObs);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(" "));

            PdfPTable tableFirma = new PdfPTable(1);
            tableFirma.setWidthPercentage(100);
            tableFirma.addCell(celdaTop("Nombre - Rut - Firma - Fecha - Timbre", Element.ALIGN_CENTER));
            doc.add(tableFirma);
            doc.close();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=orden-servicio.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(baos.toByteArray());

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }

    }

    private PdfPCell celda(String texto, int posicion) {
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, normalFont));
        cell.setPadding(6f);
        cell.setHorizontalAlignment(posicion);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell tituloCelda(String texto) {
        Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.DARK_GRAY);
        Color color = new Color(220, 230, 241);
        PdfPCell cell = new PdfPCell(new Phrase(texto, boldFont));
        cell.setBackgroundColor(color); // celeste claro
        cell.setPadding(4f);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setBorderColor(Color.GRAY);
        return cell;
    }

    private PdfPCell CeldaSinLineas(String texto, int posicion) {
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, normalFont));
        cell.setPadding(6f);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setHorizontalAlignment(posicion);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell celdaTop(String texto, int posicion) {
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, normalFont));
        cell.setBorder(Rectangle.TOP);
        cell.setPadding(6f);
        cell.setHorizontalAlignment(posicion);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell celdaTopLimpio(String texto, int posicion) {
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, normalFont));
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.BOTTOM);
        cell.setPadding(6f);
        cell.setHorizontalAlignment(posicion);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    public void deleteDetalle(Long id) {
        ordenServicioDetalleService.delete(id);
    }

    public void asignarImagen(List<MultipartFile> files, OrdenServicioEntity ordenServicio) {
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                try {
                    imagenService.guardarArchivo(file, Modulo.ORDEN_SERVICIO.getCodigo(), ordenServicio.getId(), rutaOrdenes);
                } catch (Exception e) {
                    throw new OrdenServicioException("Error al guardar la imagen: " + e.getMessage(), e);
                }
            }
        }
    }

    public void cargarOs(MultipartFile file) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CsvToBean<Os> csvToBean = new CsvToBeanBuilder<Os>(reader)
                    .withType(Os.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Os> ordenes = csvToBean.parse();

            Map<String, Map<String, List<Long>>> agrupado = ordenes.stream()
                    .collect(Collectors.groupingBy(
                            Os::getFecha,
                            Collectors.groupingBy(
                                    Os::getVehiculo,
                                    Collectors.mapping(Os::getOs, Collectors.toList())
                            )
                    ));

            agrupado.forEach((fecha, vehiculos) -> {
                vehiculos.forEach((vehiculo, listaOs) -> {
                    System.out.println("Vehículo: " + vehiculo + " → OS: " + listaOs);

                    // Cambiado el patrón para parsear la fecha en formato d/M/yyyy
                    LocalDate localDate = LocalDate.parse(fecha, DateTimeFormatter.ofPattern("d/M/yyyy"));
                    Instant fechaInstant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant();

                    AtomicInteger ordenIndex = new AtomicInteger(0);
                    listaOs.forEach((os) -> {
                        if (os != null) {
                            Optional<OrdenServicioEntity> ordenServicioEntityOptional = ordenServicioRepository.findById(os);
                            if (ordenServicioEntityOptional.isPresent()) {
                                OrdenServicioEntity ordenServicioEntity = ordenServicioEntityOptional.get();
                                ordenServicioEntity.setEntregado(true);
                                ordenServicioEntity.setFechaEntrega(fechaInstant);
                                ordenServicioRepository.save(ordenServicioEntity);
                            }
                        }
                    });
                });
            });
        }
    }

    public List<Reporte> obtenerItemsMasDespachados(OrdenServicioFiltro filtro) {
        List<Object[]> items = ordenServicioDetalleRepository.findItemsMasDespachadosPorEscuela(filtro.getEscuelaId());
        List<Reporte> reportes = new ArrayList<>();
        for (Object[] item : items) {
            if (item.length == 2) {
                String nombreItem = (String) item[0];

                BigDecimal cantidadDecimal = (BigDecimal) item[1];
                Long cantidad = cantidadDecimal.longValue();

                reportes.add(new Reporte(nombreItem, cantidad));
            }
        }
        return reportes;
    }
}
