package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class InfoCinema extends Fragment implements View.OnClickListener{
    private String name;
    private TextView nameCinema;
    private TextView descriptionCinema;
    private ImageButton buttonDrive;
    private ImageButton buttonTransit;
    private ImageButton buttonWalk;
    private ImageButton buttonBike;
    private TextView streetCinema;
    private TextView cityCinema;

    public static InfoCinema newInstance(){
        InfoCinema Fragment = new InfoCinema();
        return Fragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_infocinema, container, false);
        name = ((cercaFilm)getActivity()).getMyData();

        MyApplication myApplication = (MyApplication) getActivity().getApplication();
        ArrayList<DataCinema> dataCinema = myApplication.getDataInfo().cinemas;
        int position;
        for (position=0; position<dataCinema.size(); position++){
            if (dataCinema.get(position).getName().equals(name)) break;
        }

        nameCinema = (TextView) view.findViewById(R.id.nameCinema);
        descriptionCinema = (TextView) view.findViewById(R.id.descriptionCinema);
        buttonDrive = (ImageButton) view.findViewById(R.id.buttonDriveCinema);
        buttonDrive.setOnClickListener(this);
        buttonTransit = (ImageButton) view.findViewById(R.id.buttonTransitCinema);
        buttonTransit.setOnClickListener(this);
        buttonWalk = (ImageButton) view.findViewById(R.id.buttonFootCinema);
        buttonWalk.setOnClickListener(this);
        buttonBike = (ImageButton) view.findViewById(R.id.buttonBikeCinema);
        buttonBike.setOnClickListener(this);
        streetCinema = (TextView) view.findViewById(R.id.streetCinema);
        cityCinema = (TextView) view.findViewById(R.id.cityCinema);

        nameCinema.setText(dataCinema.get(position).getName());
        descriptionCinema.setText(dataCinema.get(position).getCap());
        streetCinema.setText(dataCinema.get(position).getAddress());
        cityCinema.setText(dataCinema.get(position).getCity());
        return view;
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(getContext(), MapsActivity.class);
        switch (v.getId()){
            case R.id.buttonDriveCinema: intent.putExtra("mode", "driving"); break;
            case R.id.buttonTransitCinema: intent.putExtra("mode", "transit"); break;
            case R.id.buttonFootCinema: intent.putExtra("mode", "walking"); break;
            case R.id.buttonBikeCinema: intent.putExtra("mode", "bicycling"); break;
        }
        intent.putExtra("name", name);
        startActivity(intent);
    }
}

