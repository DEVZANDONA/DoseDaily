package com.tcc.DoseDaily.API;

import com.google.gson.annotations.SerializedName;

public class PrincipioAtivo {
    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    @SerializedName("nome")
    private String nome;

    // Construtores, getters e setters conforme necessário

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

    // Outros campos e métodos conforme necessário
}
