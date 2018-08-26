package nf.application.emanuele.tesi1;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainFragment extends Fragment implements View.OnClickListener{
    private CardView map;
    private CardView saved;
    private CardView search;
    private CardView events;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_main, container, false);
        map = (CardView) view.findViewById(R.id.MapButton);
        map = (CardView) view.findViewById(R.id.MapButton);
        saved = (CardView) view.findViewById(R.id.SavedButton);
        search = (CardView) view.findViewById(R.id.SearchButton);
        events = (CardView) view.findViewById(R.id.EventiButton);
        map.setOnClickListener(this);
        saved.setOnClickListener(this);
        search.setOnClickListener(this);
        events.setOnClickListener(this);
        return view;
    }

    public void onClick (View v){
        Intent intent = null;
        switch (v.getId()){
            case R.id.MapButton:
                intent = new Intent (getContext(), MapsActivity.class);
                intent.putExtra("name", "");
                break;
            case R.id.SavedButton:
                intent = new Intent (getContext(), Preferiti.class);
                intent.putExtra("name", "preferiti");
                intent.putExtra("warning1", "0");
                break;
            case R.id.SearchButton:
                intent = new Intent (getContext(), cercaFilm.class);
                intent.putExtra("name", "search");
                intent.putExtra("warning", "0");
                break;
            case R.id.EventiButton:
                intent = new Intent (getContext(), cercaFilm.class);
                intent.putExtra("name", "eventi");
                intent.putExtra("warning", "0");
                break;
        }
        if (intent!=null){
            startActivity(intent);
        }
    }
}
