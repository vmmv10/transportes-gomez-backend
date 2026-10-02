package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.ProveedorEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<ProveedorEntity, Long> {
    Page<ProveedorEntity> findAll(Specification<ProveedorEntity> proveedorEntitySpecification, Pageable pageable);

    @Query("select p from ProveedorEntity p where p.activo = ?1 order by p.razonSocial")
    List<ProveedorEntity> findByActivo(Boolean activo);

    /** Mismo nombre sin importar mayúsculas ni espacios al inicio o al final. */
    @Query("select p from ProveedorEntity p where upper(trim(p.razonSocial)) = upper(trim(:nombre))")
    List<ProveedorEntity> buscarPorNombre(@Param("nombre") String nombre);

    Optional<ProveedorEntity> findFirstByRut(String rut);

    // ---- Fusión de proveedores: traspasa las referencias de uno a otro ----

    @Modifying
    @Query(value = "update item_codigos_proveedores set proveedor_id = :destino where proveedor_id = :origen", nativeQuery = true)
    int traspasarCodigosItems(@Param("origen") Long origen, @Param("destino") Long destino);

    @Modifying
    @Query(value = "update documentos set proveedor = :destino where proveedor = :origen", nativeQuery = true)
    int traspasarDocumentos(@Param("origen") Long origen, @Param("destino") Long destino);

    @Modifying
    @Query(value = "update ordenes_servicios set proveedor_id = :destino where proveedor_id = :origen", nativeQuery = true)
    int traspasarOrdenes(@Param("origen") Long origen, @Param("destino") Long destino);

    @Modifying
    @Query(value = "update ingresos set transportista_id = :destino where transportista_id = :origen", nativeQuery = true)
    int traspasarIngresos(@Param("origen") Long origen, @Param("destino") Long destino);
}
