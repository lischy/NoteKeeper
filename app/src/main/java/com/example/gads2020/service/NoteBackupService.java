package com.example.gads2020.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;


public class NoteBackupService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    private static final String TAG = NoteBackupService.class.getSimpleName();
    public NoteBackupService() {
        super("MyWorkerThread");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String backupCourseId = intent.getStringExtra("backupCourseId");
        NoteBackup.doBackup(this,NoteBackup.ALL_COURSES);
    }
}
