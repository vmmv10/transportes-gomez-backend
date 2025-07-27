package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.IngresoEmergenciaAdapter;
import com.transporte_gomez.erp.adapter.IngresoEmergenciaDetalleAdapter;
import com.transporte_gomez.erp.dto.*;
import com.transporte_gomez.erp.dto.IngresosEmergencia;
import com.transporte_gomez.erp.entity.IngresosEmergenciaDetalleEntity;
import com.transporte_gomez.erp.entity.IngresosEmergenciaEntity;
import com.transporte_gomez.erp.enums.IngresoEmergenciaEstado;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipo;
import com.transporte_gomez.erp.enums.MovimientoInventarioTipoOperacion;
import com.transporte_gomez.erp.repository.IngresosEmergenciaDetalleRepository;
import com.transporte_gomez.erp.repository.IngresosEmergenciaRepository;
import com.transporte_gomez.erp.specification.IngresosEmergenciaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class IngresoEmergenciaService {
    private final IngresoEmergenciaAdapter ingresoEmergenciaAdapter;
    private final IngresoEmergenciaDetalleAdapter ingresoEmergenciaDetalleAdapter;
    private final IngresosEmergenciaDetalleRepository ingresosEmergenciaDetalleRepository;
    private final IngresosEmergenciaRepository ingresosEmergenciaRepository;
    private final ItemService itemService;
    private final SaldoBodegaService saldoBodegaService;
    private final MovimientoInventarioService movimientoInventarioService;

    public Page<IngresosEmergencia> findAll(Pageable pageable, IngresosEmergenciaFiltro filtros) {
        return ingresosEmergenciaRepository.findAll(IngresosEmergenciaSpecification.conFiltros(filtros),pageable)
                .map(ingresosEmergenciaEntity -> ingresoEmergenciaAdapter.toDto(ingresosEmergenciaEntity, false));

    }

    public IngresosEmergencia create(IngresosEmergencia ingresoEmergencia, Usuario usuario) {
        IngresosEmergenciaEntity ingresosEmergenciaEntity = ingresoEmergenciaAdapter.toEntity(ingresoEmergencia);
        ingresosEmergenciaEntity.setFecha(Instant.now());
        ingresosEmergenciaEntity.setUser(usuario.getId());
        ingresosEmergenciaEntity = ingresosEmergenciaRepository.save(ingresosEmergenciaEntity);
        return ingresoEmergenciaAdapter.toDto(ingresosEmergenciaEntity, false);
    }

    public IngresosEmergencia getByFolio(Integer folio) {
        IngresosEmergenciaEntity ingresosEmergenciaEntity = ingresosEmergenciaRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado con folio: " + folio));
        return ingresoEmergenciaAdapter.toDto(ingresosEmergenciaEntity, true);
    }

    public IngresosEmergenciaDetalle createDetalle(Integer folio, String codigo) {
        IngresosEmergenciaEntity ingresosEmergenciaEntity = ingresosEmergenciaRepository.getReferenceById(folio);

        Item item = itemService.getByCodigo(codigo);

        IngresosEmergenciaDetalle ingresosEmergenciaDetalle = new IngresosEmergenciaDetalle();
        ingresosEmergenciaDetalle.setCantidad(BigDecimal.ONE);
        ingresosEmergenciaDetalle.setItem(item);

        IngresosEmergenciaDetalleEntity ingresoEmergenciaDetalleEntity = ingresoEmergenciaDetalleAdapter.create(ingresosEmergenciaDetalle);
        ingresoEmergenciaDetalleEntity.setIngreso(ingresosEmergenciaEntity);

        IngresosEmergenciaDetalleEntity ingresoDetalleEntitySave = ingresosEmergenciaDetalleRepository.save(ingresoEmergenciaDetalleEntity);

        return ingresoEmergenciaDetalleAdapter.get(ingresoDetalleEntitySave);
    }

    public void sumarCantidadDetalle(Integer detalleId, BigDecimal cantidad) {
        IngresosEmergenciaDetalleEntity ingresoEmergenciaDetalleEntity = ingresosEmergenciaDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + detalleId));

        ingresoEmergenciaDetalleEntity.setCantidad(ingresoEmergenciaDetalleEntity.getCantidad().add(cantidad));
        ingresosEmergenciaDetalleRepository.save(ingresoEmergenciaDetalleEntity);
    }

    public void actualizarEstado(Integer folio, Integer estado) {
        IngresosEmergenciaEntity ingresosEmergenciaEntity = ingresosEmergenciaRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrada con folio: " + folio));

        ingresosEmergenciaEntity.setEstado(estado);
        ingresosEmergenciaRepository.save(ingresosEmergenciaEntity);
    }

    public void updateEstado(Integer folio, Integer estado) {
        IngresosEmergenciaEntity ingresosEmergenciaEntity = ingresosEmergenciaRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrada con folio: " + folio));

        ingresosEmergenciaEntity.setEstado(estado);

        if (IngresoEmergenciaEstado.CERRADO.getCodigo().equals(estado)) {
            ingresosEmergenciaEntity.setFecha(Instant.now());
            ingresosEmergenciaEntity.getDetalles().forEach(detalle -> {
                movimientoInventarioService.create(MovimientoInventarioTipo.INGRESO_EMERGENCIA, MovimientoInventarioTipoOperacion.ENTRADA, detalle.getId().longValue(), 3L);
                saldoBodegaService.createOrUpdate(detalle.getItem().getId(), 3L, "ENTRADA", detalle.getCantidad());
            });
        }
        ingresosEmergenciaRepository.save(ingresosEmergenciaEntity);
    }

    public void modificarCantidadDetalle(Integer detalleId, BigDecimal cantidad) {
        IngresosEmergenciaDetalleEntity ingresoEmergenciaDetalleEntity = ingresosEmergenciaDetalleRepository.getReferenceById(detalleId);
        if (ingresoEmergenciaDetalleEntity == null) {
            throw new IllegalArgumentException("Detalle no encontrado con ID: " + detalleId);
        }
        if (cantidad.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }
        ingresoEmergenciaDetalleEntity.setCantidad(cantidad);
        ingresosEmergenciaDetalleRepository.save(ingresoEmergenciaDetalleEntity);
    }

    public void eliminarDetalle(Integer detalleId) {
        IngresosEmergenciaDetalleEntity ingresoEmergenciaDetalleEntity = ingresosEmergenciaDetalleRepository.findById(detalleId)
                .orElseThrow(() -> new IllegalArgumentException("Detalle no encontrado con ID: " + detalleId));

        ingresosEmergenciaDetalleRepository.delete(ingresoEmergenciaDetalleEntity);
    }

    public IngresosEmergencia getTemporalByUser(Usuario usuario) {
        List<IngresosEmergenciaEntity> ingresosEmergenciaEntity = ingresosEmergenciaRepository.findByUserAndEstado(usuario.getId(), IngresoEmergenciaEstado.TERMPORAL.getCodigo());
        if (ingresosEmergenciaEntity.isEmpty()) {
            return null;
        }
        return ingresoEmergenciaAdapter.toDto(ingresosEmergenciaEntity.get(0), true);
    }

    public void delete(Integer folio) {
        IngresosEmergenciaEntity ingresosEmergenciaEntity = ingresosEmergenciaRepository.findById(folio)
                .orElseThrow(() -> new IllegalArgumentException("Ingreso no encontrado con folio: " + folio));
        ingresosEmergenciaRepository.delete(ingresosEmergenciaEntity);
    }
}
