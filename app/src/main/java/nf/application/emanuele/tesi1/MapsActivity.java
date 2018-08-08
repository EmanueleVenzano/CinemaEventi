package nf.application.emanuele.tesi1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    Marker lastclicked=null;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap){
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        Cinemas c = new Cinemas();
        for (int i=0; i<c.cinemas.size(); i++){
            CinemaDB db = new CinemaDB(this);
            String name =c.cinemas.get(i).name;
            Location location = db.getCinemaLocation(name);
            if (location!=null){
                LatLng temp = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(temp).title(name));
            }
        }
        mMap.setOnMarkerClickListener(this);
        LatLng centered = new LatLng(44.416899, 8.917900);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(centered));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centered, 12), 1000, null);
    }

    @Override
    public boolean onMarkerClick(Marker marker){
        if (lastclicked!=null){
            lastclicked.setIcon(null);
        }
        marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_audiotrack_light));
        lastclicked=marker;
        return true;
    }

}
