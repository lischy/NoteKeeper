package com.example.gads2020;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.example.gads2020.data.sqlite.NoteKeeperOpenHelper;
import com.example.gads2020.data.sqlite.myNoteContract.courseInfoEntry;
import com.example.gads2020.data.sqlite.myNoteContract.noteInfoEntry;

public class NoteActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES_ID = 10;
    public static final int LOADER_COURSE_ID = 11;
    private String TAG = getClass().getSimpleName();
    public static final String NOTE_ID = "com.example.gads2020NOTE_POSITION";
    public static final int ID_NOT_SET = -1;
    private NoteInfo noteInfoNote;
    private boolean isNewNote;
    private Spinner courseSpinner;
    private EditText noteTitle;
    private EditText noteText;
    private int mNoteId;
    private boolean isCancelling;

    private NoteActivityViewModel viewModel;
    private NoteKeeperOpenHelper mDbOpenHelper;
    private Cursor noteCursor;
    private int courseIdColumnPosition;
    private int noteTitleColumnPosition;
    private int noteTextColumnPosition;
    private SimpleCursorAdapter courseAdapter;
    private boolean mCourseQueryFinished;
    private boolean mNotesQueryFinished;
    private ModuleStatusView moduleStatusView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        Toolbar myToolBar = findViewById(R.id.myToolBar);
        setSupportActionBar(myToolBar);
        getSupportActionBar().setTitle("Note Keeper");


        noteTitle = findViewById(R.id.text_note_title);
        noteText = findViewById(R.id.text_note_text);

        ViewModelProvider viewModelProvider = new ViewModelProvider(getViewModelStore(), ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()));
        viewModel = viewModelProvider.get(NoteActivityViewModel.class);
        viewModel.isNewlyCreated = false;
        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        if (viewModel.isNewlyCreated && savedInstanceState != null) {
            viewModel.restoreState(savedInstanceState);
        }

        courseSpinner = findViewById(R.id.spinner_courses);
//        List<CourseInfo> course = DataManager.getInstance().getCourses();
        courseAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, null,
                new String[]{courseInfoEntry.COLUMN_COURSE_TITLE},
                new int[]{android.R.id.text1},0);
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        courseSpinner.setAdapter(courseAdapter);

        getLoaderManager().initLoader(LOADER_COURSE_ID,null,this);
        getValuesFromIntent();

        if (!isNewNote) getLoaderManager().initLoader(LOADER_NOTES_ID,null,this);
//        saveOriginalNoteValue();
        moduleStatusView = findViewById(R.id.module_status);
        loadModuleStatusValues();
        Log.d(TAG, "onCreate Method");

        
    }

    private void loadModuleStatusValues() {
        int totalNumberOfModules =11;
        int completedNoOfModules = 7;
        boolean[] moduleStatus = new boolean[totalNumberOfModules];
        for (int moduleIndex = 0 ; moduleIndex < completedNoOfModules ; moduleIndex ++){
            moduleStatus[moduleIndex] = true;
        }
    }


    private void loadCourseData() {
                SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
                String[] courseColumns = {
                        courseInfoEntry.COLUMN_COURSE_TITLE,
                        courseInfoEntry.COLUMN_COURSE_ID,
                        courseInfoEntry._ID
                };

                Cursor courseCursor = db.query(
                        courseInfoEntry.TABLE_NAME,
                        courseColumns,
                        null,
                        null,
                        null,
                        null,
                        courseInfoEntry.COLUMN_COURSE_TITLE
                );
        courseAdapter.changeCursor(courseCursor);
    }


    private void loadNoteData() {
        SQLiteDatabase readableDatabase = mDbOpenHelper.getReadableDatabase();

//        String courseId = "android_intents";
//        String titleStarts = "dynamic";
        String notesSelection = noteInfoEntry._ID + " =?";
        String[] notesSelectionArgs = {Integer.toString(mNoteId)};

        String[] notesProjection = {
                noteInfoEntry.COLUMN_COURSE_ID,
                noteInfoEntry.COLUMN_NOTE_TITLE,
                noteInfoEntry.COLUMN_NOTE_TEXT
        };

        noteCursor = readableDatabase.query(
                noteInfoEntry.TABLE_NAME,
                notesProjection,
                notesSelection,
                notesSelectionArgs,
                null,
                null,
                null
        );

        courseIdColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry.COLUMN_COURSE_ID);
        noteTitleColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry.COLUMN_NOTE_TEXT);

        noteCursor.moveToNext();

        displayNotes();
    }

    private void saveOriginalNoteValue() {
        if (isNewNote) {
            return;
        } else {
            viewModel.originalCourseId = noteInfoNote.getCourse().getmCourseId();
            viewModel.originalNoteTitle = noteInfoNote.getTitle();
            viewModel.originalNoteText = noteInfoNote.getText();
        }
    }

    private void displayNotes() {

        String courseId = noteCursor.getString(courseIdColumnPosition);
        String noteTitleDb = noteCursor.getString(noteTitleColumnPosition);
        String noteTextDb = noteCursor.getString(noteTextColumnPosition);

        CourseInfo course = DataManager.getInstance().getCourse(courseId);

//        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        int courseIndex = getIndexOfCourseId(courseId);
        courseSpinner.setSelection(courseIndex);
        noteTitle.setText(noteTitleDb);
        noteText.setText(noteTextDb);

        noteInfoNote = new NoteInfo(mNoteId,course,noteTitleDb,noteTextDb);
    }

    private int getIndexOfCourseId(String courseId) {
        Cursor cursor = courseAdapter.getCursor();
        int courseIdColumnPosition = cursor.getColumnIndexOrThrow(courseInfoEntry.COLUMN_COURSE_ID);
        int courseRowIndex = 0 ;
        boolean more = cursor.moveToFirst();

        while (more){
            String cursorCourseId = cursor.getString(courseIdColumnPosition);
            if (cursorCourseId.equals(courseId))
                break;

                courseRowIndex++;
                more = cursor.moveToNext();

        }
        return  courseRowIndex;

    }

    private void getValuesFromIntent() {
        Intent intent = getIntent();
        mNoteId = intent.getIntExtra(NOTE_ID, ID_NOT_SET);

        isNewNote = mNoteId == ID_NOT_SET;
        if (isNewNote) {
            createNewNote();
        }
        Log.i(TAG, "notePosition: " + mNoteId);
//        note = DataManager.getInstance().getNotes().get(mNoteId);
    }

    private void createNewNote() {
        Log.d(TAG,"Create new note");
        AsyncTask<ContentValues , Integer , Uri> task = new AsyncTask<ContentValues , Integer , Uri>(){
            private ProgressBar mProgressBar;
            @Override
            protected void onPreExecute() {
                mProgressBar = (ProgressBar)findViewById(R.id.progress_bar);
                mProgressBar.setVisibility(View.VISIBLE);
                mProgressBar.setProgress(1);
            }


            @Override
            protected Uri doInBackground(ContentValues... contentValues) {
                ContentValues values = contentValues[0];
                Uri uri = noteInfoEntry.CONTENT_URI;

                Uri result = getContentResolver().insert(uri,values);
                Log.d(TAG, String.valueOf(result));
                Log.d(TAG,"Call to execute - doInBackground: " + Thread.currentThread().getId());
                publishProgress(2);


                return result;
            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                int progressValue = values[0];
                mProgressBar.setProgress(progressValue);
            }


            @Override
            protected void onPostExecute(Uri uri) {
                mNoteId = (int) ContentUris.parseId(uri);
                Log.d(TAG, String.valueOf(mNoteId));
                mProgressBar.setProgress(3);
//                mProgressBar.setVisibility(View.GONE);
            }

        };

        ContentValues values = new ContentValues();
        values.put(noteInfoEntry.COLUMN_COURSE_ID, "");
        values.put(noteInfoEntry.COLUMN_NOTE_TITLE, "");
        values.put(noteInfoEntry.COLUMN_NOTE_TEXT, "");

            task.execute(values);
//        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
//        mNoteId= (int) db.insert(noteInfoEntry.TABLE_NAME,null,values);

//        DataManager dm = DataManager.getInstance();
//        mNoteId = dm.createNewNote(0);
//        note = dm.getNotes().get(notePosition);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.send_as_email:
                sendEmail();
                return true;
            case R.id.action_cancel:
                isCancelling = true;
                finish();
            case R.id.set_reminder:
                showReminderNotification();
//            case R.id.action_next:
//                moveNext();
//                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void showReminderNotification() {
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem item = menu.findItem(R.id.action_next);
        int lastNoteIndex = DataManager.getInstance().getNotes().size() - 1;
        item.setEnabled(mNoteId < lastNoteIndex);
        return super.onPrepareOptionsMenu(menu);
    }

    private void moveNext() {
        saveNote();
        ++mNoteId;
        noteInfoNote = DataManager.getInstance().getNotes().get(mNoteId);
        saveOriginalNoteValue();
        displayNotes();
        invalidateOptionsMenu();//Call onPrepareOptionsMenu to disable the menu item if we are at the end of the noteList.
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isCancelling) {
            Log.i(TAG, "cancelling note at position: " + mNoteId);
            if (isNewNote) {
                deleteNoteFromDatabase();
//                DataManager.getInstance().removeNote(mNoteId);

            } else {
                storePreviousNoteValues();
            }

        } else {
            saveNote();
        }
        Log.d(TAG, "onPause");

    }

    private void deleteNoteFromDatabase() {
        final String selection = noteInfoEntry._ID + " = ? ";
        final String[] selectionArgs = { Integer.toString(mNoteId)};

        AsyncTask task = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
                db.delete(noteInfoEntry.TABLE_NAME,selection,selectionArgs);
                return null;
            }
        };
        task.execute();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null)
            viewModel.saveState(outState);
    }

    private void storePreviousNoteValues() {
        CourseInfo course = DataManager.getInstance().getCourse(viewModel.originalCourseId);
        noteInfoNote.setCourse(course);
        noteInfoNote.setText(viewModel.originalNoteText);
        noteInfoNote.setTitle(viewModel.originalNoteTitle);
    }

    private void saveNote() {
        String courseId = selectedCourseId();
        String noteTitleValue = noteTitle.getText().toString();
        String noteTextValue =noteText.getText().toString();

        saveNoteToDb(courseId,noteTitleValue,noteTextValue);
    }

    private String selectedCourseId() {
        int selectedPosition = courseSpinner.getSelectedItemPosition();
        Cursor cursor = courseAdapter.getCursor();
        cursor.moveToPosition(selectedPosition);
        int courseIdColumnPosition = cursor.getColumnIndexOrThrow(courseInfoEntry.COLUMN_COURSE_ID);
        String courseId = cursor.getString(courseIdColumnPosition);
        return courseId;
    }

    private void saveNoteToDb(String courseId , String noteTitle , String noteText){

        // Which row to update, based on the title
        String selection = noteInfoEntry._ID + " LIKE ?";
        String[] selectionArgs = { Integer.toString(mNoteId) };

        ContentValues values = new ContentValues();
        values.put(noteInfoEntry.COLUMN_COURSE_ID,courseId);
        values.put(noteInfoEntry.COLUMN_NOTE_TITLE, noteTitle);
        values.put(noteInfoEntry.COLUMN_NOTE_TEXT, noteText);

        SQLiteDatabase db = mDbOpenHelper.getWritableDatabase();
        db.update(noteInfoEntry.TABLE_NAME,values,selection,selectionArgs);

    }

    private void sendEmail() {

        CourseInfo course = (CourseInfo) courseSpinner.getSelectedItem();
        String subject = noteTitle.getText().toString();
        String text = course.getmTitle() + "\n" + noteText.getText();

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc2822");
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, text);
        startActivity(intent);
    }
    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if (id == LOADER_NOTES_ID)
            loader = createLoaderNotes();
        else if(id == LOADER_COURSE_ID)
                loader = createLoaderCourses();
        return loader;
    }

    private CursorLoader createLoaderCourses() {
        mCourseQueryFinished = false;
        Uri uri = courseInfoEntry.CONTENT_URI;

        String[] courseColumns = {
                courseInfoEntry.COLUMN_COURSE_TITLE,
                courseInfoEntry.COLUMN_COURSE_ID,
                courseInfoEntry._ID
        };
        return  new CursorLoader(this,uri,courseColumns,null,null,courseInfoEntry.COLUMN_COURSE_TITLE);
//       return new CursorLoader(this){
//           @Override
//           public Cursor loadInBackground() {
//               SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//               String[] courseColumns = {
//                       courseInfoEntry.COLUMN_COURSE_TITLE,
//                       courseInfoEntry.COLUMN_COURSE_ID,
//                       courseInfoEntry._ID
//               };
//
//               Cursor courseCursor = db.query(
//                       courseInfoEntry.TABLE_NAME,
//                       courseColumns,
//                       null,
//                       null,
//                       null,
//                       null,
//                       courseInfoEntry.COLUMN_COURSE_TITLE
//               );
//               return courseCursor;
//           }
//       };
    }

    private CursorLoader createLoaderNotes() {
        mNotesQueryFinished = false;
//        String notesSelection = noteInfoEntry._ID + " =?";
//        String[] notesSelectionArgs = {Integer.toString(mNoteId)};
        Uri uri = ContentUris.withAppendedId(noteInfoEntry.CONTENT_URI,mNoteId);
        String[] notesProjection = {
                noteInfoEntry.COLUMN_COURSE_ID,
                noteInfoEntry.COLUMN_NOTE_TITLE,
                noteInfoEntry.COLUMN_NOTE_TEXT
        };
        return new CursorLoader(this,uri,notesProjection,null,null,null);
//        return new CursorLoader(this){
//            @Override
//            public Cursor loadInBackground() {
//                SQLiteDatabase readableDatabase = mDbOpenHelper.getReadableDatabase();
//
////        String courseId = "android_intents";
////        String titleStarts = "dynamic";
//                String notesSelection = noteInfoEntry._ID + " =?";
//                String[] notesSelectionArgs = {Integer.toString(mNoteId)};
//
//                String[] notesProjection = {
//                        noteInfoEntry.COLUMN_COURSE_ID,
//                        noteInfoEntry.COLUMN_NOTE_TITLE,
//                        noteInfoEntry.COLUMN_NOTE_TEXT
//                };
//
//                return readableDatabase.query(
//                        noteInfoEntry.TABLE_NAME,
//                        notesProjection,
//                        notesSelection,
//                        notesSelectionArgs,
//                        null,
//                        null,
//                        null
//                );
//            }
//        };
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        if(loader.getId() == LOADER_NOTES_ID){
            loadFinishedNotes(data);
        } else if(loader.getId() == LOADER_COURSE_ID){
            loadFinishedCourses(data);

        }

    }

    private void loadFinishedCourses(Cursor data) {
        courseAdapter.changeCursor(data);
        mCourseQueryFinished = true;
        displayNotesWhenQueriesFinish();
    }

    private void loadFinishedNotes(Cursor data) {
        noteCursor = data;
        courseIdColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry.COLUMN_COURSE_ID);
        noteTitleColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry.COLUMN_NOTE_TITLE);
        noteTextColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry.COLUMN_NOTE_TEXT);

        noteCursor.moveToNext();
        mNotesQueryFinished = true;
        displayNotesWhenQueriesFinish();
    }

    private void displayNotesWhenQueriesFinish() {
        if (mNotesQueryFinished && mCourseQueryFinished)
            displayNotes();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES_ID){
            if(noteCursor != null)  noteCursor.close();
        }else if(loader.getId() == LOADER_COURSE_ID){
            courseAdapter.changeCursor(null);
        }
    }
}
