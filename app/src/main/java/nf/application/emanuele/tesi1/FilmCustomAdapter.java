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

public class FilmCustomAdapter extends ArrayAdapter<ArrayList<String>> {
    private Context context;
    private List<ArrayList<String>> cinemas;
    private ArrayList<ArrayList<DataShowTimes>> dataShowTimes;
    private String film;

    public FilmCustomAdapter (Context context, int textViewResourceId, List<ArrayList<String>> cinemas, ArrayList<ArrayList<DataShowTimes>> dataShowTimes, String film){
        super(context, textViewResourceId, cinemas);
        this.context = context;
        this.cinemas = cinemas;
        this.dataShowTimes = dataShowTimes;
        this.film = film;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.proiezioni_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.cinema = (TextView)convertView.findViewById(R.id.nomeCinema);
            viewHolder.button = (Button) convertView.findViewById(R.id.bottoneCinema);
            viewHolder.expandableListView = (ExpandableListView)convertView.findViewById(R.id.proiezioniExpandableListView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
//        final String cinema = getItem(position).get(0);
        viewHolder.cinema.setText(getItem(position).get(0));
        viewHolder.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, MapsActivity.class);
                intent.putExtra("cinema", getItem(position).get(0));
                context.startActivity(intent);
            }
        });
        ArrayList<ArrayList<DataShowTimes>> proiezioniPerGiorno = new ArrayList<>();
        ArrayList<String> giorni = new ArrayList<>();
        for (int i = 0; i < dataShowTimes.get(position).size(); i++){
            if (!dataShowTimes.get(position).get(i).getCinema_id().equals(getItem(position).get(1))) continue;
            String[] data = dataShowTimes.get(position).get(i).getStart().split("T");
            int pos = giorni.indexOf(data[0]);
            if (pos==-1){
                giorni.add(data[0]);
                proiezioniPerGiorno.add(new ArrayList<DataShowTimes>());
                pos = proiezioniPerGiorno.size()-1;
            }
            proiezioniPerGiorno.get(pos).add(dataShowTimes.get(position).get(i));
        }
        ExpandableListAdapter listAdapter = new ExpandableListAdapter(context, giorni, proiezioniPerGiorno, getItem(position).get(0), film);
        viewHolder.expandableListView.setAdapter(listAdapter);
        return convertView;
    }

    private class ViewHolder {
        public TextView cinema;
        public ExpandableListView expandableListView;
        public Button button;

        public ViewHolder(){
            cinema = null;
            expandableListView = null;
            button = null;
        }
    }
}
