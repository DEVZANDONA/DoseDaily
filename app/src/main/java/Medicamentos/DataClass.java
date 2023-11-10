package Medicamentos;

public class DataClass {

    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;

    private String horaNotificacao; // Formato: "HH:mm"
    private String dataNotificacao; // Formato: "yyyy-MM-dd"
    private String key;
    private String userId; // Adicionando o campo userId na classe

    // Construtor vazio para compatibilidade com Firebase, por exemplo
    public DataClass() {}



    // Construtor principal


    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage, String key, String userId) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.key = key;
        this.userId = userId;
    }

    public DataClass(String dataTitle, String dataDesc, String dataLang, String dataImage, String horaNotificacao, String dataNotificacao, String key, String userId) {
        this.dataTitle = dataTitle;
        this.dataDesc = dataDesc;
        this.dataLang = dataLang;
        this.dataImage = dataImage;
        this.horaNotificacao = horaNotificacao;
        this.dataNotificacao = dataNotificacao;
        this.key = key;
        this.userId = userId;
    }


    // Getters e Setters

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDataTitle() {
        return dataTitle;
    }

    public void setDataTitle(String dataTitle) {
        this.dataTitle = dataTitle;
    }

    public String getDataDesc() {
        return dataDesc;
    }

    public void setDataDesc(String dataDesc) {
        this.dataDesc = dataDesc;
    }

    public String getDataLang() {
        return dataLang;
    }

    public void setDataLang(String dataLang) {
        this.dataLang = dataLang;
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

    @Override
    public String toString() {
        return "DataClass{" +
                "dataTitle='" + dataTitle + '\'' +
                ", dataDesc='" + dataDesc + '\'' +
                ", dataLang='" + dataLang + '\'' +
                ", dataImage='" + dataImage + '\'' +
                ", key='" + key + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
