package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Entrega;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EntregaAdapter {

    private final OrdenServicioAdapter ordenServicioAdapter;

    public EntregaEntity createEntrega(RutaEntity rutaEntity, OrdenServicioEntity ordenServicioEntity, Integer orden) {
        EntregaEntity entregaEntity = new EntregaEntity();
        entregaEntity.setRuta(rutaEntity);
        entregaEntity.setOrdenServicio(ordenServicioEntity);
        entregaEntity.setCreadoEn(java.time.Instant.now());
        entregaEntity.setEntregado(false);
        entregaEntity.setOrden(orden);

        return entregaEntity;
    }

    public Entrega getEntrega(EntregaEntity entregaEntity) {
        Entrega entrega = new Entrega();
        entrega.setId(entregaEntity.getId());
        entrega.setOrdenServicio(ordenServicioAdapter.getOrdenServicio(entregaEntity.getOrdenServicio(), false));
        entrega.setEntregado(entregaEntity.getEntregado());
        entrega.setRuta(entregaEntity.getRuta().getId());
        return entrega;
    }

}
