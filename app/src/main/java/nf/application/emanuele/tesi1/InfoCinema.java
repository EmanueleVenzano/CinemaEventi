package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class InfoCinema extends Fragment implements View.OnClickListener{
    private String name;
    private ImageView imageCinema;
    private TextView nameCinema;
    private TextView descriptionCinema;
    private SharedPreferences savedValues;
    private Button button;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_infocinema, container, false);
        savedValues = getActivity().getSharedPreferences("SavedValues", Context.MODE_PRIVATE );
        name = savedValues.getString("name", "");
        SharedPreferences.Editor editor = savedValues.edit();
        editor.putString("name", "");
        editor.commit();
        CinemaDB db = new CinemaDB(getContext());
        ArrayList<String> info = db.getInfoCinema(name);

        imageCinema = (ImageView) view.findViewById(R.id.imageCinema);
        nameCinema = (TextView) view.findViewById(R.id.nameCinema);
        descriptionCinema = (TextView) view.findViewById(R.id.descriptionCinema);
        button = (Button) view.findViewById(R.id.buttonCinema);
        button.setOnClickListener(this);

        String path = getActivity().getApplication().getFilesDir().getAbsolutePath();
        InputStream is = null;
        try{
             is = new FileInputStream(path + "/"+info.get(1));
        }catch (FileNotFoundException e){
            try{
                 is = new FileInputStream(path+"/noImg.jpg");
            }catch (FileNotFoundException e1){
                e1.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        Drawable icon = new BitmapDrawable(is);
        Log.i("Fnord", "width="+icon.getIntrinsicWidth()+
                " height="+icon.getIntrinsicHeight());
        imageCinema.setImageDrawable(icon);
        nameCinema.setText(info.get(0));
        descriptionCinema.setText(info.get(2));
        return view;
    }

    @Override
    public void onClick(View v){
        if (v.getId()==R.id.buttonCinema){
            SharedPreferences.Editor editor = savedValues.edit();
            editor.putString("name", name);
            editor.commit();
        }
        getActivity().onBackPressed();
    }
}
