package com.silvia.apiconsultaip.controller;

import com.silvia.apiconsultaip.config.TargetProperties;
import com.silvia.apiconsultaip.model.ConsultaResponse;
import com.silvia.apiconsultaip.service.ConsultaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Expone los endpoints de esta API. El cliente llama a esta API, y esta a
 * su vez hace la peticion HTTP real hacia la IP configurada en
 * TargetProperties (variables TARGET_IP / TARGET_PORT / TARGET_SCHEME).
 *
 * Ejemplos de uso una vez desplegada en EC2 (IP publica de tu instancia,
 * puerto 8080):
 *
 *   GET  http://TU_EC2_IP:8080/api/consulta?path=/estado
 *   POST http://TU_EC2_IP:8080/api/consulta?path=/procesar   (con body JSON)
 *
 * Cambia "path" por la ruta real que expone el servicio en la IP destino.
 */
@RestController
@RequestMapping("/api")
public class ConsultaController {

    private final ConsultaService consultaService;
    private final TargetProperties targetProperties;

    public ConsultaController(ConsultaService consultaService, TargetProperties targetProperties) {
        this.consultaService = consultaService;
        this.targetProperties = targetProperties;
    }

    /** Confirma que la API esta viva y muestra contra que IP esta configurada. */
    @GetMapping("/info")
    public Map<String, Object> info() {
        return Map.of(
                "aplicacion", "api-consulta-ip de silvia",
                "targetBaseUrl", targetProperties.baseUrl()
        );
    }

    /**
     * Hace GET hacia target.baseUrl() + path y devuelve el resultado.
     * Todos los query params, EXCEPTO "path", se reenvian tal cual al servicio remoto.
     */
    @GetMapping("/consulta")
    public ResponseEntity<ConsultaResponse> consultarGet(
            @RequestParam(defaultValue = "/") String path,
            @RequestParam Map<String, String> todosLosParametros) {

        todosLosParametros.remove("path");
        ConsultaResponse resultado = consultaService.consultarGet(path, todosLosParametros);
        return ResponseEntity.ok(resultado);
    }

    /**
     * Hace POST hacia target.baseUrl() + path, reenviando el cuerpo (JSON) recibido.
     */
    @PostMapping("/consulta")
    public ResponseEntity<ConsultaResponse> consultarPost(
            @RequestParam(defaultValue = "/") String path,
            @RequestParam Map<String, String> todosLosParametros,
            @RequestBody(required = false) Object cuerpo) {

        todosLosParametros.remove("path");
        ConsultaResponse resultado = consultaService.consultarPost(path, todosLosParametros, cuerpo);
        return ResponseEntity.ok(resultado);
    }
}
