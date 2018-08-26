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

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class InfoFilm2 extends Fragment {

    private ImageView imgFilm;
    private TextView titleFilm;
    ChangeListener showTimesListener = new ChangeListener();
    ArrayList<DataShowTimes> showTimes = new ArrayList<>();
    ListView explistView;
    List<ArrayList<String>> cinema = new ArrayList<>();
    List<String> giorni = new ArrayList<>();
    ArrayList<ArrayList<DataShowTimes>> showtimes = new ArrayList<>();
    String name = "";
    MyApplication myApplication;
    int p = 0;

    public static InfoFilm2 newInstance(){
        InfoFilm2 Fragment = new InfoFilm2();
        return Fragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        View view = inflater.inflate(R.layout.activity_infofilm, container, false);
        name = ((cercaFilm)getActivity()).getMyData();
        myApplication = (MyApplication) getActivity().getApplication();
        String movie_id="";
        String urlToString ="";
        for (int i=0; i<myApplication.getDataInfo().films.size(); i++){
            if (myApplication.getDataInfo().films.get(i).getTitle().equals(name)){
                movie_id = myApplication.getDataInfo().films.get(i).getId();
                urlToString = myApplication.getDataInfo().films.get(i).getImg();
                break;
            }
        }
        if (myApplication.getDataInfo().showTimes == null){
            p = 1;
            DataInfo dF = myApplication.getDataInfo();
            String[] params = new String[dF.nearestCinemas.size()];
            for (int i=0; i<dF.nearestCinemas.size(); i++){
                if (Integer.parseInt(dF.nearestCinemas.get(i).getId()) > 4){
                    params[i] = "https://api.internationalshowtimes.com/v4/showtimes?movie_id="+movie_id+"&cinema_id="+dF.nearestCinemas.get(i).getId();
                }else {
                    params[i] = "";
                }
            }
            try {
                boolean b = new DownloadShowTimesTask().execute(params).get();
                myApplication.setDataShowTimes(showTimes);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }else{
            showTimes = myApplication.getDataInfo().showTimes;
        }
        imgFilm = (ImageView)view.findViewById(R.id.imgFilm);
        titleFilm = (TextView)view.findViewById(R.id.titleFilm);
        explistView = (ListView)view.findViewById(R.id.proiezioniListView);
        prepareListData();
        if (urlToString.equals("null")) {
            Drawable placeholder = imgFilm.getContext().getResources().getDrawable(R.drawable.bho1);
            imgFilm.setImageDrawable(placeholder);
        }else{
            new InfoFilm2.ImageDownloaderTask(imgFilm).execute(urlToString);
        }
        titleFilm.setText(name);
        orderByDate();
        giorni = convertDays(giorni);
        FilmCustomAdapter listAdapter = new FilmCustomAdapter(InfoFilm2.this.getContext(), R.layout.proiezioni_listview, cinema, giorni, showtimes, name, explistView);
        explistView.setAdapter(listAdapter);
        FilmCustomAdapter listadp = (FilmCustomAdapter) explistView.getAdapter();
        if (listadp != null) {
            int totalHeight = 0;
            for (int i = 0; i < listadp.getCount(); i++) {
                View listItem = listadp.getView(i, null, explistView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
            }
            ViewGroup.LayoutParams params = explistView.getLayoutParams();
            params.height = totalHeight + (explistView.getDividerHeight() * (listadp.getCount() - 1));
            explistView.setLayoutParams(params);
            explistView.requestLayout();
            listadp.setHeight(explistView.getLayoutParams().height);
        }
        return view;
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

    public class DownloadShowTimesTask extends AsyncTask<String, Void, Boolean>{
        @Override
        public Boolean doInBackground(String... param){
            for (int i=0; i<param.length; i++){
                if (param[i].equals("")) continue;
                ArrayList<DataShowTimes> temp = downloadShowTimes(param[i]);
                showTimes.addAll(temp);
            }
            ArrayList<DataShowTimes> filmFiumara = new ArrayList<>();
            for (int i=0; i<showTimes.size(); i++){
                if (showTimes.get(i).getCinema_id().equals("60603")){
                    filmFiumara.add(showTimes.get(i));
                }
            }
            for (int i = 0; i < myApplication.getDataInfo().nearestCinemas.size(); i++){
                if (Integer.parseInt(myApplication.getDataInfo().nearestCinemas.get(i).getId()) > 4){
                    continue;
                }
                ArrayList<DataShowTimes> temp2 = createShowTime(Integer.parseInt(myApplication.getDataInfo().nearestCinemas.get(i).getId()), filmFiumara);
                showTimes.addAll(temp2);
            }
            showTimesListener.somethingChanged();
            return true;
        }
    }

    public ArrayList<DataShowTimes> downloadShowTimes(String urlString){
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(urlString);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("X-API-Key",myApplication.getApiKey());
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
            data = "";
        }
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

    public List<String> convertDays(List<String> actual){
        List<String> temp = new ArrayList<>();
        for (int k=0; k<actual.size()-1; k++){
            int i=k;
            String[] b = actual.get(i).split("-");
            Calendar bigger = Calendar.getInstance();
            bigger.set(Integer.parseInt(b[0]), Integer.parseInt(b[1]), Integer.parseInt(b[2]));
            for (int j=i+1; j<actual.size(); j++){
                String[] l = actual.get(j).split("-");
                Calendar little = Calendar.getInstance();
                little.set(Integer.parseInt(l[0]), Integer.parseInt(l[1]), Integer.parseInt(l[2]));
                if (bigger.after(little)){
                    ArrayList<DataShowTimes> tempS = showtimes.get(i);
                    showtimes.set(i, showtimes.get(j));
                    showtimes.set(j, tempS);
                    String tempD =  actual.get(i);
                    actual.set(i, actual.get(j));
                    actual.set(j, tempD);
                    i=j;
                }
            }
        }
        for (String headerTitle: actual){
            String[] dataTime = headerTitle.split("-");
            Calendar calendar = Calendar.getInstance();
            calendar.set(Integer.parseInt(dataTime[0]), Integer.parseInt(dataTime[1])-1, Integer.parseInt(dataTime[2]));
            Date date = calendar.getTime();
            int day = date.getDay();
            switch (day){
                case 0: temp.add("Domenica"); break;
                case 1: temp.add("Lunedì"); break;
                case 2: temp.add("Martedì"); break;
                case 3: temp.add("Mercoledì"); break;
                case 4: temp.add("Giovedì"); break;
                case 5: temp.add("Venerdì"); break;
                case 6: temp.add("Sabato"); break;
                default: temp.add("Invalid"); break;
            }
        }
        return temp;
    }

    public void prepareListData () {
        for (int j=0; j<showTimes.size(); j++){
            String cinema_name = "";
            for (int k=0; k<myApplication.getDataInfo().cinemas.size(); k++){
                if (myApplication.getDataInfo().cinemas.get(k).getId().equals(showTimes.get(j).getCinema_id())){
                    cinema_name=myApplication.getDataInfo().cinemas.get(k).getName();
                    int l=0;
                    for (int m=0; m<cinema.size(); m++){
                        if (cinema.get(m).get(0).equals(cinema_name)){
                            l=1;
                            break;
                        }
                    }
                    if (l==0){
                        ArrayList<String> temp = new ArrayList<>();
                        temp.add(cinema_name);
                        temp.add(showTimes.get(j).getCinema_id());
                        cinema.add(temp);
                    }
                }
            }
            String[] dataTime = showTimes.get(j).getStart().split("T");
            int position=-1;
            for (int k=0; k<giorni.size(); k++){
                if (giorni.get(k).equals(dataTime[0])){
                    position=k;
                    break;
                }
            }
            if (position==-1) {
                giorni.add(dataTime[0]);
                position=giorni.size()-1;
            }
            while(position>=showtimes.size()) showtimes.add(new ArrayList<DataShowTimes>());
            showtimes.get(position).add(showTimes.get(j));
        }
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

    private void orderByDate(){
        for (int i=0; i<showtimes.size()-1; i++){
            for (int j=0; j<showtimes.get(i).size(); j++){
                int index = j;
                for (int k=j+1; k<showtimes.get(i).size(); k++){
                    Calendar little = Calendar.getInstance();
                    String[] temp = showtimes.get(i).get(index).getStart().split("T");
                    String[] temp1 = temp[0].split("-");
                    String[] temp2 = temp[1].split(":");
                    little.set(Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1])-1, Integer.parseInt(temp1[2]), Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
                    Date l = little.getTime();
                    Calendar bigger = Calendar.getInstance();
                    String[] atemp = showtimes.get(i).get(k).getStart().split("T");
                    String[] atemp1 = atemp[0].split("-");
                    String[] atemp2 = atemp[1].split(":");
                    bigger.set(Integer.parseInt(atemp1[0]), Integer.parseInt(atemp1[1])-1, Integer.parseInt(atemp1[2]), Integer.parseInt(atemp2[0]), Integer.parseInt(atemp2[1]));
                    Date b = bigger.getTime();
                    if (little.after(bigger)){
                        index = k;
                    }
                }
                DataShowTimes dataShowTimes = showtimes.get(i).get(index);
                showtimes.get(i).set(index, showtimes.get(i).get(j));
                showtimes.get(i).set(j, dataShowTimes);
            }
        }
    }


}
