package com.example.gads2020;

import android.content.Context;

import androidx.core.app.NotificationCompat;

public class NoteReminderNotification {
    Context mContext ;
    public NoteReminderNotification(Context context){
        mContext = context;
    }

    private static final String CHANNEL_ID = "my_id";
    NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext,CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assignment_black_24dp)
            .setContentTitle("textTitle")
            .setContentText("textContent")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


}
