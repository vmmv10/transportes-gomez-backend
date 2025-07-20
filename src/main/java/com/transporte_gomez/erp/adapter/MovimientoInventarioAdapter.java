package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.entity.IngresosEmergenciaDetalleEntity;
import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.BodegaRepository;
import com.transporte_gomez.erp.repository.DevolucionDetalleRepository;
import com.transporte_gomez.erp.repository.IngresosEmergenciaDetalleRepository;
import com.transporte_gomez.erp.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovimientoInventarioAdapter {

    private final BodegaRepository bodegaRepository;
    private final ItemRepository itemRepository;
    private final DevolucionDetalleRepository devolucionDetalleRepository;
    private final IngresosEmergenciaDetalleRepository ingresosEmergenciaDetalleRepository;

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
            case DEVOLUCION -> {
                DevolucionDetalleEntity devolucionDetalle = devolucionDetalleRepository.findById(detalleId)
                        .orElseThrow(() -> new IllegalArgumentException("Devolución detalle no encontrado con ID: " + detalleId));
                movimiento.setItem(itemRepository.getReferenceById(devolucionDetalle.getItem().getId()));
                movimiento.setCantidad(devolucionDetalle.getCantidad());
                movimiento.setTipo(MovimientoInventarioTipo.DEVOLUCION.getId());
            }
            case INGRESO_EMERGENCIA -> {
                IngresosEmergenciaDetalleEntity ingresoDetalle = ingresosEmergenciaDetalleRepository.findById(detalleId.intValue())
                        .orElseThrow(() -> new IllegalArgumentException("Ingreso emergencia detalle no encontrado con ID: " + detalleId));
                movimiento.setItem(itemRepository.getReferenceById(ingresoDetalle.getItem().getId()));
                movimiento.setCantidad(ingresoDetalle.getCantidad());
                movimiento.setTipo(MovimientoInventarioTipo.INGRESO_EMERGENCIA.getId());
            }
            default -> throw new IllegalArgumentException("Tipo de origen no soportado: " + origenTipo);
        }

        return movimiento;
    }


}
