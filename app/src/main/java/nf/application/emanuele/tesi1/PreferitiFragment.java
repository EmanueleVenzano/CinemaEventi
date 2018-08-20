package nf.application.emanuele.tesi1;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class PreferitiFragment extends Fragment {
    private ListView listView;
    private ArrayList<ArrayList<String>> infoPreferito;

    public static PreferitiFragment newInstance(){
        PreferitiFragment Fragment = new PreferitiFragment();
        return Fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.preferiti_fragment, container, false);
        PreferitiDB db = new PreferitiDB(getContext());
        infoPreferito = db.getAllFavourites("1");
        listView=(ListView) v.findViewById(R.id.preferiti_listview);

        PreferitiCustomAdapter customAdapter = new PreferitiCustomAdapter(getContext(), R.layout.preferiti_items_listview, infoPreferito, "1");
        listView.setAdapter(customAdapter);
        return v;
    }

    public void deletePastEvents () {
        for(int i=0; i<infoPreferito.size(); i++) {
            String[] dataTime = infoPreferito.get(i).get(0).split("T");
            String[] temp1 = dataTime[0].split("-");
            String[] temp2 = dataTime[1].split(":");
            Calendar movie = Calendar.getInstance();
            movie.set(Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1]), Integer.parseInt(temp1[2]), Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
            Calendar currentTime = Calendar.getInstance(Calendar.getInstance().getTimeZone());
            if(currentTime.after(movie)) {
                PreferitiDB db = new PreferitiDB(getContext());
                db.deletePreferito(infoPreferito.get(i).get(2), infoPreferito.get(i).get(1), infoPreferito.get(i).get(0));
                infoPreferito.remove(i);
                i--;
            }
        }
    }
}
