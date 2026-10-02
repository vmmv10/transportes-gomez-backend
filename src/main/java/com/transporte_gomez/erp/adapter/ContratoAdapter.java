package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Contrato;
import com.transporte_gomez.erp.entity.ContratoEntity;
import com.transporte_gomez.erp.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ContratoAdapter {

    private final ClienteRepository clienteRepository;

    public Contrato toDto(ContratoEntity entity) {
        if (entity == null) {
            return null;
        }
        Contrato dto = new Contrato();
        dto.setId(entity.getId());
        dto.setClienteId(entity.getCliente().getId());
        dto.setClienteNombre(entity.getCliente().getNombreCorto() != null
                ? entity.getCliente().getNombreCorto()
                : entity.getCliente().getRazonSocial());
        dto.setCodigo(entity.getCodigo());
        dto.setNombre(entity.getNombre());
        dto.setFechaInicio(entity.getFechaInicio());
        dto.setFechaFin(entity.getFechaFin());
        dto.setObservaciones(entity.getObservaciones());
        dto.setActivo(entity.getActivo());
        dto.setVigente(entity.isVigente(LocalDate.now()));
        return dto;
    }

    public ContratoEntity toEntity(Contrato dto, ContratoEntity entity) {
        if (dto.getClienteId() == null) {
            throw new IllegalArgumentException("El contrato debe tener cliente");
        }
        entity.setCliente(clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + dto.getClienteId())));
        entity.setCodigo(dto.getCodigo() != null ? dto.getCodigo().trim() : null);
        entity.setNombre(dto.getNombre() != null ? dto.getNombre().trim() : null);
        entity.setFechaInicio(dto.getFechaInicio());
        entity.setFechaFin(dto.getFechaFin());
        entity.setObservaciones(dto.getObservaciones());
        return entity;
    }
}
