package com.transporte_gomez.erp.enums;

/** Resultado de la entrega. NO_ENTREGADO y RECHAZADO se pueden reprogramar en otra ruta. */
public enum EntregaEstado {
    PENDIENTE, ENTREGADO, NO_ENTREGADO, RECHAZADO;

    public boolean resuelta() {
        return this != PENDIENTE;
    }

    public boolean reprogramable() {
        return this == NO_ENTREGADO || this == RECHAZADO;
    }
}
