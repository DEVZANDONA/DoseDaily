package com.tcc.DoseDaily.API;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Interacao {
    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    @SerializedName("gravidade")
    private String gravidade;

    @SerializedName("evidencia")
    private String evidencia;

    @SerializedName("acao")
    private String acao;

    @SerializedName("explicacao")
    private String explicacao;

    @SerializedName("principios_ativos")
    private List<PrincipioAtivo> principiosAtivos;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getGravidade() {
        return gravidade;
    }

    public void setGravidade(String gravidade) {
        this.gravidade = gravidade;
    }

    public String getEvidencia() {
        return evidencia;
    }

    public void setEvidencia(String evidencia) {
        this.evidencia = evidencia;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(String acao) {
        this.acao = acao;
    }

    public String getExplicacao() {
        return explicacao;
    }

    public void setExplicacao(String explicacao) {
        this.explicacao = explicacao;
    }

    public List<PrincipioAtivo> getPrincipiosAtivos() {
        return principiosAtivos;
    }

    public void setPrincipiosAtivos(List<PrincipioAtivo> principiosAtivos) {
        this.principiosAtivos = principiosAtivos;
    }

    public static class PrincipioAtivo {
        @SerializedName("id")
        private int id;

        @SerializedName("url")
        private String url;

        @SerializedName("nome")
        private String nome;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getNome() {
            return nome;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }
    }
}
