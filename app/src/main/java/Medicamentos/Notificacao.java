package Medicamentos;

public class Notificacao {
    private String titulo;
    private String corpo;

    private String deviceToken;

    public Notificacao() {
        // Construtor vazio necessário para Firebase
    }

    public Notificacao(String titulo, String corpo, String deviceToken) {
        this.titulo = titulo;
        this.corpo = corpo;
        this.deviceToken = deviceToken;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
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
}

