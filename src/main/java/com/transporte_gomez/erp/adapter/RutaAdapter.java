package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Entrega;
import com.transporte_gomez.erp.dto.OrdenServicio;
import com.transporte_gomez.erp.dto.Ruta;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import com.transporte_gomez.erp.repository.EntregaRepository;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RutaAdapter {

    private final UsuarioRepository usuarioRepository;
    private final EntregaRepository entregaRepository;
    private final UsuarioAdapter usuarioAdapter;
    private final EntregaAdapter entregaAdapter;
    private final RutaRepository rutaRepository;

    public Ruta getRuta(RutaEntity rutaEntity) {
        Ruta ruta = new Ruta();
        ruta.setId(rutaEntity.getId());
        ruta.setFecha(rutaEntity.getFecha());
        ruta.setEstado(rutaEntity.getEstado());

        if (rutaEntity.getChofer() != null) {
            ruta.setChofer(usuarioAdapter.getUsuario(rutaEntity.getChofer()));
        }

        List<EntregaEntity> entregas = entregaRepository.findByRuta_Id(rutaEntity.getId());

        if (entregas != null && !entregas.isEmpty()) {
            ruta.setEntregas(entregas.stream()
                    .map(entregaAdapter::getEntrega)
                    .toList());
        }
        List<OrdenServicio> ordenes = new ArrayList<>();
        for (Entrega entrega : ruta.getEntregas()) {
            if (entrega.getOrdenServicio() != null) {
                ordenes.add(entrega.getOrdenServicio());
            }
        }

        ruta.setOrdenes(ordenes);

        return ruta;
    }

    public RutaEntity createRuta(Ruta ruta) {
        RutaEntity rutaEntity = new RutaEntity();
        rutaEntity.setId(ruta.getId());
        rutaEntity.setFecha(ruta.getFecha());
        rutaEntity.setEstado("PENDIENTE");

        if (ruta.getChofer() != null) {
            rutaEntity.setChofer(usuarioRepository.findById(ruta.getChofer().getId())
                    .orElseThrow(() -> new RuntimeException("Chofer not found with id: " + ruta.getChofer().getId())));
        }

        return  rutaEntity;
    }

    public RutaEntity update(Ruta ruta, RutaEntity rutaEntity) {

        if (ruta.getChofer() != null) {
            rutaEntity.setChofer(usuarioRepository.findById(ruta.getChofer().getId())
                    .orElseThrow(() -> new RuntimeException("Chofer not found with id: " + ruta.getChofer().getId())));
        }
        rutaEntity.setFecha(ruta.getFecha());
        rutaEntity.setEstado(ruta.getEstado());

        return  rutaEntity;
    }
}
