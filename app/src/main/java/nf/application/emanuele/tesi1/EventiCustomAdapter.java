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
            viewHolder.luogo = (TextView)convertView.findViewById(R.id.item_luogo_evento);
            viewHolder.data = (TextView)convertView.findViewById(R.id.item_data_evento);
            viewHolder.img = (ImageView)convertView.findViewById(R.id.ratingBar);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final ArrayList<String> copertina = getItem(position);
        viewHolder.titolo.setText(copertina.get(2));
        viewHolder.luogo.setText("Dove: "+copertina.get(1));
        viewHolder.data.setText("Quando: "+copertina.get(0));
        PreferitiDB db = new PreferitiDB(context);
        final ArrayList<ArrayList<String>> infoPreferito = db.getPreferito(copertina.get(2));
        ArrayList<String> preferitiLuogo = new ArrayList<>();
        ArrayList<String> preferitiOrario = new ArrayList<>();
        for (int i=0; i<infoPreferito.size(); i++){
            preferitiLuogo.add(infoPreferito.get(i).get(1));
            preferitiOrario.add(infoPreferito.get(i).get(0));
        }
        boolean pippo = false;
        for (int i=0; i<infoPreferito.size(); i++) {
            if(copertina.get(1).equals(preferitiLuogo.get(i)) && copertina.get(0).equals(preferitiOrario.get(i))) {
                pippo=true;
                break;
            }
        }
        if(pippo) {
            viewHolder.img.setImageResource(R.drawable.friends);
            viewHolder.img.setTag(R.drawable.friends);
        }
        else{
            viewHolder.img.setImageResource(R.drawable.calendar);
            viewHolder.img.setTag(R.drawable.calendar);
        }

        final View p = convertView;
        final ImageView img_selected = (ImageView) convertView.findViewById(R.id.ratingBar);
        img_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PreferitiDB db = new PreferitiDB(context);
                long result;
                switch ((Integer)img_selected.getTag()){
                    case R.drawable.friends:
                        img_selected.setImageResource(R.drawable.calendar);
                        img_selected.setTag(R.drawable.calendar);
                        result = db.deletePreferito(copertina.get(2), copertina.get(1), copertina.get(0));
                        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel((int) result);
                        break;
                    case R.drawable.calendar:
                        Date currentTime = Calendar.getInstance(Calendar.getInstance().getTimeZone()).getTime();
                        Date filmTime = new Date();
                        filmTime.setTime(currentTime.getTime());
                        String[] dataFilm = copertina.get(0).split(" ");
                        String[] temp1 = dataFilm[0].split("/");
                        String tempYear = "20"+temp1[2];
                        String[] temp2 = dataFilm[1].split(":");
                        filmTime.setDate(Integer.parseInt(temp1[0]));
                        filmTime.setMonth(Integer.parseInt(temp1[1]));
                        filmTime.setYear(Integer.parseInt(tempYear));
                        filmTime.setHours(Integer.parseInt(temp2[0]));
                        filmTime.setMinutes(Integer.parseInt(temp2[1]));
                        //if (filmTime.after(currentTime)) {
                        if (true) {
                            img_selected.setImageResource(R.drawable.friends);
                            img_selected.setTag(R.drawable.friends);
                            result = db.insertPreferito(copertina.get(2), "0", copertina.get(0), copertina.get(1));
                            Intent resultIntent = new Intent(context, MainActivity.class);
                            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
                            taskStackBuilder.addNextIntentWithParentStack(resultIntent);
                            PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent((int) result, PendingIntent.FLAG_UPDATE_CURRENT);
                            long temp = filmTime.getTime();
                            long futureInMillis = filmTime.getTime()-604800000;
                            Uri sound = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION));
                            Notification notification;
                            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, (int) result);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                            if(futureInMillis-currentTime.getTime() < 0) {
                                notification = new Notification.Builder(context)
                                        .setContentIntent(resultPendingIntent)
                                        .setContentText("Affrettati, "+copertina.get(2)+" inizierà tra meno di una settimana a "+copertina.get(1))
                                        .setContentTitle(copertina.get(2))
                                        .setSound(sound)
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Affrettati, "+copertina.get(2)+" inizierà tra meno di una settimana a "+copertina.get(1)))
                                        .build();
                            } else {
                                notification = new Notification.Builder(context)
                                        .setContentIntent(resultPendingIntent)
                                        .setContentText("Preparati ! Tra una settimana devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!")
                                        .setContentTitle(copertina.get(2))
                                        .setSound(sound)
                                        .setSmallIcon(R.drawable.ticket)
                                        .setStyle(new Notification.BigTextStyle().bigText("Preparati ! Tra una settimana devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!"))
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

        return convertView;

    }


    public class ViewHolder {
        public TextView titolo;
        public TextView luogo;
        public TextView data;
        public ImageView img;

        public ViewHolder(){
            titolo=null;
            luogo=null;
            data=null;
            img=null;
        }
    }
}