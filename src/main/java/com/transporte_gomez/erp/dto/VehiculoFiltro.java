package com.transporte_gomez.erp.dto;

import com.transporte_gomez.erp.enums.VehiculoEstado;
import com.transporte_gomez.erp.enums.VehiculoPropiedad;
import com.transporte_gomez.erp.enums.VehiculoTipo;
import lombok.Data;

@Data
public class VehiculoFiltro {
    /** Busca en nombre, patente, marca y modelo */
    private String texto;
    private VehiculoTipo tipo;
    private VehiculoPropiedad propiedad;
    private VehiculoEstado estado;
    private Boolean activo;
    private Long proveedor;
}
