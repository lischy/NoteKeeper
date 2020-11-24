package com.example.gads2020.data.sqlite;

import android.net.Uri;
import android.provider.BaseColumns;

public final class myNoteContract {

    public static final String AUTHORITY = "com.example.gads2020.data.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    private myNoteContract(){};

    public static class courseInfoEntry implements BaseColumns {
        public static final String PATH_NAME = "courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_NAME);

        public static final String TABLE_NAME = "course_info";
        public static final String COLUMN_COURSE_ID = "course_id";
        public static final String COLUMN_COURSE_TITLE = "course_title";

        public static final String INDEX1 = TABLE_NAME +"_index1";
        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1+ " ON " + TABLE_NAME +
                        "(" + COLUMN_COURSE_TITLE + ")";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + courseInfoEntry.TABLE_NAME + " (" +
                        _ID + " INTEGER PRIMARY KEY, " +
                        courseInfoEntry.COLUMN_COURSE_ID+ " TEXT UNIQUE NOT NULL, "+
                        courseInfoEntry.COLUMN_COURSE_TITLE + " TEXT NOT NULL)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + courseInfoEntry.TABLE_NAME;

    }

    public static class noteInfoEntry implements BaseColumns{
        public static final String PATH_NAME = "notes";
        public static final String PATH_NAME_COURSES= "notes_join_courses";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_NAME);
        public static final Uri CONTENT_JOINED_URI = Uri.withAppendedPath(BASE_CONTENT_URI,PATH_NAME_COURSES);
        public  static final String TABLE_NAME = "note_info";
        public static final String COLUMN_NOTE_TITLE = "note_title";
        public static final String COLUMN_NOTE_TEXT = "note_text";
        public static final String COLUMN_COURSE_ID = "course_id";

        public static final String INDEX1 = TABLE_NAME +"_index1";

        public static final String SQL_CREATE_INDEX1 =
                "CREATE INDEX " + INDEX1+ " ON " + TABLE_NAME +
                        "(" + COLUMN_NOTE_TITLE + ")";

        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + noteInfoEntry.TABLE_NAME+ " ("+
                        _ID + " INTEGER PRIMARY KEY ,"+
                        noteInfoEntry.COLUMN_NOTE_TITLE + " TEXT NOT NULL, "+
                        noteInfoEntry.COLUMN_NOTE_TEXT +" TEXT, "+
                        noteInfoEntry.COLUMN_COURSE_ID+ " TEXT  NOT NULL)";
        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + noteInfoEntry.TABLE_NAME;
    }
}
