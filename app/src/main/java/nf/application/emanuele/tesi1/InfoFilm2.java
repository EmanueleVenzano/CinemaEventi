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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class InfoFilm2 extends Fragment {

    private ImageView imgFilm;
    private TextView titleFilm;
/*    private TextView genereFilm;
    private TextView durataFilm;
    private TextView descrizioneFilm;
*/  ListView explistView;
    List<ArrayList<String>> cinema;
    List<String> giorni;
    ArrayList<ArrayList<DataShowTimes>> showtimes;
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
        explistView = (ListView)view.findViewById(R.id.proiezioniListView);

        cinema = new ArrayList<>();
        giorni = new ArrayList<>();

        prepareListData();
        giorni = convertDays(giorni);
        FilmCustomAdapter listAdapter = new FilmCustomAdapter(InfoFilm2.this.getContext(), R.layout.proiezioni_listview, cinema, giorni, showtimes, name);
        explistView.setAdapter(listAdapter);
        return view;
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
        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        DataInfo dataInfo = myApplication.getDataInfo();
        String urlToString = "";
        showtimes = new ArrayList<>();
        for (int i=0; i<dataInfo.films.size(); i++){
            if (dataInfo.films.get(i).getTitle().equals(name)){
                urlToString = dataInfo.films.get(i).getImg();
                for (int j=0; j<dataInfo.showTimes.size(); j++){
                    DataShowTimes dataShowTimes = dataInfo.showTimes.get(j);
                    String cinema_name = "";
                    for (int k=0; k<dataInfo.cinemas.size(); k++){
                        if (dataInfo.cinemas.get(k).getId().equals(dataShowTimes.getCinema_id())){
                            cinema_name=dataInfo.cinemas.get(k).getName();
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
                                temp.add(dataShowTimes.getCinema_id());
                                cinema.add(temp);
                            }
                        }
                    }
                    String[] dataTime = dataShowTimes.getStart().split("T");
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
                    showtimes.get(position).add(dataShowTimes);
                }
                break;
            }
        }
        if (urlToString == null) {
            Drawable placeholder = imgFilm.getContext().getResources().getDrawable(R.drawable.bho1);
            imgFilm.setImageDrawable(placeholder);
        }else{
            new InfoFilm2.ImageDownloaderTask(imgFilm).execute(urlToString);
        }
        titleFilm.setText(Html.fromHtml("<b>Titolo: </b>" + name));
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
