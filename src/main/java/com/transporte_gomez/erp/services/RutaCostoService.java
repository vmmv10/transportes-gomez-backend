package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.adapter.RutaCostoAdapter;
import com.transporte_gomez.erp.dto.RutaCosto;
import com.transporte_gomez.erp.dto.RutaCostoFiltro;
import com.transporte_gomez.erp.dto.RutaCostoResumen;
import com.transporte_gomez.erp.entity.RutaCostoEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import com.transporte_gomez.erp.entity.VehiculoEntity;
import com.transporte_gomez.erp.enums.RutaCostoTipo;
import com.transporte_gomez.erp.enums.VehiculoPropiedad;
import com.transporte_gomez.erp.repository.ProveedorRepository;
import com.transporte_gomez.erp.repository.RutaCostoRepository;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RutaCostoService {

    private static final ZoneId CHILE = ZoneId.of("America/Santiago");
    /** Rango máximo del resumen */
    private static final long MAX_DIAS = 400;

    private final RutaCostoRepository rutaCostoRepository;
    private final RutaRepository rutaRepository;
    private final ProveedorRepository proveedorRepository;
    private final UsuarioRepository usuarioRepository;
    private final VehiculoService vehiculoService;
    private final RutaCostoAdapter rutaCostoAdapter;

    @Transactional(readOnly = true)
    public List<RutaCosto> listar(Integer rutaId) {
        buscarRuta(rutaId);
        return rutaCostoRepository.findByRuta_IdOrderByFechaAscIdAsc(rutaId).stream()
                .map(rutaCostoAdapter::toDto)
                .toList();
    }

    @Transactional
    public RutaCosto crear(Integer rutaId, RutaCosto costo, String auth0Id) {
        RutaCostoEntity entity = new RutaCostoEntity();
        entity.setRuta(buscarRuta(rutaId));
        aplicar(costo, entity);
        if (auth0Id != null) {
            usuarioRepository.findByAuth0Id(auth0Id).ifPresent(entity::setUsuario);
        }
        return rutaCostoAdapter.toDto(rutaCostoRepository.save(entity));
    }

    @Transactional
    public RutaCosto actualizar(Integer rutaId, Long costoId, RutaCosto costo) {
        RutaCostoEntity entity = buscar(rutaId, costoId);
        aplicar(costo, entity);
        return rutaCostoAdapter.toDto(rutaCostoRepository.save(entity));
    }

    @Transactional
    public void eliminar(Integer rutaId, Long costoId) {
        rutaCostoRepository.delete(buscar(rutaId, costoId));
    }

    /** Costo real por ruta en un rango de fechas (por defecto, el mes actual). */
    @Transactional(readOnly = true)
    public List<RutaCostoResumen> resumen(RutaCostoFiltro filtro) {
        LocalDate hoy = LocalDate.now(CHILE);
        LocalDate desde = fecha(filtro.getDesde(), hoy.withDayOfMonth(1));
        LocalDate hasta = fecha(filtro.getHasta(), hoy);
        if (hasta.isBefore(desde)) {
            throw new IllegalArgumentException("La fecha hasta no puede ser anterior a la fecha desde");
        }
        if (ChronoUnit.DAYS.between(desde, hasta) > MAX_DIAS) {
            throw new IllegalArgumentException("El rango máximo es de " + MAX_DIAS + " días");
        }

        Map<Integer, RutaCostoResumen> rutas = new LinkedHashMap<>();
        for (Object[] f : rutaCostoRepository.resumen(desde, hasta, filtro.getVehiculo(), filtro.getChofer(),
                Boolean.TRUE.equals(filtro.getConCostos()))) {
            Integer rutaId = ((Number) f[0]).intValue();
            RutaCostoResumen r = rutas.computeIfAbsent(rutaId, id -> {
                RutaCostoResumen n = new RutaCostoResumen();
                n.setRutaId(id);
                n.setFecha(f[1] instanceof Date d ? d.toLocalDate() : (LocalDate) f[1]);
                n.setEstado((String) f[2]);
                n.setChofer((String) f[3]);
                n.setVehiculoId(f[4] != null ? ((Number) f[4]).longValue() : null);
                n.setVehiculo(f[5] != null ? (String) f[5] : "Sin vehículo");
                n.setKilometros(f[6] != null ? ((Number) f[6]).intValue() : null);
                n.setEntregas(((Number) f[7]).intValue());
                n.setTotal(BigDecimal.ZERO);
                n.setPorTipo(new LinkedHashMap<>());
                return n;
            });
            if (f[8] != null) {
                BigDecimal monto = new BigDecimal(f[9].toString());
                r.getPorTipo().merge((String) f[8], monto, BigDecimal::add);
                r.setTotal(r.getTotal().add(monto));
                if (f[10] != null) {
                    BigDecimal litros = new BigDecimal(f[10].toString());
                    r.setLitros(r.getLitros() == null ? litros : r.getLitros().add(litros));
                }
            }
        }

        List<RutaCostoResumen> resultado = new ArrayList<>(rutas.values());
        for (RutaCostoResumen r : resultado) {
            if (r.getKilometros() != null && r.getKilometros() > 0 && r.getTotal().signum() > 0) {
                r.setCostoPorKm(r.getTotal().divide(BigDecimal.valueOf(r.getKilometros()), 0, RoundingMode.HALF_UP));
            }
            if (r.getEntregas() != null && r.getEntregas() > 0 && r.getTotal().signum() > 0) {
                r.setCostoPorEntrega(r.getTotal().divide(BigDecimal.valueOf(r.getEntregas()), 0, RoundingMode.HALF_UP));
            }
        }
        return resultado;
    }

    private void aplicar(RutaCosto dto, RutaCostoEntity entity) {
        if (dto.getTipo() == null) {
            throw new IllegalArgumentException("El tipo de costo es obligatorio");
        }
        if (dto.getMonto() == null || dto.getMonto().signum() <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor a cero");
        }
        if (dto.getLitros() != null && dto.getLitros().signum() < 0) {
            throw new IllegalArgumentException("Los litros no pueden ser negativos");
        }
        entity.setTipo(dto.getTipo());
        entity.setFecha(dto.getFecha() != null ? dto.getFecha() : entity.getRuta().getFecha());
        entity.setMonto(dto.getMonto().setScale(0, RoundingMode.HALF_UP));
        entity.setLitros(dto.getTipo() == RutaCostoTipo.COMBUSTIBLE ? dto.getLitros() : null);

        VehiculoEntity vehiculo = dto.getVehiculoId() != null ? vehiculoService.buscar(dto.getVehiculoId()) : null;
        entity.setVehiculo(vehiculo);

        if (dto.getProveedorId() != null) {
            entity.setProveedor(proveedorRepository.findById(dto.getProveedorId())
                    .orElseThrow(() -> new IllegalArgumentException("Proveedor no encontrado con ID: " + dto.getProveedorId())));
        } else if (vehiculo != null && vehiculo.getPropiedad() == VehiculoPropiedad.ARRENDADO) {
            // Arriendo de lancha: si no se indica a quién se pagó, se usa el arrendador del vehículo
            entity.setProveedor(vehiculo.getProveedor());
        } else {
            entity.setProveedor(null);
        }
        entity.setDescripcion(texto(dto.getDescripcion()));
        entity.setComprobante(texto(dto.getComprobante()));
    }

    private RutaEntity buscarRuta(Integer rutaId) {
        return rutaRepository.findById(rutaId)
                .orElseThrow(() -> new IllegalArgumentException("Ruta no encontrada con ID: " + rutaId));
    }

    private RutaCostoEntity buscar(Integer rutaId, Long costoId) {
        RutaCostoEntity entity = rutaCostoRepository.findById(costoId)
                .orElseThrow(() -> new IllegalArgumentException("Costo no encontrado con ID: " + costoId));
        if (!entity.getRuta().getId().equals(rutaId)) {
            throw new IllegalArgumentException("El costo " + costoId + " no pertenece a la ruta " + rutaId);
        }
        return entity;
    }

    private static LocalDate fecha(String valor, LocalDate porDefecto) {
        if (valor == null || valor.isBlank()) {
            return porDefecto;
        }
        String v = valor.trim();
        return LocalDate.parse(v.length() > 10 ? v.substring(0, 10) : v);
    }

    private static String texto(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
