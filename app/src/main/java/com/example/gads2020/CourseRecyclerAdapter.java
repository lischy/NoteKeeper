package com.example.gads2020;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class CourseRecyclerAdapter extends RecyclerView.Adapter<CourseRecyclerAdapter.ViewHolder> {

    private final Context mContext;
    private final LayoutInflater layoutInflater;
    private final List<CourseInfo> mCourses;
    public CourseRecyclerAdapter(Context mContext, List<CourseInfo> note) {
        this.mContext = mContext;
        layoutInflater = LayoutInflater.from(mContext);
        mCourses = note;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.item_course_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseInfo course = mCourses.get(position);
        holder.currentPosition = position;
        holder.textCourse.setText(course.getmTitle());

    }

    @Override
    public int getItemCount() {
        return mCourses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public final TextView textCourse;
        public int currentPosition;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textCourse = itemView.findViewById(R.id.text_course);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(mContext, NoteActivity.class);
//                    intent.putExtra(NoteActivity.NOTE_POSITION,currentPosition);
//                    mContext.startActivity(intent);
                    Snackbar.make(v,mCourses.get(currentPosition).getmTitle(),Snackbar.LENGTH_SHORT).show();
                }
            });

        }
    }
}
