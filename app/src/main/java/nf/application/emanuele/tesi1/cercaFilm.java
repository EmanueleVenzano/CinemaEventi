package nf.application.emanuele.tesi1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

public class cercaFilm extends AppCompatActivity implements KeyEvent.Callback {
    int next=0;
    String param;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        param = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cerca_film);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    /*case R.id.navigation_mappe:
                        Toast.makeText(BottomBar.this, "Mappe", Toast.LENGTH_SHORT).show();
                        break;*/
                    case R.id.navigation_film:
                        Toast.makeText(cercaFilm.this, "Film", Toast.LENGTH_SHORT).show();
                        selectedFragment = Locandine.newInstance();
                        break;
                    case R.id.navigation_preferiti:
                        Toast.makeText(cercaFilm.this, "Preferiti", Toast.LENGTH_SHORT).show();
                        selectedFragment = PreferitiFragment.newInstance();
                        break;
                    case R.id.navigation_eventi:
                        Toast.makeText(cercaFilm.this, "Eventi", Toast.LENGTH_SHORT).show();
                        selectedFragment = EventiFragment.newInstance();
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
        if (name.equals("search")){
            next=0;
            onSobstitute(0);
        }
        if (name.equals("eventi")){
            next=3;
            onSobstitute(3);
        }

    }

    public void onSobstitute (int next) {
        this.next=next;
        switch (next){
            case 0:
                try {
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment, new Locandine());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 1:
                try {
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment, new InfoFilm2());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                try {
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment, new PreferitiFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            case 3:
                try {
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment, new EventiFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
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
            } else{
                next=0;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
