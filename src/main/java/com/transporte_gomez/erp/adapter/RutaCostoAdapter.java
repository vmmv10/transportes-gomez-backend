package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.RutaCosto;
import com.transporte_gomez.erp.entity.RutaCostoEntity;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import org.springframework.stereotype.Service;

@Service
public class RutaCostoAdapter {

    public RutaCosto toDto(RutaCostoEntity entity) {
        RutaCosto dto = new RutaCosto();
        dto.setId(entity.getId());
        dto.setRutaId(entity.getRuta().getId());
        dto.setTipo(entity.getTipo());
        dto.setFecha(entity.getFecha());
        dto.setMonto(entity.getMonto());
        dto.setLitros(entity.getLitros());
        if (entity.getProveedor() != null) {
            dto.setProveedorId(entity.getProveedor().getId());
            dto.setProveedorNombre(entity.getProveedor().getRazonSocial());
        }
        if (entity.getVehiculo() != null) {
            dto.setVehiculoId(entity.getVehiculo().getId());
            dto.setVehiculoDescripcion(VehiculoAdapter.descripcion(entity.getVehiculo()));
        }
        dto.setDescripcion(entity.getDescripcion());
        dto.setComprobante(entity.getComprobante());
        dto.setUsuarioNombre(nombre(entity.getUsuario()));
        return dto;
    }

    public static String nombre(UsuarioEntity u) {
        if (u == null) {
            return null;
        }
        String n = ((u.getNombre() != null ? u.getNombre() : "") + " " + (u.getApellidos() != null ? u.getApellidos() : "")).trim();
        return n.isEmpty() ? u.getEmail() : n;
    }
}
