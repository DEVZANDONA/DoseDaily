package com.tcc.DoseDaily.API;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class Medicamento {

    @SerializedName("id")
    private int id;

    @SerializedName("url")
    private String url;

    @SerializedName("nome")
    private String nome;

    @SerializedName("principios_ativos_anvisa")
    private List<String> principiosAtivosAnvisa;

    @SerializedName("principios_ativos")
    private List<PrincipioAtivo> principiosAtivos;

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

    public List<String> getPrincipiosAtivosAnvisa() {
        return principiosAtivosAnvisa;
    }

    public void setPrincipiosAtivosAnvisa(List<String> principiosAtivosAnvisa) {
        this.principiosAtivosAnvisa = principiosAtivosAnvisa;
    }

    public List<PrincipioAtivo> getPrincipiosAtivos() {
        return principiosAtivos;
    }

    public void setPrincipiosAtivos(List<PrincipioAtivo> principiosAtivos) {
        this.principiosAtivos = principiosAtivos;
    }

    // Outros campos e métodos conforme necessário
}
