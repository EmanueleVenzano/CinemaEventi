package nf.application.emanuele.tesi1;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParserCinema {
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
}