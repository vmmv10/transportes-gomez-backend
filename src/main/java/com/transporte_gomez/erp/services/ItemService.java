package com.transporte_gomez.erp.services;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.transporte_gomez.erp.adapter.ItemAdapter;
import com.transporte_gomez.erp.dto.Item;
import com.transporte_gomez.erp.dto.ItemFilter;
import com.transporte_gomez.erp.entity.*;
import com.transporte_gomez.erp.repository.CategoriaRepository;
import com.transporte_gomez.erp.repository.ItemRepository;
import com.transporte_gomez.erp.repository.MarcaRepository;
import com.transporte_gomez.erp.specification.ItemSpecification;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemAdapter itemAdapter;
    private final MarcaRepository marcaRepository;
    private final CategoriaRepository categoriaRepository;

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
        ItemEntity existingItem = itemRepository.findByCodigo(item.getCodigo());
        if (existingItem != null) {
            throw new IllegalArgumentException("Ya existe un item con el código: " + item.getCodigo());
        }
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

    public Item getByCodigo(String codigo) {
        ItemEntity itemEntity = itemRepository.findByCodigo(codigo);
        if (itemEntity == null) {
            throw new IllegalArgumentException("Item no encontrado con código: " + codigo);
        }
        return itemAdapter.getItem(itemEntity);
    }

    public void leerEXCEL(MultipartFile file) throws Exception {
        try (InputStreamReader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            CsvToBean<Item> csvToBean = new CsvToBeanBuilder<Item>(reader)
                    .withType(Item.class)
                    .withSeparator(',')
                    .withIgnoreLeadingWhiteSpace(true)
                    .build();

            List<Item> items = csvToBean.parse();
            List<ItemEntity> itemEntities = new ArrayList<>();
            for (Item item : items) {
                log.info("items: {}", item);
                ItemEntity itemEntity = new ItemEntity();
                itemEntity.setNombre(item.getNombre());
                itemEntity.setDescripcion(item.getDescripcion());
                itemEntity.setCodigo(item.getCodigo());
                itemEntities.add(itemEntity);
            }

            if(!itemEntities.isEmpty()) {
                itemRepository.saveAll(itemEntities);
            }
        }
    }
}
