package com.transporte_gomez.erp.dto;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class Os {
    @CsvBindByName(column = "FECHA")
    private String fecha;

    @CsvBindByName(column = "VEHICULO")
    private String vehiculo;

    @CsvBindByName(column = "OS")
    private Long os;
}
