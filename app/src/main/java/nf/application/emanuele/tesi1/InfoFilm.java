package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class InfoFilm extends Activity {
    private ImageView imgFilm;
    private TextView titleFilm;
    private TextView durataFilm;
    private TextView descrizioneFilm;
    private TextView proiezioniFilm;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infofilm);
        imgFilm = (ImageView)findViewById(R.id.imgFilm);
        titleFilm = (TextView)findViewById(R.id.titleFilm);
        durataFilm = (TextView)findViewById(R.id.durataFilm);
        descrizioneFilm = (TextView)findViewById(R.id.descrizioneFilm);
        proiezioniFilm = (TextView) findViewById(R.id.proiezioniFilm);

        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");

        Cinemas c = new Cinemas();

        //TODO metti immagine che non sono capapce

        ArrayList<ArrayList<Proiezione>> orari = new ArrayList<>();
        ArrayList<String> cinema = new ArrayList<>();
        int durata = 0;
        String descrizione = "";
        for (int i=0; i< c.cinemas.size(); i++){
            for (int j=0; j<c.cinemas.get(i).films.size(); j++) {
                if (c.cinemas.get(i).films.get(j).Titolo.equals(name)){
                    durata = c.cinemas.get(i).films.get(j).durata;
                    descrizione = c.cinemas.get(i).films.get(j).trama;
                    orari.add(c.cinemas.get(i).films.get(j).proiezione);
                    cinema.add(c.cinemas.get(i).name);
                }
            }
        }

        titleFilm.setText("Titolo: "+name);
        durataFilm.setText("Durata: "+durata);
        descrizioneFilm.setText("Trama: "+descrizione);
        String p = "";
        for (int i=0; i<cinema.size(); i++){
            p+=cinema.get(i)+": ";
            for (Proiezione pro: orari.get(i)){
                p+=pro.orario+"(sala: "+pro.sala+") ";
            }
            p+="\n";
        }
        proiezioniFilm.setText("Proiezioni: "+p);

    }
}
