package nf.application.emanuele.tesi1;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class MyApplication extends Application {
    private DataInfo dataInfo = new DataInfo();
    @Override
    public void onCreate() {
        super.onCreate();
        ArrayList<DataCinema> cinemas = new ArrayList<>();
        ArrayList<DataFilm> films = new ArrayList<>();
        ArrayList<DataShowTimes> showTimes = new ArrayList<>();
        cinemas = downloadCinemas("https://api.internationalshowtimes.com/v4/cinemas/?city_ids=11861");
        int downloaded= cinemas.size();
        cinemas.get(0).setUrl_img("http://www.spimgenova.it/wp-content/uploads/2017/10/spim-uci.jpg");
        cinemas.add(new DataCinema("http://www.portoantico.it/wp-content/uploads/2014/04/the-space-cinema-002_mini-960x600.jpg", "TheSpace", "1", "44.408220", "8.921539", "Via Magazzini del Cotone", "16128", "Genova"));
        cinemas.add(new DataCinema("http://www.ilsecoloxix.it/rf/Image-lowres_Multimedia/IlSecoloXIXWEB/genova/foto/2015/01/16/cinema_italia-H150116150454.jpg", "CinemaItalia", "2", "44.403022", "8.684162", "Via Sauli Pallavicino, 21, Arenzano", "16011", "Arenzano"));
        cinemas.add(new DataCinema("https://www.taxidrivers.it/wp-content/uploads/2016/05/foto-sivori.jpg", "CinemaSivori", "3", "44.409894", "8.936847", "Salita di S.Caterina, 48", "16123", "Genova"));
        cinemas.add(new DataCinema("http://www.ilsecoloxix.it/rf/Image-lowres_Multimedia/IlSecoloXIXWEB/genova/foto/2014/01/22/cinemapalmaro.jpg","CinemaPalmaro", "4", "44.427484", "8.774715", "Via Pra', 164, Genova Pra'", "16157", "Genova"));
        films = downloadFilms("https://api.internationalshowtimes.com/v4/movies/?city_ids=11861");
        showTimes = downloadShowTimes("https://api.internationalshowtimes.com/v4/showtimes/?city_ids=11861");
        ArrayList<DataShowTimes> filmFiumara = new ArrayList<>();
        for (int i=0; i<showTimes.size(); i++){
            if (showTimes.get(i).getCinema_id().equals("60603")){
                filmFiumara.add(showTimes.get(i));
            }
        }
        for (int i=downloaded; i<cinemas.size(); i++){
            ArrayList<DataShowTimes> temp = createShowTime(Integer.parseInt(cinemas.get(i).getId()), filmFiumara);
            showTimes.addAll(temp);
        }
        dataInfo.cinemas = cinemas;
        dataInfo.films = films;
        dataInfo.showTimes = showTimes;
    }

    public DataInfo getDataInfo (){
        return dataInfo;
    }

    public ArrayList<DataShowTimes> createShowTime (int id, ArrayList<DataShowTimes> film){
        ArrayList<DataShowTimes> dataShowTimes = new ArrayList<>();
        Random random = new Random();
        int percent;
        switch (id){
            case 1: percent=60; break;
            case 2: percent=40; break;
            case 3: percent=20; break;
            case 4: percent=20; break;
            default: percent=20; break;
        }
        for (int i=0; i<film.size(); i++){
            if (random.nextInt(100)<percent){
                    dataShowTimes.add(new DataShowTimes(String.valueOf(id), film.get(i).getMovie_id(), film.get(i).getStart(), film.get(i).getLanguage(), film.get(i).getSubtitle(), film.get(i).getAuditorium(), film.get(i).isIs3D(), film.get(i).isIMax(), film.get(i).getLink()));
            }
        }
        return dataShowTimes;
    }

    public ArrayList<DataShowTimes> downloadShowTimes(String url){
        String data = "";
        try{
            data = new downloadUrlTask().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONObject jObject;
        ArrayList<DataShowTimes> showTimes = new ArrayList<>();
        try {
            jObject = new JSONObject(data);
            Log.d("ParserTask", data.toString());
            showTimes = parseShowTime(jObject);
            Log.d("ParserTask", "Executing routes");
            Log.d("ParserTask", showTimes.toString());
        } catch (Exception e) {
            Log.d("ParserTask", e.toString());
            e.printStackTrace();
        }
        return showTimes;
    }

    public ArrayList<DataFilm> downloadFilms(String url){
        String data = "";
        try{
            data = new downloadUrlTask().execute(url).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        JSONObject jObject;
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

    public ArrayList<DataCinema> downloadCinemas(String url){
        String data = "";
        try{
            data = new downloadUrlTask().execute(url).get();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        JSONObject jObject;
        ArrayList<DataCinema> cinema = new ArrayList<>();
        try {
            jObject = new JSONObject(data);
            Log.d("ParserTask", data.toString());
            cinema = parseCinema(jObject);
            Log.d("ParserTask", "Executing routes");
            Log.d("ParserTask", cinema.toString());

        } catch (Exception e) {
            Log.d("ParserTask", e.toString());
            e.printStackTrace();
        }
        return cinema;
    }

    private class downloadUrlTask extends AsyncTask<String, String, String>{
        @Override
        protected String doInBackground(String... param){
            String data = "";
            InputStream iStream = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(param[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("X-API-Key","9AmxQRqw3gltnlBKQvR9CgJDwUaC6DLg");
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
            return data;
        }
        @Override
        protected void onPostExecute (String result){
            super.onPostExecute(result);
        }
    }
    public ArrayList<DataCinema> parseCinema(JSONObject jObject) {
        ArrayList<DataCinema> dataCinemas = new ArrayList<>();
        JSONArray cinemas;
        try {
            cinemas = jObject.getJSONArray("cinemas");
            for (int i = 0; i < cinemas.length(); i++) {
                DataCinema dataCinema = new DataCinema();
                JSONObject jsonObject = cinemas.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("name");

                JSONObject location = jsonObject.getJSONObject("location");
                String lat = location.getString("lat");
                String lon = location.getString("lon");

                JSONObject address = location.getJSONObject("address");
                String street = address.getString("display_text");
                String cap = address.getString("zipcode");
                String city = address.getString("city");

                dataCinema.setId(id);
                dataCinema.setName(name);
                dataCinema.setLat(lat);
                dataCinema.setLon(lon);
                dataCinema.setAddress(street);
                dataCinema.setCap(cap);
                dataCinema.setCity(city);
                dataCinemas.add(dataCinema);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataCinemas;
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
    public ArrayList<DataShowTimes> parseShowTime(JSONObject jObject) {
        ArrayList<DataShowTimes> dataShowTimes = new ArrayList<>();
        JSONArray showTime;
        try {
            showTime = jObject.getJSONArray("showtimes");
            for (int i = 0; i < showTime.length(); i++) {
                DataShowTimes dataShowTime = new DataShowTimes();
                JSONObject jsonObject = showTime.getJSONObject(i);

                String cinema = jsonObject.getString("cinema_id");
                String film = jsonObject.getString("movie_id");
                String start = jsonObject.getString("start_at");
                String language = jsonObject.getString("language");
                String sub = jsonObject.getString("subtitle_language");
                String auditorium = jsonObject.getString("auditorium");
                boolean is3D = jsonObject.getBoolean("is_3d");
                boolean isIMax = jsonObject.getBoolean("is_imax");
                String link = jsonObject.getString("booking_link");

                dataShowTime.setCinema_id(cinema);
                dataShowTime.setMovie_id(film);
                dataShowTime.setStart(start);
                dataShowTime.setLanguage(language);
                dataShowTime.setSubtitle(sub);
                dataShowTime.setAuditorium(auditorium);
                dataShowTime.setIs3D(is3D);
                dataShowTime.setIMax(isIMax);
                dataShowTime.setLink(link);

                dataShowTimes.add(dataShowTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataShowTimes;
    }
}
