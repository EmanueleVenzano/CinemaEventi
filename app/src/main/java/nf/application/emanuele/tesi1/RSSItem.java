package nf.application.emanuele.tesi1;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RSSItem {
    private String titolo;
    private String immagine;
    private String regista;
    private ArrayList<String> attori;
    private String genere;
    private String anno;
    private String trailer;
    private String durata;
    private String trama;
    private ArrayList<Pro> films;
    private String tramaBreve;
    public class Pro{
        public String cinema;
        public String orario;

        public Pro (String cinema, String orario){
            this.cinema = cinema;
            this.orario = orario;
        }
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getImmagine() {
        return immagine;
    }

    public void setImmagine(String immagine) {
        this.immagine = immagine;
    }

    public String getRegista() {
        return regista;
    }

    public void setRegista(String regista) {
        this.regista = regista;
    }

    public ArrayList<String> getAttori() {
        return attori;
    }

    public void setAttori(ArrayList<String> attori) {
        this.attori = attori;
    }

    public void addAttori(String attore){
        attori.add(attore);
    }

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
    }

    public String getAnno() {
        return anno;
    }

    public void setAnno(String anno) {
        this.anno = anno;
    }

    public String getTrailer() {
        return trailer;
    }

    public void setTrailer(String trailer) {
        this.trailer = trailer;
    }

    public String getDurata() {
        return durata;
    }

    public void setDurata(String durata) {
        this.durata = durata;
    }

    public String getTrama() {
        return trama;
    }

    public void setTrama(String trama) {
        this.trama = trama;
    }

    public ArrayList<Pro> getFilms() {
        return films;
    }

    public void setFilms(ArrayList<Pro> films) {
        this.films = films;
    }

    public void addFilm (String cinema, String orario){
        films.add(new Pro(cinema, orario));
    }

    public String getTramaBreve() {
        return tramaBreve;
    }

    public void setTramaBreve(String tramaBreve) {
        this.tramaBreve = tramaBreve;
    }
}
