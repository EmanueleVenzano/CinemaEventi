package nf.application.emanuele.tesi1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

public class Preferiti extends AppCompatActivity {

    String warning1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferiti_activity);

        getIntent().getFlags();
        warning1 = getIntent().getStringExtra("warning1");

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Cinema"));
        tabLayout.addTab(tabLayout.newTab().setText("Eventi"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PreferitiAdapter adapter = new PreferitiAdapter (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom);
        bottomNavigationView.getMenu().findItem(R.id.navigation_preferiti).setChecked(true);
        BottomNavigationViewHelper.removeShiftMode(bottomNavigationView);
        //bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent = null;
                int flag = 0;
                switch (item.getItemId()) {
                    case R.id.navigation_mappe:
                        Toast.makeText(Preferiti.this, "Mappe", Toast.LENGTH_SHORT).show();
                        Intent intent2 = new Intent(Preferiti.this, MapsActivity.class);
                        intent2.putExtra("name", " ");
                        startActivity(intent2);
                        break;
                    case R.id.navigation_film:
                        Toast.makeText(Preferiti.this, "Film", Toast.LENGTH_SHORT).show();
                        intent = new Intent (Preferiti.this, cercaFilm.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("name", "search");
                        intent.putExtra("warning", "1");
                        break;
                    case R.id.navigation_preferiti:
                        Toast.makeText(Preferiti.this, "Preferiti", Toast.LENGTH_SHORT).show();
                        flag = 1;
                        break;
                    case R.id.navigation_eventi:
                        Toast.makeText(Preferiti.this, "Eventi", Toast.LENGTH_SHORT).show();
                        intent = new Intent (Preferiti.this, cercaFilm.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("name", "eventi");
                        intent.putExtra("warning", "1");
                        break;
                }
                if(flag==0) {
                    startActivity(intent);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
           if(warning1.equals("0")) {
               finish();
           } else {
               warning1="0";
               Intent intent = new Intent(Preferiti.this, MainActivity.class);
               intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
               startActivity(intent);
           }
        }
        return true;
    }
}

