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

public class InfoCinema extends Activity implements View.OnClickListener{
    private String name;
    private ImageView imageCinema;
    private TextView nameCinema;
    private TextView descriptionCinema;
    private Button button;

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
        button = (Button) findViewById(R.id.buttonCinema);
        button.setOnClickListener(this);

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
        if (v.getId()==R.id.buttonCinema){
            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("name", name);
            startActivity(intent);
        }
    }
}
