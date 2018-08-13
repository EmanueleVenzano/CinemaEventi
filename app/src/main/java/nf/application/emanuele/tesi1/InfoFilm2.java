package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InfoFilm2 extends Fragment {

    private ImageView imgFilm;
    private TextView titleFilm;
    private TextView genereFilm;
    private TextView durataFilm;
    private TextView descrizioneFilm;

    ExpandableListAdapter listAdapter;
    ExpandableListView explistView;
    List<String> cinema;
    HashMap<String, List<String>> figli;

    //nome del film che mi viene passato
    String name = "";

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_infofilm, container, false);

        name = ((cercaFilm)getActivity()).getMyData();

        imgFilm = (ImageView)view.findViewById(R.id.imgFilm);
        titleFilm = (TextView)view.findViewById(R.id.titleFilm);
        durataFilm = (TextView)view.findViewById(R.id.durataFilm);
        genereFilm = (TextView)view.findViewById(R.id.genereFilm);
        descrizioneFilm = (TextView)view.findViewById(R.id.descrizioneFilm);
        explistView = (ExpandableListView)view.findViewById(R.id.proiezioniListView);

        cinema = new ArrayList<String>();
        figli = new HashMap<String, List<String>>();

        prepareListData();

        listAdapter = new ExpandableListAdapter(InfoFilm2.this.getContext(), cinema, figli, name);
        explistView.setAdapter(listAdapter);

  /*      explistView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                Toast.makeText(getApplicationContext(), "Group clicked"+cinema.get(groupPosition), Toast.LENGTH_LONG).show();
                return false;
            }
        });

        explistView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                Toast.makeText(getApplicationContext(), cinema.get(groupPosition)+"Expanded", Toast.LENGTH_LONG).show();
            }
        });

        explistView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getApplicationContext(), cinema.get(groupPosition)+" : "+figli.get(cinema.get(groupPosition)).get(childPosition), Toast.LENGTH_LONG).show();
                Toast.makeText(getApplicationContext(), (String) listAdapter.getChild(groupPosition, childPosition), Toast.LENGTH_LONG).show();
                int starNera = getResources().getIdentifier("android:drawable/star_big_off", null, null);
                //ImageView img_selection = (ImageView) explistView.getChildAt(groupPosition).findViewById(R.id.ratingBar);

                return false;
            }
        });*/
      return view;
    }


    public void prepareListData () {
        Cinemas c = new Cinemas();
        //ArrayList<ArrayList<Proiezione>> orari = new ArrayList<>();
        //ArrayList<String> cinema = new ArrayList<>();

        int durata = 0;
        String genere="";
        String descrizione = "";
        String urlToString = "";
        for (int i=0; i< c.cinemas.size(); i++){
            for (int j=0; j<c.cinemas.get(i).films.size(); j++) {
                if (c.cinemas.get(i).films.get(j).Titolo.equals(name)){
                    List<String> ora = new ArrayList<>();

                    durata = c.cinemas.get(i).films.get(j).durata;
                    genere = c.cinemas.get(i).films.get(j).genere;
                    descrizione = c.cinemas.get(i).films.get(j).trama;
                    urlToString = c.cinemas.get(i).films.get(j).immagine;

                    ArrayList<Proiezione> temp = c.cinemas.get(i).films.get(j).proiezione;
                    for(Proiezione z: temp) {
                        ora.add(z.orario);
                    }

                    cinema.add(c.cinemas.get(i).name);
                    figli.put(c.cinemas.get(i).name, ora);
                }
            }
        }

        new InfoFilm2.ImageDownloaderTask(imgFilm).execute(urlToString);

        titleFilm.setText(Html.fromHtml("<b>Titolo: </b>"+name));
        durataFilm.setText(Html.fromHtml("<b>Durata: </b>"+durata+"min"));
        genereFilm.setText(Html.fromHtml("<b>Genere: </b>"+genere));
        descrizioneFilm.setText(Html.fromHtml("<b>Trama: </b><br />"+descrizione));

    }



    private class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
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
