package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Item;
import com.transporte_gomez.erp.dto.ItemFilter;
import com.transporte_gomez.erp.services.ItemService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping()
    public Page<Item> getItems(Pageable pageable, ItemFilter filter) {
        return itemService.getAll(pageable, filter);
    }

    @GetMapping("/{id}")
    public Item getItemById(@PathVariable Long id) {
        return itemService.getById(id);
    }

    @PostMapping
    public Item createItem(@RequestBody Item item) {
        return itemService.create(item);
    }

    @PutMapping("/{id}")
    public Item updateItem(@PathVariable Long id, @RequestBody Item item) {
        return itemService.update(id, item);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable Long id) {
        itemService.delete(id);
    }

    @PutMapping("/{id}/desactivate")
    public void desactivateItem(@PathVariable Long id) {
        itemService.desactivate(id);
    }
}
