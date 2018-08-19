package nf.application.emanuele.tesi1;

import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;

public class EventiFragment extends Fragment {
    private ListView listView;

    public static EventiFragment newInstance(){
        EventiFragment Fragment = new EventiFragment();
        return Fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.eventi_fragment, container, false);
        PreferitiDB db = new PreferitiDB(getContext());
        final ArrayList<ArrayList<String>> infoEvento = db.getAllEvents();
        listView=(ListView) v.findViewById(R.id.eventi_listview);

        EventiCustomAdapter customAdapter = new EventiCustomAdapter(getContext(), R.layout.eventi_items_listview, infoEvento);
        listView.setAdapter(customAdapter);
        return v;
    }
}
