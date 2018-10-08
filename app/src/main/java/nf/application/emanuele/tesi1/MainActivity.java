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
//            new DownloadCinemaTask().execute("https://api.internationalshowtimes.com/v4/cinemas/?countries=IT");
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
//        Toast.makeText(MainActivity.this, "*onLocationChanged*", Toast.LENGTH_LONG).show();
        if (location != null){
//            this.location = new LatLng(location.getLatitude(), location.getLongitude());
            this.location = new LatLng(44.403058, 8.958195);
//            this.location = new LatLng(45.076846, 7.674095);
//            this.location = new LatLng(45.482619, 9.178466);
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