package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Proveedor;
import com.transporte_gomez.erp.entity.ProveedorEntity;
import org.springframework.stereotype.Service;

@Service
public class ProveedorAdpater {

    public Proveedor getProveedor(ProveedorEntity proveedor) {
        Proveedor proveedorDto = new Proveedor();
        proveedorDto.setId(proveedor.getId());
        proveedorDto.setNombre(proveedor.getRazonSocial());
        proveedorDto.setRut(proveedor.getRut());
        proveedorDto.setDireccion(proveedor.getDireccion());
        proveedorDto.setTelefono(proveedor.getTelefono());
        proveedorDto.setEmail(proveedor.getEmail());
        proveedorDto.setActivo(proveedor.getActivo());

        return proveedorDto;
    }

    public ProveedorEntity createProveedor(Proveedor proveedor) {
        ProveedorEntity ProveedorEntity = new ProveedorEntity();

        ProveedorEntity.setRazonSocial(proveedor.getNombre());
        ProveedorEntity.setRut(proveedor.getRut());
        ProveedorEntity.setDireccion(proveedor.getDireccion());
        ProveedorEntity.setTelefono(proveedor.getTelefono());
        ProveedorEntity.setEmail(proveedor.getEmail());

        return ProveedorEntity;
    }

    public ProveedorEntity updateProveedor(ProveedorEntity ProveedorEntity, Proveedor proveedor) {
        ProveedorEntity.setId(proveedor.getId());
        ProveedorEntity.setRazonSocial(proveedor.getNombre());
        ProveedorEntity.setRut(proveedor.getRut());
        ProveedorEntity.setDireccion(proveedor.getDireccion());
        ProveedorEntity.setTelefono(proveedor.getTelefono());
        ProveedorEntity.setEmail(proveedor.getEmail());

        return ProveedorEntity;
    }
}
