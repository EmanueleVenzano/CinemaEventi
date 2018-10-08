package nf.application.emanuele.tesi1;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

public class NotificationPublisher extends BroadcastReceiver {

    public static String NOTIFICATION_ID = "notification-id";
    public static String NOTIFICATION = "notification";

    public void onReceive (Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = intent.getParcelableExtra(NOTIFICATION);
        int id = intent.getIntExtra(NOTIFICATION_ID, 0);
        notificationManager.notify(id, notification);

/*        Intent resultIntent = new Intent(context, MainActivity.class);
        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
        taskStackBuilder.addNextIntentWithParentStack(resultIntent);
        PendingIntent resultPendingIntent = taskStackBuilder.getPendingIntent(10000, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri sound = RingtoneManager.getDefaultUri((RingtoneManager.TYPE_NOTIFICATION));
        notification = new Notification.Builder(context)
                .setContentIntent(resultPendingIntent)
                .setContentText("Affrettati, ")
                .setContentTitle("sss")
                .setSound(sound)
                .setSmallIcon(R.drawable.ticket)
                .setStyle(new Notification.BigTextStyle().bigText("Affrettati,"))
                .build();
        notificationManager.notify(10000, notification);*/

    }

}
