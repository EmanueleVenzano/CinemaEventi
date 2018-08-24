package nf.application.emanuele.tesi1;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static java.lang.StrictMath.abs;

public class DirectionsInfo extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{
    private GoogleMap mMap;
    ArrayList<ArrayList<Double>> lineCoefficents = new ArrayList<>();
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    Location mLastLocation;
    String goTo=null;
    String mode="";
    String flag;
    LatLng end=null;
    ArrayList<ArrayList<String>> data;
    boolean followMe = true;
    boolean recalc = true;
    boolean notFirsTime = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.directions_info);
        goTo = getIntent().getStringExtra("name");
        mode= getIntent().getStringExtra("mode");
        data = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }
        getIntent().getFlags();
        if(getIntent().getStringExtra("flag")==null) {
            flag="0";
        } else {
            flag=getIntent().getStringExtra("flag");
        }

        //------------------------------------------------------------------------------
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        bottomNavigationView.getMenu().findItem(R.id.navigation_mappe).setChecked(true);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        //bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                int flag = 0;
                switch (item.getItemId()) {
                    case R.id.navigation_mappe:
                        Toast.makeText(DirectionsInfo.this, "Mappe", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_film:
                        flag=1;
                        Toast.makeText(DirectionsInfo.this, "Film", Toast.LENGTH_SHORT).show();
                        intent = new Intent (DirectionsInfo.this, cercaFilm.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("name", "search");
                        intent.putExtra("warning", "1");
                        break;
                    case R.id.navigation_preferiti:
                        flag=1;
                        Toast.makeText(DirectionsInfo.this, "Preferiti", Toast.LENGTH_SHORT).show();
                        intent = new Intent (DirectionsInfo.this, Preferiti.class);
                        intent.putExtra("warning1", "1");
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        break;
                    case R.id.navigation_eventi:
                        flag=1;
                        Toast.makeText(DirectionsInfo.this, "Eventi", Toast.LENGTH_SHORT).show();
                        intent = new Intent (DirectionsInfo.this, cercaFilm.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("name", "eventi");
                        intent.putExtra("warning", "1");
                        break;
                }
                if(flag==1) {
                    startActivity(intent);
                }
                return true;
            }
        });
        //-------------------------------------------------------------------------------

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(true);
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                followMe = false;
                final Button follow = (Button) findViewById(R.id.button_follow_me);
                follow.setEnabled(true);
                follow.setVisibility(View.VISIBLE);
                follow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mLastLocation!=null){
                            followMe = true;
                            follow.setEnabled(false);
                            follow.setVisibility(View.INVISIBLE);
                            float bearing = mLastLocation.getBearing();
                            final CameraPosition SYDNEY = new CameraPosition.Builder().target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                                    .zoom(18)
                                    .bearing(bearing)
                                    .tilt(60)
                                    .build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), 2000, null);
                        }
                    }
                });
            }
        });

        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                float zoom = cameraPosition.zoom;
                if (zoom != 18){
                    followMe = false;
                    final Button follow = (Button) findViewById(R.id.button_follow_me);
                    follow.setEnabled(true);
                    follow.setVisibility(View.VISIBLE);
                    follow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (mLastLocation!=null){
                                followMe = true;
                                follow.setEnabled(false);
                                follow.setVisibility(View.INVISIBLE);
                                float bearing = mLastLocation.getBearing();
                                final CameraPosition SYDNEY = new CameraPosition.Builder().target(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()))
                                        .zoom(18)
                                        .bearing(bearing)
                                        .tilt(60)
                                        .build();
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), 2000, null);

                            }
                        }
                    });
                }
            }
        });
        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }

        MyApplication myApplication = (MyApplication) getApplication();
        ArrayList<DataCinema> dataCinemas = myApplication.getDataInfo().cinemas;
        for(int i=0; i<dataCinemas.size(); i++) {
            if(dataCinemas.get(i).getName().equals(goTo)) {
                end = new LatLng(Double.parseDouble(dataCinemas.get(i).getLat()), Double.parseDouble(dataCinemas.get(i).getLon()));
                break;
            }
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.416899, 8.917900), 12));
        followMe = true;
//        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(44.416899, 8.917900), 12), 1000, null);

    }

    private String getUrl(LatLng origin, LatLng dest) {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters+ "&mode=" + mode;
        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
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
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class FetchUrl extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data.toString());
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
    }

    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
       String dota;
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0].toString());
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());
                routes = parser.parse(jObject);
                DirectionParser directionParser = new DirectionParser();
                data = directionParser.parse(jObject, mode);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());
            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points;
            PolylineOptions lineOptions = null;
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<>();
                lineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = result.get(i);
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);
                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.RED);
                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
            }

            if (lineOptions != null) {
                mMap.addPolyline(lineOptions);
            } else {
                Log.d("onPostExecute", "without Polylines drawn");
            }
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
                .setInterval(5000)
                .setFastestInterval(2000)
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

    private class IfWrongWay extends AsyncTask<LatLng, Void, Boolean>{
        @Override
        protected Boolean doInBackground (LatLng... params){
            if (lineCoefficents.size()>0){
                int foundedOk = 0;
                for (int i = 0; i < lineCoefficents.size(); i++){
                    double xp = params[0].latitude;
                    double yp = params[0].longitude;
                    double m = lineCoefficents.get(i).get(0);
                    double q = lineCoefficents.get(i).get(1);
                    double Px = ((xp/m)+yp-q)/(m+(1/m));
                    if (Px>(min(lineCoefficents.get(i).get(2),lineCoefficents.get(i).get(4))) && Px<(max(lineCoefficents.get(i).get(2),lineCoefficents.get(i).get(4)))){
                        double distance = (abs(yp-((m*xp)+q)))/sqrt(1+(m*m));
                        if (distance < 0.00065){
                            foundedOk++;
                            break;
                        }
                    }else {
                        double Py = m*Px+q;
                        double d1 = sqrt(pow((Py-lineCoefficents.get(i).get(3)), 2.0)+pow((Px-lineCoefficents.get(i).get(2)), 2.0));
                        double d2 = sqrt(pow((Py-lineCoefficents.get(i).get(5)), 2.0)+pow((Px-lineCoefficents.get(i).get(4)), 2.0));
                        if (min(d1,d2) < 0.00065){
                            foundedOk++;
                            break;
                        }
                    }
                }
                if (foundedOk == 0){
                    recalc = true;
                }else {
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(params[0])
                            .title("Current Position")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }
            }
            return false;
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        new IfWrongWay().execute(latLng);
        if(end!=null) {
            if (recalc){
                mMap.clear();
                if (notFirsTime){
                    MarkerOptions markerOptions2 = new MarkerOptions();
                    markerOptions2.position(latLng);
                    markerOptions2.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                    mMap.addMarker(markerOptions2);
                }
                notFirsTime = true;
                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(end);
                markerOptions1.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                mMap.addMarker(markerOptions1);

                String url = getUrl(latLng, end);
                Log.d("onMapClick", url.toString());
                try {
                    String finish = new FetchUrl().execute(url).get();
                    List<List<HashMap<String, String>>> dota = new ParserTask().execute(finish).get();
                    lineCoefficents = new LineCalcTask().execute(dota).get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                try{
                    TextView startTime = (TextView) findViewById(R.id.directionStart);
                    startTime.setText(Html.fromHtml("<b>Partenza: </b>"+ data.get(0).get(0)));
                    TextView endTime = (TextView) findViewById(R.id.directionArr);
                    endTime.setText(Html.fromHtml("<b>Arrivo: </b>"+ data.get(0).get(1)));
                    TextView time = (TextView) findViewById(R.id.directionTime);
                    time.setText(Html.fromHtml("<b>Durata: </b>"+data.get(0).get(2)));
                    TextView km = (TextView) findViewById(R.id.directionDistance);
                    km.setText(Html.fromHtml("<b>Distanza: </b>"+data.get(0).get(3)));

                    if (mode.equals("transit")){
                        ArrayList<ArrayList<String>> toPass = new ArrayList<>();
                        for (int i=1; i<data.size(); i++){
                            toPass.add(data.get(i));
                        }
                        DirectionAdapter directionAdapter = new DirectionAdapter(DirectionsInfo.this, R.layout.direction_item, toPass, mMap);
                        ListView listView = (ListView) findViewById(R.id.directionList);
                        listView.setAdapter(directionAdapter);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{

            }
        }
        if (followMe){
            float bearing = location.getBearing();
            final CameraPosition SYDNEY = new CameraPosition.Builder().target(latLng)
                            .zoom(18)
                            .bearing(bearing)
                            .tilt(60)
                            .build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(SYDNEY), 2000, null);
        }
    }

    private class LineCalcTask extends AsyncTask<List<List<HashMap<String, String>>>, Void, ArrayList<ArrayList<Double>>> {
        @Override
        protected ArrayList<ArrayList<Double>> doInBackground (List<List<HashMap<String, String>>>... params){
            ArrayList<ArrayList<Double>> lines = new ArrayList<>();
            for (int i=0; i<params[0].size(); i++){
                List<HashMap<String, String>> path = params[0].get(i);
                for (int j=1; j<path.size(); j++){
                    double x1 = Double.parseDouble(path.get(j-1).get("lat"));
                    double y1 = Double.parseDouble(path.get(j-1).get("lng"));
                    double x2 = Double.parseDouble(path.get(j).get("lat"));
                    double y2 = Double.parseDouble(path.get(j).get("lng"));
                    double m = (y2-y1)/(x2-x1);
                    double q = -((x1*(y2-y1))/(x2-x1))+y1;
                    ArrayList<Double> temp = new ArrayList<>();
                    temp.add(m);
                    temp.add(q);
                    temp.add(x1);
                    temp.add(y1);
                    temp.add(x2);
                    temp.add(y2);
                    lines.add(temp);
                }
            }
            return lines;
        }

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    @Override
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
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(flag.equals("0")) {
                Intent intent = new Intent(DirectionsInfo.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else{
                finish();
            }
        }
        return true;
    }
}
