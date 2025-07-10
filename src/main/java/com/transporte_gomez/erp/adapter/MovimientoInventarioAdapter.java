package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.DevolucionDetalle;
import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.BodegaRepository;
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

    public MovimientosInventarioEntity createByDevolucion(DevolucionDetalle devolucionDetalle, Long bodega) {
        MovimientosInventarioEntity movimiento = new MovimientosInventarioEntity();
        movimiento.setBodega(bodegaRepository.getReferenceById(bodega));
        movimiento.setItem(itemRepository.getReferenceById(devolucionDetalle.getItem().getId()));
        movimiento.setCantidad(devolucionDetalle.getCantidad());
        movimiento.setTipoMovimiento(MovimientoInventarioTipoOperacion.ENTRADA.getTipo());
        movimiento.setTipo(MovimientoInventarioTipo.DEVOLUCION.getId());
        movimiento.setFecha(Instant.now());

        return movimiento;
    }
}
