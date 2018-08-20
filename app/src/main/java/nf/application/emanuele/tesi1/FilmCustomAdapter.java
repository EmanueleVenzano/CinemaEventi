package nf.application.emanuele.tesi1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FilmCustomAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<ArrayList<String>> cinemas;
    private ArrayList<ArrayList<DataShowTimes>> dataShowTimes;
    private String film;
    private List<String> giorni;

    public FilmCustomAdapter (Context context, int textViewResourceId, List<ArrayList<String>> cinemas, List<String> giorni, ArrayList<ArrayList<DataShowTimes>> dataShowTimes, String film){
        super(context, textViewResourceId, giorni);
        this.context = context;
        this.cinemas = cinemas;
        this.dataShowTimes = dataShowTimes;
        this.film = film;
        this.giorni = giorni;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.proiezioni_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.giorno = (TextView)convertView.findViewById(R.id.nomeGiorno);
            viewHolder.expandableListView = (ExpandableListView)convertView.findViewById(R.id.proiezioniExpandableListView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.giorno.setText(getItem(position));
        ArrayList<ArrayList<DataShowTimes>> proiezioniPerCinema = new ArrayList<>();
        ArrayList<String> cinema = new ArrayList<>();
        for (int i = 0; i < dataShowTimes.get(position).size(); i++){
//            if (!dataShowTimes.get(position).get(i).getCinema_id().equals(getItem(position).get(1))) continue;
            String cinema_name="";
            for (int j=0; j<cinemas.size(); j++){
                if (cinemas.get(j).get(1).equals(dataShowTimes.get(position).get(i).getCinema_id())){
                    cinema_name = cinemas.get(j).get(0);
                    break;
                }
            }
            int pos = cinema.indexOf(cinema_name);
            if (pos==-1){
                cinema.add(cinema_name);
                proiezioniPerCinema.add(new ArrayList<DataShowTimes>());
                pos = proiezioniPerCinema.size()-1;
            }
            proiezioniPerCinema.get(pos).add(dataShowTimes.get(position).get(i));
        }
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(context, cinema, proiezioniPerCinema, film);
        viewHolder.expandableListView.setAdapter(listAdapter);

        viewHolder.expandableListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        viewHolder.expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {

            }
        });

        return convertView;
    }

    private class ViewHolder {
        public TextView giorno;
        public ExpandableListView expandableListView;

        public ViewHolder(){
            giorno = null;
            expandableListView = null;
        }
    }

}
