package com.transporte_gomez.erp.config;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import static com.transporte_gomez.erp.config.Roles.*;
import static org.springframework.http.HttpMethod.*;

/**
 * Tabla de permisos: qué roles pueden usar cada API.
 * La primera regla que calza gana, por eso las más específicas van primero.
 *
 * Resumen:
 *  - Administrador: todo.
 *  - Operaciones: todo lo de Bodega, más órdenes de servicio, rutas, entregas, destinos y establecimientos.
 *  - Bodega: ingresos, bultos, inventario, artículos, proveedores, devoluciones; crea órdenes desde un ingreso.
 *  - Operaciones además administra la flota (vehículos) y los costos de ruta.
 *  - Conductor: sus rutas y entregas (consultar, comenzar, registrar la recepción, finalizar); consulta vehículos.
 *  - Cliente: consulta; crea y habilita ingresos y usa el chat del ingreso.
 *  - Todos los usuarios con sesión: catálogos (comunas, tipos de servicio, unidades, bodegas) y su perfil.
 */
public final class Permisos {

    private static final String[] INTERNOS = {ADMINISTRADOR, OPERACIONES, BODEGA};
    private static final String[] INTERNOS_Y_CONDUCTOR = {ADMINISTRADOR, OPERACIONES, BODEGA, CONDUCTOR};
    private static final String[] TODOS = {ADMINISTRADOR, OPERACIONES, BODEGA, CONDUCTOR, CLIENTE};

    private Permisos() {
    }

    public static void aplicar(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry a) {
        // ---- Administración y cargas masivas ----
        a.requestMatchers("/api/admin/**", "/api/csv/**").hasRole(ADMINISTRADOR);
        a.requestMatchers(POST, "/api/escuelas/cargar/**", "/api/items/cargar", "/api/ordenes-servicios/cargar").hasRole(ADMINISTRADOR);

        // ---- Perfil propio y catálogos: cualquier usuario con sesión ----
        a.requestMatchers("/api/usuarios/**").authenticated();
        a.requestMatchers(GET, "/api/comunas", "/api/servicios-tipos", "/api/unidad-medida/**", "/api/categorias/**",
                "/api/documentos-tipos/**", "/api/bodegas/**", "/api/ordenes-servicio-categorias/**").authenticated();

        // ---- Comercial: clientes y contratos (los consultan todos; los administra el Administrador) ----
        a.requestMatchers(GET, "/api/clientes/**", "/api/contratos/**").hasAnyRole(TODOS);
        a.requestMatchers("/api/clientes/**", "/api/contratos/**").hasRole(ADMINISTRADOR);

        // ---- Destinos y establecimientos ----
        a.requestMatchers(GET, "/api/destinos/**", "/api/escuelas/**").hasAnyRole(TODOS);
        a.requestMatchers("/api/destinos/**", "/api/escuelas/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);

        // ---- Proveedores ----
        a.requestMatchers(POST, "/api/proveedores/*/fusionar").hasRole(ADMINISTRADOR);
        a.requestMatchers(GET, "/api/proveedores/**").hasAnyRole(TODOS);
        a.requestMatchers("/api/proveedores/**").hasAnyRole(ADMINISTRADOR, OPERACIONES, BODEGA);

        // ---- Bodega: artículos, marcas, saldos, bodegas ----
        a.requestMatchers(GET, "/api/items/**", "/api/marcas/**", "/api/saldo-bodega/**").hasAnyRole(TODOS);
        a.requestMatchers("/api/items/**", "/api/marcas/**", "/api/saldo-bodega/**", "/api/bodegas/**").hasAnyRole(ADMINISTRADOR, OPERACIONES, BODEGA);
        a.requestMatchers("/api/ordenes-servicio-categorias/**").hasRole(ADMINISTRADOR);

        // ---- Ingresos y bultos (el Cliente crea, habilita y conversa sus ingresos) ----
        a.requestMatchers("/api/ingresos/*/bultos", "/api/bultos/**").hasAnyRole(INTERNOS);
        a.requestMatchers(GET, "/api/ingresos/**").hasAnyRole(ADMINISTRADOR, OPERACIONES, BODEGA, CLIENTE);
        a.requestMatchers(PUT, "/api/ingresos/*/cerrar", "/api/ingresos/*/abrir").hasAnyRole(ADMINISTRADOR, OPERACIONES, BODEGA);
        a.requestMatchers("/api/ingresos/**").hasAnyRole(ADMINISTRADOR, OPERACIONES, BODEGA, CLIENTE);

        // ---- Devoluciones y documentos ----
        a.requestMatchers("/api/devoluciones/**", "/api/documentos/**").hasAnyRole(INTERNOS);

        // ---- Órdenes de servicio ----
        a.requestMatchers(GET, "/api/ordenes-servicios/**").hasAnyRole(TODOS);
        a.requestMatchers(POST, "/api/ordenes-servicios/*/pdf").hasAnyRole(TODOS);
        a.requestMatchers(POST, "/api/ordenes-servicios/*/imagenes").hasAnyRole(INTERNOS_Y_CONDUCTOR);
        a.requestMatchers("/api/ordenes-servicios/**").hasAnyRole(INTERNOS);

        // ---- Comercial (fase 4): tarifas, cotizaciones y mensajes de la landing ----
        a.requestMatchers(GET, "/api/tarifas/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);
        a.requestMatchers("/api/tarifas/**").hasRole(ADMINISTRADOR);
        a.requestMatchers("/api/cotizaciones/**", "/api/mensajes-contacto/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);

        // ---- Flota y costos de ruta (fase 2) ----
        a.requestMatchers(GET, "/api/vehiculos/**").hasAnyRole(ADMINISTRADOR, OPERACIONES, CONDUCTOR);
        a.requestMatchers("/api/vehiculos/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);
        // El Conductor consulta y registra los costos de su ruta desde la app (combustible, peajes, lancha)
        a.requestMatchers(GET, "/api/rutas/*/costos").hasAnyRole(ADMINISTRADOR, OPERACIONES, CONDUCTOR);
        a.requestMatchers(POST, "/api/rutas/*/costos").hasAnyRole(ADMINISTRADOR, OPERACIONES, CONDUCTOR);
        a.requestMatchers("/api/rutas/*/costos/**", "/api/costos-rutas/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);

        // ---- Rutas: el Conductor ve y ejecuta las suyas ----
        a.requestMatchers(GET, "/api/rutas/**").hasAnyRole(ADMINISTRADOR, OPERACIONES, CONDUCTOR);
        a.requestMatchers(PUT, "/api/rutas/*/comenzar", "/api/rutas/*/kilometros", "/api/rutas/*/finalizar")
                .hasAnyRole(ADMINISTRADOR, OPERACIONES, CONDUCTOR);
        a.requestMatchers("/api/rutas/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);

        // ---- Entregas: consulta y reportes para todos; el Conductor registra la recepción ----
        a.requestMatchers(GET, "/api/entregas/**").hasAnyRole(TODOS);
        a.requestMatchers(POST, "/api/entregas/excel").hasAnyRole(TODOS);
        a.requestMatchers(PUT, "/api/entregas/*/recepcionado").hasAnyRole(ADMINISTRADOR, OPERACIONES, CONDUCTOR);
        a.requestMatchers(POST, "/api/entregas/*/no-entregado", "/api/entregas/*/proximidad").hasAnyRole(ADMINISTRADOR, OPERACIONES, CONDUCTOR);
        a.requestMatchers("/api/entregas/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);

        // ---- Imágenes (fotos de OS y documentos) ----
        a.requestMatchers(GET, "/api/imagenes/**").hasAnyRole(TODOS);
        a.requestMatchers("/api/imagenes/**").hasAnyRole(ADMINISTRADOR, OPERACIONES);
    }
}
