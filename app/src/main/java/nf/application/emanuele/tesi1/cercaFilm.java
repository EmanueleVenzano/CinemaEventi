package nf.application.emanuele.tesi1;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

public class cercaFilm extends AppCompatActivity implements KeyEvent.Callback {
    int next=0;
    String param;
    String param1;
    String warning;
    String tornaInfoFilm;
    String nomeCinema;
    String nomeFilm;
    int flag = 0;
    BottomNavigationView bottomNavigationView = null;

    @Override
    public void onCreate (Bundle savedInstanceState) {
        //param = "";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cerca_film);

        getIntent().getFlags();
        String name;

        //--------------------------------------------------------------------
        nomeCinema = getIntent().getStringExtra("cinemaName");
        if(nomeCinema!=null) {
             name="cinema";
        } else {
             name = getIntent().getStringExtra("name");
        }
        tornaInfoFilm = getIntent().getStringExtra("tornaInfoFilm");
        try{
            if(tornaInfoFilm.equals("1")) {
                nomeFilm = getIntent().getStringExtra("nomeFilm");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //--------------------------------------------------------------------

        warning = getIntent().getStringExtra("warning");
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        //bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    Intent intent = null;
                    switch (item.getItemId()) {
                        case R.id.navigation_mappe:
                            flag=1;
//                            Toast.makeText(cercaFilm.this, "Mappe", Toast.LENGTH_SHORT).show();
                            intent = new Intent(cercaFilm.this, MapsActivity.class);
                            intent.putExtra("name", "");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            break;
                        case R.id.navigation_film:
                            next=2;
                            warning="1";
//                            Toast.makeText(cercaFilm.this, "Film", Toast.LENGTH_SHORT).show();
                            selectedFragment = Locandine.newInstance();
                            break;
                        case R.id.navigation_preferiti:
//                            Toast.makeText(cercaFilm.this, "Preferiti", Toast.LENGTH_SHORT).show();
                            flag=1;
                            intent = new Intent (cercaFilm.this, Preferiti.class);
                            intent.putExtra("warning1", "1");
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            break;
                        case R.id.navigation_eventi:
                            next=3;
                            warning="1";
//                            Toast.makeText(cercaFilm.this, "Eventi", Toast.LENGTH_SHORT).show();
                            selectedFragment = EventiFragment.newInstance();
                            break;
                    }
                    if (flag==0) {
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.main_fragment, selectedFragment);
                        transaction.commit();
                    } else {
                        startActivity(intent);
                    }
                    return true;
                }
            });

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
        if(name.equals("cinema")) {
            next=4;
            if(nomeCinema!=null){
                setMyData(nomeCinema);
            } else {
                param=getIntent().getStringExtra("cinemaName");
            }
            setTornaInfoFilm(tornaInfoFilm);
            setNomeFilm(nomeFilm);
            onSobstitute(4);
        }

    }

    public void onSobstitute (int next) {
        this.next=next;
        switch (next){
            case 0:
                try {
                    bottomNavigationView.getMenu().findItem(R.id.navigation_film).setChecked(true);
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
                    bottomNavigationView.getMenu().findItem(R.id.navigation_film).setChecked(true);
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
                break;
            case 3:
                try {
                    bottomNavigationView.getMenu().findItem(R.id.navigation_eventi).setChecked(true);
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment, new EventiFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                try {
                    FragmentTransaction transaction = (FragmentTransaction) getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.main_fragment, new InfoCinema());
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

    public void setNomeFilm(String param1) {
        this.param1 = param1;
    }

    public String getNomeFilm() {
        return param1;
    }

    public void setTornaInfoFilm (String tornaInfoFilm) {
        this.tornaInfoFilm = tornaInfoFilm;
    }

    public String getTornaInfoFilm () {
        return tornaInfoFilm;
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (next!=1 && next!=4 && warning.equals("0")) {
                this.finish();
                return true;
            } else if (next!=1 && next!=4 && (warning.equals("1"))) {
                Intent intent = new Intent(cercaFilm.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                warning="0";
                startActivity(intent);
                return true;
            } else if (next==4 && tornaInfoFilm.equals("0")) {
                Intent intent = new Intent(cercaFilm.this, MapsActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            } else if (next==4 && tornaInfoFilm.equals("1")) {
                next=1;

                /*onSobstitute(1);
                return true;*/

                /*this.finish();
                return true;*/

                return super.onKeyDown(keyCode, event);
            } else{
                next=0;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

}
