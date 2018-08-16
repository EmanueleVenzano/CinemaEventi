package nf.application.emanuele.tesi1;

public class DataFilm {
    private String id;
    private String title;
    private String img;

    public DataFilm(String id, String title, String img) {
        this.id = id;
        this.title = title;
        this.img = img;
    }

    public DataFilm() {
        this.id = "";
        this.title = "";
        this.img = "";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
