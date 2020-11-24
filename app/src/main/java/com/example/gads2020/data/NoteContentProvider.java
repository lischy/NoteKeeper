package com.example.gads2020.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.example.gads2020.data.sqlite.NoteKeeperOpenHelper;

import static com.example.gads2020.data.sqlite.myNoteContract.*;

public class NoteContentProvider extends ContentProvider {
    private NoteKeeperOpenHelper mDbOpenHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    public static final int COURSES_TABLE = 1;
    public static final int NOTES_TABLE = 2;
    public static final int COURSE_ROW_ID = 3;

    public static final int COURSE_ROW_ARGS = 4;

    public static final int NOTE_ROW_ID = 5;

    public static final int NOTE_ROW_ARGS = 6;

    public static final int NOTES_JOIN_COURSES = 7;

    static {
        uriMatcher.addURI(AUTHORITY, courseInfoEntry.PATH_NAME, COURSES_TABLE);
        uriMatcher.addURI(AUTHORITY, noteInfoEntry.PATH_NAME, NOTES_TABLE);
        uriMatcher.addURI(AUTHORITY,noteInfoEntry.PATH_NAME_COURSES, NOTES_JOIN_COURSES);
        uriMatcher.addURI(AUTHORITY,courseInfoEntry.PATH_NAME+"/#",COURSE_ROW_ID );
        uriMatcher.addURI(AUTHORITY,noteInfoEntry.PATH_NAME+"/#", NOTE_ROW_ID);
        uriMatcher.addURI(AUTHORITY,noteInfoEntry.PATH_NAME+"/*", NOTE_ROW_ARGS);
        uriMatcher.addURI(AUTHORITY,courseInfoEntry.PATH_NAME+"/*", COURSE_ROW_ARGS);


    }
    public NoteContentProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Implement this to handle requests to delete one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)){
            case NOTES_TABLE:
                long rowId = db.insert(noteInfoEntry.TABLE_NAME,null,values);
                if (rowId ==-1) return null;
                return ContentUris.withAppendedId(uri,rowId);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public boolean onCreate() {
        mDbOpenHelper = new NoteKeeperOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor = null;
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        switch (uriMatcher.match(uri)){
            case COURSES_TABLE:

                cursor = db.query(courseInfoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                         break;
            case NOTES_TABLE:
                cursor = db.query(noteInfoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                        break;
            case NOTES_JOIN_COURSES:
                String joinClause = noteInfoEntry.TABLE_NAME + " JOIN " +
                        courseInfoEntry.TABLE_NAME + " ON " +
                        noteInfoEntry.TABLE_NAME +"."+ noteInfoEntry.COLUMN_COURSE_ID +" = " +
                        courseInfoEntry.TABLE_NAME +"."+ courseInfoEntry.COLUMN_COURSE_ID;
                cursor = db.query(
                        joinClause,
                        projection,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                        break;
            case NOTE_ROW_ID:
                long noteRowID = ContentUris.parseId(uri);
                String rowSelection = noteInfoEntry._ID + " LIKE ?";
                String[] rowSelectionArgs = { Long.toString(noteRowID) };
                cursor = db.query(noteInfoEntry.TABLE_NAME,projection,rowSelection,rowSelectionArgs,
                        null,null,null);
                break;
                default:
                throw new UnsupportedOperationException("Not yet implemented");

        }

        return cursor;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
