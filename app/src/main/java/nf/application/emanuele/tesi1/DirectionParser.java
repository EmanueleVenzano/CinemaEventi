package nf.application.emanuele.tesi1;

import android.widget.ScrollView;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
                JSONObject allLegs = legs.getJSONObject(0);
                JSONObject distanceT = allLegs.getJSONObject("distance");
                String distanceString = distanceT.getString("text");
                JSONObject duration = allLegs.getJSONObject("duration");
                String durationString = duration.getString("text");
                String departure_timeString;
                Calendar actual = Calendar.getInstance(Calendar.getInstance().getTimeZone());
                Date now = actual.getTime();
                String[] splitted = durationString.split(" ");
                try{
                    JSONObject departure_time = allLegs.getJSONObject("departure_time");
                    departure_timeString = departure_time.getString("text");
                    String tot="";
                    int l;
                    for (l=0; departure_timeString.charAt(l)!=':'; l++){
                        tot+=departure_timeString.charAt(l);
                    }
                    l++;
                    int t = (Integer.parseInt(tot));
                    tot = "";
                    for (int m=l; departure_timeString.charAt(m)!='a'&&departure_timeString.charAt(m)!='p'; m++){
                        tot += departure_timeString.charAt(m);
                    }
                    if (departure_timeString.charAt(departure_timeString.length()-2) == 'p'){
                        t = (t+12)%24;
                    }
                    departure_timeString = String.valueOf(t)+":"+tot;
                }catch (JSONException e){
                    departure_timeString = String.valueOf(now.getHours())+":"+String.valueOf(now.getMinutes());
                }
                String arrival_timeString;
                try {
                    JSONObject arrival_time = allLegs.getJSONObject("arrival_time");
                    arrival_timeString = arrival_time.getString("text");
                    String tot="";
                    int l;
                    for (l=0; arrival_timeString.charAt(l)!=':'; l++){
                        tot+=arrival_timeString.charAt(l);
                    }
                    l++;
                    int t = (Integer.parseInt(tot));
                    tot = "";
                    for (int m=l; arrival_timeString.charAt(m)!='a'&&arrival_timeString.charAt(m)!='p'; m++){
                        tot += arrival_timeString.charAt(m);
                    }
                    if (arrival_timeString.charAt(arrival_timeString.length()-2) == 'p'){
                        t = (t+12)%24;
                    }
                    arrival_timeString = String.valueOf(t)+":"+tot;
                }catch (JSONException e){
                    int minMod = (now.getMinutes()+Integer.parseInt(splitted[0]))%60;
                    int hoMod = (now.getHours()+(now.getMinutes()+Integer.parseInt(splitted[0]))/60)%24;
                    actual.set(now.getYear(), now.getMonth(), now.getDate(), hoMod, minMod);
                    now = actual.getTime();
                    arrival_timeString = String.valueOf(now.getHours())+":"+String.valueOf(now.getMinutes());
                }
                timing.add(departure_timeString);
                timing.add(arrival_timeString);
                timing.add(durationString);
                timing.add(distanceString);
                definitivo.add(timing);
                if (mode.equals("transit")) {
                    JSONArray steps = allLegs.getJSONArray("steps");
                    for (int j = 0; j < steps.length(); j++) {
                        ArrayList<String> step = new ArrayList<>();
                        JSONObject actualStep = steps.getJSONObject(j);
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