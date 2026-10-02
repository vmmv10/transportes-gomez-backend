package com.transporte_gomez.erp.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Roles de Auth0. La Action "AddRolesToToken" los agrega al token en el claim
 * {@value #CLAIM}; aquí se convierten en autoridades ROLE_Administrador, ROLE_Bodega, etc.
 */
public final class Roles {

    public static final String CLAIM = "https://transportes-gomez.cl/roles";

    public static final String ADMINISTRADOR = "Administrador";
    public static final String OPERACIONES = "Operaciones";
    public static final String BODEGA = "Bodega";
    public static final String CONDUCTOR = "Conductor";
    public static final String CLIENTE = "Cliente";

    private Roles() {
    }

    public static Converter<Jwt, AbstractAuthenticationToken> convertidorJwt() {
        JwtGrantedAuthoritiesConverter scopes = new JwtGrantedAuthoritiesConverter();
        return jwt -> {
            Collection<GrantedAuthority> autoridades = new ArrayList<>(scopes.convert(jwt));
            List<String> roles = jwt.getClaimAsStringList(CLAIM);
            if (roles != null) {
                roles.forEach(rol -> autoridades.add(new SimpleGrantedAuthority("ROLE_" + rol)));
            }
            return new JwtAuthenticationToken(jwt, autoridades, jwt.getSubject());
        };
    }
}
