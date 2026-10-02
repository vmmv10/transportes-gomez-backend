package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Cliente {
    private Long id;
    private String rut;
    private String razonSocial;
    private String nombreCorto;
    /** NATURAL o JURIDICA */
    private String tipoPersona;
    /** PUBLICO o PRIVADO */
    private String sector;
    private String giro;
    private String direccion;
    private Comuna comuna;
    private String telefono;
    private String email;
    private String contacto;
    private String codigoMp;
    private Boolean activo;
}
