package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.SaldoBodegaAdapter;
import com.transporte_gomez.erp.dto.SaldoBodega;
import com.transporte_gomez.erp.dto.SaldoBodegaFiltro;
import com.transporte_gomez.erp.repository.SaldosBodegaRepository;
import com.transporte_gomez.erp.specification.SaldoBodegaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class SaldoBodegaService {

    private final SaldoBodegaAdapter saldoBodegaAdapter;
    private final SaldosBodegaRepository saldosBodegaRepository;

    public void createOrUpdate(Long id, Long bodega, String tipoOperacion, BigDecimal cantidad) {
        if (tipoOperacion.equalsIgnoreCase("ENTRADA")) {
            saldoBodegaAdapter.createOrUpdate(id, cantidad, bodega, true);
        } else if (tipoOperacion.equalsIgnoreCase("SALIDA")) {
            saldoBodegaAdapter.createOrUpdate(id, cantidad, bodega, false);
        }
    }

    public Page<SaldoBodega> findAll(SaldoBodegaFiltro filtro, Pageable pageable) {
        return saldosBodegaRepository.findAll(SaldoBodegaSpecification.conFiltros(filtro), pageable)
                .map(saldoBodegaAdapter::get);
    }

    public SaldoBodega findByCodigo(String codigo) {
        return saldoBodegaAdapter.get(saldosBodegaRepository.findByItem_Codigo(codigo));
    }
}
