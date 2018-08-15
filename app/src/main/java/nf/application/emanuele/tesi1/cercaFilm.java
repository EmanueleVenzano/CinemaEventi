package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;

public class cercaFilm extends AppCompatActivity implements KeyEvent.Callback{
    int next=0;
    String param;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        param = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cerca_film);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()){
                    case R.id.menu_bottom_map:
                        selectedFragment = Locandine.newInstance();
                        break;
                    case R.id.menu_bottom_events:
                        selectedFragment = Locandine.newInstance();
                        break;
                    case R.id.menu_bottom_saved:
                        selectedFragment = PreferitiFragment.newInstance();
                        break;
                    case R.id.menu_bottom_search:
                        selectedFragment = Locandine.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.main_fragment, selectedFragment );
                transaction.commit();
                return true;
            }
        });



        String name = getIntent().getStringExtra("name");
        if (name.equals("preferiti")){
            next=2;
            onSobstitute(2);
        }
    }

    public void onSobstitute (int next) {
        this.next=next;
        switch (next){
            case 0:
                try {
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(android.R.id.content, new Locandine());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(android.R.id.content, new InfoFilm2());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(android.R.id.content, new PreferitiFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }

    public void setMyData(String param) {
        this.param = param;
    }

    public String getMyData(){
        return param;
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (next != 1) {
                this.finish();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
