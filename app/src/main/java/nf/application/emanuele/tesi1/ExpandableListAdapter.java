package nf.application.emanuele.tesi1;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

    public ExpandableListAdapter (Context context, List<String> listDataHeader, HashMap<String, List<String>> listDataChild) {
        this.context = context;
        this.listDataChild = listDataChild;
        this.listDataHeader = listDataHeader;
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
    public View getChildView (int groupPosition, final int childPosition,
                              boolean isLastChild, View convertView, ViewGroup parent) {
        final String childText = (String) getChild(groupPosition, childPosition);

        if(convertView==null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.proiezione_listview, null);
        }

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
