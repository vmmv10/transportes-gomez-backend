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
        usuario.setId(usuarioEntity.getId());
        usuario.setNombre(usuarioEntity.getNombre());
        usuario.setTelefono(usuarioEntity.getTelefono());
        usuario.setEmail(usuarioEntity.getEmail());
        usuario.setApellidos(usuarioEntity.getApellidos());
        usuario.setModoOscuro(usuarioEntity.getTemaOscuro());

        return usuario;
    }

    public UsuarioEntity uptdateUsuario(UsuarioEntity usuarioEntity, Usuario usuario) {
        usuarioEntity.setNombre(usuario.getNombre());
        usuarioEntity.setTelefono(usuario.getTelefono());
        usuarioEntity.setEmail(usuario.getEmail());
        usuarioEntity.setApellidos(usuario.getApellidos());
        return usuarioEntity;
    }

    public List<Usuario> obtenerTodosUsuarios(List<UsuarioEntity> listaUsuarios) {
        return listaUsuarios.stream().map(u -> this.getUsuario(u)).collect(Collectors.toList());
    }
}
