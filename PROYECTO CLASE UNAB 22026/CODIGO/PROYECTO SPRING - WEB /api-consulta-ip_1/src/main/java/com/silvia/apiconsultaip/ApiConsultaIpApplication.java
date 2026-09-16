package com.silvia.apiconsultaip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicacion.
 *
 * Esta API expone endpoints que hacen peticiones HTTP hacia una IP configurable
 * (por ejemplo, un servicio interno, otro servidor, o una API de terceros) y
 * devuelven el resultado al cliente que consulta esta API.
 */
@SpringBootApplication
public class ApiConsultaIpApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiConsultaIpApplication.class, args);
    }
}
