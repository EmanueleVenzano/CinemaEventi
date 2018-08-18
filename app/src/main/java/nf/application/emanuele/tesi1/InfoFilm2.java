package nf.application.emanuele.tesi1;

import android.support.v4.app.Fragment;
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
import android.widget.ListView;
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
    ListView explistView;
    List<ArrayList<String>> cinema;
    ArrayList<ArrayList<DataShowTimes>> showtimes;
    //HashMap<String, List<DataShowTimes>> figli;


    //nome del film che mi viene passato
    String name = "";

    public static InfoFilm2 newInstance(){
        InfoFilm2 Fragment = new InfoFilm2();
        return Fragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.activity_infofilm, container, false);

        name = ((cercaFilm)getActivity()).getMyData();

        imgFilm = (ImageView)view.findViewById(R.id.imgFilm);
        titleFilm = (TextView)view.findViewById(R.id.titleFilm);
//        durataFilm = (TextView)view.findViewById(R.id.durataFilm);
  //      genereFilm = (TextView)view.findViewById(R.id.genereFilm);
    //    descrizioneFilm = (TextView)view.findViewById(R.id.descrizioneFilm);
        explistView = (ListView)view.findViewById(R.id.proiezioniListView);

        cinema = new ArrayList<ArrayList<String>>();
        //figli = new HashMap<String, List<DataShowTimes>>();

        prepareListData();

//        FilmCustomAdapter listAdapter = new FilmCustomAdapter(InfoFilm2.this.getContext(), cinema, figli, name);
        FilmCustomAdapter listAdapter = new FilmCustomAdapter(InfoFilm2.this.getContext(), R.layout.proiezioni_listview,cinema, showtimes , name);
        explistView.setAdapter(listAdapter);

/*        explistView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                int height = 0;
                for (int i = 0; i < explistView.getChildCount(); i++) {
                    height += explistView.getChildAt(i).getMeasuredHeight();
                    height += explistView.getDividerHeight();
                }
                explistView.getLayoutParams().height = (height)*2;
            }
        });


        // Listview Group collapsed listener
        explistView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                int height = 0;
                for (int i = 0; i < explistView.getChildCount(); i++) {
                    height += explistView.getChildAt(i).getMeasuredHeight();
                    height += explistView.getDividerHeight();
                }
                explistView.getLayoutParams().height = (height);
            }

        });
*/

        return view;
    }


    public void prepareListData () {
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        DataInfo dataInfo = myApplication.getDataInfo();
        String urlToString = "";
        showtimes = new ArrayList<>();
        for (int i = 0; i < dataInfo.films.size(); i++) {
            if (dataInfo.films.get(i).getTitle().equals(name)) {
                urlToString = dataInfo.films.get(i).getImg();
                String movie_id = dataInfo.films.get(i).getId();
                for (int j = 0; j < dataInfo.showTimes.size(); j++){
                    if (!movie_id.equals(dataInfo.showTimes.get(j).getMovie_id())) continue;
                    String cinema_id = dataInfo.showTimes.get(j).getCinema_id();
                    String cinema_name = "";
                    for (int k = 0; k < dataInfo.cinemas.size(); k++){
                        if (dataInfo.cinemas.get(k).getId().equals(cinema_id)){
                            cinema_name = dataInfo.cinemas.get(k).getName();
                            int l = 0;
                            for (int m = 0; m < cinema.size(); m++){
                                if (cinema.get(m).get(0).equals(cinema_name)){
                                    l=1;
                                    break;
                                }
                            }
                            if (l==0){
                                ArrayList<String> temp = new ArrayList<>();
                                temp.add(cinema_name);
                                temp.add(cinema_id);
                                cinema.add(temp);
                            }
                            break;
                        }
                    }
                    int position = -1;
                    for (int k = 0; k < cinema.size(); k++){
                        if (cinema.get(k).get(0).equals(cinema_name)){
                            position = k;
                            break;
                        }
                    }
                    DataShowTimes dataShowTimes = dataInfo.showTimes.get(j);
                    while (position>=showtimes.size()){
                        showtimes.add(new ArrayList<DataShowTimes>());
                    }
                    showtimes.get(position).add(dataShowTimes);
                }
                break;
            }
        }
        //for (int i = 0; i < cinema.size(); i++){
          //  figli.put(cinema.get(i), showtimes.get(i));
        //}
        new InfoFilm2.ImageDownloaderTask(imgFilm).execute(urlToString);
        titleFilm.setText(Html.fromHtml("<b>Titolo: </b>" + name));
    }

       /* Cinemas c = new Cinemas();
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

    }*/

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
