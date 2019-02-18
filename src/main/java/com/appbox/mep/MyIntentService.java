package com.appbox.mep;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 *
 * helper methods.
 */
public class MyIntentService extends IntentService {

    private static final String ACTION_FRIENDS = "com.appbox.mep.action.FOO";
    private static final String ACTION_EVENTS = "com.appbox.mep.action.BAZ";
    private static final String EXTRA_PARAM1 = "com.appbox.mep.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.appbox.mep.extra.PARAM2";
    private DatabaseReference mDatabaseReference;
    private final String CHANNEL_ID= "1";
    private final int Noti_id = 1;

    public MyIntentService() {
        super("MyIntentService");
    }
    public static Intent FriendsIntent(Context con, String param1){
        return new Intent(con, MyIntentService.class)
                .setAction(ACTION_EVENTS)
                .putExtra(EXTRA_PARAM1, param1);
    }

    public static void setAlarm(Context context, String parama1, boolean isOn){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY,13);
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(ALARM_SERVICE);
        Intent in = MyIntentService.FriendsIntent(context,parama1);
        PendingIntent intent = PendingIntent.getService(context,0,in,0);
        if (isOn){
            alarmManager.setInexactRepeating(AlarmManager.RTC,calendar.getTimeInMillis(),AlarmManager.INTERVAL_HALF_DAY,intent);
        }else{
            alarmManager.cancel(intent);
            intent.cancel();
        }
    }
    public static boolean isAlarmOn(Context context, String id){
        Intent intent = MyIntentService.FriendsIntent(context, id);
        PendingIntent pi = PendingIntent.getService(context,0,intent,0);
        return pi != null;
    }
    public static void startActionFriends(Context context, String param1) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FRIENDS);
        intent.putExtra(EXTRA_PARAM1, param1);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FRIENDS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                handleActionFriends(param1);
            } else if (ACTION_EVENTS.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionEvents(param1, param2);
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFriends(String param1) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child(param1).child("Pending Reuqests").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                palNotify(user);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void palNotify(User user) {
        NotificationManager manager = (NotificationManager)getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Friend Requests", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        Intent intent = ProfileActivity.newInstance(MyIntentService.this,user.getId());
        PendingIntent pi = PendingIntent.getActivity(MyIntentService.this,0,intent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MyIntentService.this,CHANNEL_ID)
                .setContentTitle("You have a new Friend Request")
                .setAutoCancel(true)
                .setContentText(user.getName())
                .setContentIntent(pi)
                .addAction(R.drawable.ic_person_add_black_24dp,"Accept",pi)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(user.getBio()));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(Noti_id, builder.build());
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionEvents(String param1, String param2) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
