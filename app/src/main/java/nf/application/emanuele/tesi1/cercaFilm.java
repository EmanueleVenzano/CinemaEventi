package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class cercaFilm extends Activity {

    String param;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        param = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cerca_film);
    }

    public void onSobstitute (int next) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        try {
            transaction.replace(android.R.id.content, new InfoFilm2());
            transaction.addToBackStack(null);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMyData(String param) {
        this.param = param;
    }

    public String getMyData(){
        return param;
    }

}
