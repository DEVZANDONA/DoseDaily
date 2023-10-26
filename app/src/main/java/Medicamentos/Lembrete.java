package Medicamentos;

public class Lembrete {
    private String titulo;
    private String descricao;
    private String userId;

    public Lembrete() {
        // Construtor vazio necessário para Firebase
    }

    public Lembrete(String titulo, String descricao, String userId) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.userId = userId;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
