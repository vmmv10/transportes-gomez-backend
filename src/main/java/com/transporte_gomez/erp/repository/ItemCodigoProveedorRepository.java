package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ItemCodigoProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemCodigoProveedorRepository extends JpaRepository<ItemCodigoProveedorEntity, Long> {
    @Query("select i from ItemCodigoProveedorEntity i where i.item.id = ?1")
    List<ItemCodigoProveedorEntity> findByItem_Id(Long id);

    @Query("select i from ItemCodigoProveedorEntity i where i.proveedor.id = ?1 and i.item.id = ?2")
    ItemCodigoProveedorEntity findByProveedor_IdAndItem_Id(Long id, Long id1);
}