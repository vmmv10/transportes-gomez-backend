package com.transporte_gomez.erp.enums;

public enum MovimientoInventarioTipo {
    ORDEN_SERVICIO("ORDEN_SERVICIO", 2),
    INGRESO("INGRESO", 1),
    AJUSTE("AJUSTE", 3);

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

    public static MovimientoInventarioTipo fromId(Integer id) {
        for (MovimientoInventarioTipo value : MovimientoInventarioTipo.values()) {
            if (value.getId().equals(id)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Tipo de movimiento de inventario no válido: " + id);
    }
}
