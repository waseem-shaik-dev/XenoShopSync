package com.example.XenoShopSync.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.web.cors.*;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Value("${app.cors.allowed-origins}")
    private String allowedOriginsCsv;

    @Value("${app.cors.allowed-methods}")
    private String allowedMethodsCsv;

    @Value("${app.cors.allowed-headers}")
    private String allowedHeadersCsv;

    @Value("${app.cors.allow-credentials:true}")
    private boolean allowCredentials;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(allowedOriginsCsv.split(",")));
        config.setAllowedMethods(Arrays.asList(allowedMethodsCsv.split(",")));
        config.setAllowedHeaders(Arrays.asList(allowedHeadersCsv.split(",")));
        config.setAllowCredentials(allowCredentials);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
