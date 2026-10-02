package com.transporte_gomez.erp.enums;

/** Motivo que elige quien escribe en el formulario de contacto de la landing. */
public enum MensajeMotivo {
    CONSULTA("Consulta general"),
    ENVIO("Estado de un envío"),
    RECLAMO("Reclamo o sugerencia"),
    PROVEEDOR("Proveedor"),
    TRABAJO("Trabajar con nosotros"),
    PRECIO("Pide un precio");

    private final String texto;

    MensajeMotivo(String texto) {
        this.texto = texto;
    }

    public String getTexto() {
        return texto;
    }

    /** Valor de la landing; si no se reconoce, es una consulta general. */
    public static MensajeMotivo desde(String valor) {
        if (valor == null || valor.isBlank()) {
            return CONSULTA;
        }
        try {
            return valueOf(valor.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return CONSULTA;
        }
    }
}
