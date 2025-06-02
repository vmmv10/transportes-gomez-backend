package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Escuela;
import com.transporte_gomez.erp.entity.EscuelaEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EscuelaAdapter {

    public Escuela toDto(EscuelaEntity escuelaEntity) {
        Escuela escuela = new Escuela();
        escuela.setId(escuelaEntity.getId());
        escuela.setNombre(escuelaEntity.getNombre());
        escuela.setDireccion(escuelaEntity.getDireccion());
        escuela.setEmail(escuelaEntity.getEmail());
        escuela.setTelefono(escuelaEntity.getTelefono());
        escuela.setDirector(escuelaEntity.getDirector());
        escuela.setRbd(escuelaEntity.getRbd());
        escuela.setLatitud(escuelaEntity.getLatitud());
        escuela.setLongitud(escuelaEntity.getLongitud());
        escuela.setComuna(escuelaEntity.getComuna());
        return escuela;
    }

    public List<Escuela> toDto(List<EscuelaEntity> escuelaEntity) {
        return escuelaEntity.stream().map(this::toDto).collect(Collectors.toList());
    }

    public EscuelaEntity createEscuelaEntity(Escuela escuela) {
        EscuelaEntity escuelaEntity = new EscuelaEntity();
        escuelaEntity.setNombre(escuela.getNombre());
        escuelaEntity.setLatitud(escuela.getLatitud());
        escuelaEntity.setLongitud(escuela.getLongitud());
        escuelaEntity.setRbd(escuela.getRbd());
        escuelaEntity.setComuna(escuela.getComuna());
        return escuelaEntity;
    }

    public EscuelaEntity updateEscuelaEntity(Escuela escuela, EscuelaEntity escuelaEntity) {
        escuelaEntity.setNombre(escuela.getNombre());
        escuelaEntity.setLatitud(escuela.getLatitud());
        escuelaEntity.setLongitud(escuela.getLongitud());
        escuelaEntity.setRbd(escuela.getRbd());
        escuelaEntity.setComuna(escuela.getComuna());
        return escuelaEntity;
    }


}
