package com.tcc.DoseDaily.API;

import com.google.gson.annotations.SerializedName;

public class Interacao {

    @SerializedName("id")
    private int id;

    // Outros campos
    @SerializedName("principios_ativos")
    private String principiosAtivos;

    @SerializedName("ean")
    private String ean;

    @SerializedName("gravidade")
    private String gravidade;

    @SerializedName("evidencia")
    private String evidencia;

    @SerializedName("acao")
    private String acao;

    // Adicione outros campos conforme necessário

    public int getId() {
        return id;
    }

    public String getPrincipiosAtivos() {
        return principiosAtivos;
    }

    public String getEan() {
        return ean;
    }

    public String getGravidade() {
        return gravidade;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public String getAcao() {
        return acao;
    }

    // Adicione getters/setters para outros campos, se necessário
}
