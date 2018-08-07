package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{
    private Button map;
    private Button saved;
    private Button search;
    private Button events;

    @Override
    public void onCreate (Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_main);
        map = (Button) findViewById(R.id.MapButton);
        saved = (Button) findViewById(R.id.SavedButton);
        search = (Button) findViewById(R.id.SearchButton);
        events = (Button) findViewById(R.id.EventidButton);
        map.setOnClickListener(this);
        saved.setOnClickListener(this);
        search.setOnClickListener(this);
        events.setOnClickListener(this);
    }

    public void onClick (View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.MapButton:
                intent = new Intent (this, MapsActivity.class);
                break;
            case R.id.SavedButton:
                intent = new Intent (this, MapsActivity.class);
                break;
            case R.id.SearchButton:
                intent = new Intent (this, Locandine.class);
                break;
            case R.id.EventidButton:
                intent = new Intent (this, MapsActivity.class);
                break;
        }
        if (intent!=null){
            startActivity(intent);
        }
    }
}
