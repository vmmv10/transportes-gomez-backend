package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.SaldoBodegaAdapter;
import com.transporte_gomez.erp.repository.SaldosBodegaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class SaldoBodegaService {

    private final SaldoBodegaAdapter saldoBodegaAdapter;

    public void createOrUpdate(Long id, Long bodega, String tipoOperacion, BigDecimal cantidad) {
        if (tipoOperacion.equals("ENTRADA")) {
            saldoBodegaAdapter.createOrUpdate(id, cantidad, bodega, true);
        } else if (tipoOperacion.equals("SALIDA")) {
            saldoBodegaAdapter.createOrUpdate(id, cantidad, bodega, false);
        }
    }

}
