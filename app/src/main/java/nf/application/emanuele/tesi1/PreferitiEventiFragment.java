package nf.application.emanuele.tesi1;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class PreferitiEventiFragment extends Fragment {
    private ListView listView;

    public static PreferitiEventiFragment newInstance(){
        PreferitiEventiFragment Fragment = new PreferitiEventiFragment();
        return Fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.preferiti_fragment, container, false);
        PreferitiDB db = new PreferitiDB(getContext());
        final ArrayList<ArrayList<String>> infoPreferito = db.getAllFavourites("0");
        listView=(ListView) v.findViewById(R.id.preferiti_listview);

        PreferitiCustomAdapter customAdapter = new PreferitiCustomAdapter(getContext(), R.layout.preferiti_items_listview, infoPreferito, "0");
        listView.setAdapter(customAdapter);
        return v;
    }
}
