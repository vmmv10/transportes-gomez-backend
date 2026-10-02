package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ContratoAdapter;
import com.transporte_gomez.erp.dto.Contrato;
import com.transporte_gomez.erp.dto.ContratoFiltro;
import com.transporte_gomez.erp.entity.ContratoEntity;
import com.transporte_gomez.erp.repository.ContratoRepository;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.transporte_gomez.erp.specification.ContratoSpecification.conFiltros;

@Service
@RequiredArgsConstructor
public class ContratoService {

    private final ContratoRepository contratoRepository;
    private final ContratoAdapter contratoAdapter;
    private final OrdenServicioRepository ordenServicioRepository;

    @Transactional(readOnly = true)
    public Page<Contrato> getAll(Pageable pageable, ContratoFiltro filtro) {
        return contratoRepository.findAll(conFiltros(filtro), pageable).map(contratoAdapter::toDto);
    }

    /** Para selectores: contratos vigentes hoy, opcionalmente de un cliente. */
    @Transactional(readOnly = true)
    public List<Contrato> listarVigentes(Long clienteId) {
        ContratoFiltro filtro = new ContratoFiltro();
        filtro.setCliente(clienteId);
        filtro.setVigente(true);
        return contratoRepository.findAll(conFiltros(filtro), Sort.by("nombre")).stream()
                .map(contratoAdapter::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Contrato getById(Long id) {
        return contratoAdapter.toDto(buscar(id));
    }

    @Transactional
    public Contrato create(Contrato contrato) {
        validar(contrato);
        if (contratoRepository.existsByCliente_IdAndCodigoIgnoreCase(contrato.getClienteId(), contrato.getCodigo().trim())) {
            throw new IllegalArgumentException("El cliente ya tiene un contrato con código " + contrato.getCodigo());
        }
        ContratoEntity entity = contratoAdapter.toEntity(contrato, new ContratoEntity());
        entity.setActivo(true);
        return contratoAdapter.toDto(contratoRepository.save(entity));
    }

    @Transactional
    public Contrato update(Long id, Contrato contrato) {
        ContratoEntity entity = buscar(id);
        validar(contrato);
        if (contratoRepository.existsByCliente_IdAndCodigoIgnoreCaseAndIdNot(contrato.getClienteId(), contrato.getCodigo().trim(), id)) {
            throw new IllegalArgumentException("El cliente ya tiene un contrato con código " + contrato.getCodigo());
        }
        return contratoAdapter.toDto(contratoRepository.save(contratoAdapter.toEntity(contrato, entity)));
    }

    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        ContratoEntity entity = buscar(id);
        entity.setActivo(activo);
        contratoRepository.save(entity);
    }

    /** Solo se elimina un contrato que no tiene órdenes de servicio asociadas. */
    @Transactional
    public void eliminar(Long id) {
        ContratoEntity entity = buscar(id);
        long ordenes = ordenServicioRepository.countByContrato_Id(id);
        if (ordenes > 0) {
            throw new IllegalArgumentException("No se puede eliminar: el contrato " + entity.getCodigo() + " tiene "
                    + ordenes + (ordenes == 1 ? " orden de servicio asociada" : " órdenes de servicio asociadas")
                    + ". Puedes desactivarlo.");
        }
        contratoRepository.delete(entity);
    }

    private ContratoEntity buscar(Long id) {
        return contratoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado con ID: " + id));
    }

    private void validar(Contrato contrato) {
        if (contrato.getClienteId() == null) {
            throw new IllegalArgumentException("El contrato debe tener cliente");
        }
        if (contrato.getCodigo() == null || contrato.getCodigo().isBlank()) {
            throw new IllegalArgumentException("El código (ID de licitación u OC) es obligatorio");
        }
        if (contrato.getNombre() == null || contrato.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del contrato es obligatorio");
        }
        if (contrato.getFechaInicio() != null && contrato.getFechaFin() != null
                && contrato.getFechaFin().isBefore(contrato.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de término no puede ser anterior a la de inicio");
        }
    }
}
