package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.DestinoAdapter;
import com.transporte_gomez.erp.dto.Destino;
import com.transporte_gomez.erp.dto.DestinoFiltro;
import com.transporte_gomez.erp.entity.DestinoEntity;
import com.transporte_gomez.erp.entity.EscuelaEntity;
import com.transporte_gomez.erp.repository.ComunaRepository;
import com.transporte_gomez.erp.repository.DestinoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.Set;

import static com.transporte_gomez.erp.specification.DestinoSpecification.conFiltros;

@Service
@RequiredArgsConstructor
public class DestinoService {

    private static final Set<String> TIPOS = Set.of("ESCUELA", "JARDIN", "OFICINA", "PERSONA", "EMPRESA", "OTRO");

    private final DestinoRepository destinoRepository;
    private final DestinoAdapter destinoAdapter;
    private final ComunaRepository comunaRepository;

    @Transactional(readOnly = true)
    public Page<Destino> getAll(Pageable pageable, DestinoFiltro filtro) {
        return destinoRepository.findAll(conFiltros(filtro), pageable).map(destinoAdapter::toDto);
    }

    @Transactional(readOnly = true)
    public Destino getById(Long id) {
        return destinoAdapter.toDto(buscar(id));
    }

    @Transactional
    public Destino create(Destino destino) {
        validar(destino);
        DestinoEntity entity = destinoAdapter.toEntity(destino, new DestinoEntity());
        entity.setActivo(true);
        return destinoAdapter.toDto(destinoRepository.save(entity));
    }

    @Transactional
    public Destino update(Long id, Destino destino) {
        DestinoEntity entity = buscar(id);
        validar(destino);
        return destinoAdapter.toDto(destinoRepository.save(destinoAdapter.toEntity(destino, entity)));
    }

    @Transactional
    public void cambiarEstado(Long id, boolean activo) {
        DestinoEntity entity = buscar(id);
        entity.setActivo(activo);
        destinoRepository.save(entity);
    }

    /**
     * Destino de una escuela. Si no existe (escuela nueva o que nunca recibió órdenes),
     * lo crea con los datos de la escuela.
     */
    @Transactional
    public DestinoEntity obtenerOCrearParaEscuela(EscuelaEntity escuela) {
        return destinoRepository.findByEscuela_Id(escuela.getId())
                .orElseGet(() -> {
                    DestinoEntity nuevo = new DestinoEntity();
                    nuevo.setEscuela(escuela);
                    nuevo.setTipo(tipoSegunEscuela(escuela));
                    nuevo.setActivo(true);
                    copiarDatosEscuela(escuela, nuevo);
                    return destinoRepository.save(nuevo);
                });
    }

    /** Mantiene el destino al día cuando se edita la escuela. No crea destinos. */
    @Transactional
    public void sincronizarDesdeEscuela(EscuelaEntity escuela) {
        destinoRepository.findByEscuela_Id(escuela.getId()).ifPresent(destino -> {
            copiarDatosEscuela(escuela, destino);
            destinoRepository.save(destino);
        });
    }

    private void copiarDatosEscuela(EscuelaEntity escuela, DestinoEntity destino) {
        destino.setNombre(escuela.getNombre() != null ? escuela.getNombre().trim() : null);
        destino.setDireccion(escuela.getDireccion());
        destino.setLatitud(escuela.getLatitud());
        destino.setLongitud(escuela.getLongitud());
        destino.setContacto(escuela.getDirector());
        destino.setTelefono(escuela.getTelefono());
        destino.setEmail(escuela.getEmail());
        destino.setCliente(escuela.getCliente());
        if (escuela.getComuna() != null) {
            String buscada = sinTildes(escuela.getComuna());
            comunaRepository.findByActivoTrueOrderByNombreAsc().stream()
                    .filter(c -> sinTildes(c.getNombre()).equals(buscada))
                    .findFirst()
                    .ifPresent(destino::setComuna);
        }
    }

    private String tipoSegunEscuela(EscuelaEntity escuela) {
        String nombre = escuela.getNombre() != null ? escuela.getNombre().toUpperCase() : "";
        if ("Proveedor".equalsIgnoreCase(escuela.getTipo())) {
            return "EMPRESA";
        }
        if (sinTildes(nombre).startsWith("JARDIN")) {
            return "JARDIN";
        }
        if (nombre.startsWith("SLEP")) {
            return "OFICINA";
        }
        return "ESCUELA";
    }

    private static String sinTildes(String texto) {
        return Normalizer.normalize(texto.trim().toUpperCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
    }

    private DestinoEntity buscar(Long id) {
        return destinoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Destino no encontrado con ID: " + id));
    }

    private void validar(Destino destino) {
        if (destino.getNombre() == null || destino.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del destino es obligatorio");
        }
        if (destino.getTipo() == null || !TIPOS.contains(destino.getTipo())) {
            throw new IllegalArgumentException("Tipo de destino inválido: " + destino.getTipo());
        }
    }
}
