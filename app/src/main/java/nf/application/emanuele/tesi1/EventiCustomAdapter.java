package nf.application.emanuele.tesi1;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class EventiCustomAdapter extends ArrayAdapter<ArrayList<String>> {
    private Context context;

    public EventiCustomAdapter (Context context, int textViewResoutceId, List<ArrayList<String>> objects){
        super(context, textViewResoutceId, objects);
        this.context=context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.eventi_items_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.titolo = (TextView)convertView.findViewById(R.id.item_title_evento);
            viewHolder.luogo=(TextView)convertView.findViewById(R.id.item_luogo_evento);
            viewHolder.data = (TextView)convertView.findViewById(R.id.item_data_evento);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ArrayList<String> copertina =getItem(position);
        viewHolder.titolo.setText(copertina.get(2));
        viewHolder.luogo.setText("Dove: "+copertina.get(1));
        viewHolder.data.setText("Quando: "+copertina.get(0));

        /*
        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageView view = (ImageView) v;
                PreferitiDB db;
                long result;
                switch ((Integer) view.getTag()){
                    case R.drawable.delete:
                        db = new PreferitiDB(context);
                        result = db.deletePreferito(copertina.get(2),copertina.get(1), copertina.get(0));
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel((int) result);
                        view.setImageResource(R.drawable.plus);
                        view.setTag(R.drawable.plus);
                        break;
                    case R.drawable.plus:
                        Date currentTime = Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTime();
                        Date filmTime = new Date();
                        filmTime.setTime(currentTime.getTime());
                        String[] dataFilm = copertina.get(0).split(":");
                        filmTime.setHours(Integer.parseInt(dataFilm[0]));
                        filmTime.setMinutes(Integer.parseInt(dataFilm[1]));
//                        if (filmTime.after(currentTime)){
                        if (true) {
                            db = new PreferitiDB(context);
                            result = db.insertPreferito(copertina.get(2), "1", copertina.get(0), copertina.get(1));
                            view.setImageResource(R.drawable.delete);
                            view.setTag(R.drawable.delete);

                            Intent resultIntent = new Intent(context, MainActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addNextIntentWithParentStack(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent((int) result, PendingIntent.FLAG_UPDATE_CURRENT);
                            long futureInMillis = filmTime.getTime()-7200000;
                            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification notification;
                            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, (int) result);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            if (futureInMillis-currentTime.getTime()<0){
                                notification = new Notification.Builder(context)
                                        .setContentIntent(resultPendingIntent)
                                        .setContentText("Affrettati, "+copertina.get(2)+" inizierà tra meno di due ore a "+copertina.get(1))
                                        .setContentTitle(copertina.get(2))
                                        .setSound(sound)
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Affrettati, "+copertina.get(2)+" inizierà tra meno di due ore a "+copertina.get(1)))
                                        .build();
                            }else{
                                notification = new Notification.Builder(context)
                                        .setContentIntent(resultPendingIntent)
                                        .setContentTitle(copertina.get(2))
                                        .setSound(sound)
                                        .setContentText("Preparati! Tra 2 ore devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!")
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Preparati! Tra 2 ore devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!"))
                                        .build();
                            }
                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, (int)result, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                            if (futureInMillis-currentTime.getTime()<0){
                                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+300, pendingIntent);
                            }else{
                                alarmManager.set(AlarmManager.RTC_WAKEUP, futureInMillis, pendingIntent);
                            }
                        }
                        break;
                }
            }

        });
        */
        return convertView;

    }


    public class ViewHolder {
        public TextView titolo;
        public TextView luogo;
        public TextView data;

        public ViewHolder(){
            titolo=null;
            luogo=null;
            data=null;
        }
    }
}