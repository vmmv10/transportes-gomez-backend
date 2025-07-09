package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Item;
import com.transporte_gomez.erp.entity.ItemEntity;
import com.transporte_gomez.erp.repository.UnidadesMedidaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemAdapter {

    private final UnidadesMedidaRepository unidadesMedidaRepository;
    private final UnidadMedidaAdapter unidadMedidaAdapter;

    public Item getItem(ItemEntity itemEntity) {
        Item item = new Item();
        item.setId(itemEntity.getId());
        item.setNombre(itemEntity.getNombre());
        item.setDescripcion(itemEntity.getDescripcion());
        item.setCodigo(itemEntity.getCodigo());

        item.setUnidadMedida(unidadMedidaAdapter.getUnidadMedida(itemEntity.getUnidadMedida()));

        return item;
    }

    public List<Item> getItems(List<ItemEntity> itemEntityList) {
        return itemEntityList.stream()
                .map(this::getItem)
                .toList();
    }

    public ItemEntity updateItemEntity(Item item, ItemEntity itemEntity) {
        itemEntity.setNombre(item.getNombre());
        itemEntity.setDescripcion(item.getDescripcion());
        itemEntity.setCodigo(item.getCodigo());

        if (item.getUnidadMedida() != null && item.getUnidadMedida().getId() != null) {
            itemEntity.setUnidadMedida(unidadesMedidaRepository.findById(item.getUnidadMedida().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada")));
        }

        return itemEntity;
    }

    public ItemEntity createItemEntity(Item item) {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setNombre(item.getNombre());
        itemEntity.setDescripcion(item.getDescripcion());
        itemEntity.setCreadoEn(Instant.now());
        itemEntity.setCodigo(item.getCodigo());

        if (item.getUnidadMedida() != null && item.getUnidadMedida().getId() != null) {
            itemEntity.setUnidadMedida(unidadesMedidaRepository.findById(item.getUnidadMedida().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada")));
        }

        return itemEntity;
    }
}
