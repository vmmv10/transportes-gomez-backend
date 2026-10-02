package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.dto.Cotizacion;
import com.transporte_gomez.erp.dto.CotizacionFiltro;
import com.transporte_gomez.erp.dto.SolicitudPublica;
import com.transporte_gomez.erp.dto.TarifaSugerida;
import com.transporte_gomez.erp.entity.CotizacionEntity;
import com.transporte_gomez.erp.entity.ServicioTipoEntity;
import com.transporte_gomez.erp.enums.CotizacionCanal;
import com.transporte_gomez.erp.enums.CotizacionEstado;
import com.transporte_gomez.erp.repository.CotizacionRepository;
import com.transporte_gomez.erp.repository.ServicioTipoRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CotizacionService {

    private static final ZoneId CHILE = ZoneId.of("America/Santiago");
    private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    /** slug de la landing -> código de servicios_tipos */
    private static final Map<String, String> SERVICIOS_LANDING = Map.of(
            "terrestre", "CARGA_TER",
            "maritimo", "CARGA_MAR",
            "almacenaje", "ALMAC");

    private final CotizacionRepository cotizacionRepository;
    private final ServicioTipoRepository servicioTipoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TarifaService tarifaService;
    private final ComercialReferencias ref;
    private final NotificacionCorreoService notificaciones;

    /** Solicitud desde la landing. Devuelve el código para mostrárselo al visitante. */
    @Transactional
    public String crearDesdeLanding(SolicitudPublica s, String ip) {
        String nombre = obligatorio(s.getNombre(), "Ingresa tu nombre", 200);
        String email = obligatorio(s.getEmail(), "Ingresa tu correo", 200);
        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Ingresa un correo válido");
        }
        CotizacionEntity e = new CotizacionEntity();
        e.setCanal(CotizacionCanal.WEB);
        e.setEstado(CotizacionEstado.NUEVA);
        e.setNombre(nombre);
        e.setEmail(email);
        e.setEmpresa(opcional(s.getEmpresa(), 200));
        e.setTelefono(opcional(s.getTelefono(), 40));
        e.setServicioTexto(opcional(s.getServicioTexto(), 100));
        String codigo = s.getServicioCodigo() != null && !s.getServicioCodigo().isBlank()
                ? s.getServicioCodigo().trim().toUpperCase()
                : s.getServicio() != null ? SERVICIOS_LANDING.get(s.getServicio().trim().toLowerCase()) : null;
        e.setServicioTipo(codigo != null ? servicioTipoRepository.findByCodigo(codigo).filter(t -> Boolean.TRUE.equals(t.getActivo())).orElse(null) : null);
        if (e.getServicioTipo() != null && e.getServicioTexto() == null) {
            e.setServicioTexto(e.getServicioTipo().getNombre());
        }
        e.setComunaOrigen(comunaActiva(s.getComunaOrigenId()));
        e.setComunaDestino(comunaActiva(s.getComunaDestinoId()));
        e.setOrigen(opcional(s.getOrigen(), 200));
        e.setDestino(opcional(s.getDestino(), 200));
        if (e.getOrigen() == null && e.getComunaOrigen() == null) {
            throw new IllegalArgumentException("Indica el origen de la carga");
        }
        if (e.getDestino() == null && e.getComunaDestino() == null) {
            throw new IllegalArgumentException("Indica el destino de la carga");
        }
        BigDecimal[] origen = punto(s.getOrigenLatitud(), s.getOrigenLongitud());
        e.setOrigenLatitud(origen[0]);
        e.setOrigenLongitud(origen[1]);
        BigDecimal[] destino = punto(s.getDestinoLatitud(), s.getDestinoLongitud());
        e.setDestinoLatitud(destino[0]);
        e.setDestinoLongitud(destino[1]);
        e.setTipoCarga(opcional(s.getTipoCarga(), 200));
        e.setPesoKg(s.getPesoKg() != null && s.getPesoKg().signum() >= 0 ? s.getPesoKg() : null);
        e.setMensaje(opcional(s.getMensaje(), 2000));
        e.setIp(ip);
        CotizacionEntity guardada = guardarNueva(e);
        notificaciones.cotizacionRecibida(guardada);
        return guardada.getCodigo();
    }

    /** Comuna enviada por la landing: si no existe o está inactiva se ignora (no se rechaza la solicitud). */
    private com.transporte_gomez.erp.entity.ComunaEntity comunaActiva(Integer id) {
        if (id == null) {
            return null;
        }
        try {
            com.transporte_gomez.erp.entity.ComunaEntity c = ref.comuna(id);
            return Boolean.TRUE.equals(c.getActivo()) ? c : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    /** Punto del mapa: solo se acepta si ambas coordenadas vienen y están dentro de Chile continental e insular. */
    private static BigDecimal[] punto(BigDecimal lat, BigDecimal lng) {
        if (lat == null || lng == null) {
            return new BigDecimal[]{null, null};
        }
        double la = lat.doubleValue();
        double lo = lng.doubleValue();
        if (la < -56 || la > -17 || lo < -110 || lo > -66) {
            return new BigDecimal[]{null, null};
        }
        return new BigDecimal[]{lat.setScale(7, java.math.RoundingMode.HALF_UP), lng.setScale(7, java.math.RoundingMode.HALF_UP)};
    }

    @Transactional(readOnly = true)
    public Page<Cotizacion> getAll(Pageable pageable, CotizacionFiltro filtro) {
        return cotizacionRepository.findAll(filtros(filtro), pageable).map(this::toDto);
    }

    /** Solicitudes por atender (para el aviso del menú). */
    @Transactional(readOnly = true)
    public long pendientes() {
        return cotizacionRepository.countByEstadoIn(List.of(CotizacionEstado.NUEVA, CotizacionEstado.EN_REVISION));
    }

    @Transactional(readOnly = true)
    public Cotizacion getById(Long id) {
        return toDto(buscar(id));
    }

    /** Cotización ingresada por el equipo (ej. pedida por teléfono). */
    @Transactional
    public Cotizacion crearInterna(Cotizacion dto) {
        CotizacionEntity e = new CotizacionEntity();
        e.setCanal(CotizacionCanal.INTERNA);
        e.setEstado(CotizacionEstado.EN_REVISION);
        aplicar(dto, e);
        return toDto(guardarNueva(e));
    }

    @Transactional
    public Cotizacion update(Long id, Cotizacion dto) {
        CotizacionEntity e = buscar(id);
        aplicar(dto, e);
        if (e.getEstado() == CotizacionEstado.NUEVA) {
            e.setEstado(CotizacionEstado.EN_REVISION);
        }
        e.setFechaActualizacion(OffsetDateTime.now());
        return toDto(cotizacionRepository.save(e));
    }

    @Transactional
    public Cotizacion cambiarEstado(Long id, CotizacionEstado estado) {
        CotizacionEntity e = buscar(id);
        if (estado == null) {
            throw new IllegalArgumentException("Indica el estado");
        }
        if ((estado == CotizacionEstado.ENVIADA || estado == CotizacionEstado.ACEPTADA) && e.getMonto() == null) {
            throw new IllegalArgumentException("Antes de enviar o aceptar la cotización, registra el monto");
        }
        if (estado == CotizacionEstado.ENVIADA && e.getValidaHasta() == null) {
            e.setValidaHasta(LocalDate.now(CHILE).plusDays(15));
        }
        e.setEstado(estado);
        e.setUsuario(usuarioActual().orElse(e.getUsuario()));
        e.setFechaActualizacion(OffsetDateTime.now());
        return toDto(cotizacionRepository.save(e));
    }

    /** ¿Está configurado el envío de correos? (para mostrar el botón en el ERP) */
    public boolean correoHabilitado() {
        return notificaciones.habilitado();
    }

    /** Envía la cotización por correo al solicitante y la deja como ENVIADA. */
    @Transactional
    public Cotizacion enviarPorCorreo(Long id, String mensaje) {
        if (!notificaciones.habilitado()) {
            throw new IllegalStateException("El envío de correos no está configurado en el servidor");
        }
        CotizacionEntity e = buscar(id);
        if (e.getMonto() == null) {
            throw new IllegalArgumentException("Antes de enviar la cotización, registra el monto");
        }
        if (e.getEstado() == CotizacionEstado.RECHAZADA || e.getEstado() == CotizacionEstado.DESCARTADA) {
            throw new IllegalArgumentException("La cotización está " + e.getEstado().name().toLowerCase() + ", no se puede enviar");
        }
        if (e.getValidaHasta() == null) {
            e.setValidaHasta(LocalDate.now(CHILE).plusDays(15));
        }
        notificaciones.enviarCotizacion(e, opcional(mensaje, 2000));
        if (e.getEstado() != CotizacionEstado.ACEPTADA) {
            e.setEstado(CotizacionEstado.ENVIADA);
        }
        e.setUsuario(usuarioActual().orElse(e.getUsuario()));
        e.setFechaActualizacion(OffsetDateTime.now());
        return toDto(cotizacionRepository.save(e));
    }

    /** Monto sugerido según las tarifas vigentes. */
    @Transactional(readOnly = true)
    public TarifaSugerida sugerir(Long id) {
        CotizacionEntity e = buscar(id);
        return tarifaService.sugerir(
                e.getCliente() != null ? e.getCliente().getId() : null,
                e.getServicioTipo() != null ? e.getServicioTipo().getId() : null,
                e.getComunaOrigen() != null ? e.getComunaOrigen().getId() : null,
                e.getComunaDestino() != null ? e.getComunaDestino().getId() : null,
                e.getUnidadMedida() != null ? e.getUnidadMedida().getId() : null,
                e.getCantidad());
    }

    private CotizacionEntity guardarNueva(CotizacionEntity e) {
        e.setCodigo("TMP-" + UUID.randomUUID().toString().substring(0, 12));
        e.setFechaCreacion(OffsetDateTime.now());
        CotizacionEntity guardada = cotizacionRepository.save(e);
        guardada.setCodigo(String.format("COT-%d-%05d", LocalDate.now(CHILE).getYear(), guardada.getId()));
        return cotizacionRepository.save(guardada);
    }

    private void aplicar(Cotizacion d, CotizacionEntity e) {
        e.setNombre(obligatorio(d.getNombre(), "El nombre del solicitante es obligatorio", 200));
        String email = obligatorio(d.getEmail(), "El correo del solicitante es obligatorio", 200);
        if (!EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("El correo no es válido");
        }
        e.setEmail(email);
        e.setEmpresa(opcional(d.getEmpresa(), 200));
        e.setTelefono(opcional(d.getTelefono(), 40));
        e.setCliente(ref.cliente(d.getClienteId()));
        ServicioTipoEntity servicio = ref.servicio(d.getServicioTipoId());
        e.setServicioTipo(servicio);
        if (servicio != null && e.getServicioTexto() == null) {
            e.setServicioTexto(servicio.getNombre());
        }
        e.setOrigen(opcional(d.getOrigen(), 200));
        e.setDestino(opcional(d.getDestino(), 200));
        e.setComunaOrigen(ref.comuna(d.getComunaOrigenId()));
        e.setComunaDestino(ref.comuna(d.getComunaDestinoId()));
        e.setTipoCarga(opcional(d.getTipoCarga(), 200));
        e.setPesoKg(d.getPesoKg());
        e.setCantidad(d.getCantidad());
        e.setUnidadMedida(ref.unidad(d.getUnidadMedidaId()));
        e.setMensaje(opcional(d.getMensaje(), 2000));
        if (d.getMonto() != null && d.getMonto().signum() < 0) {
            throw new IllegalArgumentException("El monto no puede ser negativo");
        }
        e.setMonto(d.getMonto() != null ? d.getMonto().setScale(0, java.math.RoundingMode.HALF_UP) : null);
        e.setTarifa(d.getTarifaId() != null ? tarifaService.buscar(d.getTarifaId()) : null);
        e.setValidaHasta(d.getValidaHasta());
        e.setObservaciones(opcional(d.getObservaciones(), 2000));
        usuarioActual().ifPresent(e::setUsuario);
    }

    private Optional<com.transporte_gomez.erp.entity.UsuarioEntity> usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth == null || auth.getName() == null ? Optional.empty() : usuarioRepository.findByAuth0Id(auth.getName());
    }

    private CotizacionEntity buscar(Long id) {
        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Cotización no encontrada con ID: " + id));
    }

    private static String obligatorio(String valor, String mensaje, int max) {
        String t = ComercialReferencias.texto(valor);
        if (t == null) {
            throw new IllegalArgumentException(mensaje);
        }
        return t.length() > max ? t.substring(0, max) : t;
    }

    private static String opcional(String valor, int max) {
        String t = ComercialReferencias.texto(valor);
        return t == null ? null : t.length() > max ? t.substring(0, max) : t;
    }

    Cotizacion toDto(CotizacionEntity e) {
        Cotizacion d = new Cotizacion();
        d.setId(e.getId());
        d.setCodigo(e.getCodigo());
        d.setCanal(e.getCanal());
        d.setEstado(e.getEstado());
        d.setNombre(e.getNombre());
        d.setEmpresa(e.getEmpresa());
        d.setEmail(e.getEmail());
        d.setTelefono(e.getTelefono());
        if (e.getCliente() != null) {
            d.setClienteId(e.getCliente().getId());
            d.setClienteNombre(ComercialReferencias.nombreCliente(e.getCliente()));
        }
        if (e.getServicioTipo() != null) {
            d.setServicioTipoId(e.getServicioTipo().getId());
            d.setServicioTipoNombre(e.getServicioTipo().getNombre());
        }
        d.setServicioTexto(e.getServicioTexto());
        d.setOrigen(e.getOrigen());
        d.setDestino(e.getDestino());
        d.setOrigenLatitud(e.getOrigenLatitud());
        d.setOrigenLongitud(e.getOrigenLongitud());
        d.setDestinoLatitud(e.getDestinoLatitud());
        d.setDestinoLongitud(e.getDestinoLongitud());
        if (e.getComunaOrigen() != null) {
            d.setComunaOrigenId(e.getComunaOrigen().getId());
            d.setComunaOrigenNombre(e.getComunaOrigen().getNombre());
        }
        if (e.getComunaDestino() != null) {
            d.setComunaDestinoId(e.getComunaDestino().getId());
            d.setComunaDestinoNombre(e.getComunaDestino().getNombre());
        }
        d.setTipoCarga(e.getTipoCarga());
        d.setPesoKg(e.getPesoKg());
        d.setCantidad(e.getCantidad());
        if (e.getUnidadMedida() != null) {
            d.setUnidadMedidaId(e.getUnidadMedida().getId());
            d.setUnidadMedidaNombre(e.getUnidadMedida().getNombre());
        }
        d.setMensaje(e.getMensaje());
        d.setMonto(e.getMonto());
        d.setTarifaId(e.getTarifa() != null ? e.getTarifa().getId() : null);
        d.setValidaHasta(e.getValidaHasta());
        d.setObservaciones(e.getObservaciones());
        d.setUsuarioNombre(com.transporte_gomez.erp.adapter.RutaCostoAdapter.nombre(e.getUsuario()));
        d.setFechaCreacion(e.getFechaCreacion());
        d.setFechaActualizacion(e.getFechaActualizacion());
        return d;
    }

    private static Specification<CotizacionEntity> filtros(CotizacionFiltro f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();
            if (f.getEstado() != null) {
                p.add(cb.equal(root.get("estado"), f.getEstado()));
            }
            if (Boolean.TRUE.equals(f.getPendientes())) {
                p.add(root.get("estado").in(CotizacionEstado.NUEVA, CotizacionEstado.EN_REVISION));
            }
            if (f.getTexto() != null && !f.getTexto().isBlank()) {
                String t = "%" + f.getTexto().trim().toLowerCase() + "%";
                p.add(cb.or(
                        cb.like(cb.lower(root.get("codigo")), t),
                        cb.like(cb.lower(root.get("nombre")), t),
                        cb.like(cb.lower(cb.coalesce(root.<String>get("empresa"), "")), t),
                        cb.like(cb.lower(root.get("email")), t)));
            }
            if (f.getDesde() != null && !f.getDesde().isBlank()) {
                p.add(cb.greaterThanOrEqualTo(root.get("fechaCreacion"),
                        LocalDate.parse(f.getDesde().substring(0, 10)).atStartOfDay(CHILE).toOffsetDateTime()));
            }
            if (f.getHasta() != null && !f.getHasta().isBlank()) {
                p.add(cb.lessThan(root.get("fechaCreacion"),
                        LocalDate.parse(f.getHasta().substring(0, 10)).plusDays(1).atStartOfDay(CHILE).toOffsetDateTime()));
            }
            return cb.and(p.toArray(Predicate[]::new));
        };
    }
}
