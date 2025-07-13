package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.entity.DevolucionDetalleEntity;
import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.BodegaRepository;
import com.transporte_gomez.erp.repository.DevolucionDetalleRepository;
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

    public MovimientosInventarioEntity createByDevolucion(Long devolucionDetalle, Long bodega) {

        DevolucionDetalleEntity devolucionDetalleEntity = devolucionDetalleRepository.findById(devolucionDetalle)
                .orElseThrow(() -> new IllegalArgumentException("Devolución detalle no encontrado con ID: " + devolucionDetalle));

        MovimientosInventarioEntity movimiento = new MovimientosInventarioEntity();
        movimiento.setBodega(bodegaRepository.getReferenceById(bodega));
        movimiento.setItem(itemRepository.getReferenceById(devolucionDetalleEntity.getItem().getId()));
        movimiento.setCantidad(devolucionDetalleEntity.getCantidad());
        movimiento.setTipoMovimiento(MovimientoInventarioTipoOperacion.ENTRADA.getTipo());
        movimiento.setTipo(MovimientoInventarioTipo.DEVOLUCION.getId());
        movimiento.setFecha(Instant.now());

        return movimiento;
    }
}
