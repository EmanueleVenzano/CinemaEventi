package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class InfoCinema extends Activity {
    private String name;
    private ImageView imageCinema;
    private TextView nameCinema;
    private TextView descriptionCinema;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_infocinema);
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        CinemaDB db = new CinemaDB(this);
        ArrayList<String> info = db.getInfoCinema(name);

        imageCinema = (ImageView) findViewById(R.id.imageCinema);
        nameCinema = (TextView) findViewById(R.id.nameCinema);
        descriptionCinema = (TextView) findViewById(R.id.descriptionCinema);

        String path = getApplication().getFilesDir().getAbsolutePath();
        InputStream is = null;
        try{
             is = new FileInputStream(path + "/"+info.get(1));
        }catch (FileNotFoundException e){
            try{
                 is = new FileInputStream(path+"/noImg.png");
            }catch (FileNotFoundException e1){
                e1.printStackTrace();
            }
        }
        Drawable icon = new BitmapDrawable(is);
        Log.i("Fnord", "width="+icon.getIntrinsicWidth()+
                " height="+icon.getIntrinsicHeight());
        imageCinema.setImageDrawable(icon);
        nameCinema.setText(info.get(0));
        descriptionCinema.setText(info.get(2));
    }
}
