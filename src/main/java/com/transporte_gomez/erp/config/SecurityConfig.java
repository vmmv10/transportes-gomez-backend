package com.transporte_gomez.erp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
                .csrf(csrf -> csrf.disable())
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );
        http.addFilterAfter(autoRegistroUsuarioFilter, BasicAuthenticationFilter.class);
        return http.build();
    }
}
