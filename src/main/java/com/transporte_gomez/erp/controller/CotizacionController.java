package com.transporte_gomez.erp.controller;

import com.transporte_gomez.erp.dto.Cotizacion;
import com.transporte_gomez.erp.dto.CotizacionFiltro;
import com.transporte_gomez.erp.dto.MensajeContacto;
import com.transporte_gomez.erp.dto.TarifaSugerida;
import com.transporte_gomez.erp.enums.CotizacionEstado;
import com.transporte_gomez.erp.services.CotizacionService;
import com.transporte_gomez.erp.services.MensajeContactoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final MensajeContactoService mensajeContactoService;

    @GetMapping("/api/cotizaciones")
    public Page<Cotizacion> getAll(CotizacionFiltro filtro, Pageable pageable) {
        return cotizacionService.getAll(pageable, filtro);
    }

    /** Cantidad por atender, para el aviso del menú. */
    @GetMapping("/api/cotizaciones/pendientes")
    public Map<String, Long> pendientes() {
        return Map.of("cotizaciones", cotizacionService.pendientes(), "mensajes", mensajeContactoService.pendientes());
    }

    /** Si el servidor tiene configurado el envío de correos. */
    @GetMapping("/api/cotizaciones/correo")
    public Map<String, Boolean> correo() {
        return Map.of("habilitado", cotizacionService.correoHabilitado());
    }

    @GetMapping("/api/cotizaciones/{id}")
    public Cotizacion getById(@PathVariable Long id) {
        return cotizacionService.getById(id);
    }

    @PostMapping("/api/cotizaciones")
    public Cotizacion create(@RequestBody Cotizacion cotizacion) {
        return cotizacionService.crearInterna(cotizacion);
    }

    @PutMapping("/api/cotizaciones/{id}")
    public Cotizacion update(@PathVariable Long id, @RequestBody Cotizacion cotizacion) {
        return cotizacionService.update(id, cotizacion);
    }

    @PutMapping("/api/cotizaciones/{id}/estado")
    public Cotizacion cambiarEstado(@PathVariable Long id, @RequestParam CotizacionEstado estado) {
        return cotizacionService.cambiarEstado(id, estado);
    }

    /** Envía la cotización por correo al solicitante. Body opcional: {"mensaje": "..."} */
    @PostMapping("/api/cotizaciones/{id}/enviar-correo")
    public Cotizacion enviarCorreo(@PathVariable Long id, @RequestBody(required = false) Map<String, String> body) {
        return cotizacionService.enviarPorCorreo(id, body != null ? body.get("mensaje") : null);
    }

    @GetMapping("/api/cotizaciones/{id}/sugerir")
    public TarifaSugerida sugerir(@PathVariable Long id) {
        return cotizacionService.sugerir(id);
    }

    @GetMapping("/api/mensajes-contacto")
    public Page<MensajeContacto> mensajes(@RequestParam(required = false) Boolean atendido,
                                          @RequestParam(required = false) com.transporte_gomez.erp.enums.MensajeMotivo motivo,
                                          Pageable pageable) {
        return mensajeContactoService.getAll(atendido, motivo, pageable);
    }

    @PutMapping("/api/mensajes-contacto/{id}/atendido")
    public void marcarAtendido(@PathVariable Long id, @RequestParam(defaultValue = "true") boolean valor) {
        mensajeContactoService.marcarAtendido(id, valor);
    }
}
