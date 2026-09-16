package com.silvia.apiconsultaip.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * Configura el cliente HTTP (RestTemplate) que se usa para llamar al
 * servicio remoto, aplicando los timeouts definidos en TargetProperties.
 */
@Configuration
@EnableConfigurationProperties(TargetProperties.class)
public class RestClientConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder, TargetProperties targetProperties) {
        return builder
                .connectTimeout(Duration.ofMillis(targetProperties.getConnectTimeoutMs()))
                .readTimeout(Duration.ofMillis(targetProperties.getReadTimeoutMs()))
                .build();
    }
}
