package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.UsuarioEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
    @Query("select u from UsuarioEntity u where u.auth0id = ?1")
    Optional<UsuarioEntity> findByAuth0Id(String auth0Id);

    List<UsuarioEntity> findAll(Specification<UsuarioEntity> usuarioSpecification);
}
