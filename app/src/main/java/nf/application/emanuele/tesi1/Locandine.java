package nf.application.emanuele.tesi1;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class Locandine extends Fragment implements AdapterView.OnItemClickListener {
    private ListView itemsListView;
    private TextView titleTextView;
    private List<Copertina> films = new LinkedList();

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.activity_locandine, container, false);

        itemsListView = (ListView) view.findViewById(R.id.itemsListView);
        titleTextView = (TextView) view.findViewById(R.id.titleTextView);
        itemsListView.setOnItemClickListener(this);
        titleTextView.setText("Cerca film");
        Cinemas c = new Cinemas();
        for (int i=0; i< c.cinemas.size(); i++){
            for (int j=0; j<c.cinemas.get(i).films.size(); j++){
                int controller=0;
                for (int k=0; k<films.size(); k++){
                    if (films.get(k).name==c.cinemas.get(i).films.get(j).Titolo){
                        controller=1; break;
                    }
                }
                if (controller == 0){
                    Copertina temp = new Copertina();
                    temp.name = c.cinemas.get(i).films.get(j).Titolo;
                    temp.genere=c.cinemas.get(i).films.get(j).genere;
                    try {
                        temp.img = new URL(c.cinemas.get(i).films.get(j).immagine);
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }
                    films.add(temp);
                }
            }
        }

        CustomAdapter adapter = new CustomAdapter(Locandine.this.getContext(), R.layout.items_listview, films);
        itemsListView.setAdapter(adapter);
        FragmentManager fm = getFragmentManager();
        fm.popBackStack();
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id){
        Copertina item = films.get(position);
        ((cercaFilm)getActivity()).onSobstitute(1);
        ((cercaFilm) getActivity()).setMyData(item.name);
    }

    public static Locandine newInstance(){
        Locandine Fragment = new Locandine();
        return Fragment;
    }
}
