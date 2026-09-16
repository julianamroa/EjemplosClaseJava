package com.silvia.apiconsultaip.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Respuesta uniforme que esta API devuelve al cliente, envolviendo el
 * resultado de la llamada al servicio remoto.
 *
 * @Getter/@Setter (Lombok) generan los getters/setters de los campos.
 * Para el campo booleano "exito", Lombok genera isExito() (no getExito()).
 */
@Getter
@Setter
public class ConsultaResponse {

    private boolean exito;
    private String urlConsultada;
    private Integer codigoHttp;
    private Object cuerpo;
    private String error;

    public static ConsultaResponse ok(String url, int codigoHttp, Object cuerpo) {
        ConsultaResponse r = new ConsultaResponse();
        r.exito = true;
        r.urlConsultada = url;
        r.codigoHttp = codigoHttp;
        r.cuerpo = cuerpo;
        return r;
    }

    public static ConsultaResponse fallo(String url, String mensajeError) {
        ConsultaResponse r = new ConsultaResponse();
        r.exito = false;
        r.urlConsultada = url;
        r.error = mensajeError;
        return r;
    }
}
