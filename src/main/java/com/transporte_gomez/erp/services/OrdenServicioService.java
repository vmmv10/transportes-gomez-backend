package com.transporte_gomez.erp.services;

import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.transporte_gomez.erp.adapter.OrdenServicioAdapter;
import com.transporte_gomez.erp.dto.OrdenServicio;
import com.transporte_gomez.erp.dto.OrdenServicioDetalle;
import com.transporte_gomez.erp.dto.OrdenServicioFiltro;
import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.enums.AuditoriaOperacion;
import com.transporte_gomez.erp.enums.Modulo;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import com.transporte_gomez.erp.specification.OrdenServicioSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RequiredArgsConstructor
@Service
public class OrdenServicioService {

    private final DocumentoService documentoService;
    @Value("${ruta.documentos}")
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
        documentoService.asignarDocumento(ordenServicioEntitySave.getDocumento().getId(), true);
        return ordenServicioAdapter.getOrdenServicio(ordenServicioEntitySave, true);
    }

    public OrdenServicio updateOrdenServicio(Long id, OrdenServicio ordenServicio, List<MultipartFile> files, Usuario usuario) {
        OrdenServicioEntity ordenServicioEntity = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de servicio no encontrada con ID: " + id));

        OrdenServicioEntity updatedEntity = ordenServicioRepository.save(ordenServicioAdapter.updateOrdenServicio(ordenServicioEntity, ordenServicio));
        if (ordenServicio.getDetalles() != null && !ordenServicio.getDetalles().isEmpty()) {
            ordenServicioDetalleService.update(ordenServicio.getDetalles(), updatedEntity);
        }
        auditoriaService.registrarAuditoria(AuditoriaOperacion.ACTUALIZADO.getNombre(), Modulo.ORDEN_SERVICIO.getCodigo(), updatedEntity.getId(), usuario.getId());

        return ordenServicioAdapter.getOrdenServicio(updatedEntity, true);
    }

    public void deleteOrdenServicio(Long id) {
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
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
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
            tableOrden.addCell(CeldaSinLineas("Fecha: " + orden.getFecha().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), Element.ALIGN_RIGHT));
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
            PdfPCell cellTituloInsumo = new PdfPCell(new Phrase("Insumos", boldFont));
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
            tableObs.addCell(celdaBottomLimpio("Traslado de Bultos", Element.ALIGN_LEFT));
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

    private PdfPCell celdaBottom(String texto, int posicion) {
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, normalFont));
        cell.setBorder(Rectangle.BOTTOM);
        cell.setPadding(6f);
        cell.setHorizontalAlignment(posicion);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    private PdfPCell celdaBottomLimpio(String texto, int posicion) {
        Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL, Color.BLACK);
        PdfPCell cell = new PdfPCell(new Phrase(texto, normalFont));
        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT | Rectangle.TOP);
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
}
