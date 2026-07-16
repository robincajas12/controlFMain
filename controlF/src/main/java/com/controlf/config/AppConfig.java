package com.controlf.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * Beans de propósito general que no pertenecen a un paquete de
 * funcionalidad específico.
 */
@Configuration
public class AppConfig {

    /**
     * Provee un {@link RestTemplate} compartido para llamadas HTTP salientes.
     *
     * @return una instancia de {@code RestTemplate} con configuración por defecto
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
