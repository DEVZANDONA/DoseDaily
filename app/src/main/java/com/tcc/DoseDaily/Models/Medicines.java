package com.tcc.DoseDaily.Models;

public class Medicines {

    private String name;

    private String frequencia;
    private String description;
    private String dosage;
    private String dataImage;
    private String horaNotificacao;
    private String dataNotificacao;
    private String key;
    private String userId;


    public Medicines() {}

    public Medicines(String name, String description, String dosage, String dataImage, String key, String userId) {
        this.name = name;
        this.description = description;
        this.dosage = dosage;
        this.dataImage = dataImage;
        this.key = key;
        this.userId = userId;
    }

    public Medicines(String name, String description, String dosage, String dataImage, String horaNotificacao, String dataNotificacao, String key, String userId, String frequencia) {
        this.name = name;
        this.description = description;
        this.dosage = dosage;
        this.dataImage = dataImage;
        this.horaNotificacao = horaNotificacao;
        this.dataNotificacao = dataNotificacao;
        this.key = key;
        this.userId = userId;
        this.frequencia = frequencia;
    }




    // Getters e Setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDosage() {
        return dosage;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public String getDataImage() {
        return dataImage;
    }

    public void setDataImage(String dataImage) {
        this.dataImage = dataImage;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHoraNotificacao() {
        return horaNotificacao;
    }

    public String getDataNotificacao() {
        return dataNotificacao;
    }

    public void setHoraNotificacao(String horaNotificacao) {
        this.horaNotificacao = horaNotificacao;
    }

    public void setDataNotificacao(String dataNotificacao) {
        this.dataNotificacao = dataNotificacao;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public void setFrequencia(String frequencia) {
        this.frequencia = frequencia;
    }
}
