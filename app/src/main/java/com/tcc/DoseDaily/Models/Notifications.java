package com.tcc.DoseDaily.Models;

import java.io.Serializable;

public class Notifications implements Serializable {

    private String corpo;
    private String deviceToken;
    private String tempoNotificacao;
    private String titulo;
    private String key;


    public Notifications() {}

    public Notifications(String corpo, String deviceToken, String tempoNotificacao, String titulo) {
        this.corpo = corpo;
        this.deviceToken = deviceToken;
        this.tempoNotificacao = tempoNotificacao;
        this.titulo = titulo;
    }

    public String getCorpo() {
        return corpo;
    }

    public void setCorpo(String corpo) {
        this.corpo = corpo;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getTempoNotificacao() {
        return tempoNotificacao;
    }

    public void setTempoNotificacao(String tempoNotificacao) {
        this.tempoNotificacao = tempoNotificacao;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
