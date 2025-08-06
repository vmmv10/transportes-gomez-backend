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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
@Slf4j
public class MovimientoInventarioService {

    private final MovimientoInventarioAdapter movimientoInventarioAdapter;
    private final MovimientosInventarioRepository movimientosInventarioRepository;
    private final SaldosBodegaRepository saldosBodegaRepository;

    public void create(MovimientoInventarioTipo origenTipo,
                       MovimientoInventarioTipoOperacion operacion,
                       Long item,
                       Long bodegaId, Long entidad, BigDecimal cantidad) {
        MovimientosInventarioEntity movimiento = movimientoInventarioAdapter.crearMovimiento(
                origenTipo, operacion, item, bodegaId, entidad, cantidad);
        movimientosInventarioRepository.save(movimiento);
    }

    public void ajustarSaldoBodega(SaldoBodega saldoBodega, Long bodegaId) {
        movimientoInventarioAdapter.movimientoAjuste(saldoBodega.getItem(), saldoBodega.getSaldo(), bodegaId);
    }

    @Transactional
    public void deleteMovimiento(MovimientoInventarioTipo origenTipo,
                                 Long item,
                                 Long bodegaId,
                                 Long entidad) {

        MovimientosInventarioEntity movimiento = movimientosInventarioRepository
                .findByBodega_IdAndTipoAndItem_IdAndEntidadId(bodegaId, origenTipo.getId(), item, entidad);

        if (movimiento == null) {
            throw new RuntimeException("Movimiento no encontrado para eliminar");
        }

        SaldosBodegaEntity saldoBodega = saldosBodegaRepository
                .findByBodega_IdAndItem_Id(bodegaId, movimiento.getItem().getId());

        if (saldoBodega == null) {
            throw new RuntimeException("Saldo de bodega no encontrado para actualizar");
        }

        BigDecimal cantidadActual = saldoBodega.getCantidad();
        BigDecimal nuevaCantidad = cantidadActual.add(movimiento.getCantidad());

        if (nuevaCantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("Saldo resultante negativo. Verificar integridad de inventario.");
        }

        saldoBodega.setCantidad(nuevaCantidad);
        saldosBodegaRepository.save(saldoBodega);

        movimientosInventarioRepository.delete(movimiento);
    }

    @Transactional
    public void modificarCantidad(MovimientoInventarioTipo origenTipo,
                                  Long itemId,
                                  Long bodegaId,
                                  Long entidad,
                                  BigDecimal cantidadNueva) {
        log.info("Modificando cantidad de movimiento de inventario: origenTipo={}, itemId={}, bodegaId={}, entidad={}, cantidadNueva={}",
                origenTipo.getId(), itemId, bodegaId, entidad, cantidadNueva);
        MovimientosInventarioEntity movimiento = movimientosInventarioRepository
                .findByBodega_IdAndTipoAndItem_IdAndEntidadId(bodegaId, origenTipo.getId(), itemId, entidad);

        if (movimiento == null) {
            throw new RuntimeException("Movimiento no encontrado para modificar");
        }

        SaldosBodegaEntity saldoBodega = saldosBodegaRepository
                .findByBodega_IdAndItem_Id(bodegaId, movimiento.getItem().getId());

        if (saldoBodega == null) {
            throw new RuntimeException("Saldo de bodega no encontrado para actualizar");
        }

        BigDecimal cantidadAnterior = movimiento.getCantidad();
        BigDecimal diferencia = cantidadNueva.subtract(cantidadAnterior); // puede ser negativa o positiva
        BigDecimal saldoActual = saldoBodega.getCantidad();
        BigDecimal nuevoSaldo = saldoActual.subtract(diferencia); // si diferencia es negativa, se suma

        if (nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("El saldo en bodega no puede quedar negativo.");
        }

        saldoBodega.setCantidad(nuevoSaldo);
        movimiento.setCantidad(cantidadNueva);

        saldosBodegaRepository.save(saldoBodega);
        movimientosInventarioRepository.save(movimiento);
    }

}
