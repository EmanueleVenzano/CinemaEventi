package nf.application.emanuele.tesi1;

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
    private List<ArrayList<String>> films;
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
        for (int i = 0; i < filmsT.size(); i++) {
            ArrayList<String> temp = new ArrayList<>();
            if (!filmsT.get(i).getTitle().equals("null")){
                temp.add(filmsT.get(i).getTitle());
                temp.add(filmsT.get(i).getImg());
                films.add(temp);
            }
        }
        CustomAdapter adapter = new CustomAdapter(Locandine.this.getContext(), R.layout.items_listview, films);
        itemsGridView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        ArrayList<String> item = films.get(position);
        ((cercaFilm)getActivity()).onSobstitute(1);
        ((cercaFilm)getActivity()).setNomeFilm(item.get(0));
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
            data = "{" +
                    "    \"meta_info\": {\n" +
                    "        \"range_from\": 0,\n" +
                    "        \"range_to\": 3975,\n" +
                    "        \"total_count\": 3976\n" +
                    "    },\n" +
                    "    \"movies\": [\n" +
                    "        {\n" +
                    "            \"id\": \"47841\",\n" +
                    "            \"slug\": \"the-meg\",\n" +
                    "            \"title\": \"The Meg\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/xqECHNvzbDL5I3iiOVUkVPJMSbc.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"33329\",\n" +
                    "            \"slug\": \"the-happytime-murders\",\n" +
                    "            \"title\": \"The Happytime Murders\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/rWxkur51srfVnMn2QOFjE7mbq6h.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"45714\",\n" +
                    "            \"slug\": \"mission-impossible-fallout\",\n" +
                    "            \"title\": \"Mission: Impossible - Fallout\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/AkJQpZp9WoNdj7pLYSj1L0RcMMN.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"35581\",\n" +
                    "            \"slug\": \"christopher-robin\",\n" +
                    "            \"title\": \"Christopher Robin\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/xR5w0he6czZkcAz459a4iPBqXGe.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"43880\",\n" +
                    "            \"slug\": \"boku-no-hero-academia-the-movie\",\n" +
                    "            \"title\": \"My Hero Academia the Movie: The Two Heroes\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/783vbPefbFReMBRwbwD3HQkxGEr.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"47079\",\n" +
                    "            \"slug\": \"normandie-nue-351664\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"28564\",\n" +
                    "            \"slug\": \"don-t-worry-he-won-t-get-far-on-foot\",\n" +
                    "            \"title\": \"Don't Worry, He Won't Get Far on Foot\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/rKsiN37qMt8jad5GikZzSeevyI9.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"26665\",\n" +
                    "            \"slug\": \"jim-knopf-und-lukas-der-lokomotivfuhrer\",\n" +
                    "            \"title\": \"Jim Button and Luke the Engine Driver\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"39977\",\n" +
                    "            \"slug\": \"303\",\n" +
                    "            \"title\": \"303\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/rnRR4p49Ubv9tZub6PzQ1qK4GG2.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"24101\",\n" +
                    "            \"slug\": \"der-letzte-akt\",\n" +
                    "            \"title\": \"The Last Act\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/e3kvcj214d4v7DoJqTY5wQYDiIw.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"43582\",\n" +
                    "            \"slug\": \"captain-morten-and-the-spider-queen\",\n" +
                    "            \"title\": \"Captain Morten and the Spider Queen\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/nJhZ5S1MBF4Yj1MUbmcrQGcyw46.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"21023\",\n" +
                    "            \"slug\": \"they\",\n" +
                    "            \"title\": \"They\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/qiRbYjz37VqNUOC9snh1l7aPuoO.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"46361\",\n" +
                    "            \"slug\": \"vs-en-film\",\n" +
                    "            \"title\": \"Kaito Sentai Lupinranger VS Keisatsu Sentai Patranger en film\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"43178\",\n" +
                    "            \"slug\": \"roulez-jeunesse\",\n" +
                    "            \"title\": \"The Troubleshooter\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"39376\",\n" +
                    "            \"slug\": \"arizona\",\n" +
                    "            \"title\": \"Arizona\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/qzeoTO2GoOvnBsYGxE4iJq1hrc8.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"23778\",\n" +
                    "            \"slug\": \"harry-potter-and-the-goblet-of-fire\",\n" +
                    "            \"title\": \"Harry Potter and the Goblet of Fire\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/6sASqcdrEHXxUhA3nFpjrRecPD2.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"46802\",\n" +
                    "            \"slug\": \"load-wedding\",\n" +
                    "            \"title\": \"Load Wedding\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"14748\",\n" +
                    "            \"slug\": \"harry-potter-und-der-gefangene-von-askaban\",\n" +
                    "            \"title\": \"Harry Potter and the Prisoner of Azkaban\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/uDQibffYgssdiqx7izO57wdLc6.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"40092\",\n" +
                    "            \"slug\": \"27d26e23-35ad-4568-8df8-4a7ccf9ff0b6\",\n" +
                    "            \"title\": \"Non Non Biyori the Movie: Vacation\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"36930\",\n" +
                    "            \"slug\": \"zimna-wojna\",\n" +
                    "            \"title\": \"Cold War\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/wsv6vWhJyRhwzmDN4kjs3ACV97Q.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"47947\",\n" +
                    "            \"slug\": \"de-chaque-instant\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"9232\",\n" +
                    "            \"slug\": \"rennschwein-rudi-ruessel\",\n" +
                    "            \"title\": \"Rudy, the Racing Pig\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"33658\",\n" +
                    "            \"slug\": \"i-feel-pretty\",\n" +
                    "            \"title\": \"I Feel Pretty\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/iuPs45XIxfARKPLEkCGXWUrBrTR.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"35477\",\n" +
                    "            \"slug\": \"premiere-annee\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"38185\",\n" +
                    "            \"slug\": \"a-casa-tutti-bene\",\n" +
                    "            \"title\": \"There Is No Place Like Home\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"47056\",\n" +
                    "            \"slug\": \"destination-wedding-354201\",\n" +
                    "            \"title\": \"Destination Wedding\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/hCedEB69rdTH3FoUzuvmeikwfys.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"42218\",\n" +
                    "            \"slug\": \"unfriended-game-night\",\n" +
                    "            \"title\": \"Unfriended: Dark Web\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/q27yNUaEt6ohcQMEVHwvPaoKBT8.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"25169\",\n" +
                    "            \"slug\": \"a-woman-at-war\",\n" +
                    "            \"title\": \"Woman at War\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/9IwRoLxbYF1Vu1KxHt0aLbyeS5u.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"48740\",\n" +
                    "            \"slug\": \"the-day-after-valentine-s\",\n" +
                    "            \"title\": \"The Day After Valentine's\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"38121\",\n" +
                    "            \"slug\": \"safari-match-me-if-you-can\",\n" +
                    "            \"title\": \"Safari: Match Me If You Can\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"48815\",\n" +
                    "            \"slug\": \"kolamaavu-kokila-coco\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"42750\",\n" +
                    "            \"slug\": \"mcqueen\",\n" +
                    "            \"title\": \"McQueen\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/bc5kAqhNfLitH7zMlLvLRbxV0xD.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"48154\",\n" +
                    "            \"slug\": \"cliff-richard-live-60th-anniversary-tour\",\n" +
                    "            \"title\": \"Cliff Richard Live: 60th Anniversary Tour\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"41030\",\n" +
                    "            \"slug\": \"black-47\",\n" +
                    "            \"title\": \"Black 47\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/gZiu9RIcMeQLUaDVqQffQTl6bhy.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"29483\",\n" +
                    "            \"slug\": \"peter-rabbit\",\n" +
                    "            \"title\": \"Peter Rabbit\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/2yjSvEDuM3rLDng40erLsWkQRfn.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"17834\",\n" +
                    "            \"slug\": \"la-mort-du-dieu-serpent\",\n" +
                    "            \"title\": \"Death of the Serpent God\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"19545\",\n" +
                    "            \"slug\": \"everybody-wants-some\",\n" +
                    "            \"title\": \"Everybody Wants Some!!\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/mIpd0rGxruvxCnMSmh4gd8wuhmv.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"21188\",\n" +
                    "            \"slug\": \"the-young-karl-marx\",\n" +
                    "            \"title\": \"The Young Karl Marx\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/4VwBXiCzXKJyHfRdVBb843m23dx.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"22691\",\n" +
                    "            \"slug\": \"lomo-the-language-of-many-others\",\n" +
                    "            \"title\": \"Lomo - The Language of many others\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"24210\",\n" +
                    "            \"slug\": \"get-out\",\n" +
                    "            \"title\": \"Get Out\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/rdPGUJhadPg7FGFNzavib0iwTor.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"24787\",\n" +
                    "            \"slug\": \"suspiria-2017\",\n" +
                    "            \"title\": \"Suspiria\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/vF1twpLJapYBqaxqNgCPSj581yg.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"5440\",\n" +
                    "            \"slug\": \"101-dalmatiner\",\n" +
                    "            \"title\": \"101 Dalmatians\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/8o2ADoAyG796UwTjwBFjPyBz0yG.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"12664\",\n" +
                    "            \"slug\": \"blade-runner-2\",\n" +
                    "            \"title\": \"Blade Runner 2049\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/c0jCZGc0XMW1TpRP2nRCrwY3Tex.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"26099\",\n" +
                    "            \"slug\": \"lulu-the-movie\",\n" +
                    "            \"title\": \"Lulu the Movie\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"26598\",\n" +
                    "            \"slug\": \"loveless\",\n" +
                    "            \"title\": \"Loveless\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/myFTOsVDxu5sAWcw8hr0LlxBLnq.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"27706\",\n" +
                    "            \"slug\": \"senor-dame-paciencia\",\n" +
                    "            \"title\": \"Lord, Give Me Patience\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"31787\",\n" +
                    "            \"slug\": \"ostwind-aufbruch-nach-ora\",\n" +
                    "            \"title\": \"Windstorm 3\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"31876\",\n" +
                    "            \"slug\": \"29-1\",\n" +
                    "            \"title\": \"29+1\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"33087\",\n" +
                    "            \"slug\": \"phenomena\",\n" +
                    "            \"title\": \"Phenomena\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/qUwGQT1EtkIm8vtN3VtnBDOoV7q.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"33787\",\n" +
                    "            \"slug\": \"volta\",\n" +
                    "            \"title\": \"Volta\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"33983\",\n" +
                    "            \"slug\": \"filles-du-feu\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"35342\",\n" +
                    "            \"slug\": \"je-vais-mieux\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"39170\",\n" +
                    "            \"slug\": \"larguees\",\n" +
                    "            \"title\": \"Dumped\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"41393\",\n" +
                    "            \"slug\": \"upgrade\",\n" +
                    "            \"title\": \"Upgrade\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/adOzdWS35KAo21r9R4BuFCkLer6.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"41538\",\n" +
                    "            \"slug\": \"gurrumul\",\n" +
                    "            \"title\": \"Gurrumul\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/qTnmj3eR2gsLnA182JuJ6jMwciO.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"42533\",\n" +
                    "            \"slug\": \"what-keeps-you-alive\",\n" +
                    "            \"title\": \"What Keeps You Alive\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/iHrNKFoG3XOy5oPygR90TR5nfJt.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"42817\",\n" +
                    "            \"slug\": \"footprints-the-path-of-your-life\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"44372\",\n" +
                    "            \"slug\": \"on-a-20-ans-pour-changer-le-monde\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"44524\",\n" +
                    "            \"slug\": \"en-guerre\",\n" +
                    "            \"title\": \"At War\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"46580\",\n" +
                    "            \"slug\": \"i-kill-giants-356342\",\n" +
                    "            \"title\": \"I Kill Giants\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/cvit6HDbXHE6W5kGPd47jd0wthQ.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"48993\",\n" +
                    "            \"slug\": \"der-kleine-rabe-socke-3\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"3334\",\n" +
                    "            \"slug\": \"der-zauberer-von-oz\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"3368\",\n" +
                    "            \"slug\": \"flashdance\",\n" +
                    "            \"title\": \"Flashdance\",\n" +
                    "            \"poster_image_thumbnail\": \"http://image.tmdb.org/t/p/w154/pW8tRnHJbZfobnEX16CjkOg8bAH.jpg\"\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"3515\",\n" +
                    "            \"slug\": \"filmclub\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"3563\",\n" +
                    "            \"slug\": \"die-drei-raeuber\",\n" +
                    "            \"title\": \"The Three Robbers\",\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"49067\",\n" +
                    "            \"slug\": \"kidsfilmfest-kurzfilme-fur-kids\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        },\n" +
                    "        {\n" +
                    "            \"id\": \"49099\",\n" +
                    "            \"slug\": \"luv-lee-amrum-der-film\",\n" +
                    "            \"title\": null,\n" +
                    "            \"poster_image_thumbnail\": null\n" +
                    "        }\n" +
                    "    ]\n" +
                    "}";
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
