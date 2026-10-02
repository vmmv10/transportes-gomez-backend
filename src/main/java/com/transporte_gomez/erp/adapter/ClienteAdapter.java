package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Cliente;
import com.transporte_gomez.erp.entity.ClienteEntity;
import com.transporte_gomez.erp.repository.ComunaRepository;
import com.transporte_gomez.erp.util.RutUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteAdapter {

    private final ComunaAdapter comunaAdapter;
    private final ComunaRepository comunaRepository;

    public Cliente toDto(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        Cliente dto = new Cliente();
        dto.setId(entity.getId());
        dto.setRut(entity.getRut());
        dto.setRazonSocial(entity.getRazonSocial());
        dto.setNombreCorto(entity.getNombreCorto());
        dto.setTipoPersona(entity.getTipoPersona());
        dto.setSector(entity.getSector());
        dto.setGiro(entity.getGiro());
        dto.setDireccion(entity.getDireccion());
        dto.setComuna(comunaAdapter.toDto(entity.getComuna()));
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());
        dto.setContacto(entity.getContacto());
        dto.setCodigoMp(entity.getCodigoMp());
        dto.setActivo(entity.getActivo());
        return dto;
    }

    /** Versión corta para anidar en órdenes e ingresos. */
    public Cliente toDtoResumen(ClienteEntity entity) {
        if (entity == null) {
            return null;
        }
        Cliente dto = new Cliente();
        dto.setId(entity.getId());
        dto.setRut(entity.getRut());
        dto.setRazonSocial(entity.getRazonSocial());
        dto.setNombreCorto(entity.getNombreCorto());
        dto.setSector(entity.getSector());
        return dto;
    }

    /** Copia los datos del DTO a la entidad (crear o actualizar). */
    public ClienteEntity toEntity(Cliente dto, ClienteEntity entity) {
        entity.setRut(RutUtil.normalizar(dto.getRut()));
        entity.setRazonSocial(dto.getRazonSocial() != null ? dto.getRazonSocial().trim() : null);
        entity.setNombreCorto(dto.getNombreCorto());
        if (dto.getTipoPersona() != null) {
            entity.setTipoPersona(dto.getTipoPersona());
        }
        if (dto.getSector() != null) {
            entity.setSector(dto.getSector());
        }
        entity.setGiro(dto.getGiro());
        entity.setDireccion(dto.getDireccion());
        entity.setComuna(dto.getComuna() != null && dto.getComuna().getId() != null
                ? comunaRepository.findById(dto.getComuna().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Comuna no encontrada con ID: " + dto.getComuna().getId()))
                : null);
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setContacto(dto.getContacto());
        entity.setCodigoMp(dto.getCodigoMp());
        return entity;
    }
}
