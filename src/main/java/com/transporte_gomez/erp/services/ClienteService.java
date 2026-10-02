package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ClienteAdapter;
import com.transporte_gomez.erp.dto.Cliente;
import com.transporte_gomez.erp.dto.ClienteFiltro;
import com.transporte_gomez.erp.entity.ClienteEntity;
import com.transporte_gomez.erp.repository.ClienteRepository;
import com.transporte_gomez.erp.util.RutUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static com.transporte_gomez.erp.specification.ClienteSpecification.conFiltros;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private static final Set<String> SECTORES = Set.of("PUBLICO", "PRIVADO");
    private static final Set<String> TIPOS_PERSONA = Set.of("NATURAL", "JURIDICA");

    private final ClienteRepository clienteRepository;
    private final ClienteAdapter clienteAdapter;

    @Transactional(readOnly = true)
    public Page<Cliente> getAll(Pageable pageable, ClienteFiltro filtro) {
        return clienteRepository.findAll(conFiltros(filtro), pageable).map(clienteAdapter::toDto);
    }

    @Transactional(readOnly = true)
    public List<Cliente> listarActivos() {
        return clienteRepository.findByActivoTrueOrderByRazonSocialAsc().stream()
                .map(clienteAdapter::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Cliente getById(Long id) {
        return clienteAdapter.toDto(buscar(id));
    }

    @Transactional
    public Cliente create(Cliente cliente) {
        validar(cliente, null);
        ClienteEntity entity = clienteAdapter.toEntity(cliente, new ClienteEntity());
        entity.setActivo(true);
        return clienteAdapter.toDto(clienteRepository.save(entity));
    }

    @Transactional
    public Cliente update(Long id, Cliente cliente) {
        ClienteEntity entity = buscar(id);
        validar(cliente, id);
        return clienteAdapter.toDto(clienteRepository.save(clienteAdapter.toEntity(cliente, entity)));
    }

    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        ClienteEntity entity = buscar(id);
        entity.setActivo(activo);
        clienteRepository.save(entity);
    }

    private ClienteEntity buscar(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
    }

    private void validar(Cliente cliente, Long idActual) {
        if (cliente.getRazonSocial() == null || cliente.getRazonSocial().isBlank()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }
        if (!RutUtil.esValido(cliente.getRut())) {
            throw new IllegalArgumentException("RUT inválido: " + cliente.getRut());
        }
        if (cliente.getSector() != null && !SECTORES.contains(cliente.getSector())) {
            throw new IllegalArgumentException("Sector inválido: " + cliente.getSector());
        }
        if (cliente.getTipoPersona() != null && !TIPOS_PERSONA.contains(cliente.getTipoPersona())) {
            throw new IllegalArgumentException("Tipo de persona inválido: " + cliente.getTipoPersona());
        }
        clienteRepository.findByRut(RutUtil.normalizar(cliente.getRut()))
                .filter(existente -> !existente.getId().equals(idActual))
                .ifPresent(existente -> {
                    throw new IllegalArgumentException("Ya existe un cliente con RUT " + existente.getRut());
                });
    }
}
