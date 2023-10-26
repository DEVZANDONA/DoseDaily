package Medicamentos;

public class DataClass {

    private String dataTitle;
    private String dataDesc;
    private String dataLang;
    private String dataImage;
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
        this.userId = userId; // Inicializando o campo userId no construtor
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
