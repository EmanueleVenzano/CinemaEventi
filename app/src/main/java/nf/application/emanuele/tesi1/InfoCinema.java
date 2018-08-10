package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;

public class InfoCinema extends Activity implements View.OnClickListener{
    private String name;
    private ImageView imageCinema;
    private TextView nameCinema;
    private TextView descriptionCinema;
    private Button buttonDrive;
    private Button buttonTransit;
    private Button buttonWalk;
    private Button buttonBike;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infocinema);
        name = getIntent().getStringExtra("name");
        CinemaDB db = new CinemaDB(this);
        ArrayList<String> info = db.getInfoCinema(name);

        imageCinema = (ImageView) findViewById(R.id.imageCinema);
        nameCinema = (TextView) findViewById(R.id.nameCinema);
        descriptionCinema = (TextView) findViewById(R.id.descriptionCinema);
        buttonDrive = (Button) findViewById(R.id.buttonDriveCinema);
        buttonDrive.setOnClickListener(this);
        buttonTransit = (Button) findViewById(R.id.buttonTransitCinema);
        buttonTransit.setOnClickListener(this);
        buttonWalk = (Button) findViewById(R.id.buttonFootCinema);
        buttonWalk.setOnClickListener(this);
        buttonBike = (Button) findViewById(R.id.buttonBikeCinema);
        buttonBike.setOnClickListener(this);

        String path = getApplication().getFilesDir().getAbsolutePath();
        InputStream is = null;
        /*try{
             is = new FileInputStream(path + "/"+info.get(1));
        }catch (FileNotFoundException e){
            try{
                 is = new FileInputStream(path+"/noimg.jpg");
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
        */
        nameCinema.setText(info.get(0));
        descriptionCinema.setText(info.get(2));
    }

    @Override
    public void onClick(View v){
        Intent intent = new Intent(this, MapsActivity.class);
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

