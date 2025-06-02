package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Usuario;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class UsuarioAdapter {

    public Usuario getUsuario(UsuarioEntity usuarioEntity) {
        Usuario usuario = new Usuario();
        usuario.setId(usuario.getId());
        usuario.setNombre(usuarioEntity.getNombre());

        return usuario;
    }

    public List<Usuario> obtenerTodosUsuarios(List<UsuarioEntity> listaUsuarios) {
        return listaUsuarios.stream().map(u -> this.getUsuario(u)).collect(Collectors.toList());
    }
}
