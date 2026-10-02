package com.transporte_gomez.erp.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/** Usuario para la pantalla de administración: datos de Auth0 + datos del ERP. */
@Data
public class UsuarioAdmin {
    /** id en la tabla usuarios del ERP */
    private Long id;
    private String auth0Id;
    private String email;
    private String nombre;
    private String apellidos;
    private String telefono;
    /** Nombres de los roles de Auth0 (Administrador, Operaciones, Bodega, Conductor, Cliente) */
    private List<String> roles = new ArrayList<>();
    /** Organización del usuario con rol Cliente (ej. SLEP Chiloé) */
    private Long clienteId;
    private String clienteNombre;
    private Boolean bloqueado;
    private String ultimoIngreso;
    private String creado;
    private Integer ingresos;
}
