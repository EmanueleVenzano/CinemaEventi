package nf.application.emanuele.tesi1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class PreferitiEventiFragment extends Fragment {
    private ListView listView;
    private ArrayList<ArrayList<String>> infoEvento;

    public static PreferitiEventiFragment newInstance(){
        PreferitiEventiFragment Fragment = new PreferitiEventiFragment();
        return Fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.preferiti_fragment, container, false);
        PreferitiDB db = new PreferitiDB(getContext());
        infoEvento = db.getAllFavourites("0");
        deletePastEvents();
        listView=(ListView) v.findViewById(R.id.preferiti_listview);

        PreferitiCustomAdapter customAdapter = new PreferitiCustomAdapter(getContext(), R.layout.preferiti_items_listview, infoEvento, "0");
        listView.setAdapter(customAdapter);
        return v;
    }

    public void deletePastEvents () {
        for(int i=0; i<infoEvento.size(); i++) {
            String[] dataTime = infoEvento.get(i).get(0).split(" ");
            String[] temp1 = dataTime[0].split("/");
            String[] temp2 = dataTime[1].split(":");
            Calendar movie = Calendar.getInstance();
            movie.set(Integer.parseInt("20"+temp1[2]), Integer.parseInt(temp1[1])-1, Integer.parseInt(temp1[0]), Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
            Calendar currentTime = Calendar.getInstance(Calendar.getInstance().getTimeZone());
            if(currentTime.after(movie)) {
                PreferitiDB db = new PreferitiDB(getContext());
                db.deletePreferito(infoEvento.get(i).get(2), infoEvento.get(i).get(1), infoEvento.get(i).get(0));
                infoEvento.remove(i);
                i--;
            }
        }
    }
}
