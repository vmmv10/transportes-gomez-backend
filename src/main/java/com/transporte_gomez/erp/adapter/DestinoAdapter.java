package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Destino;
import com.transporte_gomez.erp.entity.DestinoEntity;
import com.transporte_gomez.erp.repository.ClienteRepository;
import com.transporte_gomez.erp.repository.ComunaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DestinoAdapter {

    private final ComunaAdapter comunaAdapter;
    private final ComunaRepository comunaRepository;
    private final ClienteRepository clienteRepository;

    public Destino toDto(DestinoEntity entity) {
        if (entity == null) {
            return null;
        }
        Destino dto = new Destino();
        dto.setId(entity.getId());
        dto.setTipo(entity.getTipo());
        dto.setNombre(entity.getNombre());
        dto.setDireccion(entity.getDireccion());
        dto.setComuna(comunaAdapter.toDto(entity.getComuna()));
        dto.setLatitud(entity.getLatitud());
        dto.setLongitud(entity.getLongitud());
        dto.setContacto(entity.getContacto());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());
        dto.setEscuelaId(entity.getEscuela() != null ? entity.getEscuela().getId() : null);
        if (entity.getCliente() != null) {
            dto.setClienteId(entity.getCliente().getId());
            dto.setClienteNombre(entity.getCliente().getNombreCorto() != null
                    ? entity.getCliente().getNombreCorto()
                    : entity.getCliente().getRazonSocial());
        }
        dto.setActivo(entity.getActivo());
        return dto;
    }

    /** Copia los datos editables del DTO a la entidad. La escuela de origen no se cambia desde aquí. */
    public DestinoEntity toEntity(Destino dto, DestinoEntity entity) {
        if (dto.getTipo() != null) {
            entity.setTipo(dto.getTipo());
        }
        entity.setNombre(dto.getNombre() != null ? dto.getNombre().trim() : null);
        entity.setDireccion(dto.getDireccion());
        entity.setComuna(dto.getComuna() != null && dto.getComuna().getId() != null
                ? comunaRepository.findById(dto.getComuna().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Comuna no encontrada con ID: " + dto.getComuna().getId()))
                : null);
        entity.setLatitud(dto.getLatitud());
        entity.setLongitud(dto.getLongitud());
        entity.setContacto(dto.getContacto());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        entity.setCliente(dto.getClienteId() != null
                ? clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + dto.getClienteId()))
                : null);
        return entity;
    }
}
