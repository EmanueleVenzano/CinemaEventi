package nf.application.emanuele.tesi1;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private ArrayList<ArrayList<DataShowTimes>> listDataChild;
    String film;

    public ExpandableListAdapter (Context context, List<String> listDataHeader, ArrayList<ArrayList<DataShowTimes>> listDataChild, String film) {
        this.context = context;
        this.listDataChild = listDataChild;
        this.listDataHeader = listDataHeader;
        this.film = film;
    }

    public String getChildH (int groupPosition, int childPosition) {
        String[] temp = this.listDataChild.get(groupPosition).get(childPosition).getStart().split("T");
        String[] temp1 = temp[1].split(":");
        return temp1[0]+":"+temp1[1];
    }

    @Override
    public Object getChild (int groupPosition, int childPosition){
        return this.listDataChild.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId (int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView (final int groupPosition, final int childPosition,
                              boolean isLastChild, View convertView, final ViewGroup parent) {
        final String childText = (String) getChildH(groupPosition, childPosition);
        if(convertView==null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.proiezione_listview, null);
        }
        final ImageView img_selected = (ImageView) convertView.findViewById(R.id.ratingBar);
        final String cinema = (String)getGroup(groupPosition);
        final DataShowTimes dataShowTimes = (DataShowTimes)getChild(groupPosition, childPosition);
        PreferitiDB db = new PreferitiDB(context);
        final ArrayList<ArrayList<String>> infoPreferito = db.getPreferito(film);
        ArrayList<String> preferitiLuogo = new ArrayList<>();
        ArrayList<String> preferitiOrario = new ArrayList<>();
        for (int i=0; i<infoPreferito.size(); i++){
            preferitiLuogo.add(infoPreferito.get(i).get(1));
            preferitiOrario.add(infoPreferito.get(i).get(0));
        }
        boolean pippo = false;
        for (int i=0; i<infoPreferito.size(); i++) {
            if(cinema.equals(preferitiLuogo.get(i)) && ((DataShowTimes) getChild(groupPosition, childPosition)).getStart().equals(preferitiOrario.get(i))) {
                pippo=true;
                break;
            }
        }
        if(pippo) {
            img_selected.setImageResource(R.drawable.friends);
            img_selected.setTag(R.drawable.friends);
        }
        else{
            img_selected.setImageResource(R.drawable.calendar);
            img_selected.setTag(R.drawable.calendar);
        }
        String[] temp = dataShowTimes.getStart().split("T");
        final String[] temp1 = temp[0].split("-");
        final String[] temp2 = temp[1].split(":");
        img_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferitiDB db = new PreferitiDB(context);
                long result;
                switch ((Integer)img_selected.getTag()){
                    case R.drawable.friends:
                        img_selected.setImageResource(R.drawable.calendar);
                        img_selected.setTag(R.drawable.calendar);
                        result = db.deletePreferito(film, cinema, dataShowTimes.getStart());
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel((int) result);
                        break;
                    case R.drawable.calendar:
                        Date currentTime = Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTime();
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1])-1, Integer.parseInt(temp1[2]), Integer.parseInt(temp2[0]), Integer.parseInt(temp2[1]));
                        Date filmTime = calendar.getTime();
                        //if (filmTime.after(currentTime)) {
                        if (true) {
                            img_selected.setImageResource(R.drawable.friends);
                            img_selected.setTag(R.drawable.friends);
                            result = db.insertPreferito(film, "1", dataShowTimes.getStart(), cinema);
                            Intent resultIntent = new Intent(context, MainActivity.class);
                            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                            taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                            PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent((int) result, PendingIntent.FLAG_UPDATE_CURRENT);
                            long futureInMillis = filmTime.getTime()-7200000;
                            Uri sound = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION));
                            Notification notification;
                            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, (int) result);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                            if(futureInMillis-currentTime.getTime() < 0) {
                                notification = new Notification.Builder(context)
                                        .setContentIntent(resultPendingIntent)
                                        .setContentText("Affrettati, "+film+" inizierà tra meno di due ore a "+cinema)
                                        .setContentTitle(film)
                                        .setSound(sound)
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Affrettati, "+film+" inizierà tra meno di due ore a "+cinema))
                                        .build();
                            } else {
                                notification = new Notification.Builder(context)
                                        .setContentIntent(resultPendingIntent)
                                        .setContentText("Preparati ! Tra due ore devi essere a "+cinema+" per vedere "+film+"!")
                                        .setContentTitle(film)
                                        .setSound(sound)
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Preparati ! Tra due ore devi essere a "+cinema+" per vedere "+film+"!"))
                                        .build();
                            }
                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int) result, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            if (futureInMillis-currentTime.getTime() < 0) {
                                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+300, pendingIntent);
                            } else {
                                alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
                            }
                        }
                }
            }
        });

        TextView orario = (TextView) convertView.findViewById(R.id.proiezioneOrario);
        orario.setText(childText);
        TextView lingua = (TextView) convertView.findViewById(R.id.proiezioneLingua);
        if (dataShowTimes.getLanguage()=="null"){
            lingua.setText(Html.fromHtml("<b>Lingua: </b>"+"ita"));
        }else{
            lingua.setText(Html.fromHtml("<b>Lingua: </b>" +dataShowTimes.getLanguage()));
        }
        TextView sub = (TextView) convertView.findViewById(R.id.proiezioneSub);
        if (dataShowTimes.getSubtitle()=="null"){
            sub.setText(Html.fromHtml("<b>Sub: </b>"+"ita"));
        }else{
            sub.setText(Html.fromHtml("<b>Sub: </b>" +dataShowTimes.getSubtitle()));
        }
        TextView sala = (TextView) convertView.findViewById(R.id.proiezioneSala);
        sala.setText(Html.fromHtml("<b>Sala: </b>"+dataShowTimes.getAuditorium()));
        String type = "";
        if (dataShowTimes.isIs3D()){
            type = "3D";
        }
        if (dataShowTimes.isIMax()){
            if (!type.equals("")){
                type+=" ";
            }
            type+= "IMax";
        }
        if (!type.equals("")){
            TextView typeP = (TextView) convertView.findViewById(R.id.proiezioneType);
            typeP.setText(Html.fromHtml("<b>"+type+"</b>"));
        }
        Button button = (Button) convertView.findViewById(R.id.proiezioneLink);
        button. setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dataShowTimes.getLink()));
                context.startActivity(browserIntent);
            }
        });
        //TextView txtListChild = (TextView) convertView.findViewById(R.id.orario);
        //txtListChild.setText(childText);

        return convertView;
    }

    @Override
    public int getChildrenCount (int groupPosition) {
        return this.listDataChild.get(groupPosition).size();
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
    public View getGroupView (final int groupPosition, final boolean isExpanded, View convertView, final ViewGroup parent) {
        final String headerTitle = (String) getGroup(groupPosition);
        if(convertView==null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.proiezione_giorno_listview, null);
        }
        TextView cinema = (TextView) convertView.findViewById(R.id.proiezioneCinema);
        cinema.setText(headerTitle);
        Button button = (Button) convertView.findViewById(R.id.bottoneCinema);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, MapsActivity.class);
                intent.putExtra("cinema", headerTitle);
                context.startActivity(intent);
            }
        });
        //ExpandableListView eLV = (ExpandableListView) parent;
        //eLV.expandGroup(groupPosition);
        convertView.setOnClickListener(new View.OnClickListener() {
            final ViewGroup p = parent;
            final int i = groupPosition;
            @Override
            public void onClick(View v) {
                ExpandableListView eLV = (ExpandableListView) parent;
                if (isExpanded){
                    eLV.collapseGroup(groupPosition);
                }else{
                    eLV.expandGroup(groupPosition);
                }

            }
        });

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
