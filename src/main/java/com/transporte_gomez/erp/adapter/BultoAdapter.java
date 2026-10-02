package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Bulto;
import com.transporte_gomez.erp.entity.BultoEntity;
import com.transporte_gomez.erp.repository.DestinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BultoAdapter {

    private final DestinoAdapter destinoAdapter;
    private final DestinoRepository destinoRepository;

    public Bulto toDto(BultoEntity entity) {
        if (entity == null) {
            return null;
        }
        Bulto dto = new Bulto();
        dto.setId(entity.getId());
        dto.setIngresoId(entity.getIngreso().getId());
        dto.setCodigoExterno(entity.getCodigoExterno());
        dto.setDescripcion(entity.getDescripcion());
        dto.setPesoKg(entity.getPesoKg());
        dto.setVolumenM3(entity.getVolumenM3());
        dto.setDestino(destinoAdapter.toDto(entity.getDestino()));
        dto.setOrdenServicioId(entity.getOrdenServicio() != null ? entity.getOrdenServicio().getId() : null);
        dto.setEstado(entity.getEstado());
        dto.setFechaEntrega(entity.getFechaEntrega());
        return dto;
    }

    /** Copia los datos editables del DTO. El ingreso y la orden se asignan en el servicio. */
    public BultoEntity toEntity(Bulto dto, BultoEntity entity) {
        entity.setCodigoExterno(dto.getCodigoExterno() != null && !dto.getCodigoExterno().isBlank()
                ? dto.getCodigoExterno().trim()
                : null);
        entity.setDescripcion(dto.getDescripcion());
        entity.setPesoKg(dto.getPesoKg());
        entity.setVolumenM3(dto.getVolumenM3());
        entity.setDestino(dto.getDestino() != null && dto.getDestino().getId() != null
                ? destinoRepository.findById(dto.getDestino().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Destino no encontrado con ID: " + dto.getDestino().getId()))
                : null);
        return entity;
    }
}
