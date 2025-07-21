package com.transporte_gomez.erp.enums;

public enum MovimientoInventarioTipo {
    DEVOLUCION("DEVOLUCION", 0),
    INGRESO_EMERGENCIA("INGRESO_EMERGENCIA", 1),
    ORDEN_SERVICIO("ORDEN_SERVICIO", 2);

    private final String tipo;
    private final Integer id;

    MovimientoInventarioTipo(String tipo, Integer id) {
        this.tipo = tipo;
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getId() {
        return id;
    }
}
