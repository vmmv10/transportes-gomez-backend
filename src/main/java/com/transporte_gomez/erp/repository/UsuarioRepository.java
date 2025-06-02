package com.transporte_gomez.erp.repository;

import com.transporte_gomez.erp.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, Long> {
}
