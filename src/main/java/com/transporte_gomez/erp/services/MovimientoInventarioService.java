package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.MovimientoInventarioAdapter;
import com.transporte_gomez.erp.dto.DevolucionDetalle;
import com.transporte_gomez.erp.entity.MovimientosInventarioEntity;
import com.transporte_gomez.erp.repository.MovimientosInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MovimientoInventarioService {

    private final MovimientoInventarioAdapter movimientoInventarioAdapter;
    private final MovimientosInventarioRepository movimientosInventarioRepository;

    public void createByDevolucion(Long bodega, Long devolucionDetalleId) {
        MovimientosInventarioEntity movimientosInventarioEntity = movimientoInventarioAdapter.createByDevolucion(devolucionDetalleId, bodega);
        movimientosInventarioRepository.save(movimientosInventarioEntity);
    }
}
