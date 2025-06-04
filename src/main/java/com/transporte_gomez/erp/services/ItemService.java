package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ItemAdapter;
import com.transporte_gomez.erp.dto.Item;
import com.transporte_gomez.erp.dto.ItemFilter;
import com.transporte_gomez.erp.entity.ItemEntity;
import com.transporte_gomez.erp.repository.ItemRepository;
import com.transporte_gomez.erp.specification.ItemSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemAdapter itemAdapter;

    public Page<Item> getAll(Pageable pageable, ItemFilter filter) {
        Page<ItemEntity> itemEntities = itemRepository.findAll(ItemSpecification.conFiltros(filter), pageable);
        return itemEntities.map(itemAdapter::getItem);
    }

    public Item getById(Long id) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado con ID: " + id));
        return itemAdapter.getItem(itemEntity);
    }

    public Item create(Item item) {
        log.info("Creando Item: {}", item);
        ItemEntity itemEntity = itemAdapter.createItemEntity(item);
        itemEntity = itemRepository.save(itemEntity);
        return itemAdapter.getItem(itemEntity);
    }

    public Item update(Long id, Item item) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado con ID: " + id));
        itemEntity = itemAdapter.updateItemEntity(item, itemEntity);
        itemEntity = itemRepository.save(itemEntity);
        return itemAdapter.getItem(itemEntity);
    }

    public void delete(Long id) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado con ID: " + id));
        itemRepository.delete(itemEntity);
    }

    public void desactivate(Long id) {
        ItemEntity itemEntity = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Item no encontrado con ID: " + id));
        itemEntity.setActivo(false);
        itemRepository.save(itemEntity);
    }
}
