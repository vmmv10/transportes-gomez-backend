package com.transporte_gomez.erp.dto;

import lombok.Data;

@Data
public class Usuario {

    public Long id;
    public String nombre;
    public String email;
    public String rol;
}
