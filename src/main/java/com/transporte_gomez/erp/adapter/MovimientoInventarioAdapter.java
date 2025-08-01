package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.entity.IngresosDetalleEntity;
import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioAdapter {

    private final BodegaRepository bodegaRepository;
    private final ItemRepository itemRepository;
    private final DevolucionDetalleRepository devolucionDetalleRepository;
    private final IngresosDetalleRepository ingresosEmergenciaDetalleRepository;
    private final OrdenServicioDetalleRepository ordenServicioDetalleRepository;

    public MovimientosInventarioEntity crearMovimiento(
            MovimientoInventarioTipo origenTipo,
            MovimientoInventarioTipoOperacion operacion,
            Long detalleId,
            Long bodegaId
    ) {
        MovimientosInventarioEntity movimiento = new MovimientosInventarioEntity();
        movimiento.setBodega(bodegaRepository.getReferenceById(bodegaId));
        movimiento.setTipoMovimiento(operacion.getTipo());
        movimiento.setFecha(Instant.now());

        switch (origenTipo) {
            case INGRESO -> {
                IngresosDetalleEntity ingresoDetalle = ingresosEmergenciaDetalleRepository.findById(detalleId.intValue())
                        .orElseThrow(() -> new IllegalArgumentException("Ingreso emergencia detalle no encontrado con ID: " + detalleId));
                movimiento.setItem(itemRepository.getReferenceById(ingresoDetalle.getItem().getId()));
                movimiento.setCantidad(ingresoDetalle.getCantidad());
                movimiento.setTipo(MovimientoInventarioTipo.INGRESO.getId());
            }
            case ORDEN_SERVICIO -> {
                OrdenServicioDetalleEntity ordenServicioDetalle = ordenServicioDetalleRepository.findById(detalleId)
                        .orElseThrow(() -> new IllegalArgumentException("Orden de servicio detalle no encontrado con ID: " + detalleId));
                movimiento.setItem(itemRepository.getReferenceById(ordenServicioDetalle.getItem()));
                movimiento.setCantidad(ordenServicioDetalle.getCantidad());
                movimiento.setTipo(MovimientoInventarioTipo.ORDEN_SERVICIO.getId());
            }
            default -> throw new IllegalArgumentException("Tipo de origen no soportado: " + origenTipo);
        }

        return movimiento;
    }


}
