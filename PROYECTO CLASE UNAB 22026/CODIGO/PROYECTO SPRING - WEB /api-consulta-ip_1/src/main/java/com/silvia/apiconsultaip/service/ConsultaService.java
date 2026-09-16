package com.silvia.apiconsultaip.service;

import com.silvia.apiconsultaip.config.TargetProperties;
import com.silvia.apiconsultaip.model.ConsultaResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

/**
 * Se encarga de armar la URL del servicio remoto (a partir de la IP
 * configurada) y de hacer la peticion HTTP hacia el, ya sea GET o POST.
 */
@Service
public class ConsultaService {

    private static final Logger log = LoggerFactory.getLogger(ConsultaService.class);

    private final RestTemplate restTemplate;
    private final TargetProperties targetProperties;

    public ConsultaService(RestTemplate restTemplate, TargetProperties targetProperties) {
        this.restTemplate = restTemplate;
        this.targetProperties = targetProperties;
    }

    /**
     * Hace un GET hacia target.baseUrl() + path, reenviando los query params
     * que llegaron a esta API (si los hay).
     *
     * @param path         ruta dentro del servicio remoto, ej: "/estado" o "/api/datos"
     * @param queryParams  parametros de consulta a reenviar
     */
    public ConsultaResponse consultarGet(String path, Map<String, String> queryParams) {
        String url = construirUrl(path, queryParams);
        try {
            ResponseEntity<Object> respuesta = restTemplate.getForEntity(url, Object.class);
            return construirRespuestaExitosa(url, respuesta);
        } catch (Exception ex) {
            return construirRespuestaError(url, ex);
        }
    }

    /**
     * Hace un POST hacia target.baseUrl() + path con el cuerpo recibido.
     */
    public ConsultaResponse consultarPost(String path, Map<String, String> queryParams, Object cuerpo) {
        String url = construirUrl(path, queryParams);
        try {
            HttpEntity<Object> entity = new HttpEntity<>(cuerpo);
            ResponseEntity<Object> respuesta = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class);
            return construirRespuestaExitosa(url, respuesta);
        } catch (Exception ex) {
            return construirRespuestaError(url, ex);
        }
    }

    private String construirUrl(String path, Map<String, String> queryParams) {
        String rutaLimpia = (path == null || path.isBlank()) ? "/" : path;
        if (!rutaLimpia.startsWith("/")) {
            rutaLimpia = "/" + rutaLimpia;
        }
        UriComponentsBuilder builder = UriComponentsBuilder
                .fromHttpUrl(targetProperties.baseUrl() + rutaLimpia);

        if (queryParams != null) {
            queryParams.forEach(builder::queryParam);
        }
        return builder.toUriString();
    }

    private ConsultaResponse construirRespuestaExitosa(String url, ResponseEntity<Object> respuesta) {
        int codigo = respuesta.getStatusCode().value();
        log.info("Consulta OK -> {} respondio {}", url, codigo);
        return ConsultaResponse.ok(url, codigo, respuesta.getBody());
    }

    private ConsultaResponse construirRespuestaError(String url, Exception ex) {
        String mensaje;
        if (ex instanceof HttpStatusCodeException httpEx) {
            HttpStatusCode codigo = httpEx.getStatusCode();
            mensaje = "El servicio remoto respondio con error HTTP " + codigo.value() + ": " + httpEx.getStatusText();
        } else if (ex instanceof ResourceAccessException) {
            mensaje = "No se pudo conectar/timeout contra el servicio remoto (" + ex.getMessage() + ")";
        } else {
            mensaje = "Error inesperado consultando el servicio remoto: " + ex.getMessage();
        }
        log.warn("Consulta con error -> {} : {}", url, mensaje);
        return ConsultaResponse.fallo(url, mensaje);
    }
}
