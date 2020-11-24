package com.example.gads2020;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gads2020.data.sqlite.myNoteContract.courseInfoEntry;
import com.example.gads2020.data.sqlite.myNoteContract.noteInfoEntry;

public class NoteRecyclerAdapter extends RecyclerView.Adapter<NoteRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater layoutInflater;
    private Cursor noteCursor;
    private int mNoteTitileColumnPosition;
    private int courseTitleColumnPosition;
    private int noteIdColumnPosition;

    public NoteRecyclerAdapter(Context mContext, Cursor cursor) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        noteCursor = cursor;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_note_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(!noteCursor.moveToPosition(position)) return;

        String noteTitle = noteCursor.getString(mNoteTitileColumnPosition);
        String courseId = noteCursor.getString(courseTitleColumnPosition);
        int noteId = noteCursor.getInt(noteIdColumnPosition);

//        NoteInfo note = notes.get(position);

//        Log.d("Size", String.valueOf(notes.size()));
        if(noteCursor != null) {
            holder.textCourse.setText(courseId);
            holder.textTitle.setText(noteTitle);
            holder.mId = noteId;
        }
    }

    @Override
    public int getItemCount() {
        return noteCursor == null? 0: noteCursor.getCount();
    }

    public void populateColumnPosition(){
        if (noteCursor == null) return;
        mNoteTitileColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry.COLUMN_NOTE_TITLE);
        courseTitleColumnPosition = noteCursor.getColumnIndexOrThrow(courseInfoEntry.COLUMN_COURSE_TITLE);
        noteIdColumnPosition = noteCursor.getColumnIndexOrThrow(noteInfoEntry._ID);
    }

    public void changeCursor(Cursor cursor)
    {
//        if(noteCursor != null) noteCursor.close();
        noteCursor = cursor;
       populateColumnPosition();
        notifyDataSetChanged();
    }
        public class ViewHolder extends RecyclerView.ViewHolder{
        public final TextView textCourse;
        public final TextView textTitle;
        public int mId;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);
            textTitle = itemView.findViewById(R.id.text_title);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, NoteActivity.class);
                    intent.putExtra(NoteActivity.NOTE_ID, mId);
                    mContext.startActivity(intent);
                }
            });

        }
    }
}
