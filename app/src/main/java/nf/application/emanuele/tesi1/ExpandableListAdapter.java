package nf.application.emanuele.tesi1;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    String film;

    public ExpandableListAdapter (Context context, List<String> listDataHeader, HashMap<String, List<String>> listDataChild, String film) {
        this.context = context;
        this.listDataChild = listDataChild;
        this.listDataHeader = listDataHeader;
        this.film = film;
    }

    @Override
    public Object getChild (int groupPosition, int childPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getChildId (int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView (final int groupPosition, final int childPosition,
                              boolean isLastChild, View convertView, final ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if(convertView==null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.proiezione_listview, null);
        }

        final ImageView img_selected = (ImageView) convertView.findViewById(R.id.ratingBar);
        PreferitiDB db = new PreferitiDB(context);
        final ArrayList<ArrayList<String>> infoPreferito = db.getPreferito(film);
        ArrayList<String> preferitiLuogo = new ArrayList<>();
        ArrayList<String> preferitiOrario = new ArrayList<>();
        for (int i=0; i<infoPreferito.size(); i++){
            preferitiLuogo.add(infoPreferito.get(i).get(1));
            preferitiOrario.add(infoPreferito.get(i).get(0));
        }
        final String actualCinema = listDataHeader.get(groupPosition);
        if (infoPreferito.size()>0 && preferitiLuogo.contains(actualCinema) && preferitiOrario.contains(childText)){
            img_selected.setImageResource(R.drawable.friends);
            img_selected.setTag(R.drawable.friends);
        }else{
            img_selected.setImageResource(R.drawable.calendar);
            img_selected.setTag(R.drawable.calendar);
        }
        final View p = convertView;
        img_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferitiDB db = new PreferitiDB(context);
                int info;
                long result;
                switch ((Integer)img_selected.getTag()){
                    case R.drawable.friends:
                        img_selected.setImageResource(R.drawable.calendar);
                        img_selected.setTag(R.drawable.calendar);
                        info = db.deletePreferito(film);
                        Toast.makeText(context, "info: "+info, Toast.LENGTH_LONG).show();
                        break;
                    case R.drawable.calendar:
                        img_selected.setImageResource(R.drawable.friends);
                        img_selected.setTag(R.drawable.friends);
                        //info = db.deletePreferito(film);
                        result = db.insertPreferito(film, "1", childText, listDataHeader.get(groupPosition));
                        //Toast.makeText(context, "info: "+info+", result: "+result, Toast.LENGTH_LONG).show();
                        Toast.makeText(context, "result: "+result, Toast.LENGTH_LONG).show();
                }
            }
        });

        TextView txtListChild = (TextView) convertView.findViewById(R.id.orario);
        txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount (int groupPosition) {
        return this.listDataChild.get(this.listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup (int groupPosition) {
        return this.listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount () {
        return this.listDataHeader.size();
    }

    @Override
    public long getGroupId (int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView (int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        if(convertView==null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.proiezioni_listview, null);
        }

        TextView nomeCinema = (TextView) convertView.findViewById(R.id.nomeCinema);
        nomeCinema.setTypeface(null, Typeface.BOLD);
        nomeCinema.setText(headerTitle);

        //ExpandableListView eLV = (ExpandableListView) parent;
        //eLV.expandGroup(groupPosition);

        return convertView;
    }

    @Override
    public boolean hasStableIds () {
        return false;
    }

    @Override
    public boolean isChildSelectable (int groupPosition, int childPosition) {
        return true;
    }

}
