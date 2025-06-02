package com.transporte_gomez.erp.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Establecimiento {

    @CsvBindByName(column = "AGNO")
    private int agno;

    @CsvBindByName(column = "RBD")
    private String rbd;

    @CsvBindByName(column = "DGV_RBD")
    private int dgvRbd;

    @CsvBindByName(column = "NOM_RBD")
    private String nombreRbd;

    @CsvBindByName(column = "RUT_SOSTENEDOR")
    private String rutSostenedor;

    @CsvBindByName(column = "NOM_REG_RBD_A")
    private String nombreRegion;

    @CsvBindByName(column = "COD_PRO_RBD")
    private String codProRbd;

    @CsvBindByName(column = "LONGITUD")
    private String longitud;

    @CsvBindByName(column = "LATITUD")
    private String latitud;

    @CsvBindByName(column = "NOM_COM_RBD ")
    private String nombreComRbd;

}
