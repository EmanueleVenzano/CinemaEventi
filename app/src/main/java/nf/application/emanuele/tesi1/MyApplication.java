package nf.application.emanuele.tesi1;

import android.Manifest;
import android.app.Application;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.OnMapReadyCallback;
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
import java.util.concurrent.ExecutionException;

import static java.lang.Math.sqrt;

public class MyApplication extends Application {
    private DataInfo dataInfo = null;
    private String api_key = "O2SDCQ4xaVnyRRfX6AmyOuGGaZQ24GBJ";
    private String country_code;

    @Override
    public void onCreate(){
        super.onCreate();
        country_code = getApplicationContext().getResources().getConfiguration().locale.getCountry();
    }

    public String getCountry_code() {
        return country_code;
    }

    public void setDataShowTimes(ArrayList<DataShowTimes> dataShowTimes){
        if (dataInfo == null){
            dataInfo = new DataInfo();
        }
        dataInfo.showTimes = dataShowTimes;
    }

    public void setDataFilms(ArrayList<DataFilm> dataFilms){
        if (dataInfo == null){
            dataInfo = new DataInfo();
        }
        dataInfo.films = dataFilms;
    }

    public void setDataCinemas(ArrayList<DataCinema> dataCinemas){
        if (dataInfo == null){
            dataInfo = new DataInfo();
        }
        dataInfo.cinemas = dataCinemas;
    }

    public void setDataNearestCinemas(ArrayList<DataCinema> dataCinemas){
        if (dataInfo == null){
            dataInfo = new DataInfo();
        }
        dataInfo.nearestCinemas = dataCinemas;
    }

    public String getApiKey(){
        return api_key;
    }

    public DataInfo getDataInfo (){
        return dataInfo;
    }
}
