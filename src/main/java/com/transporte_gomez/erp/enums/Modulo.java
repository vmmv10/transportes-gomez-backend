package com.transporte_gomez.erp.enums;

import java.util.Objects;

public enum Modulo {
        ORDEN_SERVICIO(1, "Orden de Servicio"),
        ESCUELA(2, "Escuela"),
        PROVEEDOR(3, "Proveedor"),
        DOCUMENTO(4, "Documento"),
        USUARIO(5, "Usuario"),
        IMAGEN(6, "Imagen"),
        INVENTARIO(7, "Inventario"),
        RUTA(8, "Ruta de Entrega");

        private final Integer codigo;
        private final String nombre;

        Modulo(Integer codigo, String nombre) {
            this.codigo = codigo;
            this.nombre = nombre;
        }

        public Integer getCodigo() {
            return codigo;
        }

        public String getNombre() {
            return nombre;
        }

        public static Modulo fromCodigo(Integer codigo) {
            for (Modulo modulo : values()) {
                if (Objects.equals(modulo.codigo, codigo)) {
                    return modulo;
                }
            }
            throw new IllegalArgumentException("Código de módulo inválido: " + codigo);
        }
}
