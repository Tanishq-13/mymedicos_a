package com.medical.my_medicos.activities.notifications.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.job.JobDetailsActivity;

import java.util.concurrent.atomic.AtomicInteger;

public class FirebaseServiceReceivernotification extends FirebaseMessagingService {

    private static final String CHANNEL_ID = "Job Alerts";
    private static final AtomicInteger notificationId = new AtomicInteger(0);
    private static final String PREFS_NAME = "JobNotificationPrefs";
    private static final String SENT_KEY = "notificationsSent";
    private static final String OPENED_KEY = "notificationsOpened";

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int notificationsSent = prefs.getInt(SENT_KEY, 0);
        int notificationsOpened = prefs.getInt(OPENED_KEY, 0);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager);
        }

        String title = remoteMessage.getData().get("title");
        String organiser = remoteMessage.getData().get("Organiser");
        String jobId = remoteMessage.getData().get("documentId");

        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("documentid", jobId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.placeholderjobouter, null);
        Bitmap largeIcon = getBitmapFromDrawable(drawable);

        String contentText = "Organized by " + organiser;
        if (notificationsSent - notificationsOpened > 2) {
            contentText = "More than two jobs in your specialty have been posted!";
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(OPENED_KEY, notificationsSent);  // This assumes user sees this important notification
            editor.apply();
        }
        Log.d("bjckbdskb", String.valueOf(notificationsSent));
        Log.d("bjckbdskb", String.valueOf(notificationsOpened));

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("New Opening: " + title)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.logoooooooofor)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(notificationId.incrementAndGet(), notificationBuilder.build());

        // Update sent notifications count
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(SENT_KEY, notificationsSent + 1);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotificationChannel(NotificationManager manager) {
        Uri soundUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.new_job);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build();

        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Job Alerts", NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription("Notifications for job openings");
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.setSound(soundUri, audioAttributes);
        manager.createNotificationChannel(channel);
    }

    public static Bitmap getBitmapFromDrawable(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        } else if (drawable instanceof VectorDrawable) {
            Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        }
        return null;
    }
}
