package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ItemCodigoProveedorAdapter;
import com.transporte_gomez.erp.dto.ItemCodigoProveedor;
import com.transporte_gomez.erp.entity.ItemCodigoProveedorEntity;
import com.transporte_gomez.erp.entity.ItemEntity;
import com.transporte_gomez.erp.repository.ItemCodigoProveedorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemCodigoProveeedorService {

    private final ItemCodigoProveedorRepository itemCodigoProveedorRepository;
    private final ItemCodigoProveedorAdapter itemCodigoProveedorAdapter;

    public List<ItemCodigoProveedor> findAllItemCodigoProveedor(Long item) {
        List<ItemCodigoProveedorEntity> itemCodigoProveedorEntities = itemCodigoProveedorRepository.findByItem_Id(item);
        return itemCodigoProveedorEntities.stream()
                .map(itemCodigoProveedorAdapter::get)
                .toList();
    }

    public void createItemCodigoProveedor(ItemCodigoProveedor itemCodigoProveedor, ItemEntity item) {
        ItemCodigoProveedorEntity existingCod = itemCodigoProveedorRepository.findByProveedor_IdAndItem_Id(itemCodigoProveedor.getProveedor().getId(), item.getId());
        if (existingCod != null) {
            throw new IllegalArgumentException("Ya existe un código de proveedor para este item y proveedor.");
        }
        ItemCodigoProveedorEntity itemCodigoProveedorEntity = itemCodigoProveedorAdapter.createItemCodigoProveedorEntity(itemCodigoProveedor, item);
        itemCodigoProveedorRepository.save(itemCodigoProveedorEntity);
    }

    public void updateItemCodigoProveedor(Long id, ItemCodigoProveedor itemCodigoProveedor) {
        ItemCodigoProveedorEntity itemCodigoProveedorEntity = itemCodigoProveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Código de proveedor no encontrado con ID: " + id));
        itemCodigoProveedorEntity = itemCodigoProveedorAdapter.updateItemCodigoProveedorEntity(itemCodigoProveedor, itemCodigoProveedorEntity);
        itemCodigoProveedorRepository.save(itemCodigoProveedorEntity);
    }

    public void deleteItemCodigoProveedor(Long id) {
        System.out.println("Deleting ItemCodigoProveedor with ID: " + id);
        itemCodigoProveedorRepository.deleteById(id);
    }
}
