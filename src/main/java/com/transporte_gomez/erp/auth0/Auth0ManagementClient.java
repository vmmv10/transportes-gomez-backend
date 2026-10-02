package com.transporte_gomez.erp.auth0;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Cliente de la Auth0 Management API (crear usuarios, roles, bloqueo, contraseña).
 *
 * Usa una aplicación "Machine to Machine" de Auth0 autorizada para la Management API.
 * Las credenciales se leen de variables de entorno (ver application.properties):
 * AUTH0_MGMT_CLIENT_ID y AUTH0_MGMT_CLIENT_SECRET.
 */
@Slf4j
@Component
public class Auth0ManagementClient {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA = new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA = new ParameterizedTypeReference<>() {};

    private final String dominio;
    private final String clientId;
    private final String clientSecret;
    private final String conexion;
    private final String appClientId;
    private final RestClient http;

    private String token;
    private Instant tokenVence = Instant.EPOCH;

    public Auth0ManagementClient(@Value("${auth0.domain}") String dominio,
                                 @Value("${auth0.mgmt.client-id:}") String clientId,
                                 @Value("${auth0.mgmt.client-secret:}") String clientSecret,
                                 @Value("${auth0.connection:Username-Password-Authentication}") String conexion,
                                 @Value("${auth0.app.client-id:}") String appClientId) {
        this.dominio = dominio;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.conexion = conexion;
        this.appClientId = appClientId;
        this.http = RestClient.builder().baseUrl("https://" + dominio).build();
    }

    public boolean configurado() {
        return !clientId.isBlank() && !clientSecret.isBlank();
    }

    // ------------------------------------------------------------------ token

    private synchronized String token() {
        if (!configurado()) {
            throw new IllegalStateException("Falta configurar AUTH0_MGMT_CLIENT_ID y AUTH0_MGMT_CLIENT_SECRET en el backend");
        }
        if (token != null && Instant.now().isBefore(tokenVence)) {
            return token;
        }
        Map<String, Object> respuesta;
        try {
            respuesta = http.post()
                    .uri("/oauth/token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of(
                            "grant_type", "client_credentials",
                            "client_id", clientId,
                            "client_secret", clientSecret,
                            "audience", "https://" + dominio + "/api/v2/"))
                    .retrieve()
                    .body(MAPA);
        } catch (RestClientResponseException e) {
            log.error("Auth0: no se pudo obtener el token de la Management API: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == 401) {
                throw new IllegalStateException("Auth0 rechazó las credenciales del backend: revisa AUTH0_MGMT_CLIENT_ID y AUTH0_MGMT_CLIENT_SECRET");
            }
            if (e.getStatusCode().value() == 403) {
                throw new IllegalStateException("La aplicación Machine to Machine no está autorizada para la Auth0 Management API "
                        + "(Applications → APIs → Auth0 Management API → Machine to Machine Applications)");
            }
            throw new IllegalStateException("Auth0 no entregó el token de la Management API (" + e.getStatusCode().value() + ")");
        }
        token = (String) respuesta.get("access_token");
        long segundos = ((Number) respuesta.getOrDefault("expires_in", 3600)).longValue();
        tokenVence = Instant.now().plusSeconds(Math.max(60, segundos - 120));
        return token;
    }

    private RestClient.RequestHeadersSpec<?> conToken(RestClient.RequestHeadersSpec<?> spec) {
        return spec.header("Authorization", "Bearer " + token());
    }

    // ------------------------------------------------------------------ usuarios

    /** Todos los usuarios de la conexión (la empresa tiene pocos: se piden de a 100). */
    public List<Map<String, Object>> listarUsuarios() {
        List<Map<String, Object>> todos = new ArrayList<>();
        for (int pagina = 0; pagina < 50; pagina++) {
            final int p = pagina;
            List<Map<String, Object>> lote = llamar("leer los usuarios (permiso read:users)", () -> conToken(http.get().uri(u -> u.path("/api/v2/users")
                    .queryParam("per_page", 100)
                    .queryParam("page", p)
                    .queryParam("fields", "user_id,email,name,given_name,family_name,blocked,last_login,created_at,logins_count")
                    .queryParam("include_fields", true)
                    .build()))
                    .retrieve().body(LISTA));
            if (lote == null || lote.isEmpty()) {
                break;
            }
            todos.addAll(lote);
            if (lote.size() < 100) {
                break;
            }
        }
        return todos;
    }

    public Map<String, Object> obtenerUsuario(String userId) {
        return llamar("leer el usuario (permiso read:users)", () -> conToken(http.get().uri("/api/v2/users/{id}", userId)).retrieve().body(MAPA));
    }

    /** Crea el usuario con una contraseña aleatoria: la persona define la suya con el correo de cambio de contraseña. */
    public Map<String, Object> crearUsuario(String email, String nombre, String apellidos) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("connection", conexion);
        cuerpo.put("email", email);
        cuerpo.put("password", claveAleatoria());
        // Verificado: lo crea un administrador y la persona solo puede definir su contraseña
        // desde el correo. La Action "AddRolesToToken" niega el acceso a correos sin verificar.
        cuerpo.put("email_verified", true);
        cuerpo.put("verify_email", false);
        String completo = ((nombre == null ? "" : nombre) + " " + (apellidos == null ? "" : apellidos)).trim();
        if (!completo.isBlank()) {
            cuerpo.put("name", completo);
        }
        if (nombre != null && !nombre.isBlank()) {
            cuerpo.put("given_name", nombre.trim());
        }
        if (apellidos != null && !apellidos.isBlank()) {
            cuerpo.put("family_name", apellidos.trim());
        }
        try {
            return conToken(http.post().uri("/api/v2/users").contentType(MediaType.APPLICATION_JSON).body(cuerpo))
                    .retrieve().body(MAPA);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().value() == 409) {
                throw new IllegalArgumentException("Ya existe un usuario con el correo " + email);
            }
            throw error("crear el usuario", e);
        }
    }

    public void actualizarNombre(String userId, String nombre, String apellidos) {
        Map<String, Object> cuerpo = new HashMap<>();
        String completo = ((nombre == null ? "" : nombre) + " " + (apellidos == null ? "" : apellidos)).trim();
        if (!completo.isBlank()) {
            cuerpo.put("name", completo);
        }
        cuerpo.put("given_name", nombre == null || nombre.isBlank() ? null : nombre.trim());
        cuerpo.put("family_name", apellidos == null || apellidos.isBlank() ? null : apellidos.trim());
        patch(userId, cuerpo, "actualizar el nombre");
    }

    public void bloquear(String userId, boolean bloqueado) {
        patch(userId, Map.of("blocked", bloqueado), bloqueado ? "bloquear el usuario" : "desbloquear el usuario");
    }

    private void patch(String userId, Map<String, Object> cuerpo, String accion) {
        try {
            conToken(http.patch().uri("/api/v2/users/{id}", userId).contentType(MediaType.APPLICATION_JSON).body(cuerpo))
                    .retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw error(accion, e);
        }
    }

    /** Auth0 envía al usuario el correo para crear o cambiar su contraseña. */
    public void enviarCorreoClave(String email) {
        if (appClientId.isBlank()) {
            throw new IllegalStateException("Falta configurar auth0.app.client-id (client id de la aplicación web) en el backend");
        }
        try {
            http.post().uri("/dbconnections/change_password")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Map.of("client_id", appClientId, "email", email, "connection", conexion))
                    .retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw error("enviar el correo de contraseña", e);
        }
    }

    // ------------------------------------------------------------------ roles

    /** Roles definidos en Auth0: id y nombre. */
    public List<Map<String, Object>> listarRoles() {
        return llamar("leer los roles (permiso read:roles)", () -> conToken(http.get().uri("/api/v2/roles?per_page=100")).retrieve().body(LISTA));
    }

    /** user_id de los usuarios que tienen un rol. */
    public List<String> usuariosDeRol(String roleId) {
        List<String> ids = new ArrayList<>();
        for (int pagina = 0; pagina < 50; pagina++) {
            final int p = pagina;
            List<Map<String, Object>> lote = llamar("leer los usuarios de cada rol (permiso read:role_members)", () -> conToken(http.get().uri(u -> u.path("/api/v2/roles/{id}/users")
                    .queryParam("per_page", 100)
                    .queryParam("page", p)
                    .build(roleId)))
                    .retrieve().body(LISTA));
            if (lote == null || lote.isEmpty()) {
                break;
            }
            lote.forEach(u -> ids.add((String) u.get("user_id")));
            if (lote.size() < 100) {
                break;
            }
        }
        return ids;
    }

    public List<Map<String, Object>> rolesDeUsuario(String userId) {
        return llamar("leer los roles del usuario (permiso read:roles)", () -> conToken(http.get().uri("/api/v2/users/{id}/roles", userId)).retrieve().body(LISTA));
    }

    public void asignarRoles(String userId, List<String> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        try {
            conToken(http.post().uri("/api/v2/users/{id}/roles", userId)
                    .contentType(MediaType.APPLICATION_JSON).body(Map.of("roles", roleIds)))
                    .retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw error("asignar roles", e);
        }
    }

    public void quitarRoles(String userId, List<String> roleIds) {
        if (roleIds.isEmpty()) {
            return;
        }
        try {
            conToken(http.method(org.springframework.http.HttpMethod.DELETE).uri("/api/v2/users/{id}/roles", userId)
                    .contentType(MediaType.APPLICATION_JSON).body(Map.of("roles", roleIds)))
                    .retrieve().toBodilessEntity();
        } catch (RestClientResponseException e) {
            throw error("quitar roles", e);
        }
    }

    // ------------------------------------------------------------------ utilidades

    private <T> T llamar(String accion, Supplier<T> llamada) {
        try {
            return llamada.get();
        } catch (RestClientResponseException e) {
            throw error(accion, e);
        }
    }

    private RuntimeException error(String accion, RestClientResponseException e) {
        log.error("Auth0: no se pudo {}: {} {}", accion, e.getStatusCode(), e.getResponseBodyAsString());
        HttpStatusCode estado = e.getStatusCode();
        if (estado.value() == 400) {
            String cuerpo = e.getResponseBodyAsString();
            if (cuerpo.contains("PasswordStrengthError")) {
                return new IllegalStateException("Auth0 rechazó la contraseña temporal por la política de contraseñas");
            }
            return new IllegalArgumentException("Auth0 rechazó la solicitud al " + accion + ": " + cuerpo);
        }
        if (estado.value() == 403) {
            return new IllegalStateException("La aplicación Machine to Machine no tiene permiso en Auth0 para " + accion
                    + ". Agrégalo en Applications → APIs → Auth0 Management API → Machine to Machine Applications.");
        }
        if (estado.value() == 401) {
            return new IllegalStateException("Auth0 rechazó el token del backend al " + accion);
        }
        return new IllegalStateException("Error de Auth0 al " + accion + " (" + estado.value() + ")");
    }

    /** 24 caracteres con mayúsculas, minúsculas, números y símbolos (cumple cualquier política de Auth0). */
    private static String claveAleatoria() {
        String mayus = "ABCDEFGHJKLMNPQRSTUVWXYZ";
        String minus = "abcdefghijkmnpqrstuvwxyz";
        String numeros = "23456789";
        String simbolos = "!@#$%^&*-_+=?";
        String todos = mayus + minus + numeros + simbolos;
        SecureRandom azar = new SecureRandom();
        StringBuilder clave = new StringBuilder();
        clave.append(mayus.charAt(azar.nextInt(mayus.length())));
        clave.append(minus.charAt(azar.nextInt(minus.length())));
        clave.append(numeros.charAt(azar.nextInt(numeros.length())));
        clave.append(simbolos.charAt(azar.nextInt(simbolos.length())));
        for (int i = 0; i < 20; i++) {
            clave.append(todos.charAt(azar.nextInt(todos.length())));
        }
        return clave.toString();
    }
}
