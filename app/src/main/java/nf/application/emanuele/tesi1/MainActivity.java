package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity implements View.OnClickListener{
    private CardView map;
    private CardView saved;
    private CardView search;
    private CardView events;

    @Override
    public void onCreate (Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
        setContentView(R.layout.activity_main);

        getIntent().getFlags();

        map = (CardView) findViewById(R.id.MapButton);
        saved = (CardView) findViewById(R.id.SavedButton);
        search = (CardView) findViewById(R.id.SearchButton);
        events = (CardView) findViewById(R.id.EventiButton);
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
                intent.putExtra("name", "");
                break;
            case R.id.SavedButton:
                intent = new Intent (this, Preferiti.class);
                intent.putExtra("name", "preferiti");
                intent.putExtra("warning1", "0");
                break;
            case R.id.SearchButton:
                intent = new Intent (this, cercaFilm.class);
                intent.putExtra("name", "search");
                intent.putExtra("warning", "0");
                break;
            case R.id.EventiButton:
                intent = new Intent (this, cercaFilm.class);
                intent.putExtra("name", "eventi");
                intent.putExtra("warning", "0");
                break;
        }
        if (intent!=null){
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return true;
    }
}