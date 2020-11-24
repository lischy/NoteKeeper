package com.example.gads2020.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.net.Uri;
import android.os.AsyncTask;

public class NoteUploaderJobService extends JobService {
    public static final String EXTRA_DATA_URI= "com.example.gads2020.service.DATA_URI";
    private NoteUploader noteUploader;

    public NoteUploaderJobService(){};
    @Override
    public boolean onStartJob(JobParameters params) {
        AsyncTask<JobParameters , Void ,Void > task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... jobParameters) {
                JobParameters parameters = jobParameters[0];
                String stringDataUri = parameters.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                noteUploader.doUpload(dataUri);
                if( !noteUploader.isCanceled())
                jobFinished(parameters,false);
                return null;
            }
        };
        noteUploader = new NoteUploader(this);
        task.execute(params);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        noteUploader.cancel();
        return true;
    }
}
