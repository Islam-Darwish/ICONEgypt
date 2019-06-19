package com.mixapplications.iconegypt.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.mixapplications.iconegypt.R;
import com.mixapplications.iconegypt.activity.SelectActivity;
import com.mixapplications.iconegypt.activity.SplashScreen;
import com.mixapplications.iconegypt.fragment.MainFragment;
import com.mixapplications.iconegypt.models.AppData;
import com.mixapplications.iconegypt.models.Employee;
import com.mixapplications.iconegypt.models.Events;
import com.mixapplications.iconegypt.models.Prefs;

import static com.mixapplications.iconegypt.models.AppData.currentUser;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    FirebaseAuth auth;

    private static PendingIntent prepareIntent(Context context, String fragment) {
        Intent intent = new Intent(context, SelectActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("fragment", fragment);
        intent.putExtra("isLogin", currentUser != null);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onNewToken(String token) {
        super.onNewToken(token);
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
        if (remoteMessage.getFrom().substring(remoteMessage.getFrom().lastIndexOf("/") + 1).equalsIgnoreCase("notifications_news")) {
            Intent intent;
            String fragment = "";
            long timestamp = 0;
            if (remoteMessage.getData().get("fromEmail") != null && currentUser != null && !remoteMessage.getData().get("fromEmail").equalsIgnoreCase(currentUser.getEmail())) {

                Prefs.initPrefs(getApplicationContext(), "icon_egypt", Context.MODE_PRIVATE);
                Prefs.putBoolean(currentUser.getEmail() + "-news", true);
                //news 0 , tasks 1 ,  forms 2 , events 3
                try {
                    if (SelectActivity.fragmentTag.equalsIgnoreCase("main_fragment"))
                        MainFragment.setRedDotVisibility(getApplicationContext(), 0, true);
                } catch (Exception e) {
                }
                if (AppData.currentUser != null) {
                    intent = new Intent(this, SelectActivity.class);
                    fragment = "news";
                } else {
                    intent = new Intent(this, SplashScreen.class);
                }
                try {
                    timestamp = Long.parseLong(remoteMessage.getData().get("timestamp"));
                } catch (Exception e) {
                }
                showNewsNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), intent, fragment, timestamp);
            }
        }

        if (remoteMessage.getFrom().substring(remoteMessage.getFrom().lastIndexOf("/") + 1).equalsIgnoreCase("notifications_tasks")) {
            Intent intent;
            String fragment = "";
            long timestamp = 0;
            if (remoteMessage.getData().get("fromEmployee") != null &&
                    currentUser != null &&
                    !remoteMessage.getData().get("fromEmployee").equalsIgnoreCase(currentUser.getEmail())
                    && remoteMessage.getData().get("toEmployee").equals(currentUser.getEmail())) {

                Prefs.initPrefs(getApplicationContext(), "icon_egypt", Context.MODE_PRIVATE);
                Prefs.putBoolean(currentUser.getEmail() + "-tasks", true);
                //news 0 , tasks 1 ,  forms 2 , events 3
                try {
                    if (SelectActivity.fragmentTag.equalsIgnoreCase("main_fragment"))
                        MainFragment.setRedDotVisibility(getApplicationContext(), 1, true);
                } catch (Exception e) {
                }
                if (AppData.currentUser != null) {
                    intent = new Intent(this, SelectActivity.class);
                    fragment = "tasks";
                } else {
                    intent = new Intent(this, SplashScreen.class);
                }
                try {
                    timestamp = Long.parseLong(remoteMessage.getData().get("timestamp"));
                } catch (Exception e) {
                }
                showNewsNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), intent, fragment, timestamp);
            }
        }


        if (remoteMessage.getFrom().substring(remoteMessage.getFrom().lastIndexOf("/") + 1).equalsIgnoreCase("notifications_events")) {
            final Context context = this;
            FirebaseDatabase database;
            DatabaseReference ref;
            database = FirebaseDatabase.getInstance();
            ref = database.getReference();
            final long timestamp;
            timestamp = Long.parseLong(remoteMessage.getData().get("timestamp"));
            if (timestamp != 0) {
                Query query = ref.child("events").orderByChild("timestamp").equalTo(timestamp);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            final Events event = snapshot.getValue(Events.class);
                            if (event != null) {
                                if (remoteMessage.getData().get("fromEmployee") != null &&
                                        currentUser != null &&
                                        !remoteMessage.getData().get("fromEmployee").equalsIgnoreCase(currentUser.getEmail())) {
                                    boolean isMine = false;
                                    for (Employee employee : event.getToEmployee()) {
                                        if (employee.getEmail().equalsIgnoreCase(currentUser.getEmail()))
                                            isMine = true;
                                    }
                                    if (isMine) {
                                        Intent intent;
                                        String fragment;
                                        Prefs.initPrefs(getApplicationContext(), "icon_egypt", Context.MODE_PRIVATE);
                                        Prefs.putBoolean(currentUser.getEmail() + "-events", true);
                                        //news 0 , tasks 1 ,  forms 2 , events 3
                                        try {
                                            if (SelectActivity.fragmentTag.equalsIgnoreCase("main_fragment"))
                                                MainFragment.setRedDotVisibility(getApplicationContext(), 3, true);
                                        } catch (Exception e) {
                                        }
                                        if (AppData.currentUser != null) {
                                            intent = new Intent(context, SelectActivity.class);
                                            fragment = "events";
                                        } else {
                                            intent = new Intent(context, SplashScreen.class);
                                            fragment = "";
                                        }

                                        showNewsNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("body"), intent, fragment, timestamp);
                                    }
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }


        }

    }

    public void showNewsNotification(String title, String body, Intent intent, String fragment, long timestamp) {
        int notificationId = (int) (timestamp + 1556318124);
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            StatusBarNotification[] notifications =
                    notificationManager.getActiveNotifications();
            for (StatusBarNotification notification : notifications) {

                if (notification.getId() == notificationId) {
                    return;
                }
            }
        }
        String name = "ICON Egypt Notifications";
        String channelId = "channel-03";
        String descriptionText = "ICON Egypt Notification Channel";
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(intent);
        PendingIntent resultPendingIntent = prepareIntent(getApplicationContext(), fragment);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);
        builder.setDefaults(Notification.DEFAULT_ALL).setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setColor(0x960000)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setContentText(body)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setAutoCancel(true)
                .setLights(0xffff0000, 300, 300)
                .setVibrate(new long[]{100, 100});

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, name, importance);
            mChannel.setDescription(descriptionText);
            mChannel.enableLights(true);
            mChannel.setLightColor(0xffff0000);
            mChannel.setShowBadge(true);
            mChannel.setLockscreenVisibility(1);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 100});
            if (notificationManager != null)
                notificationManager.createNotificationChannel(mChannel);
        }


        if (notificationManager != null)
            notificationManager.notify(notificationId, builder.build());
    }
}