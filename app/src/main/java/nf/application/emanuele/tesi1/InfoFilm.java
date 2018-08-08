package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

public class InfoFilm extends Activity {
    private ImageView imgFilm;
    private TextView titleFilm;
    private TextView genereFilm;
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
        genereFilm = (TextView)findViewById(R.id.genereFilm);
        descrizioneFilm = (TextView)findViewById(R.id.descrizioneFilm);
        proiezioniFilm = (TextView) findViewById(R.id.proiezioniFilm);

        Intent intent = this.getIntent();
        String name = intent.getStringExtra("name");

        Cinemas c = new Cinemas();
        ArrayList<ArrayList<Proiezione>> orari = new ArrayList<>();
        ArrayList<String> cinema = new ArrayList<>();
        int durata = 0;
        String genere="";
        String descrizione = "";
        String urlToString = "";
        for (int i=0; i< c.cinemas.size(); i++){
            for (int j=0; j<c.cinemas.get(i).films.size(); j++) {
                if (c.cinemas.get(i).films.get(j).Titolo.equals(name)){
                    durata = c.cinemas.get(i).films.get(j).durata;
                    genere = c.cinemas.get(i).films.get(j).genere;
                    descrizione = c.cinemas.get(i).films.get(j).trama;
                    urlToString = c.cinemas.get(i).films.get(j).immagine;
                    orari.add(c.cinemas.get(i).films.get(j).proiezione);
                    cinema.add(c.cinemas.get(i).name);
                }
            }
        }

        new ImageDownloaderTask(imgFilm).execute(urlToString);

        titleFilm.setText(Html.fromHtml("<b>Titolo: </b>"+name));
        durataFilm.setText(Html.fromHtml("<b>Durata: </b>"+durata+"min"));
        genereFilm.setText(Html.fromHtml("<b>Genere: </b>"+genere));
        descrizioneFilm.setText(Html.fromHtml("<b>Trama: </b><br />"+descrizione));
        String p = "";
        for (int i=0; i<cinema.size(); i++){
            p+=cinema.get(i)+":<br />";
            for (Proiezione pro: orari.get(i)){
                p+=pro.orario+"(sala: "+pro.sala+")<br /> ";
            }
            p+="<br />";
        }
        proiezioniFilm.setText(Html.fromHtml("<b>Proiezioni: </b><br /><br />"+p));

    }

    private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap>{
        private final WeakReference<ImageView> imageViewWeakReference;

        public ImageDownloaderTask (ImageView imageView){
            imageViewWeakReference = new WeakReference<ImageView>(imageView);
        }

        @Override
        protected Bitmap doInBackground (String... params){
            return downloadBitmap(params[0]);
        }

        @Override
        protected void onPostExecute (Bitmap result){
            if (isCancelled()){
                result=null;
            }
            if (imageViewWeakReference!=null){
                ImageView imageView = imageViewWeakReference.get();
                if (imageView!=null){
                    if (result!=null) {
                        imageView.setImageBitmap(result);
                    }else{
                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.ic_launcher_background);
                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }

        private Bitmap downloadBitmap(String url){
            HttpURLConnection urlConnection = null;
            try{
                URL uri = new URL(url);
                urlConnection = (HttpURLConnection) uri.openConnection();
                int statusCode = urlConnection.getResponseCode();
                if (statusCode!=200){
                    return null;
                }
                InputStream inputStream = urlConnection.getInputStream();

                if (inputStream!=null){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds=true;
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    return bitmap;
                }
            }catch (Exception e){
                urlConnection.disconnect();
                Log.w("Image Downloader", "Error downloding image from "+url);
            }finally {
                if (urlConnection!=null){
                    urlConnection.disconnect();
                }
            }
            return null;
        }
    }

}
