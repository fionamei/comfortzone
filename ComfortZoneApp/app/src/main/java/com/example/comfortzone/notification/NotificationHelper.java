package com.example.comfortzone.notification;

import static com.example.comfortzone.ui.HostActivity.REQUEST_CODE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.comfortzone.R;
import com.example.comfortzone.ui.HostActivity;

public class NotificationHelper {

    private Context context;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";
    public static final String TITLE = "ComfortZone";
    public static final String CONTENT = "How's the weather right now?";

    public NotificationHelper(Context context) {
        this.context = context;
    }

    public void createNotification() {

        Intent intent = new Intent(context, HostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                REQUEST_CODE, intent,
                PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.blue_launcher);
        notificationBuilder.setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(CONTENT)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                TITLE, NotificationManager.IMPORTANCE_DEFAULT);
        assert notificationManager != null;
        notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        notificationManager.createNotificationChannel(notificationChannel);

        notificationManager.notify(REQUEST_CODE, notificationBuilder.build());
    }
}
