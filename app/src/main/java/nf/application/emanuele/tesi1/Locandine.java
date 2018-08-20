package nf.application.emanuele.tesi1;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Locandine extends Fragment implements AdapterView.OnItemClickListener {
    private GridView itemsGridView;
    private TextView titleTextView;
    private List<Copertina> films = new LinkedList();
    DataInfo dataInfo;

    public static Locandine newInstance(){
        Locandine Fragment = new Locandine();
        return Fragment;
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_locandine, container, false);

        MyApplication myApplication = (MyApplication)getActivity().getApplication();
        dataInfo = myApplication.getDataInfo();

        itemsGridView = (GridView) view.findViewById(R.id.itemsListView);
        //titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        itemsGridView.setOnItemClickListener(this);
        //titleTextView.setText("Cerca film");

        if(films.size()<1) {

            for (int i = 0; i < dataInfo.films.size(); i++) {
                Copertina temp = new Copertina();
                temp.name = dataInfo.films.get(i).getTitle();
                try {
                    temp.img = new URL(dataInfo.films.get(i).getImg());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                films.add(temp);
            }

        }

        CustomAdapter adapter = new CustomAdapter(Locandine.this.getContext(), R.layout.items_listview, films);
        itemsGridView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        Copertina item = films.get(position);
        ((cercaFilm)getActivity()).onSobstitute(1);
        ((cercaFilm)getActivity()).setMyData(item.name);
    }
}
