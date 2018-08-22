package nf.application.emanuele.tesi1;

public class DataCinema {
    private String url_img;
    private String name;
    private String id;
    private String lat;
    private String lon;
    private String address;
    private String cap;
    private String city;

    public DataCinema(String url_img, String name, String id, String lat, String lon, String address, String cap, String city) {
        this.url_img = url_img;
        this.name = name;
        this.id = id;
        this.lat = lat;
        this.lon = lon;
        this.address = address;
        this.cap = cap;
        this.city = city;
    }

    public DataCinema() {
        this.url_img = "";
        this.name = "";
        this.id = "";
        this.lat = "";
        this.lon = "";
        this.address = "";
        this.cap = "";
        this.city = "";
    }

    public String getUrl_img() {
        return url_img;
    }

    public void setUrl_img(String url_img) {
        this.url_img = url_img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCap() {
        return cap;
    }

    public void setCap(String cap) {
        this.cap = cap;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
