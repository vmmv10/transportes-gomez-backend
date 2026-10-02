package com.transporte_gomez.erp.adapter;

import com.transporte_gomez.erp.dto.Entrega;
import com.transporte_gomez.erp.dto.OrdenServicio;
import com.transporte_gomez.erp.dto.Ruta;
import com.transporte_gomez.erp.entity.EntregaEntity;
import com.transporte_gomez.erp.entity.RutaEntity;
import com.transporte_gomez.erp.entity.VehiculoEntity;
import com.transporte_gomez.erp.repository.EntregaRepository;
import com.transporte_gomez.erp.repository.RutaRepository;
import com.transporte_gomez.erp.repository.RutaCostoRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import com.transporte_gomez.erp.repository.VehiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RutaAdapter {

    private final UsuarioRepository usuarioRepository;
    private final EntregaRepository entregaRepository;
    private final UsuarioAdapter usuarioAdapter;
    private final EntregaAdapter entregaAdapter;
    private final RutaRepository rutaRepository;
    private final VehiculoRepository vehiculoRepository;
    private final VehiculoAdapter vehiculoAdapter;
    private final RutaCostoRepository rutaCostoRepository;

    public Ruta getRuta(RutaEntity rutaEntity) {
        Ruta ruta = new Ruta();
        ruta.setId(rutaEntity.getId());
        LocalDate fecha = rutaEntity.getFecha();
        ZonedDateTime fechaChile = fecha.atStartOfDay(ZoneId.of("America/Santiago"));
        ruta.setFecha(fechaChile.toLocalDate());
        ruta.setEstado(rutaEntity.getEstado());
        ruta.setEnTransito(rutaEntity.getEnTransito());
        ruta.setKilometros(rutaEntity.getKilometros());
        ruta.setKmSalida(rutaEntity.getKmSalida());
        ruta.setKmLlegada(rutaEntity.getKmLlegada());
        ruta.setInicio(rutaEntity.getInicio());
        ruta.setFin(rutaEntity.getFin());
        ruta.setVehiculo(vehiculoAdapter.toDto(rutaEntity.getVehiculo()));
        ruta.setCostoTotal(rutaCostoRepository.totalPorRuta(rutaEntity.getId()));

        if (rutaEntity.getChofer() != null) {
            ruta.setChofer(usuarioAdapter.getUsuario(rutaEntity.getChofer()));
        }

        List<EntregaEntity> entregas = entregaRepository.findByRuta_Id(rutaEntity.getId());

        if (entregas != null && !entregas.isEmpty()) {
            ruta.setEntregas(entregas.stream()
                    .map(entregaAdapter::getEntrega)
                    .toList());

            List<OrdenServicio> ordenes = new ArrayList<>();
            for (Entrega entrega : ruta.getEntregas()) {
                if (entrega.getOrdenServicio() != null) {
                    ordenes.add(entrega.getOrdenServicio());
                }
            }

            ruta.setOrdenes(ordenes);
        }

        return ruta;
    }

    public RutaEntity createRuta(Ruta ruta) {
        RutaEntity rutaEntity = new RutaEntity();
        rutaEntity.setId(ruta.getId());
        LocalDate fecha = ruta.getFecha();
        ZonedDateTime fechaChile = fecha.atStartOfDay(ZoneId.of("America/Santiago"));
        rutaEntity.setFecha(fechaChile.toLocalDate());
        rutaEntity.setEstado("PENDIENTE");
        rutaEntity.setVehiculo(vehiculo(ruta));
        aplicarKilometraje(rutaEntity, ruta.getKmSalida(), ruta.getKmLlegada(), ruta.getKilometros());

        if (ruta.getChofer() != null) {
            rutaEntity.setChofer(usuarioRepository.findById(ruta.getChofer().getId())
                    .orElseThrow(() -> new RuntimeException("Chofer not found with id: " + ruta.getChofer().getId())));
        }

        return  rutaEntity;
    }

    public RutaEntity update(Ruta ruta, RutaEntity rutaEntity) {

        if (ruta.getChofer() != null) {
            rutaEntity.setChofer(usuarioRepository.findById(ruta.getChofer().getId())
                    .orElseThrow(() -> new RuntimeException("Chofer not found with id: " + ruta.getChofer().getId())));
        }
        rutaEntity.setFecha(ruta.getFecha());
        rutaEntity.setEstado(ruta.getEstado());
        rutaEntity.setVehiculo(vehiculo(ruta));
        aplicarKilometraje(rutaEntity, ruta.getKmSalida(), ruta.getKmLlegada(), ruta.getKilometros());

        return  rutaEntity;
    }

    private VehiculoEntity vehiculo(Ruta ruta) {
        if (ruta.getVehiculo() == null || ruta.getVehiculo().getId() == null) {
            return null;
        }
        return vehiculoRepository.findById(ruta.getVehiculo().getId())
                .orElseThrow(() -> new IllegalArgumentException("Vehículo no encontrado con ID: " + ruta.getVehiculo().getId()));
    }

    /**
     * Odómetro de salida y llegada. Si están los dos, los kilómetros recorridos se calculan;
     * si no, se usan los kilómetros ingresados a mano.
     */
    public static void aplicarKilometraje(RutaEntity entity, Integer kmSalida, Integer kmLlegada, Integer kilometros) {
        if ((kmSalida != null && kmSalida < 0) || (kmLlegada != null && kmLlegada < 0) || (kilometros != null && kilometros < 0)) {
            throw new IllegalArgumentException("Los kilómetros no pueden ser negativos");
        }
        if (kmSalida != null && kmLlegada != null && kmLlegada < kmSalida) {
            throw new IllegalArgumentException("El kilometraje de llegada (" + kmLlegada
                    + ") no puede ser menor que el de salida (" + kmSalida + ")");
        }
        entity.setKmSalida(kmSalida);
        entity.setKmLlegada(kmLlegada);
        entity.setKilometros(kmSalida != null && kmLlegada != null ? Integer.valueOf(kmLlegada - kmSalida) : kilometros);
    }
}
