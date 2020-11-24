package com.example.gads2020.data.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.gads2020.data.sqlite.myNoteContract.courseInfoEntry;
import com.example.gads2020.data.sqlite.myNoteContract.noteInfoEntry;

public class NoteKeeperOpenHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "NoteKeeper.db";
    public NoteKeeperOpenHelper(@Nullable Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("CreatingDb","OnCreate");
        db.execSQL(noteInfoEntry.SQL_CREATE_ENTRIES);
        db.execSQL(courseInfoEntry.SQL_CREATE_ENTRIES);
        db.execSQL(courseInfoEntry.SQL_CREATE_INDEX1);
        db.execSQL(noteInfoEntry.SQL_CREATE_INDEX1);

        DatabaseDataWorker myWorker = new DatabaseDataWorker(db);
        myWorker.insertCourses();
        myWorker.insertSampleNotes();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 2){
            db.execSQL(courseInfoEntry.SQL_CREATE_INDEX1);
            db.execSQL(noteInfoEntry.SQL_CREATE_INDEX1);
        }

//        db.execSQL(noteInfoEntry.SQL_DELETE_ENTRIES);
//        db.execSQL(courseInfoEntry.SQL_DELETE_ENTRIES);
//        onCreate(db);
    }
}
