package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Item;
import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.entity.IngresosDetalleEntity;
import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import com.transporte_gomez.erp.entity.OrdenServicioDetalleEntity;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
            Long itemId,
            Long bodegaId,
            Long entidadId,
            BigDecimal cantidad
    ) {
        MovimientosInventarioEntity movimiento = new MovimientosInventarioEntity();
        movimiento.setBodega(bodegaRepository.getReferenceById(bodegaId));
        movimiento.setTipoMovimiento(operacion.getTipo());
        movimiento.setFecha(Instant.now());
        movimiento.setEntidadId(entidadId);

        switch (origenTipo) {
            case INGRESO -> {
                movimiento.setItem(itemRepository.getReferenceById(itemId));
                movimiento.setCantidad(cantidad);
                movimiento.setTipo(MovimientoInventarioTipo.INGRESO.getId());
            }
            case ORDEN_SERVICIO -> {
                movimiento.setItem(itemRepository.getReferenceById(itemId));
                movimiento.setCantidad(cantidad);
                movimiento.setTipo(MovimientoInventarioTipo.ORDEN_SERVICIO.getId());
            }
            default -> throw new IllegalArgumentException("Tipo de origen no soportado: " + origenTipo);
        }

        return movimiento;
    }

    public MovimientosInventarioEntity movimientoAjuste(Item item, BigDecimal cantidad, Long bodegaId) {
        MovimientosInventarioEntity movimiento = new MovimientosInventarioEntity();
        movimiento.setBodega(bodegaRepository.getReferenceById(bodegaId));
        movimiento.setItem(itemRepository.getReferenceById(item.getId()));
        movimiento.setCantidad(cantidad);
        movimiento.setTipo(MovimientoInventarioTipo.AJUSTE.getId());
        movimiento.setFecha(Instant.now());
        movimiento.setTipoMovimiento(MovimientoInventarioTipoOperacion.AJUSTE.getTipo());

        return movimiento;
    }
}
