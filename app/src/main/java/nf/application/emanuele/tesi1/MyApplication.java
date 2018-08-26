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












    /*private class downloadUrlTask extends AsyncTask<String, String, String>{
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
    }*/


    public DataInfo getDataInfo (){
        return dataInfo;
    }

        /*@Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (mGoogleApiClient == null) {
                            buildGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }*/

}
