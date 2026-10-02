package com.transporte_gomez.erp.services;

import com.transporte_gomez.erp.auth0.Auth0ManagementClient;
import com.transporte_gomez.erp.dto.UsuarioAdmin;
import com.transporte_gomez.erp.entity.ClienteEntity;
import com.transporte_gomez.erp.entity.UsuarioEntity;
import com.transporte_gomez.erp.repository.ClienteRepository;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Administración de usuarios desde la app: los datos de acceso (correo, roles, bloqueo)
 * viven en Auth0; nombre, teléfono y cliente viven en la tabla usuarios del ERP.
 */
@Service
@RequiredArgsConstructor
public class UsuarioAdminService {

    public static final String ROL_CLIENTE = "Cliente";

    private final Auth0ManagementClient auth0;
    private final UsuarioRepository usuarioRepository;
    private final ClienteRepository clienteRepository;

    public List<String> roles() {
        return auth0.listarRoles().stream()
                .map(r -> (String) r.get("name"))
                .sorted()
                .toList();
    }

    @Transactional
    public List<UsuarioAdmin> listar() {
        // Roles de cada usuario: se piden por rol (pocas llamadas) en vez de por usuario
        Map<String, List<String>> rolesPorUsuario = new HashMap<>();
        for (Map<String, Object> rol : auth0.listarRoles()) {
            String nombreRol = (String) rol.get("name");
            for (String userId : auth0.usuariosDeRol((String) rol.get("id"))) {
                rolesPorUsuario.computeIfAbsent(userId, k -> new ArrayList<>()).add(nombreRol);
            }
        }

        List<UsuarioAdmin> resultado = new ArrayList<>();
        for (Map<String, Object> u : auth0.listarUsuarios()) {
            String userId = (String) u.get("user_id");
            UsuarioEntity local = obtenerOCrearLocal(userId, (String) u.get("email"));
            UsuarioAdmin dto = aDto(u, local);
            List<String> roles = rolesPorUsuario.getOrDefault(userId, new ArrayList<>());
            Collections.sort(roles);
            dto.setRoles(roles);
            resultado.add(dto);
        }
        resultado.sort(Comparator.comparing((UsuarioAdmin d) -> Boolean.TRUE.equals(d.getBloqueado()))
                .thenComparing(d -> nombreCompleto(d).toLowerCase()));
        return resultado;
    }

    @Transactional
    public UsuarioAdmin crear(UsuarioAdmin datos) {
        String email = datos.getEmail() == null ? "" : datos.getEmail().trim().toLowerCase();
        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException("Correo inválido: " + datos.getEmail());
        }
        if (datos.getRoles() == null || datos.getRoles().isEmpty()) {
            throw new IllegalArgumentException("Asigna al menos un rol");
        }
        Map<String, String> idsRoles = idsDeRoles();
        validarRoles(datos.getRoles(), idsRoles);
        ClienteEntity cliente = clienteSegunRoles(datos);

        Map<String, Object> creado = auth0.crearUsuario(email, datos.getNombre(), datos.getApellidos());
        String userId = (String) creado.get("user_id");
        auth0.asignarRoles(userId, datos.getRoles().stream().map(idsRoles::get).toList());

        UsuarioEntity local = obtenerOCrearLocal(userId, email);
        local.setNombre(limpio(datos.getNombre()));
        local.setApellidos(limpio(datos.getApellidos()));
        local.setTelefono(limpio(datos.getTelefono()));
        local.setCliente(cliente);
        usuarioRepository.save(local);

        auth0.enviarCorreoClave(email);

        UsuarioAdmin dto = aDto(creado, local);
        dto.setRoles(new ArrayList<>(datos.getRoles()));
        return dto;
    }

    @Transactional
    public UsuarioAdmin actualizar(Long id, UsuarioAdmin datos) {
        UsuarioEntity local = buscar(id);
        String userId = local.getAuth0id();
        Map<String, String> idsRoles = idsDeRoles();
        List<String> nuevos = datos.getRoles() == null ? List.of() : datos.getRoles();
        if (nuevos.isEmpty()) {
            throw new IllegalArgumentException("Asigna al menos un rol");
        }
        validarRoles(nuevos, idsRoles);
        ClienteEntity cliente = clienteSegunRoles(datos);

        List<String> actuales = auth0.rolesDeUsuario(userId).stream().map(r -> (String) r.get("name")).toList();
        auth0.asignarRoles(userId, nuevos.stream().filter(r -> !actuales.contains(r)).map(idsRoles::get).toList());
        auth0.quitarRoles(userId, actuales.stream().filter(r -> !nuevos.contains(r)).map(idsRoles::get)
                .filter(Objects::nonNull).toList());
        auth0.actualizarNombre(userId, datos.getNombre(), datos.getApellidos());

        local.setNombre(limpio(datos.getNombre()));
        local.setApellidos(limpio(datos.getApellidos()));
        local.setTelefono(limpio(datos.getTelefono()));
        local.setCliente(cliente);
        usuarioRepository.save(local);

        UsuarioAdmin dto = aDto(auth0.obtenerUsuario(userId), local);
        List<String> roles = new ArrayList<>(nuevos);
        Collections.sort(roles);
        dto.setRoles(roles);
        return dto;
    }

    public void bloquear(Long id, boolean bloqueado, String authActual) {
        UsuarioEntity local = buscar(id);
        if (bloqueado && local.getAuth0id().equals(authActual)) {
            throw new IllegalArgumentException("No puedes bloquear tu propio usuario");
        }
        auth0.bloquear(local.getAuth0id(), bloqueado);
    }

    public void enviarCorreoClave(Long id) {
        UsuarioEntity local = buscar(id);
        String email = local.getEmail();
        if (email == null || email.isBlank()) {
            email = (String) auth0.obtenerUsuario(local.getAuth0id()).get("email");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El usuario no tiene correo registrado");
        }
        auth0.enviarCorreoClave(email);
    }

    // ------------------------------------------------------------------

    private UsuarioEntity buscar(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
    }

    /** Fila del ERP para un usuario de Auth0 (la crea si nunca ha iniciado sesión). */
    private UsuarioEntity obtenerOCrearLocal(String userId, String email) {
        UsuarioEntity local = usuarioRepository.findByAuth0Id(userId).orElseGet(() -> {
            UsuarioEntity nuevo = new UsuarioEntity();
            nuevo.setAuth0id(userId);
            return nuevo;
        });
        boolean cambio = local.getId() == null;
        if (email != null && !email.equalsIgnoreCase(Objects.toString(local.getEmail(), ""))) {
            local.setEmail(email);
            cambio = true;
        }
        return cambio ? usuarioRepository.save(local) : local;
    }

    private Map<String, String> idsDeRoles() {
        Map<String, String> ids = new HashMap<>();
        auth0.listarRoles().forEach(r -> ids.put((String) r.get("name"), (String) r.get("id")));
        return ids;
    }

    private void validarRoles(List<String> roles, Map<String, String> idsRoles) {
        for (String rol : roles) {
            if (!idsRoles.containsKey(rol)) {
                throw new IllegalArgumentException("El rol " + rol + " no existe en Auth0");
            }
        }
    }

    /** El rol Cliente exige indicar la organización; los demás roles no la usan. */
    private ClienteEntity clienteSegunRoles(UsuarioAdmin datos) {
        boolean esCliente = datos.getRoles() != null && datos.getRoles().contains(ROL_CLIENTE);
        if (!esCliente) {
            return null;
        }
        if (datos.getClienteId() == null) {
            throw new IllegalArgumentException("Un usuario con rol Cliente debe tener asignada su organización (cliente)");
        }
        return clienteRepository.findById(datos.getClienteId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado con ID: " + datos.getClienteId()));
    }

    private UsuarioAdmin aDto(Map<String, Object> auth0User, UsuarioEntity local) {
        UsuarioAdmin dto = new UsuarioAdmin();
        dto.setId(local.getId());
        dto.setAuth0Id(local.getAuth0id());
        dto.setEmail((String) auth0User.getOrDefault("email", local.getEmail()));
        String nombre = local.getNombre() != null ? local.getNombre() : (String) auth0User.get("given_name");
        String apellidos = local.getApellidos() != null ? local.getApellidos() : (String) auth0User.get("family_name");
        if (nombre == null && apellidos == null) {
            String nombreAuth0 = (String) auth0User.get("name");
            nombre = nombreAuth0 != null && !nombreAuth0.equalsIgnoreCase(dto.getEmail()) ? nombreAuth0 : null;
        }
        dto.setNombre(nombre);
        dto.setApellidos(apellidos);
        dto.setTelefono(local.getTelefono());
        if (local.getCliente() != null) {
            dto.setClienteId(local.getCliente().getId());
            dto.setClienteNombre(local.getCliente().getNombreCorto() != null
                    ? local.getCliente().getNombreCorto() : local.getCliente().getRazonSocial());
        }
        dto.setBloqueado(Boolean.TRUE.equals(auth0User.get("blocked")));
        dto.setUltimoIngreso((String) auth0User.get("last_login"));
        dto.setCreado((String) auth0User.get("created_at"));
        Object logins = auth0User.get("logins_count");
        dto.setIngresos(logins instanceof Number n ? n.intValue() : 0);
        return dto;
    }

    private static String nombreCompleto(UsuarioAdmin d) {
        String n = ((d.getNombre() == null ? "" : d.getNombre()) + " " + (d.getApellidos() == null ? "" : d.getApellidos())).trim();
        return n.isEmpty() ? (d.getEmail() == null ? "" : d.getEmail()) : n;
    }

    private static String limpio(String texto) {
        return texto == null || texto.isBlank() ? null : texto.trim();
    }
}
