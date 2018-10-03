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
        orderByDate();
        deletePastEvents();
        listView=(ListView) v.findViewById(R.id.preferiti_listview);

        PreferitiCustomAdapter customAdapter = new PreferitiCustomAdapter(getContext(), R.layout.preferiti_items_listview, infoEvento, "0");
        listView.setAdapter(customAdapter);
        return v;
    }

    public void orderByDate (){
        for (int i=0; i<infoEvento.size()-1; i++){
            for (int j=i+1; j<infoEvento.size(); j++){
                if (isBefore(infoEvento.get(i).get(0), infoEvento.get(j).get(0))){
                    ArrayList<String> temp = new ArrayList<>();
                    temp = infoEvento.get(i);
                    infoEvento.set(i, infoEvento.get(j));
                    infoEvento.set(j, temp);
                }
            }
        }
    }

    public boolean isBefore (String i, String j){
        String[] tempDataI = i.split(" ");
        String[] tempDataJ = j.split(" ");
        String[] dataI = tempDataI[0].split("/");
        String[] dataJ = tempDataJ[0].split("/");
        if (Integer.parseInt(dataJ[2])<Integer.parseInt(dataI[2])){
            return true;
        }
        if (Integer.parseInt(dataJ[2])>Integer.parseInt(dataI[2])){
            return false;
        }
        if (Integer.parseInt(dataJ[1])<Integer.parseInt(dataI[1])){
            return true;
        }
        if (Integer.parseInt(dataJ[1])>Integer.parseInt(dataI[1])){
            return false;
        }
        if (Integer.parseInt(dataJ[0])<Integer.parseInt(dataI[0])){
            return true;
        }
        if (Integer.parseInt(dataJ[0])>Integer.parseInt(dataI[0])){
            return false;
        }
        String[] oraI = tempDataI[1].split(":");
        String[] oraJ = tempDataJ[1].split(":");
        if (Integer.parseInt(oraJ[0])<Integer.parseInt(oraI[0])){
            return true;
        }
        if (Integer.parseInt(oraJ[0])>Integer.parseInt(oraI[0])){
            return false;
        }
        if (Integer.parseInt(oraJ[1])<Integer.parseInt(oraI[1])){
            return true;
        }
        if (Integer.parseInt(oraJ[1])>Integer.parseInt(oraI[1])){
            return false;
        }
        return false;
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
