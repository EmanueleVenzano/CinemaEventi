package nf.application.emanuele.tesi1;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DirectionParser {
    public ArrayList<ArrayList<String>> parse(JSONObject jObject, String mode) {
        ArrayList<ArrayList<String>> definitivo = new ArrayList<>();
        JSONArray routes;
        try {
            routes = jObject.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {
                JSONObject object = routes.getJSONObject(i);
                JSONArray legs = object.getJSONArray("legs");
                ArrayList<String> timing = new ArrayList<>();
                for (int j = 0; j < 4; j++) {
                    JSONObject info = legs.getJSONObject(j);
                    timing.add(info.getString("text"));
                }
                definitivo.add(timing);
                if (mode.equals("transit")) {
                    JSONArray steps = legs.getJSONArray(0);
                    for (int j = 0; j < steps.length(); j++) {
                        ArrayList<String> step = new ArrayList<>();
                        JSONObject actualStep = steps.getJSONObject(i);
                        JSONObject start_location = actualStep.getJSONObject("start_location");
                        step.add(start_location.getString("lat"));
                        step.add(start_location.getString("lng"));
                        JSONObject end_location = actualStep.getJSONObject("end_location");
                        step.add(end_location.getString("lat"));
                        step.add(end_location.getString("lng"));
                        JSONObject distance = actualStep.getJSONObject("distance");
                        step.add(distance.getString("text"));
                        String travel_mode = actualStep.getString("travel_mode");
                        step.add(travel_mode);
                        if (!travel_mode.equals("WALKING")){
                            JSONObject transit_details = actualStep.getJSONObject("transit_details");
                            JSONObject line = transit_details.getJSONObject("line");
                            String headsign = line.getString("short_name")+" - "+transit_details.getString("headsign");
                            step.add(headsign);
                            step.add(transit_details.getString("num_stops"));
                            JSONObject departure_time = transit_details.getJSONObject("departure_time");
                            step.add(departure_time.getString("text"));
                            JSONObject arrival_time = transit_details.getJSONObject("arrival_time");
                            step.add(arrival_time.getString("text"));
                            JSONObject vehicle = line.getJSONObject("vehicle");
                            step.add(vehicle.getString("icon"));
                        }
                        definitivo.add(step);
                    }
                }
                return definitivo;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return definitivo;
    }
}