package com.transporte_gomez.erp.enums;

public enum IngresoEmergenciaEstado {
    TERMPORAL("Temporal", 0),
    ABIERTO("Abierto", 1),
    CERRADO("Cerrado", 2);

    private final String descripcion;
    private final Integer codigo;

    IngresoEmergenciaEstado(String descripcion, Integer codigo) {
        this.descripcion = descripcion;
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getByCodigo(Integer codigo) {
        for (DevolucionEstado estado : DevolucionEstado.values()) {
            if (estado.getCodigo().equals(codigo)) {
                return estado.getDescripcion();
            }
        }
        return null; // or throw an exception if preferred
    }
}
