package com.silvia.apiconsultaip.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Agrupa la configuracion del servicio remoto ("target") que esta API va a
 * consultar: esquema (http/https), ip/host, puerto y timeouts.
 *
 * Los valores se leen desde application.properties, y estos a su vez pueden
 * venir de variables de entorno (ver application.properties), para poder
 * cambiar la IP destino en EC2 sin recompilar el proyecto.
 *
 * @Getter/@Setter (Lombok) generan automaticamente los getters y setters de
 * los campos de abajo en tiempo de compilacion.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "target")
public class TargetProperties {

    private String scheme = "http";
    private String ip;
    private int port = 80;
    private int connectTimeoutMs = 3000;
    private int readTimeoutMs = 5000;

    /** Construye la URL base, por ejemplo: http://203.0.113.10:80 */
    public String baseUrl() {
        return scheme + "://" + ip + ":" + port;
    }
}
