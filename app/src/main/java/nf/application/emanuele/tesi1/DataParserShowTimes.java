package nf.application.emanuele.tesi1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataParserShowTimes {
    public ArrayList<DataShowTimes> parseShowTime(JSONObject jObject) {
        ArrayList<DataShowTimes> dataShowTimes = new ArrayList<>();
        JSONArray showTime;
        try {
            showTime = jObject.getJSONArray("showtimes");
            for (int i = 0; i < showTime.length(); i++) {
                DataShowTimes dataShowTime = new DataShowTimes();
                JSONObject jsonObject = showTime.getJSONObject(i);

                String cinema = jsonObject.getString("cinema_id");
                String film = jsonObject.getString("movie_id");
                String start = jsonObject.getString("start_at");
                String language = jsonObject.getString("language");
                String sub = jsonObject.getString("subtitle_language");
                String auditorium = jsonObject.getString("auditorium");
                boolean is3D = jsonObject.getBoolean("is_3d");
                boolean isIMax = jsonObject.getBoolean("is_imax");
                String link = jsonObject.getString("booking_link");

                dataShowTime.setCinema_id(cinema);
                dataShowTime.setMovie_id(film);
                dataShowTime.setStart(start);
                dataShowTime.setLanguage(language);
                dataShowTime.setSubtitle(sub);
                dataShowTime.setAuditorium(auditorium);
                dataShowTime.setIs3D(is3D);
                dataShowTime.setIMax(isIMax);
                dataShowTime.setLink(link);

                dataShowTimes.add(dataShowTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataShowTimes;
    }
}
