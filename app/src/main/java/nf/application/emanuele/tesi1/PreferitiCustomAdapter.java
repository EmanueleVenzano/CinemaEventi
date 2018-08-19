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

public class PreferitiCustomAdapter extends ArrayAdapter<ArrayList<String>> {
    private Context context;
    private String isFilm;

    public PreferitiCustomAdapter (Context context, int textViewResoutceId, List<ArrayList<String>> objects, String isFilm){
        super(context, textViewResoutceId, objects);
        this.context=context;
        this.isFilm=isFilm;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ViewHolder viewHolder = null;
        if (convertView==null){
            LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.preferiti_items_listview, null);
            viewHolder = new ViewHolder();
            viewHolder.nome = (TextView)convertView.findViewById(R.id.item_title_film);
            viewHolder.luogo=(TextView)convertView.findViewById(R.id.item_luogo_film);
            viewHolder.data = (TextView)convertView.findViewById(R.id.item_data_film);
            viewHolder.ora = (TextView)convertView.findViewById(R.id.item_ora_film);
            viewHolder.img=(ImageView)convertView.findViewById(R.id.item_star_film);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final ArrayList<String> copertina =getItem(position);
        String[] tempData;
        if (isFilm.equals("0")){
            tempData = copertina.get(0).split(" ");
        }else {
            tempData = copertina.get(0).split("T");
            String[] t = tempData[1].split(":");
            tempData[1] = t[0]+":"+t[1];
        }

        viewHolder.nome.setText(copertina.get(2));
        viewHolder.luogo.setText("Dove: "+copertina.get(1));
        viewHolder.data.setText("Data: "+tempData[0]);
        viewHolder.ora.setText("Ora: "+tempData[1]);

        viewHolder.img.setImageResource(R.drawable.delete);
        viewHolder.img.setTag(R.drawable.delete);
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
                        Calendar calendar = Calendar.getInstance();
                        Date filmTime = new Date();
                        filmTime.setTime(currentTime.getTime());
                        if (isFilm.equals("0")){
                            String[] temp = copertina.get(0).split(" ");
                            String[] temp1 = temp[0].split("/");
                            String[] dataFilm = temp[1].split(":");
                            calendar.set(Integer.parseInt("20"+temp1[2]), Integer.parseInt(temp1[1])-1, Integer.parseInt(temp1[0]), Integer.parseInt(dataFilm[0]), Integer.parseInt(dataFilm[1]));
                        }else{
                            String[] temp = copertina.get(0).split("T");
                            String[] temp1 = temp[0].split("-");
                            String[] temp2 = temp[1].split(":");
                            calendar.set(Integer.parseInt(temp1[0]), Integer.parseInt(temp1[1])-1, Integer.parseInt(temp1[2]), Integer.parseInt(temp2[0]), Integer.parseInt(temp2[0]));
                        }
                        filmTime = calendar.getTime();
                        //                        if (filmTime.after(currentTime)){
                        if (true) {
                            db = new PreferitiDB(context);
                            result = db.insertPreferito(copertina.get(2), isFilm, copertina.get(0), copertina.get(1));
                            view.setImageResource(R.drawable.delete);
                            view.setTag(R.drawable.delete);

                            Intent resultIntent = new Intent(context, MainActivity.class);
                            TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                            stackBuilder.addNextIntentWithParentStack(resultIntent);
                            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent((int) result, PendingIntent.FLAG_UPDATE_CURRENT);
                            long futureInMillis;
                            if (isFilm.equals("0")){
                                futureInMillis = filmTime.getTime()-604800000;
                            }else{
                                futureInMillis = filmTime.getTime()-7200000;
                            }
                            Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                            Notification notification;
                            Intent notificationIntent = new Intent(context, NotificationPublisher.class);
                            notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, (int) result);
                            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                            if (futureInMillis-currentTime.getTime()<0){
                                if (isFilm.equals("0")){
                                    notification = new Notification.Builder(context)
                                            .setContentIntent(resultPendingIntent)
                                            .setContentTitle(copertina.get(2))
                                            .setSound(sound)
                                            .setSmallIcon(R.drawable.ticket)
                                            .setContentText("Affrettati, "+copertina.get(2)+" inizierà tra meno di una settimana a "+copertina.get(1))
                                            .setStyle(new Notification.BigTextStyle().bigText("Affrettati, "+copertina.get(2)+" inizierà tra meno di una settimana a "+copertina.get(1)))
                                            .build();
                                }else{
                                    notification = new Notification.Builder(context)
                                            .setContentIntent(resultPendingIntent)
                                            .setContentTitle(copertina.get(2))
                                            .setSound(sound)
                                            .setSmallIcon(R.drawable.ticket)
                                            .setContentText("Affrettati, "+copertina.get(2)+" inizierà tra meno di due ore a "+copertina.get(1))
                                            .setStyle(new Notification.BigTextStyle().bigText("Affrettati, "+copertina.get(2)+" inizierà tra meno di due ore a "+copertina.get(1)))
                                            .build();
                                }
                            }else{
                                if (isFilm.equals("0")){
                                    notification = new Notification.Builder(context)
                                            .setContentIntent(resultPendingIntent)
                                            .setContentTitle(copertina.get(2))
                                            .setSound(sound)
                                            .setContentText("Preparati! Tra una settimana devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!")
                                            .setSmallIcon(R.drawable.ticket)
                                            .setStyle(new Notification.BigTextStyle().bigText("Preparati! Tra una settimana devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!"))
                                            .build();
                                }else {
                                    notification = new Notification.Builder(context)
                                            .setContentIntent(resultPendingIntent)
                                            .setContentTitle(copertina.get(2))
                                            .setSound(sound)
                                            .setContentText("Preparati! Tra due ore devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!")
                                            .setSmallIcon(R.drawable.ticket)
                                            .setStyle(new Notification.BigTextStyle().bigText("Preparati! Tra due ore devi essere a "+copertina.get(1)+" per vedere "+copertina.get(2)+"!"))
                                            .build();
                                }
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
        return convertView;
    }

    private class ViewHolder {
        public TextView nome;
        public TextView luogo;
        public TextView data;
        public TextView ora;
        public ImageView img;

        public ViewHolder(){
            nome=null;
            luogo=null;
            data=null;
            ora=null;
            img=null;
        }
    }
}