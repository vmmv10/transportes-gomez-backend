package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.VehiculoAdapter;
import com.transporte_gomez.erp.dto.Vehiculo;
import com.transporte_gomez.erp.dto.VehiculoFiltro;
import com.transporte_gomez.erp.entity.VehiculoEntity;
import com.transporte_gomez.erp.enums.VehiculoEstado;
import com.transporte_gomez.erp.enums.VehiculoPropiedad;
import com.transporte_gomez.erp.enums.VehiculoTipo;
import com.transporte_gomez.erp.repository.RutaCostoRepository;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.transporte_gomez.erp.specification.VehiculoSpecification.conFiltros;

@Service
@RequiredArgsConstructor
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoAdapter vehiculoAdapter;
    private final RutaRepository rutaRepository;
    private final RutaCostoRepository rutaCostoRepository;

    @Transactional(readOnly = true)
    public Page<Vehiculo> getAll(Pageable pageable, VehiculoFiltro filtro) {
        return vehiculoRepository.findAll(conFiltros(filtro), pageable).map(vehiculoAdapter::toDto);
    }

    /** Para selectores: activos, opcionalmente de un tipo. */
    @Transactional(readOnly = true)
    public List<Vehiculo> listarActivos(VehiculoTipo tipo) {
        VehiculoFiltro filtro = new VehiculoFiltro();
        filtro.setActivo(true);
        filtro.setTipo(tipo);
        return vehiculoRepository.findAll(conFiltros(filtro), Sort.by("tipo", "nombre")).stream()
                .map(vehiculoAdapter::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public Vehiculo getById(Long id) {
        return vehiculoAdapter.toDto(buscar(id));
    }

    @Transactional
    public Vehiculo create(Vehiculo vehiculo) {
        VehiculoEntity entity = vehiculoAdapter.toEntity(validar(vehiculo), new VehiculoEntity());
        verificarPatente(entity.getPatente(), null);
        entity.setActivo(true);
        return vehiculoAdapter.toDto(vehiculoRepository.save(entity));
    }

    @Transactional
    public Vehiculo update(Long id, Vehiculo vehiculo) {
        VehiculoEntity entity = vehiculoAdapter.toEntity(validar(vehiculo), buscar(id));
        verificarPatente(entity.getPatente(), id);
        return vehiculoAdapter.toDto(vehiculoRepository.save(entity));
    }

    @Transactional
    public void cambiarActivo(Long id, boolean activo) {
        VehiculoEntity entity = buscar(id);
        entity.setActivo(activo);
        vehiculoRepository.save(entity);
    }

    /** Solo se elimina un vehículo que no se ha usado en rutas ni costos; si no, se desactiva. */
    @Transactional
    public void eliminar(Long id) {
        VehiculoEntity entity = buscar(id);
        long rutas = rutaRepository.countByVehiculo_Id(id);
        long costos = rutaCostoRepository.countByVehiculo_Id(id);
        if (rutas + costos > 0) {
            throw new IllegalArgumentException("No se puede eliminar: " + VehiculoAdapter.descripcion(entity)
                    + " está en " + rutas + (rutas == 1 ? " ruta" : " rutas")
                    + (costos > 0 ? " y " + costos + (costos == 1 ? " costo" : " costos") : "")
                    + ". Puedes desactivarlo.");
        }
        vehiculoRepository.delete(entity);
    }

    VehiculoEntity buscar(Long id) {
        return vehiculoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + id));
    }

    private void verificarPatente(String patente, Long id) {
        if (patente != null && vehiculoRepository.existePatente(patente.trim().toUpperCase(), id)) {
            throw new IllegalArgumentException("Ya existe un vehículo con patente o matrícula " + patente);
        }
    }

    private Vehiculo validar(Vehiculo v) {
        if (v.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de vehículo es obligatorio");
        }
        if (v.getNombre() == null || v.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del vehículo es obligatorio (ej. \"Hilux blanca\")");
        }
        if (v.getPropiedad() == VehiculoPropiedad.ARRENDADO && v.getProveedorId() == null) {
            throw new IllegalArgumentException("Un vehículo arrendado debe tener arrendador (proveedor)");
        }
        if (v.getAnio() != null && (v.getAnio() < 1950 || v.getAnio() > 2100)) {
            throw new IllegalArgumentException("El año no es válido");
        }
        if (v.getEstado() == null) {
            v.setEstado(VehiculoEstado.OPERATIVO);
        }
        return v;
    }
}
