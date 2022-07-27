package com.example.comfortzone.notification;

import static android.content.Context.ALARM_SERVICE;
import static com.example.comfortzone.ui.HostActivity.REQUEST_CODE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.comfortzone.R;
import com.example.comfortzone.ui.HostActivity;

import java.util.Calendar;
import java.util.Date;

public class NotificationUtil {

    public static final int NOTIF_TIME = 11;
    public static final String ARG_AUTO_OPEN_SCREEN = "FRAGMENT";
    public static final String AUTO_OPEN_PROFILE = "profile";
    public static final String AUTO_OPEN_INPUT = "input";
    public static final String AUTO_OPEN_FLIGHT = "flight";
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public static void notificationSetup(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, NOTIF_TIME);
        calendar.set(Calendar.MINUTE, 36);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        Intent intent = new Intent(context.getApplicationContext(), NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(), REQUEST_CODE, intent, PendingIntent.FLAG_IMMUTABLE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        }
    }

    public static void createNotification(Context context) {

        Intent intent = new Intent(context, HostActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ARG_AUTO_OPEN_SCREEN, AUTO_OPEN_INPUT);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                REQUEST_CODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.drawable.logo);
        notificationBuilder.setContentTitle(context.getResources().getString(R.string.app_name))
                .setContentText(context.getResources().getString(R.string.notification_content))
                .setAutoCancel(true)
                .setContentIntent(resultPendingIntent);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                context.getResources().getString(R.string.app_name), NotificationManager.IMPORTANCE_DEFAULT);
        if (notificationManager != null) {
            notificationBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
            notificationManager.notify(REQUEST_CODE, notificationBuilder.build());
        }
    }

}
