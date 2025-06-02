package com.transporte_gomez.erp.entity;

import lombok.*;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios", schema = "qa")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "rol")
    private String rol;

    @Column(name = "informacion_contacto")
    private String informacionContacto;

    @Column(name = "telefono")
    private String telefono;
}
