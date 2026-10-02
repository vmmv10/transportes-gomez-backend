package com.transporte_gomez.erp.util;

import org.springframework.web.util.HtmlUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Arma el cuerpo de un correo en HTML (con los colores de la empresa) y en texto plano,
 * a la vez. Todo lo que viene del usuario se escapa.
 */
public final class CorreoPlantilla {

    private static final String AZUL = "#2178bd";
    private static final String AZUL_OSCURO = "#185f97";
    private static final String EMPRESA = "Transportes Gómez Velásquez";

    private final StringBuilder html = new StringBuilder();
    private final StringBuilder texto = new StringBuilder();
    private final List<String[]> datos = new ArrayList<>();
    private final String titulo;
    private String pie = "Este correo fue enviado automáticamente por el sistema de " + EMPRESA + ".";

    private CorreoPlantilla(String titulo) {
        this.titulo = titulo;
        texto.append(titulo).append("\n").append("=".repeat(Math.min(titulo.length(), 60))).append("\n\n");
    }

    public static CorreoPlantilla nueva(String titulo) {
        return new CorreoPlantilla(titulo);
    }

    public CorreoPlantilla parrafo(String valor) {
        if (valor == null || valor.isBlank()) {
            return this;
        }
        cerrarDatos();
        html.append("<p style=\"margin:0 0 16px;font-size:15px;line-height:1.55;color:#1e293b\">")
                .append(esc(valor).replace("\n", "<br>")).append("</p>");
        texto.append(valor).append("\n\n");
        return this;
    }

    /** Fila "etiqueta: valor". Se omite si el valor está vacío. */
    public CorreoPlantilla dato(String etiqueta, Object valor) {
        if (valor != null && !valor.toString().isBlank()) {
            datos.add(new String[]{etiqueta, valor.toString()});
        }
        return this;
    }

    /** Recuadro destacado, por ejemplo el monto de la cotización. */
    public CorreoPlantilla destacado(String etiqueta, String valor, String nota) {
        cerrarDatos();
        html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 0 20px;border-collapse:separate\">")
                .append("<tr><td style=\"background:#eff6fc;border:1px solid #cfe3f4;border-radius:12px;padding:18px 20px\">")
                .append("<div style=\"font-size:12px;letter-spacing:.08em;text-transform:uppercase;color:#475569\">").append(esc(etiqueta)).append("</div>")
                .append("<div style=\"font-size:28px;font-weight:800;color:").append(AZUL_OSCURO).append(";margin-top:4px\">").append(esc(valor)).append("</div>");
        if (nota != null && !nota.isBlank()) {
            html.append("<div style=\"font-size:13px;color:#475569;margin-top:4px\">").append(esc(nota)).append("</div>");
        }
        html.append("</td></tr></table>");
        texto.append(etiqueta).append(": ").append(valor);
        if (nota != null && !nota.isBlank()) {
            texto.append(" (").append(nota).append(")");
        }
        texto.append("\n\n");
        return this;
    }

    public CorreoPlantilla boton(String etiqueta, String url) {
        if (url == null || url.isBlank()) {
            return this;
        }
        cerrarDatos();
        html.append("<p style=\"margin:8px 0 20px\"><a href=\"").append(esc(url))
                .append("\" style=\"display:inline-block;background:").append(AZUL)
                .append(";color:#ffffff;text-decoration:none;font-weight:600;font-size:14px;padding:12px 22px;border-radius:999px\">")
                .append(esc(etiqueta)).append("</a></p>");
        texto.append(etiqueta).append(": ").append(url).append("\n\n");
        return this;
    }

    public CorreoPlantilla pie(String valor) {
        this.pie = valor;
        return this;
    }

    public String texto() {
        cerrarDatos();
        return texto + "--\n" + EMPRESA + "\n" + pie + "\n";
    }

    public String html() {
        cerrarDatos();
        return "<!doctype html><html lang=\"es\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"></head>"
                + "<body style=\"margin:0;padding:0;background:#f1f5f9;font-family:Arial,Helvetica,sans-serif\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"background:#f1f5f9;padding:24px 12px\"><tr><td align=\"center\">"
                + "<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"max-width:600px;background:#ffffff;border-radius:16px;overflow:hidden\">"
                + "<tr><td style=\"background:" + AZUL + ";background-image:linear-gradient(135deg,#185f97,#2178bd,#2b8fd8);padding:22px 28px\">"
                + "<div style=\"font-size:11px;letter-spacing:.3em;color:#dbeafe;font-weight:700\">TRANSPORTES</div>"
                + "<div style=\"font-size:20px;color:#ffffff;font-weight:800\">GÓMEZ VELÁSQUEZ</div></td></tr>"
                + "<tr><td style=\"padding:28px\">"
                + "<h1 style=\"margin:0 0 18px;font-size:21px;line-height:1.3;color:#0f172a\">" + esc(titulo) + "</h1>"
                + html
                + "</td></tr>"
                + "<tr><td style=\"padding:16px 28px;background:#f8fafc;border-top:1px solid #e2e8f0;font-size:12px;color:#64748b;line-height:1.5\">"
                + esc(EMPRESA) + " · Chiloé<br>" + esc(pie) + "</td></tr>"
                + "</table></td></tr></table></body></html>";
    }

    private void cerrarDatos() {
        if (datos.isEmpty()) {
            return;
        }
        html.append("<table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" style=\"margin:0 0 20px;border-collapse:collapse;font-size:14px\">");
        for (String[] d : datos) {
            html.append("<tr><td style=\"padding:8px 12px 8px 0;border-bottom:1px solid #e2e8f0;color:#64748b;width:38%;vertical-align:top\">")
                    .append(esc(d[0]))
                    .append("</td><td style=\"padding:8px 0;border-bottom:1px solid #e2e8f0;color:#0f172a;vertical-align:top\">")
                    .append(esc(d[1]).replace("\n", "<br>")).append("</td></tr>");
            texto.append("- ").append(d[0]).append(": ").append(d[1]).append("\n");
        }
        html.append("</table>");
        texto.append("\n");
        datos.clear();
    }

    private static String esc(String valor) {
        return valor == null ? "" : HtmlUtils.htmlEscape(valor, "UTF-8");
    }
}
