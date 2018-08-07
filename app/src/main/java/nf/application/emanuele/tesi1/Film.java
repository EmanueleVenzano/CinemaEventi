package nf.application.emanuele.tesi1;

import java.util.ArrayList;

public class Film {
    public String Titolo;
    public String trama;
    public int durata;
    public String immagine;
    public ArrayList<Proiezione> proiezione;

    public Film(){
        Titolo="";
        trama="";
        durata=0;
        immagine="";
        proiezione= new ArrayList<>();
    }
}
