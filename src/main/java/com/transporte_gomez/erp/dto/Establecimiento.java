package com.transporte_gomez.erp.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Establecimiento {

    @CsvBindByName(column = "RBD")
    private String rbd;

    @CsvBindByName(column = "ESTABLECIMIENTO EDUCACIONAL")
    private String nombre;

    @CsvBindByName(column = "COMUNA")
    private String comuna;

    @CsvBindByName(column = "DIRECTOR")
    private String director;

    @CsvBindByName(column = "TELEFONO")
    private String telefono;

    @CsvBindByName(column = "CORREO")
    private String correo;

    @CsvBindByName(column = "FIJA")
    private String fija;

}
