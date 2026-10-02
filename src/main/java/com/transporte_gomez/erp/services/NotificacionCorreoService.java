package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.entity.CotizacionEntity;
import com.transporte_gomez.erp.entity.MensajeContactoEntity;
import com.transporte_gomez.erp.util.CorreoPlantilla;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Correos del área comercial:
 * - aviso al equipo cuando llega una cotización o un mensaje desde la landing;
 * - acuse de recibo al solicitante con su código COT;
 * - la cotización con el monto, enviada desde el ERP.
 * Los datos se leen antes de enviar en segundo plano (no se toca la sesión de JPA fuera de la transacción).
 */
@Service
@RequiredArgsConstructor
public class NotificacionCorreoService {

    private static final DateTimeFormatter FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    private static final Locale CHILE = Locale.forLanguageTag("es-CL");

    private final CorreoService correo;

    /** Direcciones del equipo que reciben los avisos, separadas por coma. */
    @Value("${app.correo.avisos:}")
    private String avisos;

    /** Enviar acuse de recibo a quien pide la cotización en la landing. */
    @Value("${app.correo.acuse-solicitante:true}")
    private boolean acuseSolicitante;

    @Value("${app.correo.erp-url:}")
    private String erpUrl;

    @Value("${app.correo.telefono:}")
    private String telefono;

    public boolean habilitado() {
        return correo.habilitado();
    }

    /** Nueva cotización desde la landing: aviso al equipo y acuse al solicitante. */
    public void cotizacionRecibida(CotizacionEntity c) {
        List<String> equipo = CorreoService.direcciones(avisos);
        if (!equipo.isEmpty()) {
            CorreoPlantilla p = CorreoPlantilla.nueva("Nueva solicitud de cotización " + c.getCodigo())
                    .parrafo("Llegó una solicitud desde la página web. Puedes responder este correo directamente al solicitante.")
                    .dato("Nombre", c.getNombre())
                    .dato("Empresa", c.getEmpresa())
                    .dato("Correo", c.getEmail())
                    .dato("Teléfono", c.getTelefono())
                    .dato("Servicio", servicio(c))
                    .dato("Origen", lugar(c.getOrigen(), c.getComunaOrigen() != null ? c.getComunaOrigen().getNombre() : null))
                    .dato("Destino", lugar(c.getDestino(), c.getComunaDestino() != null ? c.getComunaDestino().getNombre() : null))
                    .dato("Tipo de carga", c.getTipoCarga())
                    .dato("Peso", c.getPesoKg() != null ? numero(c.getPesoKg()) + " kg" : null)
                    .dato("Mensaje", c.getMensaje())
                    .boton("Ver trayecto en el mapa", enlaceMapa(c))
                    .boton("Abrir en el ERP", enlaceErp("/comercial/cotizaciones/" + c.getId()));
            correo.enviarDespues(new CorreoService.Correo(equipo, "Nueva cotización " + c.getCodigo() + " · " + c.getNombre(),
                    p.texto(), p.html(), c.getEmail()));
        }
        if (acuseSolicitante && CorreoService.esCorreo(c.getEmail())) {
            CorreoPlantilla p = CorreoPlantilla.nueva("Recibimos tu solicitud de cotización")
                    .parrafo("Hola " + corto(c.getNombre(), 60) + ", gracias por escribirnos. Revisaremos los datos de tu carga y te enviaremos la cotización a este correo.")
                    .destacado("Tu código", c.getCodigo(), "Menciónalo si nos contactas por esta solicitud.")
                    .dato("Servicio", servicio(c))
                    .dato("Origen", corto(c.getOrigen(), 120))
                    .dato("Destino", corto(c.getDestino(), 120))
                    .parrafo(telefono.isBlank() ? null : "Si necesitas algo urgente, llámanos al " + telefono + ".");
            correo.enviarDespues(new CorreoService.Correo(List.of(c.getEmail().trim()), "Recibimos tu solicitud " + c.getCodigo(),
                    p.texto(), p.html(), primera(equipo)));
        }
    }

    /** Nuevo mensaje del formulario de contacto: aviso al equipo y confirmación a quien escribió. */
    public void mensajeRecibido(MensajeContactoEntity m) {
        String motivo = m.getMotivo() != null ? m.getMotivo().getTexto() : "Consulta general";
        List<String> equipo = CorreoService.direcciones(avisos);
        if (!equipo.isEmpty()) {
            CorreoPlantilla p = CorreoPlantilla.nueva("Nuevo mensaje de contacto · " + motivo)
                    .parrafo("Llegó un mensaje desde la página web. Puedes responder este correo directamente a quien escribió.")
                    .dato("Motivo", motivo)
                    .dato("Nombre", m.getNombre())
                    .dato("Correo", m.getEmail())
                    .dato("Teléfono", m.getTelefono())
                    .dato("Código de seguimiento", m.getCodigoSeguimiento())
                    .dato("Mensaje", m.getMensaje())
                    .boton("Ver mensajes en el ERP", enlaceErp("/comercial/mensajes"));
            correo.enviarDespues(new CorreoService.Correo(equipo, "[" + motivo + "] Mensaje de " + m.getNombre(), p.texto(), p.html(), m.getEmail()));
        }
        // Confirmación sin repetir el texto del mensaje (así el formulario no sirve para enviar spam a terceros)
        if (acuseSolicitante && CorreoService.esCorreo(m.getEmail())) {
            CorreoPlantilla p = CorreoPlantilla.nueva("Recibimos tu mensaje")
                    .parrafo("Hola " + corto(m.getNombre(), 60) + ", gracias por escribirnos. Te responderemos a este correo en horario hábil.")
                    .dato("Motivo", motivo)
                    .parrafo(telefono.isBlank() ? null : "Si es urgente, llámanos o escríbenos por WhatsApp al " + telefono + ".");
            correo.enviarDespues(new CorreoService.Correo(List.of(m.getEmail().trim()), "Recibimos tu mensaje · Transportes Gómez Velásquez",
                    p.texto(), p.html(), primera(equipo)));
        }
    }

    /** Envía la cotización al solicitante (ahora, para informar si falla). */
    public void enviarCotizacion(CotizacionEntity c, String mensaje) {
        String cantidad = c.getCantidad() != null
                ? numero(c.getCantidad()) + (c.getUnidadMedida() != null ? " " + c.getUnidadMedida().getNombre() : "")
                : null;
        CorreoPlantilla p = CorreoPlantilla.nueva("Cotización " + c.getCodigo())
                .parrafo("Hola " + corto(c.getNombre(), 60) + ",")
                .parrafo(mensaje != null && !mensaje.isBlank() ? mensaje : "Gracias por preferirnos. Te enviamos nuestra propuesta para el servicio solicitado.")
                .destacado("Valor del servicio", pesos(c.getMonto()), "Valor neto, más IVA")
                .dato("Servicio", servicio(c))
                .dato("Origen", lugar(c.getOrigen(), c.getComunaOrigen() != null ? c.getComunaOrigen().getNombre() : null))
                .dato("Destino", lugar(c.getDestino(), c.getComunaDestino() != null ? c.getComunaDestino().getNombre() : null))
                .dato("Tipo de carga", c.getTipoCarga())
                .dato("Peso", c.getPesoKg() != null ? numero(c.getPesoKg()) + " kg" : null)
                .dato("Cantidad", cantidad)
                .dato("Válida hasta", c.getValidaHasta() != null ? FECHA.format(c.getValidaHasta()) : null)
                .parrafo("Para aceptar la cotización o hacer consultas, responde este correo indicando el código " + c.getCodigo()
                        + (telefono.isBlank() ? "." : " o llámanos al " + telefono + "."))
                .pie("Cotización " + c.getCodigo() + ". Los valores pueden variar si cambian las condiciones de la carga.");
        correo.enviar(new CorreoService.Correo(List.of(c.getEmail().trim()), "Cotización " + c.getCodigo() + " · Transportes Gómez Velásquez",
                p.texto(), p.html(), primera(CorreoService.direcciones(avisos))));
    }

    private String enlaceErp(String ruta) {
        return erpUrl == null || erpUrl.isBlank() ? null : erpUrl.replaceAll("/+$", "") + ruta;
    }

    /** Google Maps con el trayecto (o el punto, si solo se marcó uno). null si no hay puntos. */
    static String enlaceMapa(CotizacionEntity c) {
        String o = c.getOrigenLatitud() != null ? c.getOrigenLatitud().toPlainString() + "," + c.getOrigenLongitud().toPlainString() : null;
        String d = c.getDestinoLatitud() != null ? c.getDestinoLatitud().toPlainString() + "," + c.getDestinoLongitud().toPlainString() : null;
        if (o != null && d != null) {
            return "https://www.google.com/maps/dir/?api=1&origin=" + o + "&destination=" + d;
        }
        String uno = o != null ? o : d;
        return uno == null ? null : "https://www.google.com/maps/search/?api=1&query=" + uno;
    }

    private static String servicio(CotizacionEntity c) {
        if (c.getServicioTipo() != null) {
            return c.getServicioTipo().getNombre();
        }
        return c.getServicioTexto();
    }

    private static String lugar(String texto, String comuna) {
        if (texto == null || texto.isBlank()) {
            return comuna;
        }
        return comuna == null || texto.toLowerCase().contains(comuna.toLowerCase()) ? texto : texto + ", " + comuna;
    }

    private static String pesos(BigDecimal monto) {
        return monto == null ? "" : "$" + numero(monto.setScale(0, java.math.RoundingMode.HALF_UP));
    }

    private static String numero(BigDecimal valor) {
        NumberFormat f = NumberFormat.getNumberInstance(CHILE);
        f.setMaximumFractionDigits(2);
        return f.format(valor);
    }

    private static String corto(String valor, int max) {
        if (valor == null) {
            return "";
        }
        String t = valor.trim();
        return t.length() > max ? t.substring(0, max) : t;
    }

    private static String primera(List<String> lista) {
        return lista.isEmpty() ? null : lista.get(0);
    }
}
