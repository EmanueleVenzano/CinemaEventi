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

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listDataHeader;
    private ArrayList<ArrayList<DataShowTimes>> listDataChild;
    String film;
    String cinema;

    public ExpandableListAdapter (Context context, List<String> listDataHeader, ArrayList<ArrayList<DataShowTimes>> listDataChild, String cinema, String film) {
        this.context = context;
        this.listDataChild = listDataChild;
        this.listDataHeader = listDataHeader;
        this.film = film;
        this.cinema = cinema;
    }

    @Override
    public Object getChild (int groupPosition, int childPosition) {
        return this.listDataChild.get(groupPosition).get(childPosition).getStart();
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

        boolean pippo = false;
        for (int i=0; i<infoPreferito.size(); i++) {
            if(cinema.equals(preferitiLuogo.get(i)) && childText.equals(preferitiOrario.get(i))) {
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
        String[] temp = childText.split("T");
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
                        result = db.deletePreferito(film, cinema, childText);
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel((int) result);
                        break;
                    case R.drawable.calendar:
                        Date currentTime = Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTime();
                        Date filmTime = new Date();
                        filmTime.setTime(currentTime.getTime());
                        filmTime.setDate(Integer.parseInt(temp1[2]));
                        filmTime.setMonth(Integer.parseInt(temp1[1]));
                        filmTime.setYear(Integer.parseInt(temp1[0]));
                        filmTime.setHours(Integer.parseInt(temp2[0]));
                        filmTime.setMinutes(Integer.parseInt(temp2[1]));
                        //if (filmTime.after(currentTime)) {
                        if (true) {
                            img_selected.setImageResource(R.drawable.friends);
                            img_selected.setTag(R.drawable.friends);
                            result = db.insertPreferito(film, "1", childText, listDataHeader.get(groupPosition));
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
        orario.setText(temp2[0]+":"+temp2[1]);
        TextView lingua = (TextView) convertView.findViewById(R.id.proiezioneLingua);
        if (listDataChild.get(groupPosition).get(childPosition).getLanguage()=="null"){
            lingua.setText(Html.fromHtml("<b>Lingua: </b>"+"ita"));
        }else{
            lingua.setText(Html.fromHtml("<b>Lingua: </b>" +listDataChild.get(groupPosition).get(childPosition).getLanguage()));
        }
        TextView sub = (TextView) convertView.findViewById(R.id.proiezioneSub);
        if (listDataChild.get(groupPosition).get(childPosition).getSubtitle()=="null"){
            sub.setText(Html.fromHtml("<b>Sub: </b>"+"ita"));
        }else{
            sub.setText(Html.fromHtml("<b>Sub: </b>" +listDataChild.get(groupPosition).get(childPosition).getSubtitle()));
        }
        TextView sala = (TextView) convertView.findViewById(R.id.proiezioneSala);
        sala.setText(Html.fromHtml("<b>Sala: </b>"+listDataChild.get(groupPosition).get(childPosition).getAuditorium()));
        String type = "";
        if (listDataChild.get(groupPosition).get(childPosition).isIs3D()){
            type = "3D";
        }
        if (listDataChild.get(groupPosition).get(childPosition).isIMax()){
            if (!type.equals("")){
                type+=" ";
            }
            type+= "IMax";
        }
        if (!type.equals("")){
            TextView typeP = (TextView) convertView.findViewById(R.id.proiezioneType);
            typeP.setText(Html.fromHtml("<b>"+type+"</b>"));
        }
        TextView link = (TextView) convertView.findViewById(R.id.proiezioneLink);
        SpannableString content = new SpannableString(listDataChild.get(groupPosition).get(childPosition).getLink());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        link.setText(content);

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
        TextView giorno = (TextView) convertView.findViewById(R.id.proiezioneGiorno);
        giorno.setTypeface(null, Typeface.BOLD);
        String[] dataTime = headerTitle.split("-");
        Date date = new Date();
        date.setYear(Integer.parseInt(dataTime[0]));
        date.setMonth(Integer.parseInt(dataTime[1])-1);
        date.setDate(Integer.parseInt(dataTime[2]));
        int day = date.getDay();
        switch (day){
            case 0: giorno.setText("Domenica"); break;
            case 1: giorno.setText("Lunedì"); break;
            case 2: giorno.setText("Martedì"); break;
            case 3: giorno.setText("Mercoledì"); break;
            case 4: giorno.setText("Giovedì"); break;
            case 5: giorno.setText("Venerdì"); break;
            case 6: giorno.setText("Sabato"); break;
            default: giorno.setText("Invalid"); break;
        }
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
