package com.transporte_gomez.erp.config;

import com.transporte_gomez.erp.entity.UsuarioEntity;
import com.transporte_gomez.erp.repository.UsuarioRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class AutoRegistroUsuarioFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;

    public AutoRegistroUsuarioFilter(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            String sub = jwt.getSubject();
            if (usuarioRepository.findByAuth0Id(sub).isEmpty()) {
                UsuarioEntity nuevo = new UsuarioEntity();
                nuevo.setAuth0id(sub);
                usuarioRepository.save(nuevo);
            }
        }

        filterChain.doFilter(request, response);
    }
}
