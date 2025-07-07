package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.adapter.UsuarioAdapter;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import com.transporte_gomez.erp.specification.UsuarioSpecification;
import lombok.AllArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioAdapter usuarioAdapter;

    public List<Usuario> getAll(Usuario filtro) {
        List<UsuarioEntity> usuarios = usuarioRepository.findAll(UsuarioSpecification.conFiltros(filtro));
        return usuarioAdapter.obtenerTodosUsuarios(usuarios);
    }

    public Usuario obtenerUsuarioLogeado(Jwt jwt) {
        String userId = jwt.getSubject();
        UsuarioEntity usuarioEntity = usuarioRepository.findByAuth0Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + userId));
        return usuarioAdapter.getUsuario(usuarioEntity);
    }

    public Usuario UpdateUsuario(Usuario usuario) {
        UsuarioEntity usuarioEntity = usuarioRepository.getReferenceById(usuario.getId());
        UsuarioEntity updatedEntitySave = usuarioRepository.save(usuarioAdapter.uptdateUsuario(usuarioEntity, usuario));
        return usuarioAdapter.getUsuario(updatedEntitySave);
    }

}
