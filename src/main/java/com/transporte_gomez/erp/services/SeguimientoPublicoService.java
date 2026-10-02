package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.dto.SeguimientoPublico;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.OrdenServicioEntity;
import com.transporte_gomez.erp.entity.SeguimientoEventoEntity;
import com.transporte_gomez.erp.enums.EntregaEstado;
import com.transporte_gomez.erp.enums.SeguimientoTipo;
import com.transporte_gomez.erp.repository.EntregaRepository;
import com.transporte_gomez.erp.repository.OrdenServicioRepository;
import com.transporte_gomez.erp.repository.SeguimientoEventoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Seguimiento con el código público de la orden. Solo muestra el estado, el destino y los hitos;
 * nunca datos internos (coordenadas, nombres del personal, comentarios, contenido).
 */
@Service
@RequiredArgsConstructor
public class SeguimientoPublicoService {

    private static final Map<SeguimientoTipo, String> TITULOS = Map.of(
            SeguimientoTipo.EN_RUTA, "Salió a reparto",
            SeguimientoTipo.CERCA_DESTINO, "El conductor está cerca del destino",
            SeguimientoTipo.ENTREGADO, "Entregado",
            SeguimientoTipo.NO_ENTREGADO, "No se pudo entregar, se reprogramará",
            SeguimientoTipo.RECHAZADO, "No se pudo entregar, se reprogramará",
            SeguimientoTipo.REPROGRAMADO, "Entrega reprogramada");

    private final OrdenServicioRepository ordenServicioRepository;
    private final EntregaRepository entregaRepository;
    private final SeguimientoEventoRepository eventoRepository;

    @Transactional(readOnly = true)
    public Optional<SeguimientoPublico> buscar(String codigo) {
        if (codigo == null) {
            return Optional.empty();
        }
        String normalizado = codigo.trim().toUpperCase().replaceAll("[\\s-]", "");
        if (!normalizado.matches("[A-Z0-9]{6,12}")) {
            return Optional.empty();
        }
        return ordenServicioRepository.findByCodigoSeguimiento(normalizado).map(this::armar);
    }

    private SeguimientoPublico armar(OrdenServicioEntity os) {
        SeguimientoPublico s = new SeguimientoPublico();
        s.setCodigo(os.getCodigoSeguimiento());
        if (os.getDestino() != null) {
            s.setDestino(os.getDestino().getNombre());
            s.setComuna(os.getDestino().getComuna() != null ? os.getDestino().getComuna().getNombre() : null);
        } else if (os.getEscuela() != null) {
            s.setDestino(os.getEscuela().getNombre());
            s.setComuna(os.getEscuela().getComuna());
        }

        List<SeguimientoPublico.Evento> eventos = new ArrayList<>();
        if (os.getFecha() != null) {
            eventos.add(evento("RECIBIDA", "Orden recibida", os.getFecha().toOffsetDateTime()));
        }
        for (SeguimientoEventoEntity e : eventoRepository.findByOrdenServicio_IdOrderByFechaAscIdAsc(os.getId())) {
            if (Boolean.TRUE.equals(e.getVisiblePublico())) {
                eventos.add(evento(e.getTipo().name(), TITULOS.getOrDefault(e.getTipo(), e.getTipo().name()), e.getFecha()));
            }
        }
        s.setEventos(eventos);

        EntregaEntity entrega = entregaRepository.findByOrdenServicio_Id(os.getId()).orElse(null);
        if (Boolean.TRUE.equals(os.getEntregado()) || (entrega != null && entrega.getEstado() == EntregaEstado.ENTREGADO)) {
            s.setEstado("ENTREGADA");
            s.setEstadoTexto("Entregada");
            s.setFechaEntrega(entrega != null && entrega.getFecha() != null ? entrega.getFecha() : null);
            // Órdenes antiguas sin eventos: se agrega el hito de entrega
            if (eventos.stream().noneMatch(ev -> ev.getTipo().equals("ENTREGADO"))) {
                eventos.add(evento("ENTREGADO", "Entregado", s.getFechaEntrega()));
            }
        } else if (entrega != null && entrega.getEstado() != null && entrega.getEstado().reprogramable()) {
            s.setEstado("NO_ENTREGADA");
            s.setEstadoTexto("Pendiente de reprogramar");
        } else if (Boolean.TRUE.equals(os.getEnRuta()) || entrega != null) {
            s.setEstado("EN_RUTA");
            s.setEstadoTexto("En ruta de entrega");
        } else {
            s.setEstado("RECIBIDA");
            s.setEstadoTexto("Recibida, en preparación");
        }
        return s;
    }

    private static SeguimientoPublico.Evento evento(String tipo, String titulo, OffsetDateTime fecha) {
        SeguimientoPublico.Evento e = new SeguimientoPublico.Evento();
        e.setTipo(tipo);
        e.setTitulo(titulo);
        e.setFecha(fecha);
        return e;
    }
}
