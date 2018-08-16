package nf.application.emanuele.tesi1;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DataParserFilm {
    public ArrayList<DataFilm> parseFilm(JSONObject jObject) {
        ArrayList<DataFilm> dataFilms = new ArrayList<>();
        JSONArray films;
        try {
            films = jObject.getJSONArray("movies");
            for (int i = 0; i < films.length(); i++) {
                DataFilm dataFilm = new DataFilm();
                JSONObject jsonObject = films.getJSONObject(i);
                String id = jsonObject.getString("id");
                String name = jsonObject.getString("title");
                String img = jsonObject.getString("poster_image_thumbnail");
                dataFilm.setId(id);
                dataFilm.setTitle(name);
                dataFilm.setImg(img);
                dataFilms.add(dataFilm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataFilms;
    }
}