package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.OrdenServicio;
import com.transporte_gomez.erp.dto.OrdenServicioDetalle;
import com.transporte_gomez.erp.dto.SaldoBodega;
import com.transporte_gomez.erp.entity.*;
import com.transporte_gomez.erp.repository.*;
import com.transporte_gomez.erp.services.DestinoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrdenServicioAdapter {

    private final OrdenServicioDetalleAdapter ordenServicioDetalleAdapter;
    private final DocumentoAdapter documentoAdapter;
    private final EscuelaAdapter escuelaAdapter;
    private final CategoriaAdapter categoriaAdapter;
    private final EscuelaRepository escuelaRepository;
    private final DocumentoRepository documentoRepository;
    private final DocumentoTipoRepository documentoTipoRepository;
    private final BodegaRepository bodegaRepository;
    private final CategoriaRepository categoriaRepository;
    private final BodegaAdapter bodegaAdapter;
    private final ItemAdapter itemAdapter;
    private final ClienteAdapter clienteAdapter;
    private final ServicioTipoAdapter servicioTipoAdapter;
    private final DestinoAdapter destinoAdapter;
    private final ProveedorAdpater proveedorAdpater;
    private final ContratoAdapter contratoAdapter;
    private final ClienteRepository clienteRepository;
    private final ServicioTipoRepository servicioTipoRepository;
    private final DestinoRepository destinoRepository;
    private final ProveedorRepository proveedorRepository;
    private final ContratoRepository contratoRepository;
    private final DestinoService destinoService;

    public OrdenServicio getOrdenServicio(OrdenServicioEntity ordenServicioEntity, boolean conDetalles) {
        OrdenServicio ordenServicio = new OrdenServicio();

        ordenServicio.setId(ordenServicioEntity.getId());
        if (ordenServicioEntity.getBodega() != null) {
            BodegaEntity bodegaEntity = bodegaRepository.findById(ordenServicioEntity.getBodega())
                    .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + ordenServicioEntity.getBodega()));
            ordenServicio.setBodega(bodegaAdapter.getBodega(bodegaEntity));
        }
        if (ordenServicioEntity.getDocumento() != null) {
            ordenServicio.setDocumento(documentoAdapter.getDocumento(ordenServicioEntity.getDocumento()));
        }

        OffsetDateTime fechaChile = ordenServicioEntity.getFecha()
                .withZoneSameInstant(ZoneId.of("America/Santiago"))
                .toOffsetDateTime();

        ordenServicio.setFecha(fechaChile);
        if (ordenServicioEntity.getEscuela() != null) {
            ordenServicio.setEscuela(escuelaAdapter.toDto(ordenServicioEntity.getEscuela()));
        }
        ordenServicio.setEntregado(ordenServicioEntity.getEntregado());
        ordenServicio.setObservaciones(ordenServicioEntity.getObservaciones());
        ordenServicio.setIngreso(ordenServicioEntity.getIngreso());

        if (conDetalles && ordenServicioEntity.getDetalles() != null) {
            List<OrdenServicioDetalle> detalles = new ArrayList<>();

            ordenServicioEntity.getDetalles().forEach(detalleEntity -> {
                OrdenServicioDetalle detalle = ordenServicioDetalleAdapter.getOrdenServicioDetalle(detalleEntity);
                detalles.add(detalle);
            });

            ordenServicio.setDetalles(detalles);
        }

        if (ordenServicioEntity.getDocumentoReferencia() != null) {
            ordenServicio.setDocumentoReferencia(ordenServicioEntity.getDocumentoReferencia());
        }

        if (ordenServicioEntity.getCategoria() != null) {
            ordenServicio.setCategoria(categoriaAdapter.get(ordenServicioEntity.getCategoria()));
        }

        ordenServicio.setCliente(clienteAdapter.toDtoResumen(ordenServicioEntity.getCliente()));
        ordenServicio.setServicioTipo(servicioTipoAdapter.toDto(ordenServicioEntity.getServicioTipo()));
        ordenServicio.setDestino(destinoAdapter.toDto(ordenServicioEntity.getDestino()));
        if (ordenServicioEntity.getProveedor() != null) {
            ordenServicio.setProveedor(proveedorAdpater.getProveedor(ordenServicioEntity.getProveedor()));
        }
        ordenServicio.setContrato(contratoAdapter.toDto(ordenServicioEntity.getContrato()));

        return ordenServicio;
    }

    public OrdenServicioEntity createOrdenServicio(OrdenServicio ordenServicio) {
        OrdenServicioEntity ordenServicioEntity = new OrdenServicioEntity();
        ordenServicioEntity.setFecha(ZonedDateTime.now());
        ordenServicioEntity.setEntregado(false);
        ordenServicioEntity.setObservaciones(ordenServicio.getObservaciones());
        ordenServicioEntity.setEnRuta(false);
        ordenServicioEntity.setIngreso(ordenServicio.getIngreso());

        if (ordenServicio.getDocumentoReferencia() != null) {
            ordenServicioEntity.setDocumentoReferencia(ordenServicio.getDocumentoReferencia());
        }

        if (ordenServicio.getBodega() != null) {
            BodegaEntity bodegaEntity = bodegaRepository.findById(ordenServicio.getBodega().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Bodega no encontrada con ID: " + ordenServicio.getBodega().getId()));
            ordenServicioEntity.setBodega(bodegaEntity.getId());
        }

        if (ordenServicio.getDocumento() != null && ordenServicio.getDocumento().getId() > 0) {
            if (ordenServicio.getBodega() != null && ordenServicio.getBodega().getId() == 4L) {
                ordenServicioEntity.setDocumento(documentoRepository.findByNumeroAndTipo_Codigo(ordenServicio.getDocumento().getNumero(), ordenServicio.getDocumento().getTipo().getCodigo()));
            } else {
                DocumentoEntity documentoEntity = new DocumentoEntity();
                documentoEntity.setNumero(ordenServicio.getDocumento().getNumero());
                documentoEntity.setTipo(documentoTipoRepository.findByCodigo(ordenServicio.getDocumento().getTipo().getCodigo()));
                documentoRepository.save(documentoEntity);

                ordenServicioEntity.setDocumento(documentoEntity);
            }
        }

        if (ordenServicio.getEscuela() != null) {
            ordenServicioEntity.setEscuela(escuelaRepository.findById(ordenServicio.getEscuela().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Escuela no encontrada con ID: " + ordenServicio.getEscuela().getId())));
        }

        if (ordenServicio.getCategoria() != null) {
            ordenServicioEntity.setCategoria(categoriaRepository.findById(ordenServicio.getCategoria().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + ordenServicio.getCategoria().getId())));
        }

        aplicarDatosLogisticos(ordenServicioEntity, ordenServicio);

        return ordenServicioEntity;
    }

    public OrdenServicioEntity updateOrdenServicio(OrdenServicioEntity ordenServicioEntity, OrdenServicio ordenServicio) {
        ordenServicioEntity.setFecha(ZonedDateTime.now());
        ordenServicioEntity.setObservaciones(ordenServicio.getObservaciones());
        if (ordenServicio.getEscuela() != null) {
            ordenServicioEntity.setEscuela(escuelaRepository.findById(ordenServicio.getEscuela().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Escuela no encontrada con ID: " + ordenServicio.getEscuela().getId())));
        }

        if (ordenServicio.getDocumentoReferencia() != null) {
            ordenServicioEntity.setDocumentoReferencia(ordenServicio.getDocumentoReferencia());
        }

        if (ordenServicio.getDocumento() != null) {
            ordenServicioEntity.setDocumento(documentoRepository.findByNumeroAndTipo_Codigo(ordenServicio.getDocumento().getNumero(), ordenServicio.getDocumento().getTipo().getCodigo()));
        }

        if (ordenServicio.getCategoria() != null) {
            ordenServicioEntity.setCategoria(categoriaRepository.findById(ordenServicio.getCategoria().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada con ID: " + ordenServicio.getCategoria().getId())));
        }

        ordenServicio.setIngreso(ordenServicioEntity.getIngreso());

        aplicarDatosLogisticos(ordenServicioEntity, ordenServicio);

        return ordenServicioEntity;
    }

    /**
     * Cliente, destino, tipo de servicio, proveedor y contrato.
     * Lo que no venga en la solicitud se completa solo, para que las pantallas
     * actuales (que solo envían la escuela) sigan funcionando:
     *  - destino: el de la escuela (se crea si no existe)
     *  - cliente: el del contrato, la escuela o el destino
     *  - tipo de servicio: carga terrestre
     */
    private void aplicarDatosLogisticos(OrdenServicioEntity entity, OrdenServicio dto) {
        // Destino
        if (dto.getDestino() != null && dto.getDestino().getId() != null) {
            entity.setDestino(destinoRepository.findById(dto.getDestino().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Destino no encontrado con ID: " + dto.getDestino().getId())));
        } else if (entity.getEscuela() != null) {
            DestinoEntity actual = entity.getDestino();
            boolean destinoDeOtraEscuela = actual != null && actual.getEscuela() != null
                    && !actual.getEscuela().getId().equals(entity.getEscuela().getId());
            if (actual == null || destinoDeOtraEscuela) {
                entity.setDestino(destinoService.obtenerOCrearParaEscuela(entity.getEscuela()));
            }
        }
        if (entity.getEscuela() == null && entity.getDestino() == null) {
            throw new IllegalArgumentException("La orden de servicio debe tener escuela o destino");
        }

        // Contrato (el formulario siempre envía la orden completa: sin contrato = se quita)
        entity.setContrato(dto.getContrato() != null && dto.getContrato().getId() != null
                ? contratoRepository.findById(dto.getContrato().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Contrato no encontrado con ID: " + dto.getContrato().getId()))
                : null);

        // Cliente
        if (dto.getCliente() != null && dto.getCliente().getId() != null) {
            entity.setCliente(clienteRepository.findById(dto.getCliente().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + dto.getCliente().getId())));
        } else if (entity.getCliente() == null) {
            if (entity.getContrato() != null) {
                entity.setCliente(entity.getContrato().getCliente());
            } else if (entity.getEscuela() != null && entity.getEscuela().getCliente() != null) {
                entity.setCliente(entity.getEscuela().getCliente());
            } else if (entity.getDestino() != null) {
                entity.setCliente(entity.getDestino().getCliente());
            }
        }
        if (entity.getContrato() != null && entity.getCliente() != null
                && !entity.getContrato().getCliente().getId().equals(entity.getCliente().getId())) {
            throw new IllegalArgumentException("El contrato " + entity.getContrato().getCodigo() + " no pertenece al cliente de la orden");
        }

        // Tipo de servicio
        if (dto.getServicioTipo() != null && dto.getServicioTipo().getId() != null) {
            entity.setServicioTipo(servicioTipoRepository.findById(dto.getServicioTipo().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Tipo de servicio no encontrado con ID: " + dto.getServicioTipo().getId())));
        } else if (entity.getServicioTipo() == null) {
            servicioTipoRepository.findByCodigo(ServicioTipoEntity.CARGA_TERRESTRE).ifPresent(entity::setServicioTipo);
        }

        // Proveedor de la mercadería (sin proveedor = se quita)
        entity.setProveedor(dto.getProveedor() != null && dto.getProveedor().getId() != null
                ? proveedorRepository.findById(dto.getProveedor().getId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + dto.getProveedor().getId()))
                : null);
    }

    public OrdenServicio getByIngreso(IngresosEntity ingreso) {
        OrdenServicio ordenServicio = new OrdenServicio();

        ordenServicio.setIngreso(ingreso.getId());
        ordenServicio.setCliente(clienteAdapter.toDtoResumen(ingreso.getCliente()));
        ordenServicio.setDocumentoReferencia(ingreso.getOrdenCompra() != null ? String.valueOf(ingreso.getOrdenCompra()) : null);
        ordenServicio.setBodega(bodegaAdapter.getBodega(bodegaRepository.findById(1L).orElseThrow()));

        List<OrdenServicioDetalle> detalles = new ArrayList<>();

        for (IngresosDetalleEntity detalleEntity : ingreso.getDetalles()) {
            OrdenServicioDetalle detalle = new OrdenServicioDetalle();
            detalle.setCantidad(detalleEntity.getSaldo());
            detalle.setNombre(detalleEntity.getItem().getNombre());
            detalle.setItem(detalleEntity.getItem().getId());

            SaldoBodega saldoBodega = new SaldoBodega();
            saldoBodega.setSaldo(detalleEntity.getSaldo());
            saldoBodega.setItem(itemAdapter.getItem(detalleEntity.getItem()));

            detalle.setSaldoBodega(saldoBodega);
            detalles.add(detalle);
        }

        ordenServicio.setDetalles(detalles);

        return ordenServicio;
    }

}
