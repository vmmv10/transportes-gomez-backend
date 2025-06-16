package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Ruta;
import com.transporte_gomez.erp.entity.RutaEntity;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RutaAdapter {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioAdapter usuarioAdapter;

    public Ruta getRuta(RutaEntity rutaEntity) {
        Ruta ruta = new Ruta();
        ruta.setId(rutaEntity.getId());
        ruta.setFecha(rutaEntity.getFecha());
        ruta.setEstado(rutaEntity.getEstado());

        if (rutaEntity.getChofer() != null) {
            ruta.setChofer(usuarioAdapter.getUsuario(rutaEntity.getChofer()));
        }
        return ruta;
    }
}
