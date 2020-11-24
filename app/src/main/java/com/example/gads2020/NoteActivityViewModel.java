package com.example.gads2020;

import android.os.Bundle;

import androidx.lifecycle.ViewModel;

public class NoteActivityViewModel extends ViewModel {

    public static final String ORIGINAL_NOTE_COURSE_ID ="com.example.gads2020.ORIGINAL_NOTE_COURSE_ID";
    public static final String ORIGINAL_NOTE_TITLE = "com.example.gads2020.ORIGINAL_NOTE_TITLE";
    public static final String ORIGINAL_NOTE_TEXT = "com.example.gads2020.ORIGINAL_NOTE_TEXT";

    public String originalCourseId;
    public String originalNoteTitle;
    public String originalNoteText;
    public boolean isNewlyCreated = true;

    public void saveState(Bundle outState) {

        outState.putString(ORIGINAL_NOTE_COURSE_ID,originalCourseId);
        outState.putString(ORIGINAL_NOTE_TITLE,originalNoteTitle);
        outState.putString(ORIGINAL_NOTE_TEXT,originalNoteText);
    }

    public void restoreState (Bundle inState){
        originalCourseId = inState.getString(ORIGINAL_NOTE_COURSE_ID);
        originalNoteTitle = inState.getString(ORIGINAL_NOTE_TITLE);
        originalNoteText = inState.getString(ORIGINAL_NOTE_TEXT);
    }
}
