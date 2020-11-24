package com.example.gads2020;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.example.gads2020.data.sqlite.NoteKeeperOpenHelper;
import com.example.gads2020.data.sqlite.myNoteContract;
import com.example.gads2020.data.sqlite.myNoteContract.courseInfoEntry;
import com.example.gads2020.data.sqlite.myNoteContract.noteInfoEntry;
import com.example.gads2020.service.NoteBackup;
import com.example.gads2020.service.NoteBackupService;
import com.example.gads2020.service.NoteUploaderJobService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.List;

public class NoteListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int LOADER_NOTES_ID = 12;
    public static final int NOTE_UPLOADER_JOB_ID = 1;
    private NoteRecyclerAdapter noteRecyclerAdapter;

//    private ArrayAdapter<NoteInfo> notesAdapter;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private NavigationView nv;
    private RecyclerView recyclerItems;
    private LinearLayoutManager notesLinearLayoutManager;
    private CourseRecyclerAdapter courseRecyclerAdapter;
    private GridLayoutManager coursesLayoutManager;
    private NoteKeeperOpenHelper mDbOpenHelper;


    @Override
    protected void onDestroy() {
        mDbOpenHelper.close();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDbOpenHelper = new NoteKeeperOpenHelper(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NoteListActivity.this, NoteActivity.class));
            }
        });
        intialiseDisplayContent();
        drawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nv = (NavigationView) findViewById(R.id.nav_view);
        nv.setNavigationItemSelectedListener(new navItemClick());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_list_note,menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        notesAdapter.notifyDataSetChanged();
//        noteRecyclerAdapter.notifyDataSetChanged();
        getLoaderManager().restartLoader(LOADER_NOTES_ID,null,this);
//        loadNotesFromDb();

    }

    private void intialiseDisplayContent() {
//        final ListView noteList = findViewById(R.id.list_notes);
//        List<NoteInfo> notes = DataManager.getInstance().getNotes();
//        notesAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,notes);
//        noteList.setAdapter(notesAdapter);
//
//        noteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(NoteListActivity.this,NoteActivity.class);
////                NoteInfo note = (NoteInfo) noteList.getItemAtPosition(position);
//                intent.putExtra(NoteActivity.NOTE_POSITION,position);
//                startActivity(intent);
//            }
//        });
        DataManager.loadDataFromDatabase(mDbOpenHelper);

        recyclerItems = (RecyclerView) findViewById(R.id.list_notes);
        notesLinearLayoutManager = new LinearLayoutManager(this);

//        List<NoteInfo> notes = DataManager.getInstance().getNotes();
        noteRecyclerAdapter = new NoteRecyclerAdapter(this, null);

        List<CourseInfo> courses = DataManager.getInstance().getCourses();
        courseRecyclerAdapter = new CourseRecyclerAdapter(this,courses);
        coursesLayoutManager = new GridLayoutManager(this,2);


        displayNotes();

    }

    private void loadNotesFromDb(){
        SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();

        String[] noteColumnProjection ={
                noteInfoEntry.COLUMN_NOTE_TITLE,
                noteInfoEntry.COLUMN_COURSE_ID,
                noteInfoEntry._ID
        };

        String notesSortOrder = noteInfoEntry.COLUMN_COURSE_ID + " ," + noteInfoEntry.COLUMN_NOTE_TITLE;
        final Cursor noteCursor = db.query(
                noteInfoEntry.TABLE_NAME,
                noteColumnProjection,
                null,
                null,
                null,
                null,
                notesSortOrder);
        noteRecyclerAdapter.changeCursor(noteCursor);
    }
    private void displayNotes() {

        recyclerItems.setLayoutManager(notesLinearLayoutManager);
        recyclerItems.setAdapter(noteRecyclerAdapter);
        setSelectedNavMenuItem(R.id.nav_notes);
    }

    private void setSelectedNavMenuItem(int id) {
        NavigationView nav_view = (NavigationView)findViewById(R.id.nav_view) ;
        Menu menu =nav_view.getMenu();
        menu.findItem(id).setChecked(true);
    }

    private void displayCourses() {
        recyclerItems.setLayoutManager(coursesLayoutManager);
        recyclerItems.setAdapter(courseRecyclerAdapter);
        setSelectedNavMenuItem(R.id.nav_courses);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) return true;
        else if (item.getItemId()==R.id.action_settings){
            startActivity(new Intent(NoteListActivity.this,SettingsActivity.class));
        }else if(item.getItemId() == R.id.action_backup){
            backupNotes();
        }else if(item.getItemId() == R.id.action_upload_notes){
            scheduleUploadNotes();
        }
        return super.onOptionsItemSelected(item);
    }

    private void scheduleUploadNotes() {
        PersistableBundle extras = new PersistableBundle();
        extras.putString(NoteUploaderJobService.EXTRA_DATA_URI, noteInfoEntry.CONTENT_URI.toString());


        ComponentName componentName = new ComponentName(this, NoteUploaderJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(NOTE_UPLOADER_JOB_ID,componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setExtras(extras)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void backupNotes() {
        Intent intent = new Intent(this, NoteBackupService.class);
        intent.putExtra("backupCourseId",NoteBackup.ALL_COURSES);
        startService(intent);
//        NoteBackup.doBackup(NoteListActivity.this,NoteBackup.ALL_COURSES);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();

        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader loader = null;
        if(id == LOADER_NOTES_ID){
            Uri uri = noteInfoEntry.CONTENT_JOINED_URI;
            String[] noteColumnProjection = {
                    noteInfoEntry.COLUMN_NOTE_TITLE,
                    noteInfoEntry.TABLE_NAME +"."+ noteInfoEntry._ID,
                    courseInfoEntry.COLUMN_COURSE_TITLE
            };
            String notesSortOrder = courseInfoEntry.COLUMN_COURSE_TITLE + " ," + noteInfoEntry.COLUMN_NOTE_TITLE;
            loader = new CursorLoader(this,uri,noteColumnProjection,null,null, notesSortOrder);
 //            loader = new CursorLoader(this){
//                @Override
//                public Cursor loadInBackground() {
//                    SQLiteDatabase db = mDbOpenHelper.getReadableDatabase();
//
//                    String[] noteColumnProjection ={
//                            noteInfoEntry.COLUMN_NOTE_TITLE,
//                            noteInfoEntry.TABLE_NAME +"."+ noteInfoEntry._ID,
//                            courseInfoEntry.COLUMN_COURSE_TITLE
//                    };
//
//                    String notesSortOrder = courseInfoEntry.COLUMN_COURSE_TITLE + " ," + noteInfoEntry.COLUMN_NOTE_TITLE;
//                    String joinClause = noteInfoEntry.TABLE_NAME + " JOIN " +
//                            courseInfoEntry.TABLE_NAME + " ON " +
//                            noteInfoEntry.TABLE_NAME +"."+ noteInfoEntry.COLUMN_COURSE_ID +" = " +
//                            courseInfoEntry.TABLE_NAME +"."+ courseInfoEntry.COLUMN_COURSE_ID;
//
//
//
//
//                    final Cursor noteCursor = db.query(
//                            joinClause,
//                            noteColumnProjection,
//                            null,
//                            null,
//                            null,
//                            null,
//                            notesSortOrder);
//                    return noteCursor;
//                }
//            };
        }
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(loader.getId() == LOADER_NOTES_ID)
        noteRecyclerAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if(loader.getId() == LOADER_NOTES_ID)
        noteRecyclerAdapter.changeCursor(null);

    }

    private class navItemClick implements NavigationView.OnNavigationItemSelectedListener {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            int id = menuItem.getItemId();
            switch (id) {
                case R.id.nav_notes:
                    displayNotes();
                    Toast.makeText(NoteListActivity.this, "My Account", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_courses:
                    displayCourses();
                    Toast.makeText(NoteListActivity.this, "Settings", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.nav_share:
                    Toast.makeText(NoteListActivity.this, "My Cart", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    return true;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    }


}
