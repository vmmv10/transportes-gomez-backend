package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.RutaAdapter;
import com.transporte_gomez.erp.dto.Ruta;
import com.transporte_gomez.erp.dto.RutaFiltro;
import com.transporte_gomez.erp.entity.RutaEntity;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.specification.RutaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RutaService {

    private final RutaRepository rutaRepository;
    private final EntregaServices entregaServices;
    private final RutaAdapter rutaAdapter;

    public Page<Ruta> findAll(Pageable pageable, RutaFiltro filtro) {
        return rutaRepository.findAll(RutaSpecification.conFiltros(filtro), pageable)
                .map(rutaAdapter::getRuta);
    }

    public Ruta findById(Integer id) {
        return rutaRepository.findById(id)
                .map(rutaAdapter::getRuta)
                .orElseThrow(() -> new RuntimeException("Ruta not found with id: " + id));
    }

    public Ruta create(Ruta ruta) {
        RutaEntity rutaEntity = rutaAdapter.createRuta(ruta);
        RutaEntity savedRutaEntity = rutaRepository.save(rutaEntity);

        entregaServices.crearEntregas(savedRutaEntity, ruta.getOrdenes());

        return rutaAdapter.getRuta(savedRutaEntity);
    }

    public Ruta update(Integer id, Ruta ruta) {
        RutaEntity existingRuta = rutaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ruta no encontrada con el id: " + id));

        RutaEntity updatedRuta = rutaAdapter.update(ruta, existingRuta);

        RutaEntity savedRutaEntity = rutaRepository.save(updatedRuta);

        entregaServices.updateEntregas(savedRutaEntity, ruta.getOrdenes());

        return rutaAdapter.getRuta(savedRutaEntity);
    }

    public void delete(Integer id) {
        rutaRepository.deleteById(id);
    }

    public void deleteEntrega(Integer id, Long ordenServicio) {
        entregaServices.deleteEntregaByRutaAndOrden(id, ordenServicio);
    }
}
