package com.transporte_gomez.erp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.JwtClaimValidator;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig implements WebMvcConfigurer {

    private final AutoRegistroUsuarioFilter autoRegistroUsuarioFilter;

    @Value("${ruta.documentos}")
    private String rutaDocumentos;

    @Value("${ruta.ordenes}")
    private String rutaOrdenes;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/documentos/**")
                .addResourceLocations("file:///" + rutaDocumentos);

        registry
                .addResourceHandler("/ordenes/**")
                .addResourceLocations("file:///" + rutaOrdenes);
    }

    public SecurityConfig(AutoRegistroUsuarioFilter autoRegistroUsuarioFilter) {
        this.autoRegistroUsuarioFilter = autoRegistroUsuarioFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/public/**").permitAll()
                        .requestMatchers("/documentos/**").permitAll()
                        .requestMatchers("/ordenes/**").permitAll()
                        .anyRequest().authenticated()
                )
                .cors() // <- activa CORS
                .and()
                .csrf().disable() // <- probablemente no lo necesitas para APIs
                .oauth2ResourceServer(oauth2 -> oauth2.jwt());
        http.addFilterAfter(autoRegistroUsuarioFilter, BasicAuthenticationFilter.class);
        return http.build();
    }
}
