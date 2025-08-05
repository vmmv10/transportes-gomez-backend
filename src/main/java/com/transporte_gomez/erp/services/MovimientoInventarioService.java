package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.MovimientoInventarioAdapter;
import com.transporte_gomez.erp.dto.DevolucionDetalle;
import com.transporte_gomez.erp.dto.SaldoBodega;
import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import com.transporte_gomez.erp.entity.SaldosBodegaEntity;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.MovimientosInventarioRepository;
import com.transporte_gomez.erp.repository.SaldosBodegaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioAdapter movimientoInventarioAdapter;
    private final MovimientosInventarioRepository movimientosInventarioRepository;
    private final SaldosBodegaRepository saldosBodegaRepository;

    public void create(MovimientoInventarioTipo origenTipo,
                       MovimientoInventarioTipoOperacion operacion,
                       Long detalleId,
                       Long bodegaId, Long entidad) {
        MovimientosInventarioEntity movimiento = movimientoInventarioAdapter.crearMovimiento(
                origenTipo, operacion, detalleId, bodegaId, entidad);
        movimientosInventarioRepository.save(movimiento);
    }

    public void ajustarSaldoBodega(SaldoBodega saldoBodega, Long bodegaId) {
        movimientoInventarioAdapter.movimientoAjuste(saldoBodega.getItem(), saldoBodega.getSaldo(), bodegaId);
    }

    public void deleteMovimiento(MovimientoInventarioTipo origenTipo,
                                 Long detalleId,
                                 Long bodegaId, Long entidad) {
        MovimientosInventarioEntity movimiento = movimientosInventarioRepository.findByBodega_IdAndTipoMovimientoAndItem_IdAndEntidadId(bodegaId, origenTipo.getTipo(), detalleId, entidad);
        if (movimiento != null) {
            movimientosInventarioRepository.delete(movimiento);
            SaldosBodegaEntity saldoBodega = saldosBodegaRepository.findByBodega_IdAndItem_Id(bodegaId, movimiento.getItem().getId());
            if (saldoBodega != null) {
                BigDecimal cantidadActual = saldoBodega.getCantidad();
                BigDecimal nuevaCantidad = cantidadActual.add(movimiento.getCantidad());
                saldoBodega.setCantidad(nuevaCantidad);
                saldosBodegaRepository.save(saldoBodega);
            } else {
                throw new RuntimeException("Saldo de bodega no encontrado para actualizar");
            }
        } else {
            throw new RuntimeException("Movimiento no encontrado para eliminar");
        }
    }

    public void modificarCantidad(MovimientoInventarioTipo origenTipo,
                                 MovimientoInventarioTipoOperacion operacion,
                                 Long detalleId,
                                 Long bodegaId, Long entidad, BigDecimal cantidad) {
        MovimientosInventarioEntity movimiento = movimientosInventarioRepository.findByBodega_IdAndTipoMovimientoAndItem_IdAndEntidadId(bodegaId, operacion.getTipo(), detalleId, entidad);
        if (movimiento != null) {
            BigDecimal cantidadActual = movimiento.getCantidad();
            movimiento.setCantidad(cantidad);
            SaldosBodegaEntity saldoBodega = saldosBodegaRepository.findByBodega_IdAndItem_Id(bodegaId, movimiento.getItem().getId());
            if (cantidad.compareTo(cantidadActual) > 0) {
                BigDecimal cantidadNuevo = cantidad.subtract(cantidadActual);
                BigDecimal nuevoSaldo = saldoBodega.getCantidad().subtract(cantidadNuevo);
                saldoBodega.setCantidad(nuevoSaldo);
            } else if (cantidad.compareTo(cantidadActual) < 0) {
                BigDecimal cantidadNuevo = cantidadActual.subtract(cantidad);
                BigDecimal nuevoSaldo = saldoBodega.getCantidad().add(cantidadNuevo);
                saldoBodega.setCantidad(nuevoSaldo);
            }
            saldosBodegaRepository.save(saldoBodega);
            movimientosInventarioRepository.save(movimiento);
        } else {
            throw new RuntimeException("Movimiento no encontrado para modificar");
        }
    }
}
