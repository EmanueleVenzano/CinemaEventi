package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.ads.mediation.customevent.CustomEventAdapter;

import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Locandine extends Activity implements AdapterView.OnItemClickListener {
    private ListView itemsListView;
    private TextView titleTextView;
    private List<Copertina> films = new LinkedList();

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locandine);
        itemsListView = (ListView) findViewById(R.id.itemsListView);
        titleTextView = (TextView) findViewById(R.id.titleTextView);
        itemsListView.setOnItemClickListener(this);
        titleTextView.setText("Che magnifici Film!");
        Cinemas c = new Cinemas();
        for (int i=0; i< c.cinemas.size(); i++){
            for (int j=0; j<c.cinemas.get(i).films.size(); j++){
                int controller=0;
                for (int k=0; k<films.size(); k++){
                    if (films.get(k).name==c.cinemas.get(i).films.get(j).Titolo){
                        controller=1; break;
                    }
                }
                if (controller == 0){
                    Copertina temp = new Copertina();
                    temp.name = c.cinemas.get(i).films.get(j).Titolo;
                    try {
                        temp.img = new URL(c.cinemas.get(i).films.get(j).immagine);
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }
                    films.add(temp);
                }
            }
        }

        CustomAdapter adapter = new CustomAdapter(this, R.layout.items_listview, films);
        itemsListView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){

        Copertina item = films.get(position);
        Intent intent = new Intent (this, InfoFilm.class);
        intent.putExtra("name", item.name);
        this.startActivity(intent);
    }
}
