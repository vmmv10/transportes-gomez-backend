package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Vehiculo;
import com.transporte_gomez.erp.entity.VehiculoEntity;
import com.transporte_gomez.erp.enums.VehiculoEstado;
import com.transporte_gomez.erp.enums.VehiculoPropiedad;
import com.transporte_gomez.erp.enums.VehiculoTipo;
import com.transporte_gomez.erp.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehiculoAdapter {

    private final ProveedorRepository proveedorRepository;

    public Vehiculo toDto(VehiculoEntity entity) {
        if (entity == null) {
            return null;
        }
        Vehiculo dto = new Vehiculo();
        dto.setId(entity.getId());
        dto.setTipo(entity.getTipo());
        dto.setPropiedad(entity.getPropiedad());
        dto.setPatente(entity.getPatente());
        dto.setNombre(entity.getNombre());
        dto.setMarca(entity.getMarca());
        dto.setModelo(entity.getModelo());
        dto.setAnio(entity.getAnio());
        if (entity.getProveedor() != null) {
            dto.setProveedorId(entity.getProveedor().getId());
            dto.setProveedorNombre(entity.getProveedor().getRazonSocial());
        }
        dto.setCapacidadKg(entity.getCapacidadKg());
        dto.setCapacidadM3(entity.getCapacidadM3());
        dto.setCapacidadPasajeros(entity.getCapacidadPasajeros());
        dto.setEstado(entity.getEstado());
        dto.setObservaciones(entity.getObservaciones());
        dto.setActivo(entity.getActivo());
        dto.setDescripcion(descripcion(entity));
        return dto;
    }

    /** "Hilux blanca · ABCD12" */
    public static String descripcion(VehiculoEntity entity) {
        if (entity == null) {
            return null;
        }
        String patente = entity.getPatente() != null ? entity.getPatente().trim() : "";
        return patente.isEmpty() ? entity.getNombre() : entity.getNombre() + " · " + patente;
    }

    public VehiculoEntity toEntity(Vehiculo dto, VehiculoEntity entity) {
        entity.setTipo(dto.getTipo());
        entity.setPropiedad(dto.getPropiedad() != null ? dto.getPropiedad() : VehiculoPropiedad.PROPIO);
        entity.setPatente(dto.getTipo() == VehiculoTipo.LANCHA ? matricula(dto.getPatente()) : normalizarPatente(dto.getPatente()));
        entity.setNombre(dto.getNombre().trim());
        entity.setMarca(texto(dto.getMarca()));
        entity.setModelo(texto(dto.getModelo()));
        entity.setAnio(dto.getAnio());
        entity.setProveedor(dto.getProveedorId() == null ? null
                : proveedorRepository.findById(dto.getProveedorId())
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + dto.getProveedorId())));
        entity.setCapacidadKg(dto.getCapacidadKg());
        entity.setCapacidadM3(dto.getCapacidadM3());
        entity.setCapacidadPasajeros(dto.getCapacidadPasajeros());
        entity.setEstado(dto.getEstado() != null ? dto.getEstado() : VehiculoEstado.OPERATIVO);
        entity.setObservaciones(texto(dto.getObservaciones()));
        return entity;
    }

    /** Mayúsculas, sin espacios, puntos ni guiones: "ab-cd 12" -> "ABCD12". Vacía -> null. */
    public static String normalizarPatente(String patente) {
        if (patente == null) {
            return null;
        }
        String p = patente.toUpperCase().replaceAll("[\\s.\\-·]", "");
        return p.isEmpty() ? null : p;
    }

    /** Matrícula de lancha: se respeta el formato (ej. "CAS-1234"), solo mayúsculas y espacios simples. */
    public static String matricula(String valor) {
        String t = texto(valor);
        return t == null ? null : t.toUpperCase().replaceAll("\\s+", " ");
    }

    private static String texto(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
