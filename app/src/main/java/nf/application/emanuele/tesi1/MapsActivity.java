package nf.application.emanuele.tesi1;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

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
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng alessio = new LatLng(44.401916, 8.960740);
        LatLng g2 = new LatLng(44.403160, 8.958034);
        mMap.addMarker(new MarkerOptions().position(alessio).title("Marker in Sydney"));
        mMap.addMarker(new MarkerOptions().position(g2).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(g2));

        PolylineOptions polyLine = new PolylineOptions();
        polyLine.add(alessio);
        polyLine.add(g2);
        mMap.addPolyline(polyLine);
    }
}
