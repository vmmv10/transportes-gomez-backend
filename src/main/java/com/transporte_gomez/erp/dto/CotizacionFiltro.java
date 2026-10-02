package com.transporte_gomez.erp.dto;

import com.transporte_gomez.erp.enums.CotizacionEstado;
import lombok.Data;

@Data
public class CotizacionFiltro {
    private CotizacionEstado estado;
    /** true = NUEVA y EN_REVISION */
    private Boolean pendientes;
    /** Busca en código, nombre, empresa y correo */
    private String texto;
    /** yyyy-MM-dd */
    private String desde;
    private String hasta;
}
