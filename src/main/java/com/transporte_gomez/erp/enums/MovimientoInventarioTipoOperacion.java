package com.transporte_gomez.erp.enums;

public enum MovimientoInventarioTipoOperacion {
    ENTRADA("entrada"),
    SALIDA("salida");

    private final String tipo;

    MovimientoInventarioTipoOperacion(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
