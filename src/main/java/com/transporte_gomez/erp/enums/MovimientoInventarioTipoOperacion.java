package com.transporte_gomez.erp.enums;

public enum MovimientoInventarioTipoOperacion {
    ENTRADA("ENTRADA"),
    SALIDA("SALIDA");

    private final String tipo;

    MovimientoInventarioTipoOperacion(String tipo) {
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }
}
