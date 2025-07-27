package com.transporte_gomez.erp.entity;

import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name = "usuarios", schema = "qa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", length = Integer.MAX_VALUE)
    private String nombre;

    @Column(name = "rol", length = Integer.MAX_VALUE)
    private String rol;

    @Column(name = "informacion_contacto", length = Integer.MAX_VALUE)
    private String informacionContacto;

    @Column(name = "telefono", length = Integer.MAX_VALUE)
    private String telefono;

    @Column(name = "auth0id", nullable = false, length = Integer.MAX_VALUE)
    private String auth0id;

    @Column(name = "email", length = Integer.MAX_VALUE)
    private String email;

    @Column(name = "apellidos", length = Integer.MAX_VALUE)
    private String apellidos;

    @ColumnDefault("false")
    @Column(name = "tema_oscuro", nullable = false)
    private Boolean temaOscuro = false;

}
