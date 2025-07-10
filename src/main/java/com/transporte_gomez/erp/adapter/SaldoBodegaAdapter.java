package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.entity.SaldosBodegaEntity;
import com.transporte_gomez.erp.repository.BodegaRepository;
import com.transporte_gomez.erp.repository.ItemRepository;
import com.transporte_gomez.erp.repository.SaldosBodegaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class SaldoBodegaAdapter {

    private final SaldosBodegaRepository saldosBodegaRepository;
    private final BodegaRepository bodegaRepository;
    private final ItemRepository itemRepository;

    public void createOrUpdate(Long id, BigDecimal cantidad, Long bodega, boolean sumar) {
        SaldosBodegaEntity saldosBodegaEntity = saldosBodegaRepository.findByBodega_IdAndItem_Id(bodega, id);
        if (saldosBodegaEntity == null && sumar) {
            saldosBodegaEntity = new SaldosBodegaEntity();
            saldosBodegaEntity.setBodega(bodegaRepository.getReferenceById(bodega));
            saldosBodegaEntity.setItem(itemRepository.getReferenceById(id));
            saldosBodegaEntity.setCantidad(cantidad);
            saldosBodegaRepository.save(saldosBodegaEntity);
        } else {
            saldosBodegaEntity.setCantidad(saldosBodegaEntity.getCantidad().add(cantidad));
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
}
