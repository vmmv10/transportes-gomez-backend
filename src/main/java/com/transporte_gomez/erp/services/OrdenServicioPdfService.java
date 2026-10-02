package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.entity.*;
import com.transporte_gomez.erp.repository.BodegaRepository;
import com.transporte_gomez.erp.repository.EntregaRepository;
import com.transporte_gomez.erp.repository.ItemRepository;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Genera el PDF de la Orden de Servicio usando la plantilla JasperReports
 * src/main/resources/os_transporte.jrxml
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrdenServicioPdfService {

    // ---- Constantes (mover a BD / application.properties cuando existan) ----
    private static final String TIPO_SERVICIO = "Transporte";
    private static final String PATENTE_VEHICULO = "ABCD-12"; // TODO: reemplazar por la patente real
    private static final String EMPRESA_RAZON_SOCIAL = "Sociedad Comercial Gómez Velásquez Ltda.";
    private static final String EMPRESA_RUT = "76.651.672-6";
    private static final String EMPRESA_DIRECCION = "Pasaje Los Coigues 109, Quemchi";

    private static final String PLANTILLA = "variantes/os_variante_c_formulario.jrxml";
    private static final String LOGO = "images/logo-os.png";

    private static final ZoneId ZONA_CHILE = ZoneId.of("America/Santiago");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final DateTimeFormatter FORMATO_FECHA_HORA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
    private static final Locale LOCALE_CHILE = Locale.forLanguageTag("es-CL");

    private final OrdenServicioRepository ordenServicioRepository;
    private final EntregaRepository entregaRepository;
    private final BodegaRepository bodegaRepository;
    private final ItemRepository itemRepository;

    /** La plantilla se compila una sola vez y queda en memoria. */
    private volatile JasperReport plantillaCompilada;
    private volatile BufferedImage logo;

    @Transactional(readOnly = true)
    public ResponseEntity<byte[]> generarPdf(Long id) {
        OrdenServicioEntity orden = ordenServicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Orden de servicio no encontrada con ID: " + id));

        try {
            Map<String, Object> parametros = construirParametros(orden);
            List<Map<String, ?>> filas = construirDetalle(orden);

            JasperPrint print = JasperFillManager.fillReport(
                    getPlantilla(), parametros, new JRMapCollectionDataSource(filas));
            byte[] pdf = JasperExportManager.exportReportToPdf(print);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=orden-servicio-" + orden.getId() + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            log.error("Error generando PDF de la orden de servicio {}", id, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ------------------------------------------------------------------
    // Parámetros de cabecera
    // ------------------------------------------------------------------
    private Map<String, Object> construirParametros(OrdenServicioEntity orden) {
        Map<String, Object> p = new HashMap<>();
        p.put(JRParameter.REPORT_LOCALE, LOCALE_CHILE);

        // Empresa
        p.put("logo", getLogo());
        p.put("empresa", EMPRESA_RAZON_SOCIAL);
        p.put("rutEmpresa", EMPRESA_RUT);
        p.put("direccionEmpresa", EMPRESA_DIRECCION);

        // Resumen
        p.put("numero", String.valueOf(orden.getId()));
        p.put("qrImagen", generarQr("OS-" + orden.getId()));
        p.put("fecha", orden.getFecha() != null
                ? orden.getFecha().withZoneSameInstant(ZONA_CHILE).format(FORMATO_FECHA) : null);
        p.put("tipoServicio", TIPO_SERVICIO);
        p.put("documentoReferencia", orden.getDocumentoReferencia());
        p.put("estado", estado(orden));

        // Origen (bodega)
        if (orden.getBodega() != null) {
            bodegaRepository.findById(orden.getBodega()).ifPresent(b -> {
                p.put("origenNombre", b.getNombre());
                p.put("origenDireccion", b.getUbicacion());
            });
        }

        // Destino (escuela / cliente)
        EscuelaEntity escuela = orden.getEscuela();
        if (escuela != null) {
            p.put("destinoNombre", escuela.getNombre());
            p.put("destinoCodigo", escuela.getRbd() != null ? "RBD " + escuela.getRbd() : null);
            p.put("destinoDireccion", escuela.getDireccion());
            p.put("destinoComuna", escuela.getComuna());
            p.put("destinoContacto", escuela.getDirector());
            p.put("destinoTelefono", escuela.getTelefono());
        }

        // Transporte (ruta / entrega)
        p.put("vehiculo", PATENTE_VEHICULO);
        Optional<EntregaEntity> entrega = entregaRepository.findByOrdenServicio_Id(orden.getId());
        RutaEntity ruta = entrega.map(EntregaEntity::getRuta).orElse(null);

        if (ruta != null) {
            p.put("ruta", String.valueOf(ruta.getId()));
            p.put("chofer", nombreCompleto(ruta.getChofer()));
            p.put("fechaRetiro", ruta.getInicio() != null ? formatear(ruta.getInicio(), FORMATO_FECHA_HORA) : formatear(ruta.getFecha()));
        }

        // Fecha de entrega: real si existe, si no la programada de la ruta
        String fechaEntrega = null;
        if (orden.getFechaEntrega() != null) {
            fechaEntrega = formatear(orden.getFechaEntrega(), FORMATO_FECHA_HORA);
        } else if (entrega.isPresent() && entrega.get().getFecha() != null) {
            fechaEntrega = entrega.get().getFecha().atZoneSameInstant(ZONA_CHILE).format(FORMATO_FECHA_HORA);
        } else if (ruta != null) {
            fechaEntrega = formatear(ruta.getFecha());
        }
        p.put("fechaEntrega", fechaEntrega);

        p.put("observacion", orden.getObservaciones());
        return p;
    }

    // ------------------------------------------------------------------
    // Detalle de la carga
    // ------------------------------------------------------------------
    private List<Map<String, ?>> construirDetalle(OrdenServicioEntity orden) {
        List<OrdenServicioDetalleEntity> detalles = orden.getDetalles() != null ? orden.getDetalles() : List.of();

        Set<Long> itemIds = detalles.stream()
                .map(OrdenServicioDetalleEntity::getItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, ItemEntity> items = itemRepository.findAllById(itemIds).stream()
                .collect(Collectors.toMap(ItemEntity::getId, Function.identity()));

        List<Map<String, ?>> filas = new ArrayList<>();
        for (OrdenServicioDetalleEntity d : detalles) {
            ItemEntity item = d.getItem() != null ? items.get(d.getItem()) : null;
            Map<String, Object> fila = new HashMap<>();
            fila.put("codigo", item != null ? item.getCodigo() : null);
            fila.put("descripcion", d.getNombre());
            fila.put("unidad", unidad(item));
            fila.put("cantidad", d.getCantidad());
            filas.add(fila);
        }
        return filas;
    }

    // ------------------------------------------------------------------
    // Utilidades
    // ------------------------------------------------------------------
    private String estado(OrdenServicioEntity orden) {
        if (Boolean.TRUE.equals(orden.getEntregado())) return "ENTREGADO";
        if (Boolean.TRUE.equals(orden.getEnRuta())) return "EN RUTA";
        return "PENDIENTE";
    }

    private String unidad(ItemEntity item) {
        if (item == null || item.getUnidadMedida() == null) return null;
        UnidadesMedidaEntity um = item.getUnidadMedida();
        return um.getSimbolo() != null ? um.getSimbolo() : um.getCodigo();
    }

    private String nombreCompleto(UsuarioEntity u) {
        if (u == null) return null;
        String nombre = Objects.toString(u.getNombre(), "");
        String apellidos = Objects.toString(u.getApellidos(), "");
        String completo = (nombre + " " + apellidos).trim();
        return completo.isEmpty() ? null : completo;
    }

    private String formatear(Instant instante, DateTimeFormatter formato) {
        return instante == null ? null : instante.atZone(ZONA_CHILE).format(formato);
    }

    private String formatear(LocalDate fecha) {
        return fecha == null ? null : fecha.format(FORMATO_FECHA);
    }

    /** Genera el QR como imagen con zxing (sin depender del componente barcode4j de Jasper). */
    private BufferedImage generarQr(String contenido) {
        try {
            BitMatrix matriz = new QRCodeWriter().encode(contenido, BarcodeFormat.QR_CODE, 240, 240,
                    Map.of(EncodeHintType.MARGIN, 1));
            int w = matriz.getWidth(), h = matriz.getHeight();
            BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    img.setRGB(x, y, matriz.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }
            return img;
        } catch (Exception e) {
            log.warn("No se pudo generar el QR para {}", contenido, e);
            return null;
        }
    }

    private JasperReport getPlantilla() throws Exception {
        if (plantillaCompilada == null) {
            synchronized (this) {
                if (plantillaCompilada == null) {
                    try (InputStream in = new ClassPathResource(PLANTILLA).getInputStream()) {
                        plantillaCompilada = JasperCompileManager.compileReport(in);
                    }
                }
            }
        }
        return plantillaCompilada;
    }

    private BufferedImage getLogo() {
        if (logo == null) {
            try (InputStream in = new ClassPathResource(LOGO).getInputStream()) {
                logo = ImageIO.read(in);
            } catch (Exception e) {
                log.warn("No se pudo cargar el logo {}", LOGO, e);
            }
        }
        return logo;
    }
}
