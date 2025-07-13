package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.CategoriaAdapter;
import com.transporte_gomez.erp.dto.Categoria;
import com.transporte_gomez.erp.entity.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaAdapter categoriaAdapter;

    public List<Categoria> getAll() {
        return categoriaRepository.findAll().stream()
                .map(categoriaAdapter::get)
                .toList();
    }
}
