package nf.application.emanuele.tesi1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Locandine extends Fragment implements AdapterView.OnItemClickListener {
    private GridView itemsGridView;
    private List<String> films;
    MyApplication myApplication;
    ArrayList<DataFilm> filmsT ;
    ChangeListener filmlistener;

    public static Locandine newInstance(){
        Locandine Fragment = new Locandine();
        return Fragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_locandine, container, false);
        myApplication = (MyApplication) getActivity().getApplication();
        itemsGridView = (GridView) view.findViewById(R.id.itemsListView);
        itemsGridView.setOnItemClickListener(this);
        filmsT = new ArrayList<>();
        filmlistener = new ChangeListener();
        if (myApplication.getDataInfo().films == null) {
            DataInfo dF = myApplication.getDataInfo();
            String[] params = new String[dF.nearestCinemas.size()];
            for (int i=0; i<dF.nearestCinemas.size(); i++){
                if (Integer.parseInt(dF.nearestCinemas.get(i).getId()) > 4){
                    params[i] = "https://api.internationalshowtimes.com/v4/movies/?cinema_id="+dF.nearestCinemas.get(i).getId();
                }else {
                    params[i] = "";
                }
            }
            try {
                boolean b = new DownloadFilmTask().execute(params).get();
                myApplication.setDataFilms(filmsT);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else{
            filmsT = myApplication.getDataInfo().films;
        }
        films = new LinkedList();
        ArrayList<String> immagini = new ArrayList<>();
        ArrayList<Bitmap> urls = new ArrayList<>();
         for (int i = 0; i < filmsT.size(); i++) {
            if (!filmsT.get(i).getTitle().equals("null")){
                films.add(filmsT.get(i).getTitle());
                if (filmsT.get(i).getTitle().equals("Una storia senza nome")){
                    immagini.add("http://t1.gstatic.com/images?q=tbn:ANd9GcSFg3tSeV7z9rYxyPNuDhcuspEtq2YcEfxfkItMoTaS0uuWGyU0");
                }else if (filmsT.get(i).getTitle().equals("Finding Momo")){
                    immagini.add("https://www.ucicinemas.it/media/movie/l/2018/un-figlio-all-improvviso.jpg");
                }else if (filmsT.get(i).getTitle().equals("Rolling to You")){
                    immagini.add("http://t1.gstatic.com/images?q=tbn:ANd9GcRKvfWV9Vrr6_HFdPMnSZf7e6wUbf94k2Ld4PQPOnkoaIIoNLFD");
                }else if (filmsT.get(i).getTitle().equals("Titanic")){
                    immagini.add("https://movieplayer.net-cdn.it/images/2009/09/29/la-locandina-di-titanic-7522.jpg");
                }else{
                    immagini.add(filmsT.get(i).getImg());
                }
                urls.add(null);
            }
        }
        CustomAdapter adapter = new CustomAdapter(Locandine.this.getContext(), R.layout.items_listview, films, immagini, urls);
        itemsGridView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        String item = films.get(position);
        ((cercaFilm)getActivity()).onSobstitute(1);
        ((cercaFilm)getActivity()).setNomeFilm(item);
    }

    public class DownloadFilmTask extends AsyncTask<String, Void, Boolean> {
        @Override
        public Boolean doInBackground(String... param){
            ArrayList<DataFilm> temp;
            for (int i=0; i<param.length; i++){
                if (param[i].equals("")) continue;
                temp = new ArrayList<>();
                temp = downloadFilms(param[i]);
                int l = 0;
                for (int j = 0; j < temp.size(); j++){
                    for (int k=0; k < filmsT.size(); k++){
                        if (filmsT.get(k).getId().equals(temp.get(j).getId())){
                            l = 1;
                            break;
                        }
                    }
                    if (l == 0){
                        filmsT.add(temp.get(j));
                    }
                }
            }
            filmlistener.somethingChanged();
            return true;
        }
    }

    public ArrayList<DataFilm> downloadFilms(String urlString){
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-API-Key", myApplication.getApiKey());
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            data = sb.toString();
            Log.d("downloadUrl", data.toString());
            br.close();
        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            try {
                iStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            urlConnection.disconnect();
        }
        JSONObject jObject;
        if (data.equals("")){
        }
        ArrayList<DataFilm> films = new ArrayList<>();
        try {
            jObject = new JSONObject(data);
            Log.d("ParserTask", data.toString());
            films = parseFilm(jObject);
            Log.d("ParserTask", "Executing routes");
            Log.d("ParserTask", films.toString());

        } catch (Exception e) {
            Log.d("ParserTask", e.toString());
            e.printStackTrace();
        }
        return films;
    }

    public ArrayList<DataFilm> parseFilm(JSONObject jObject) {
        ArrayList<DataFilm> dataFilms = new ArrayList<>();
        JSONArray films;
        try {
            films = jObject.getJSONArray("movies");
            for (int i = 0; i < films.length(); i++) {
                DataFilm dataFilm = new DataFilm();
                JSONObject jsonObject = films.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("title");
                String img = jsonObject.getString("poster_image_thumbnail");
                dataFilm.setId(id);
                dataFilm.setTitle(name);
                dataFilm.setImg(img);
                dataFilms.add(dataFilm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataFilms;
    }
}
