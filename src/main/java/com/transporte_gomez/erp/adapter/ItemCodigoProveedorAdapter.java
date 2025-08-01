package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.ItemCodigoProveedor;
import com.transporte_gomez.erp.entity.ItemCodigoProveedorEntity;
import com.transporte_gomez.erp.entity.ItemEntity;
import com.transporte_gomez.erp.repository.ProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemCodigoProveedorAdapter {

    private final ProveedorAdpater proveedorAdpater;
    private final ProveedorRepository proveedorRepository;

    public ItemCodigoProveedor get(ItemCodigoProveedorEntity itemCodigoProveedorEntity) {
        ItemCodigoProveedor itemCodigoProveedor = new ItemCodigoProveedor();
        itemCodigoProveedor.setId(itemCodigoProveedorEntity.getId());
        itemCodigoProveedor.setCodigo(itemCodigoProveedorEntity.getCodigo());
        itemCodigoProveedor.setProveedor(proveedorAdpater.getProveedor(itemCodigoProveedorEntity.getProveedor()));
        return itemCodigoProveedor;
    }

    public ItemCodigoProveedorEntity createItemCodigoProveedorEntity(ItemCodigoProveedor itemCodigoProveedor, ItemEntity itemEntity) {
        ItemCodigoProveedorEntity itemCodigoProveedorEntity = new ItemCodigoProveedorEntity();
        itemCodigoProveedorEntity.setId(itemCodigoProveedor.getId());
        itemCodigoProveedorEntity.setCodigo(itemCodigoProveedor.getCodigo());

        if (itemCodigoProveedor.getProveedor() != null && itemCodigoProveedor.getProveedor().getId() != null) {
            itemCodigoProveedorEntity.setProveedor(proveedorRepository.findById(itemCodigoProveedor.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado")));
        }

        itemCodigoProveedorEntity.setItem(itemEntity);

        return itemCodigoProveedorEntity;
    }

    public ItemCodigoProveedorEntity updateItemCodigoProveedorEntity(ItemCodigoProveedor itemCodigoProveedor, ItemCodigoProveedorEntity itemCodigoProveedorEntity) {
        itemCodigoProveedorEntity.setCodigo(itemCodigoProveedor.getCodigo());

        if (itemCodigoProveedor.getProveedor() != null && itemCodigoProveedor.getProveedor().getId() != null) {
            itemCodigoProveedorEntity.setProveedor(proveedorRepository.findById(itemCodigoProveedor.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado")));
        }

        return itemCodigoProveedorEntity;
    }
}
