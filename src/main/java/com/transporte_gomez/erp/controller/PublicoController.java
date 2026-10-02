package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.config.ProteccionPublica;
import com.transporte_gomez.erp.dto.SeguimientoPublico;
import com.transporte_gomez.erp.dto.SolicitudPublica;
import com.transporte_gomez.erp.repository.ServicioTipoRepository;
import com.transporte_gomez.erp.services.CotizacionService;
import com.transporte_gomez.erp.services.MensajeContactoService;
import com.transporte_gomez.erp.services.SeguimientoPublicoService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Endpoints sin sesión para la landing (www): cotizar, contactar y seguir un envío.
 * Protegidos con límite por IP, campo trampa y captcha (si está configurado).
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/public")
public class PublicoController {

    private final ProteccionPublica proteccion;
    private final CotizacionService cotizacionService;
    private final MensajeContactoService mensajeContactoService;
    private final SeguimientoPublicoService seguimientoPublicoService;
    private final ServicioTipoRepository servicioTipoRepository;
    private final com.transporte_gomez.erp.repository.ComunaRepository comunaRepository;
    private final com.transporte_gomez.erp.services.CoberturaPublicaService coberturaPublicaService;

    /** Tipos de servicio activos (para el formulario de cotización). */
    @GetMapping("/servicios")
    public List<Map<String, Object>> servicios() {
        return servicioTipoRepository.findByActivoTrueOrderByIdAsc().stream()
                .map(s -> Map.<String, Object>of("codigo", s.getCodigo(), "nombre", s.getNombre()))
                .toList();
    }

    /** Comunas activas (para elegir origen y destino en el formulario de cotización). */
    @GetMapping("/comunas")
    public List<Map<String, Object>> comunas() {
        return comunaRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(c -> Map.<String, Object>of("id", c.getId(), "nombre", c.getNombre()))
                .toList();
    }

    /** Cifras de cobertura (entregas por comuna) para la página de cobertura. Se recalculan cada hora. */
    @GetMapping("/cobertura")
    public ResponseEntity<com.transporte_gomez.erp.dto.CoberturaPublica> cobertura() {
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.maxAge(java.time.Duration.ofMinutes(30)).cachePublic())
                .body(coberturaPublicaService.obtener());
    }

    /** Destinos con entregas (comuna, ubicación y total, sin nombres) para el mapa de cobertura. Se recalculan cada hora. */
    @GetMapping("/cobertura/puntos")
    public ResponseEntity<List<com.transporte_gomez.erp.dto.CoberturaPublica.Punto>> coberturaPuntos() {
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.maxAge(java.time.Duration.ofMinutes(30)).cachePublic())
                .body(coberturaPublicaService.puntos());
    }

    /** Flota activa por tipo de vehículo (sin patentes ni nombres), para la portada. Se recalcula cada hora. */
    @GetMapping("/flota")
    public ResponseEntity<List<com.transporte_gomez.erp.dto.CoberturaPublica.FlotaTipo>> flota() {
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.maxAge(java.time.Duration.ofMinutes(30)).cachePublic())
                .body(coberturaPublicaService.flota());
    }

    /** Solicitud de cotización. Responde el código (ej. COT-2026-00012) para que el visitante lo guarde. */
    @PostMapping("/cotizaciones")
    public Map<String, Object> cotizar(@RequestBody SolicitudPublica solicitud, HttpServletRequest request) {
        String ip = proteccion.ip(request);
        if (esRobot(solicitud, ip)) {
            return Map.of("ok", true);
        }
        proteccion.limitar("cotizacion", ip, 5, Duration.ofMinutes(10));
        proteccion.verificarCaptcha(solicitud.getCaptcha(), ip);
        String codigo = cotizacionService.crearDesdeLanding(solicitud, ip);
        Map<String, Object> respuesta = new HashMap<>();
        respuesta.put("ok", true);
        respuesta.put("codigo", codigo);
        return respuesta;
    }

    @PostMapping("/contacto")
    public Map<String, Object> contacto(@RequestBody SolicitudPublica solicitud, HttpServletRequest request) {
        String ip = proteccion.ip(request);
        if (esRobot(solicitud, ip)) {
            return Map.of("ok", true);
        }
        proteccion.limitar("contacto", ip, 5, Duration.ofMinutes(10));
        proteccion.verificarCaptcha(solicitud.getCaptcha(), ip);
        mensajeContactoService.crearDesdeLanding(solicitud, ip);
        return Map.of("ok", true);
    }

    /** Seguimiento con el código de la orden. 404 si no existe (sin decir nada más). */
    @GetMapping("/seguimiento/{codigo}")
    public ResponseEntity<SeguimientoPublico> seguimiento(@PathVariable String codigo, HttpServletRequest request) {
        proteccion.limitar("seguimiento", proteccion.ip(request), 30, Duration.ofMinutes(5));
        return seguimientoPublicoService.buscar(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Campo trampa con texto: se responde "ok" sin guardar nada. */
    private boolean esRobot(SolicitudPublica solicitud, String ip) {
        if (solicitud.getSitioWeb() != null && !solicitud.getSitioWeb().isBlank()) {
            log.warn("Solicitud descartada por campo trampa desde {}", ip);
            return true;
        }
        return false;
    }
}
