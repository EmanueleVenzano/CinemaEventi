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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
        boolean pippo = false;
        for (int i=0; i<infoPreferito.size(); i++) {
            if(actualCinema.equals(preferitiLuogo.get(i)) && childText.equals(preferitiOrario.get(i))) {
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
        final View p = convertView;
        img_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferitiDB db = new PreferitiDB(context);
                long result;
                switch ((Integer)img_selected.getTag()){
                    case R.drawable.friends:
                        img_selected.setImageResource(R.drawable.calendar);
                        img_selected.setTag(R.drawable.calendar);
                        result = db.deletePreferito(film, actualCinema, childText);
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel((int) result);
                        break;
                    case R.drawable.calendar:
                        Date currentTime = Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTime();
                        Date filmTime = new Date();
                        filmTime.setTime(currentTime.getTime());
                        String[] dataFilm = childText.split(":");
                        filmTime.setHours(Integer.parseInt(dataFilm[0]));
                        filmTime.setMinutes(Integer.parseInt(dataFilm[1]));
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
                                        .setContentText("Affrettati, "+film+" inizierà tra meno di due ore a "+actualCinema)
                                        .setContentTitle(film)
                                        .setSound(sound)
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Affrettati, "+film+" inizierà tra meno di due ore a "+actualCinema))
                                        .build();
                            } else {
                                notification = new Notification.Builder(context)
                                        .setContentIntent(resultPendingIntent)
                                        .setContentText("Preparati ! Tra due ore devi essere a "+actualCinema+" per vedere "+film+"!")
                                        .setContentTitle(film)
                                        .setSound(sound)
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Preparati ! Tra due ore devi essere a "+actualCinema+" per vedere "+film+"!"))
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
        final String headerTitle = (String) getGroup(groupPosition);

        if(convertView==null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.proiezioni_listview, null);
        }

        TextView nomeCinema = (TextView) convertView.findViewById(R.id.nomeCinema);
        nomeCinema.setTypeface(null, Typeface.BOLD);
        nomeCinema.setText(headerTitle);

        //ExpandableListView eLV = (ExpandableListView) parent;
        //eLV.expandGroup(groupPosition);

        Button button = (Button) convertView.findViewById(R.id.bottoneCinema);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, MapsActivity.class);
                intent.putExtra("cinema", headerTitle);
                context.startActivity(intent);
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
