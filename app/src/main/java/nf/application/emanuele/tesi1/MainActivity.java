package nf.application.emanuele.tesi1;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

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

import static java.lang.Math.sqrt;

public class MainActivity extends Activity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private MyApplication myApplication;
    private LatLng location = null;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    ArrayList<DataCinema> cinemas;
    ChangeListener listener;
    ArrayList<DataCinema> nearestCinemas = null;

    @Override
    public void onCreate (Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        getIntent().getFlags();
         myApplication = (MyApplication) getApplication();
         listener = new ChangeListener();
         cinemas = new ArrayList<>();
        if (myApplication.getDataInfo() == null) {
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainFragmentLogo()).commit();
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                boolean b = checkLocationPermission();
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    buildGoogleApiClient();
                }
            } else {
                buildGoogleApiClient();
            }
            listener.setChangeListener(new ChangeListener.Listener() {
                @Override
                public void onChange(LatLng latLng) {
                    myApplication.setDataCinemas(cinemas);
                    myApplication.setDataNearestCinemas(nearestCinemas);
                    getFragmentManager().beginTransaction().replace(android.R.id.content, new MainFragment()).commit();
                }
            });
            new DownloadCinemaTask().execute("https://api.internationalshowtimes.com/v4/cinemas/?countries="+myApplication.getCountry_code());
        }else{
            getFragmentManager().beginTransaction().replace(android.R.id.content, new MainFragment()).commit();
        }
    }

    public class DownloadCinemaTask extends AsyncTask<String, Void, Boolean> {
        @Override
        public Boolean doInBackground(String... param){
            try{
                cinemas = downloadCinemas(param[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            nearestCinemas = new ArrayList<>();
            int position = -1;
            for (int i=0; i<cinemas.size(); i++){
                if (cinemas.get(i).getId().equals("60603")){
                    position = i;
                    break;
                }
            }
            if (position > -1) {
                cinemas.get(position).setUrl_img("http://www.spimgenova.it/wp-content/uploads/2017/10/spim-uci.jpg");
            }
            cinemas.add(new DataCinema("http://www.portoantico.it/wp-content/uploads/2014/04/the-space-cinema-002_mini-960x600.jpg", "The Space", "1", "44.408220", "8.921539", "Via Magazzini del Cotone", "16128", "Genova"));
            cinemas.add(new DataCinema("http://www.ilsecoloxix.it/rf/Image-lowres_Multimedia/IlSecoloXIXWEB/genova/foto/2015/01/16/cinema_italia-H150116150454.jpg", "Cinema Italia", "2", "44.403022", "8.684162", "Via Sauli Pallavicino, 21, Arenzano", "16011", "Arenzano"));
            cinemas.add(new DataCinema("https://www.taxidrivers.it/wp-content/uploads/2016/05/foto-sivori.jpg", "Cinema Sivori", "3", "44.409894", "8.936847", "Salita di S.Caterina, 48", "16123", "Genova"));
            cinemas.add(new DataCinema("http://www.ilsecoloxix.it/rf/Image-lowres_Multimedia/IlSecoloXIXWEB/genova/foto/2014/01/22/cinemapalmaro.jpg","Cinema Palmaro", "4", "44.427484", "8.774715", "Via Pra', 164, Genova Pra'", "16157", "Genova"));
            while (location == null){}
            for (int i=0; i<cinemas.size(); i++){
                LatLng latLngCinema = new LatLng(Double.parseDouble(cinemas.get(i).getLat()), Double.parseDouble(cinemas.get(i).getLon()));
                double d = sqrt(((latLngCinema.latitude-location.latitude)*(latLngCinema.latitude-location.latitude))+(
                        (latLngCinema.longitude-location.longitude)*(latLngCinema.longitude-location.longitude)));
                if (d < 0.2851150260859641){
                    nearestCinemas.add(cinemas.get(i));
                }
            }
            listener.somethingChanged();
            return true;
        }
    }

    public ArrayList<DataCinema> downloadCinemas(String urlString){
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
        ArrayList<DataCinema> cinema = new ArrayList<>();
        if (data.equals("")){
            data = "{\"cinemas\":[{\"id\":\"60586\",\"slug\":\"multisala-gloria-milano\",\"name\":\"Multisala Gloria | Milano\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.4665,\"lon\":9.16173,\"address\":{\"display_text\":\"Corso Vercelli, 18,, Milano\",\"street\":\"Corso Vercelli\",\"house\":\"18\",\"zipcode\":\"20145\",\"city\":\"Milano\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60587\",\"slug\":\"alessandria-zona-artigianale-d5\",\"name\":\"Alessandria\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":44.8852,\"lon\":8.69838,\"address\":{\"display_text\":\"Via della Valletta, n. 160 Zona Industriale D5 Alessandria, Spinetta Marengo Alessandria\",\"street\":\"Viale della Valletta\",\"house\":null,\"zipcode\":\"15122\",\"city\":\"Zona Artigianale \\\"D5\\\"\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60588\",\"slug\":\"ancona\",\"name\":\"Ancona\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.5623,\"lon\":13.512,\"address\":{\"display_text\":\"Via Filonzi, snc, Ancona\",\"street\":\"Via Filonzi Pietro\",\"house\":null,\"zipcode\":\"60131\",\"city\":\"Ancona\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60589\",\"slug\":\"arezzo\",\"name\":\"Arezzo\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.2156,\"lon\":11.7528,\"address\":{\"display_text\":\"Via Turati, n. 2, Arezzo\",\"street\":\"Viale Filippo Turati\",\"house\":\"2\",\"zipcode\":\"52100\",\"city\":\"Arezzo\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60590\",\"slug\":\"bicocca-milano\",\"name\":\"Bicocca | Milano\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.5218,\"lon\":9.21614,\"address\":{\"display_text\":\"Viale Sarca, n. 336 angolo Via Chiese, Milano\",\"street\":\"Viale Sarca\",\"house\":null,\"zipcode\":\"20126\",\"city\":\"Milano\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60591\",\"slug\":\"bozen-bolzano\",\"name\":\"Bozen\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":46.4861,\"lon\":11.3359,\"address\":{\"display_text\":\"TWENTY, G. Galileistr., 20 – I-39100 Bozen\",\"street\":\"Via G. Galilei\",\"house\":\"20\",\"zipcode\":\"39100\",\"city\":\"Bolzano\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60592\",\"slug\":\"cagliari\",\"name\":\"Cagliari\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":39.2271,\"lon\":9.09786,\"address\":{\"display_text\":\"Via Santa Gilla, n. 18, Piazza Unione Sarda Cagliari\",\"street\":\"Via Santa Gilla\",\"house\":\"18\",\"zipcode\":\"09122\",\"city\":\"Cagliari\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60594\",\"slug\":\"casoria-napoli\",\"name\":\"Casoria | Napoli\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":40.8977,\"lon\":14.3152,\"address\":{\"display_text\":\"Via San Salvatore,, Circumvallazione Esterna\\r\\nCasoria\",\"street\":\"Via Circumvallazione esterna di Napoli\",\"house\":null,\"zipcode\":\"80026\",\"city\":\"Casoria\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60595\",\"slug\":\"catania-misterbianco\",\"name\":\"Catania\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":37.4767,\"lon\":15.0126,\"address\":{\"display_text\":\"presso Centro Commerciale Centro Sicilia, SP 54 Contrada Cubba\\r\\nMisterbianco Catania\",\"street\":\"Strada Provinciale 54\",\"house\":null,\"zipcode\":\"95045\",\"city\":\"Misterbianco\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60597\",\"slug\":\"cinepolis-marcianise-caserta-loc-aurno\",\"name\":\"Cinepolis Marcianise | Caserta\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":41.0048,\"lon\":14.3284,\"address\":{\"display_text\":\"Centro Commerciale Campania,, 81025 Aurno,\\r\\nMarcianise\",\"street\":\"S.S. Sannitica 87\",\"house\":null,\"zipcode\":\"81025\",\"city\":\"Loc. Aurno, Marcianise\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60596\",\"slug\":\"certosa-milano\",\"name\":\"Certosa | Milano\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.5096,\"lon\":9.12727,\"address\":{\"display_text\":\"Via Gentile, n. 3 angolo Via Stephenson, Milano\",\"street\":\"Via Giovanni Gentile\",\"house\":\"3\",\"zipcode\":\"20157\",\"city\":\"Milano\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60599\",\"slug\":\"curno-bergamo\",\"name\":\"Curno | Bergamo\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.6846,\"lon\":9.612,\"address\":{\"display_text\":\"Via Lega Lombarda, n. 39, Curno Bergamo\",\"street\":\"Via Lega Lombarda\",\"house\":\"39\",\"zipcode\":\"24035\",\"city\":\"Curno\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60598\",\"slug\":\"como-lucino\",\"name\":\"Como\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.7818,\"lon\":9.04191,\"address\":{\"display_text\":\"Via Leopardi, n. 1/A, Montano Lucino - Como\",\"street\":\"Via G. Leopardi\",\"house\":\"1\",\"zipcode\":\"22070\",\"city\":\"Lucino\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60600\",\"slug\":\"fano-bellocchi\",\"name\":\"Fano\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.8018,\"lon\":13.0142,\"address\":{\"display_text\":\"Via Einaudi, n. 20, Fano Pesaro-Urbino\",\"street\":\"Via Luigi Einaudi\",\"house\":\"20\",\"zipcode\":\"61032\",\"city\":\"Bellocchi\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60602\",\"slug\":\"firenze\",\"name\":\"Firenze\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.7724,\"lon\":11.1888,\"address\":{\"display_text\":\"Via del Cavallaccio, Firenze\",\"street\":\"Via del Cavallaccio\",\"house\":null,\"zipcode\":\"50142\",\"city\":\"Firenze\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60605\",\"slug\":\"gualtieri\",\"name\":\"Gualtieri\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":44.8887,\"lon\":10.6308,\"address\":{\"display_text\":\"Via Matteotti s/n - c/o Centro Commerciale Ligabue, Gualtieri Reggio Emilia\",\"street\":\"Via Matteotti\",\"house\":\"1\",\"zipcode\":\"42044\",\"city\":\"Gualtieri\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60606\",\"slug\":\"jesi-ancona\",\"name\":\"Jesi | Ancona\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.5186,\"lon\":13.254,\"address\":{\"display_text\":\"Via Marco Polo, n. 5, Jesi Ancona\",\"street\":\"Via Marco Polo\",\"house\":null,\"zipcode\":\"60035\",\"city\":\"Jesi\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60604\",\"slug\":\"fiume-veneto-pordenone\",\"name\":\"Fiume Veneto | Pordenone\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.9457,\"lon\":12.7225,\"address\":{\"display_text\":\"Via Maestri del Lavoro, n. 51, Fiume Veneto Pordenone\",\"street\":\"Via Maestri del Lavoro\",\"house\":\"51\",\"zipcode\":\"33080\",\"city\":\"Fiume Veneto\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60603\",\"slug\":\"fiumara-genova\",\"name\":\"Fiumara | Genova\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":44.4134,\"lon\":8.88077,\"address\":{\"display_text\":\"Via Mantovani, Genova Sampierdarena\",\"street\":\"Via Paolo Mantovani\",\"house\":null,\"zipcode\":\"16149\",\"city\":\"Genova\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60601\",\"slug\":\"ferrara\",\"name\":\"Ferrara\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":44.8364,\"lon\":11.6026,\"address\":{\"display_text\":\"Via Darsena, n. 73, Ferrara\",\"street\":\"Via Darsena\",\"house\":\"73\",\"zipcode\":\"44122\",\"city\":\"Ferrara\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60609\",\"slug\":\"messina\",\"name\":\"Messina\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":38.1364,\"lon\":15.5222,\"address\":{\"display_text\":\"Strada Statale 114 - Bivio Larderia - Località Tremestieri, Messina\",\"street\":\"Strada Statale 114\",\"house\":\"113\",\"zipcode\":\"98128\",\"city\":\"Messina\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60608\",\"slug\":\"meridiana-bologna-casalecchio-di-reno\",\"name\":\"Meridiana | Bologna\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":44.4843,\"lon\":11.2694,\"address\":{\"display_text\":\"Via Aldo Moro, n. 14, Casalecchio di Reno \\r\\nBologna\",\"street\":\"Via Aldo Moro\",\"house\":\"14\",\"zipcode\":\"40033\",\"city\":\"Casalecchio Di Reno\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60607\",\"slug\":\"lissone-milano\",\"name\":\"Lissone | Milano\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.634,\"lon\":9.22376,\"address\":{\"display_text\":\"Via Madre Teresa, S.S. Valassina, Lissone - Monza e Brianza\",\"street\":\"Via Madre Teresa ang. Via Valassina\",\"house\":null,\"zipcode\":\"20035\",\"city\":\"Lissone\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60610\",\"slug\":\"mestre-venezia\",\"name\":\"Mestre | Venezia\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.4569,\"lon\":12.2121,\"address\":{\"display_text\":\"Via Colombara, n. 46, Marghera Mestre Venezia\",\"street\":\"Via Colombara\",\"house\":\"46\",\"zipcode\":\"30176\",\"city\":\"Venezia\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60611\",\"slug\":\"milanofiori-milano-assago\",\"name\":\"MilanoFiori | Milano\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.4075,\"lon\":9.15205,\"address\":{\"display_text\":\"Viale MilanoFiori, Assago - Milano\",\"street\":\"Viale Milano Fiori\",\"house\":null,\"zipcode\":\"20090\",\"city\":\"Assago\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60616\",\"slug\":\"palermo\",\"name\":\"Palermo\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":38.0901,\"lon\":13.4125,\"address\":{\"display_text\":\"presso il Centro Commerciale Forum Palermo - Via Filippo Pecoraino SNC, Palermo\",\"street\":\"Via Filippo Pecoraino\",\"house\":null,\"zipcode\":\"90100\",\"city\":\"Palermo\",\"state\":\"Sicilia\",\"state_abbr\":\"Sicilia\",\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60613\",\"slug\":\"moncalieri-torino\",\"name\":\"Moncalieri | Torino\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":44.9764,\"lon\":7.70884,\"address\":{\"display_text\":\"Via Fortunato Postiglione Z.I. Vadò, Moncalieri Torino\",\"street\":\"Via Fortunato Postiglione\",\"house\":\"1\",\"zipcode\":\"10024\",\"city\":\"Moncalieri\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60617\",\"slug\":\"parco-leonardo-roma-fiumicino\",\"name\":\"Parco Leonardo | Roma\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":41.7983,\"lon\":12.2954,\"address\":{\"display_text\":\"Via Gian Lorenzo Bernini, n. 20/22, Fiumicino - Roma\",\"street\":\"Via Gian Lorenzo Bernini\",\"house\":null,\"zipcode\":\"00054\",\"city\":\"Fiumicino\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60612\",\"slug\":\"molfetta-bari\",\"name\":\"Molfetta | Bari\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":41.2167,\"lon\":16.547,\"address\":{\"display_text\":\"Strada Statale 16 bis - Uscita Molfetta Z.I., Molfetta Bari\",\"street\":\"Via dei Portuali\",\"house\":\"12\",\"zipcode\":\"70056\",\"city\":\"Molfetta\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60614\",\"slug\":\"orio-bergamo-localita-del-cucco\",\"name\":\"Orio | Bergamo\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.6614,\"lon\":9.69722,\"address\":{\"display_text\":\"Via Toscana, 2 - 24052 Azzano San Paolo (BG)\",\"street\":\"Via Toscana\",\"house\":\"2\",\"zipcode\":\"24052\",\"city\":\"Località del Cucco\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60615\",\"slug\":\"palariviera-car-conziap\",\"name\":\"PalaRiviera\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":42.9202,\"lon\":13.8981,\"address\":{\"display_text\":\"Piazzale Aldo Moro, n. 1, San Benedetto del Tronto Ascoli-Piceno\",\"street\":\"Via Aldo Moro\",\"house\":\"1\",\"zipcode\":\"63075\",\"city\":\"Car-conziap\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60618\",\"slug\":\"perugia\",\"name\":\"Perugia\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.0967,\"lon\":12.3553,\"address\":{\"display_text\":\"Viale Centova, n. 1/D, Perugia\",\"street\":\"Viale Centova\",\"house\":null,\"zipcode\":null,\"city\":\"Perugia\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60619\",\"slug\":\"pesaro\",\"name\":\"Pesaro\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.9041,\"lon\":12.8672,\"address\":{\"display_text\":\"Piazza Stefanini, n. 15, Pesaro Pesaro-Urbino\",\"street\":\"Piazza Stefanini\",\"house\":\"15\",\"zipcode\":\"61122\",\"city\":\"Pesaro\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60620\",\"slug\":\"piacenza\",\"name\":\"Piacenza\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.0284,\"lon\":9.69504,\"address\":{\"display_text\":\"Via Terenzio Visconti, n. 1, Piacenza\",\"street\":\"Via Corrado Visconti\",\"house\":\"1\",\"zipcode\":\"29122\",\"city\":\"Piacenza\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60621\",\"slug\":\"pioltello-milano\",\"name\":\"Pioltello | Milano\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.5026,\"lon\":9.32167,\"address\":{\"display_text\":\"Via San Francesco, n. 33, Pioltello - Milano\",\"street\":\"Viale San Francesco\",\"house\":\"33\",\"zipcode\":\"20096\",\"city\":\"Pioltello\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60622\",\"slug\":\"porta-di-roma\",\"name\":\"Porta di Roma | Roma\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":41.9714,\"lon\":12.5415,\"address\":{\"display_text\":\"c/o Centro Commerciale Porta di Roma - Via delle Vigne Nuove, snc, Zona Bufalotta - Roma\",\"street\":\"Via delle Vigne Nuove\",\"house\":null,\"zipcode\":\"00139\",\"city\":\"Roma\",\"state\":\"Lazio\",\"state_abbr\":\"Lazio\",\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60623\",\"slug\":\"porto-sant-elpidio-fermo\",\"name\":\"Porto Sant'Elpidio | Fermo\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.2884,\"lon\":13.7365,\"address\":{\"display_text\":\"Via Fratte, n. 41, Porto Sant'Elpidio Fermo\",\"street\":\"Via Fratte\",\"house\":\"41\",\"zipcode\":\"63821\",\"city\":\"Porto Sant'Elpidio\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60624\",\"slug\":\"red-carpet-matera\",\"name\":\"Red Carpet | Matera\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":40.6877,\"lon\":16.5444,\"address\":{\"display_text\":\", <!--td {border: 1px solid #ccc;}br {mso-data-placement:same-cell;}-->\\r\\n\\r\\nVia Enzo Ferrari\\r\\nMatera\",\"street\":\"Ⅲ^ traversa Giovanni Battista Pirelli\",\"house\":null,\"zipcode\":\"75100\",\"city\":\"Matera\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60625\",\"slug\":\"reggio-emilia\",\"name\":\"Reggio Emilia\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":44.7148,\"lon\":10.6511,\"address\":{\"display_text\":\"Piazza Azzurri d'Italia, n. 1, Reggio Emilia\",\"street\":\"Piazzale Atleti Azzurri D'Italia\",\"house\":\"1\",\"zipcode\":\"42100\",\"city\":\"Reggio Emilia\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60626\",\"slug\":\"romaest-roma\",\"name\":\"RomaEst | Roma\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":41.9159,\"lon\":12.6618,\"address\":{\"display_text\":\"Via Collatina, n. 858, Roma\",\"street\":\"Via Collatina\",\"house\":\"858\",\"zipcode\":\"00132\",\"city\":\"Roma\",\"state\":\"Lazio\",\"state_abbr\":\"Lazio\",\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60629\",\"slug\":\"showville-bari\",\"name\":\"Showville | Bari\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":41.0908,\"lon\":16.8872,\"address\":{\"display_text\":\"Via Giannini 9, 70125 BARI\",\"street\":\"Via Giannini\",\"house\":null,\"zipcode\":\"70125\",\"city\":\"Bari\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60628\",\"slug\":\"senigallia-ancona\",\"name\":\"Senigallia | Ancona\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.7087,\"lon\":13.2111,\"address\":{\"display_text\":\"Via Abbagnano, n. 8, Senigallia - Ancona\",\"street\":\"Via Nicola Abbagnano\",\"house\":\"8\",\"zipcode\":\"60019\",\"city\":\"Senigallia\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60631\",\"slug\":\"torino-lingotto\",\"name\":\"Torino Lingotto\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.0329,\"lon\":7.66609,\"address\":{\"display_text\":\"Via Nizza, n. 262, Torino\",\"street\":\"Via Nizza\",\"house\":\"262\",\"zipcode\":\"10126\",\"city\":\"Torino\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60630\",\"slug\":\"sinalunga-siena\",\"name\":\"Sinalunga | Siena\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":43.2157,\"lon\":11.7529,\"address\":{\"display_text\":\"Via Ginsburg, n. 1, Sinalunga Siena\",\"street\":\"Via Natalia Ginsburg\",\"house\":\"1\",\"zipcode\":\"53048\",\"city\":\"Sinalunga\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60633\",\"slug\":\"verona-san-giovanni-lupatoto\",\"name\":\"Verona\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.3967,\"lon\":11.0164,\"address\":{\"display_text\":\"Via Monte Amiata, San Giovanni Lupatoto Verona\",\"street\":\"Via Monte Amiata\",\"house\":null,\"zipcode\":\"37057\",\"city\":\"San Giovanni Lupatoto\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"},{\"id\":\"60634\",\"slug\":\"villesse-gorizia\",\"name\":\"Villesse | Gorizia\",\"chain_id\":\"7\",\"telephone\":null,\"website\":null,\"location\":{\"lat\":45.8652,\"lon\":13.4302,\"address\":{\"display_text\":\"Località Maranuz, 2 - c/o Centro Commerciale Tiare Shopping, Villesse - Gorizia\",\"street\":\"Località Maranuz\",\"house\":\"2\",\"zipcode\":\"34070\",\"city\":\"Villesse\",\"state\":null,\"state_abbr\":null,\"country\":\"Italy\",\"country_code\":\"IT\"}},\"booking_type\":\"external\"}]}";
        }else{
            JSONObject jObject;
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
        }
        return cinema;
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
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

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(MainActivity.this, "*onLocationChanged*", Toast.LENGTH_LONG).show();
        if (location != null){
            this.location = new LatLng(location.getLatitude(), location.getLongitude());
        }
        if (mGoogleApiClient != null){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient = null;
            mLocationRequest = null;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLocationRequest = LocationRequest.create()
                .setInterval(70)
                .setFastestInterval(30)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }
}