package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ItemEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
    Page<ItemEntity> findAll(Specification<ItemEntity> itemEntitySpecification, Pageable pageable);
}