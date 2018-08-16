package nf.application.emanuele.tesi1;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

public class BottomBar extends AppCompatActivity {

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bottom_bar);

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    /*case R.id.navigation_mappe:
                        Toast.makeText(BottomBar.this, "Mappe", Toast.LENGTH_SHORT).show();
                        break;*/
                    case R.id.navigation_film:
                        Toast.makeText(BottomBar.this, "Film", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_preferiti:
                        Toast.makeText(BottomBar.this, "Preferiti", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.navigation_eventi:
                        Toast.makeText(BottomBar.this, "Eventi", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });

    }

}
