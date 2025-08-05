package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.SaldoBodega;
import com.transporte_gomez.erp.entity.SaldosBodegaEntity;
import com.transporte_gomez.erp.repository.BodegaRepository;
import com.transporte_gomez.erp.repository.ItemRepository;
import com.transporte_gomez.erp.repository.SaldosBodegaRepository;
import com.transporte_gomez.erp.services.MovimientoInventarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
public class SaldoBodegaAdapter {

    private final SaldosBodegaRepository saldosBodegaRepository;
    private final BodegaRepository bodegaRepository;
    private final ItemRepository itemRepository;
    private final ItemAdapter itemAdapter;
    private final MovimientoInventarioService movimientoInventarioService;

    public void createOrUpdate(Long id, BigDecimal cantidad, Long bodega, boolean sumar) {
        SaldosBodegaEntity saldosBodegaEntity = saldosBodegaRepository.findByBodega_IdAndItem_Id(bodega, id);
        if (saldosBodegaEntity == null && sumar) {
            saldosBodegaEntity = new SaldosBodegaEntity();
            saldosBodegaEntity.setBodega(bodegaRepository.getReferenceById(bodega));
            saldosBodegaEntity.setItem(itemRepository.getReferenceById(id));
            saldosBodegaEntity.setCantidad(cantidad);
            saldosBodegaRepository.save(saldosBodegaEntity);
        } else {
            if (!sumar && saldosBodegaEntity.getCantidad().compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("No se puede restar más de lo que hay en el saldo de bodega.");
            }
            if (!sumar) {
                saldosBodegaEntity.setCantidad(saldosBodegaEntity.getCantidad().subtract(cantidad));
            }
            if(sumar){
               saldosBodegaEntity.setCantidad(saldosBodegaEntity.getCantidad().add(cantidad));
            }
            saldosBodegaRepository.save(saldosBodegaEntity);
        }
    }

    public SaldoBodega get(SaldosBodegaEntity saldosBodegaEntity) {
        if (saldosBodegaEntity == null) {
            return null;
        }

        SaldoBodega saldoBodega = new SaldoBodega();
        saldoBodega.setId(saldosBodegaEntity.getId());
        saldoBodega.setSaldo(saldosBodegaEntity.getCantidad());
        saldoBodega.setItem(itemAdapter.getItem(saldosBodegaEntity.getItem()));

        return saldoBodega;
    }

    public void ajustarSaldoBodega(SaldoBodega saldoBodega, Long bodegaId) {
        SaldosBodegaEntity saldosBodegaEntity = saldosBodegaRepository.findByBodega_IdAndItem_Id(bodegaId, saldoBodega.getItem().getId());
        if (saldosBodegaEntity == null) {
            throw new IllegalArgumentException("No existe un saldo de bodega para el item y bodega especificados.");
        }
        saldosBodegaEntity.setCantidad(saldoBodega.getSaldo());
        saldosBodegaRepository.save(saldosBodegaEntity);
    }
}
