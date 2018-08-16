package nf.application.emanuele.tesi1;

public class DataShowTimes {
    private String cinema_id;
    private String movie_id;
    private String start;
    private String language;
    private String subtitle;
    private String auditorium;
    private boolean is3D;
    private boolean isIMax;
    private String link;

    public DataShowTimes(String cinema_id, String movie_id, String start, String language, String subtitle, String auditorium, boolean is3D, boolean isIMax, String link) {
        this.cinema_id = cinema_id;
        this.movie_id = movie_id;
        this.start = start;
        this.language = language;
        this.subtitle = subtitle;
        this.auditorium = auditorium;
        this.is3D = is3D;
        this.isIMax = isIMax;
        this.link = link;
    }

    public DataShowTimes() {
        this.cinema_id = "";
        this.movie_id = "";
        this.start = "";
        this.language = "";
        this.subtitle = "";
        this.auditorium = "";
        this.is3D = false;
        this.isIMax = false;
        this.link = "";
    }

    public String getCinema_id() {
        return cinema_id;
    }

    public void setCinema_id(String cinema_id) {
        this.cinema_id = cinema_id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAuditorium() {
        return auditorium;
    }

    public void setAuditorium(String auditorium) {
        this.auditorium = auditorium;
    }

    public boolean isIs3D() {
        return is3D;
    }

    public void setIs3D(boolean is3D) {
        this.is3D = is3D;
    }

    public boolean isIMax() {
        return isIMax;
    }

    public void setIMax(boolean isIMax) {
        this.isIMax = isIMax;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
