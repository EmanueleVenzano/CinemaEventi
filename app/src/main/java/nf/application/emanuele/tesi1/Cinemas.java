package nf.application.emanuele.tesi1;

import java.util.ArrayList;

public class Cinemas {
    public ArrayList<Cinema> cinemas;

    public Cinemas(){
        cinemas = new ArrayList<>();
        Proiezione P1 = new Proiezione();
        Proiezione P2 = new Proiezione();
        Proiezione P3 = new Proiezione();
        Film f1 = new Film();
        Cinema c1 = new Cinema();

        P1.orario = "12:20";
        P2.orario = "14:20";
        P3.orario = "16:20";
        f1.Titolo = "BlackPanther";
        f1.durata = 110;
        f1.genere="Fantasy";
        f1.trama = "Il sogno utopico di un'Africa senza l'uomo bianco";
        f1.immagine = "https://images-na.ssl-images-amazon.com/images/I/913P9nWS%2B%2BL._SX342_.jpg";
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
        f1.Titolo = "Supersonic";
        f1.durata = 110;
        f1.genere ="Film ridicolo sugli oasis";
        f1.trama = "L'ascesa nel mondo della musica di due tossici dipendenti da droghe sintetiche che non fanno altro che litigare";
        f1.immagine = "https://images-na.ssl-images-amazon.com/images/I/91T3qCKbEnL._SY445_.jpg";
        f1.proiezione.add(P1);
        c1.films.add(f1);
        cinemas.add(c1);

        P1 = new Proiezione();
        P2 = new Proiezione();
        P3 = new Proiezione();
        f1 = new Film();
        c1 = new Cinema();
        P1.orario = "12:20";
        P2.orario = "14:20";
        P3.orario = "16:20";
        f1.Titolo = "BlackPanther";
        f1.durata = 110;
        f1.genere="Fantasy";
        f1.trama = "Il sogno utopico di un'Africa senza l'uomo bianco";
        f1.immagine = "https://images-na.ssl-images-amazon.com/images/I/913P9nWS%2B%2BL._SX342_.jpg";
        f1.proiezione.add(P1);
        f1.proiezione.add(P2);
        f1.proiezione.add(P3);
        c1.films.add(f1);
        c1.name="Cinema Italia";
        cinemas.add(c1);

        P1 = new Proiezione();
        P2 = new Proiezione();
        P3 = new Proiezione();
        f1 = new Film();
        c1 = new Cinema();
        P1.orario = "12:20";
        P2.orario = "14:20";
        P3.orario = "16:20";
        f1.Titolo = "BlackPanther";
        f1.durata = 110;
        f1.genere="Fantasy";
        f1.trama = "Il sogno utopico di un'Africa senza l'uomo bianco";
        f1.immagine = "https://images-na.ssl-images-amazon.com/images/I/913P9nWS%2B%2BL._SX342_.jpg";
        f1.proiezione.add(P1);
        f1.proiezione.add(P2);
        f1.proiezione.add(P3);
        c1.name = "Fiumara";
        c1.films.add(f1);
        cinemas.add(c1);

    }
}
