package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.entity.*;
import com.transporte_gomez.erp.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/** Busca por id los catálogos que usan tarifas y cotizaciones (null si no viene id). */
@Component
@RequiredArgsConstructor
class ComercialReferencias {

    private final ClienteRepository clienteRepository;
    private final ServicioTipoRepository servicioTipoRepository;
    private final ComunaRepository comunaRepository;
    private final UnidadesMedidaRepository unidadesMedidaRepository;

    ClienteEntity cliente(Long id) {
        return id == null ? null : clienteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + id));
    }

    ServicioTipoEntity servicio(Integer id) {
        return id == null ? null : servicioTipoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de servicio no encontrado con ID: " + id));
    }

    ComunaEntity comuna(Integer id) {
        return id == null ? null : comunaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comuna no encontrada con ID: " + id));
    }

    UnidadesMedidaEntity unidad(Integer id) {
        return id == null ? null : unidadesMedidaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unidad de medida no encontrada con ID: " + id));
    }

    static String nombreCliente(ClienteEntity c) {
        if (c == null) {
            return null;
        }
        return c.getNombreCorto() != null && !c.getNombreCorto().isBlank() ? c.getNombreCorto() : c.getRazonSocial();
    }

    static String texto(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
