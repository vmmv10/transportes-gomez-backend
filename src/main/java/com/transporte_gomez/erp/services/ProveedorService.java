package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ProveedorAdpater;
import com.transporte_gomez.erp.dto.Proveedor;
import com.transporte_gomez.erp.entity.ProveedorEntity;
import com.transporte_gomez.erp.repository.ProveedorRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorAdpater proveedorAdpater;

    public List<Proveedor> getProveedores(){
        return proveedorRepository.findAll().stream()
                .map(proveedorAdpater::getProveedor)
                .toList();
    }

    public Proveedor getProveedorById(Long id) {
        return proveedorRepository.findById(id)
                .map(proveedorAdpater::getProveedor)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id));
    }

    public Proveedor createProveedor(Proveedor proveedor) {
        ProveedorEntity proveedorEntity = proveedorAdpater.createProveedor(proveedor);
        proveedorEntity = proveedorRepository.save(proveedorEntity);
        return proveedorAdpater.getProveedor(proveedorEntity);
    }

    public Proveedor updateProveedor(Long id, Proveedor proveedor) {
        ProveedorEntity proveedorEntity = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id));
        proveedorEntity = proveedorAdpater.updateProveedor(proveedorEntity, proveedor);
        return proveedorAdpater.getProveedor(proveedorRepository.save(proveedorEntity));
    }

    public void desactivateProveedor(Long id) {
        ProveedorEntity proveedorEntity = proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id));
        proveedorEntity.setActivo(false);
        proveedorRepository.save(proveedorEntity);
    }

}
