package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.ProveedorAdpater;
import com.transporte_gomez.erp.dto.Proveedor;
import com.transporte_gomez.erp.dto.ProveedorFiltro;
import com.transporte_gomez.erp.entity.ProveedorEntity;
import com.transporte_gomez.erp.repository.ProveedorRepository;
import com.transporte_gomez.erp.specification.ProveedorSpecification;
import com.transporte_gomez.erp.util.RutUtil;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorAdpater proveedorAdpater;

    public Page<Proveedor> getAll(Pageable pageable, ProveedorFiltro filtro) {
        return proveedorRepository.findAll(ProveedorSpecification.conFiltros(filtro), pageable)
                .map(proveedorAdpater::getProveedor);
    }

    public List<Proveedor> getProveedores(){
        return proveedorRepository.findByActivo(true).stream()
                .map(proveedorAdpater::getProveedor)
                .toList();
    }

    public Proveedor getProveedorById(Long id) {
        return proveedorAdpater.getProveedor(buscar(id));
    }

    @Transactional
    public Proveedor createProveedor(Proveedor proveedor) {
        validar(proveedor, null);
        proveedor.setActivo(true);
        ProveedorEntity proveedorEntity = proveedorAdpater.createProveedor(proveedor);
        proveedorEntity = proveedorRepository.save(proveedorEntity);
        return proveedorAdpater.getProveedor(proveedorEntity);
    }

    @Transactional
    public Proveedor updateProveedor(Long id, Proveedor proveedor) {
        ProveedorEntity proveedorEntity = buscar(id);
        validar(proveedor, id);
        proveedorEntity = proveedorAdpater.updateProveedor(proveedorEntity, proveedor);
        return proveedorAdpater.getProveedor(proveedorRepository.save(proveedorEntity));
    }

    @Transactional
    public void desactivateProveedor(Long id) {
        ProveedorEntity proveedorEntity = buscar(id);
        proveedorEntity.setActivo(false);
        proveedorRepository.save(proveedorEntity);
    }

    @Transactional
    public void activarProveedor(Long id) {
        ProveedorEntity proveedorEntity = buscar(id);
        proveedorEntity.setActivo(true);
        proveedorRepository.save(proveedorEntity);
    }

    /**
     * Une dos proveedores duplicados: todo lo que apuntaba a {@code origenId}
     * (órdenes, ingresos, documentos y códigos de artículos) pasa a {@code destinoId},
     * y el de origen se elimina.
     */
    @Transactional
    public Proveedor fusionar(Long origenId, Long destinoId) {
        if (origenId.equals(destinoId)) {
            throw new IllegalArgumentException("Elige un proveedor distinto para unir");
        }
        ProveedorEntity origen = buscar(origenId);
        ProveedorEntity destino = buscar(destinoId);

        proveedorRepository.traspasarCodigosItems(origenId, destinoId);
        proveedorRepository.traspasarDocumentos(origenId, destinoId);
        proveedorRepository.traspasarOrdenes(origenId, destinoId);
        proveedorRepository.traspasarIngresos(origenId, destinoId);

        String rutOrigen = origen.getRut();
        boolean destinoSinRut = destino.getRut() == null || destino.getRut().isBlank();
        if (Boolean.TRUE.equals(origen.getActivo())) {
            destino.setActivo(true);
        }
        proveedorRepository.delete(origen);
        proveedorRepository.flush();

        if (destinoSinRut && rutOrigen != null && !rutOrigen.isBlank()) {
            destino.setRut(rutOrigen);
        }
        return proveedorAdpater.getProveedor(proveedorRepository.save(destino));
    }

    private ProveedorEntity buscar(Long id) {
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + id));
    }

    /** Nombre obligatorio y único; RUT opcional, pero si viene debe ser válido y único. */
    private void validar(Proveedor proveedor, Long idActual) {
        if (proveedor.getNombre() == null || proveedor.getNombre().isBlank()) {
            throw new IllegalArgumentException("La razón social es obligatoria");
        }
        proveedor.setNombre(proveedor.getNombre().trim().replaceAll("\\s+", " "));
        proveedorRepository.buscarPorNombre(proveedor.getNombre()).stream()
                .filter(p -> !p.getId().equals(idActual))
                .findFirst()
                .ifPresent(p -> {
                    throw new IllegalArgumentException("Ya existe un proveedor llamado " + p.getRazonSocial());
                });

        String rut = RutUtil.normalizar(proveedor.getRut());
        if (rut == null) {
            proveedor.setRut("");
            return;
        }
        if (!RutUtil.esValido(rut)) {
            throw new IllegalArgumentException("RUT inválido: " + proveedor.getRut());
        }
        proveedor.setRut(rut);
        proveedorRepository.findFirstByRut(rut)
                .filter(p -> !p.getId().equals(idActual))
                .ifPresent(p -> {
                    throw new IllegalArgumentException("El RUT " + rut + " ya está registrado para " + p.getRazonSocial());
                });
    }
}
