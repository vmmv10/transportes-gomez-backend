package com.transporte_gomez.erp.enums;

public enum AuditoriaOperacion {
    CREADO( "Creado"),
    ACTUALIZADO("Actualizado"),
    ELIMINADO("Eliminado"),
    ENTREGADO("Entregado");

    private final String nombre;

    AuditoriaOperacion(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

}
