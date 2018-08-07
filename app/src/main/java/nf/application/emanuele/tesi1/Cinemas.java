package nf.application.emanuele.tesi1;

import android.location.Location;

import java.util.ArrayList;

public class Cinemas {
    public ArrayList<Cinema> cinemas;

    public Cinemas(){
        cinemas = new ArrayList<>();
        Proiezione P1 = new Proiezione();
        Proiezione P2 = new Proiezione();
        Proiezione P3 = new Proiezione();
        P1.orario = "12:20";
        P1.sala=1;
        P2.orario = "14:20";
        P2.sala=1;
        P3.orario = "16:20";
        P3.sala=2;
        Film f1 = new Film();
        f1.Titolo = "Black panther";
        f1.durata = 110;
        f1.trama = "Non succede un cazzo";
        f1.immagine = "http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png";
        f1.proiezione.add(P1);
        f1.proiezione.add(P2);
        f1.proiezione.add(P3);
        Cinema c1 = new Cinema();
        c1.name = "Fiumara";
        c1.films.add(f1);
        cinemas.add(c1);

        P1 = new Proiezione();
        P2 = new Proiezione();
        P3 = new Proiezione();
        f1 = new Film();
        c1 = new Cinema();
        P1.orario = "12:20";
        P1.sala=1;
        P2.orario = "14:20";
        P2.sala=1;
        P3.orario = "16:20";
        P3.sala=2;
        f1.Titolo = "Black panther";
        f1.durata = 110;
        f1.trama = "Non succede un cazzo";
        f1.immagine = "http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png";
        f1.proiezione.add(P1);
        f1.proiezione.add(P2);
        f1.proiezione.add(P3);
        c1.name = "TheSpace";
        c1.films.add(f1);

        P1 = new Proiezione();
        P2 = new Proiezione();
        P3 = new Proiezione();
        f1 = new Film();
        P1.orario = "12:20";
        P1.sala=1;
        f1.Titolo = "Super Sonic";
        f1.durata = 110;
        f1.trama = "Un riccio";
        f1.immagine = "http://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png";
        f1.proiezione.add(P1);
        c1.films.add(f1);
        cinemas.add(c1);
    }
}
