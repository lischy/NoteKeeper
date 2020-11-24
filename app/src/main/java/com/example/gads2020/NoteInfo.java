package com.example.gads2020;

import android.os.Parcel;
import android.os.Parcelable;

public class NoteInfo implements Parcelable {
    private CourseInfo mCourse;
    private String mTitle;
    private String mText;
    private int noteId;

    public NoteInfo(int id, CourseInfo course, String title, String text) {
        noteId = id;
        mCourse = course;
        mTitle = title;
        mText = text;

    }


    protected NoteInfo(Parcel in) {
        mCourse = in.readParcelable(CourseInfo.class.getClassLoader());
        mTitle = in.readString();
        mText = in.readString();
        noteId = in.readInt();
    }

    public static final Creator<NoteInfo> CREATOR = new Creator<NoteInfo>() {
        @Override
        public NoteInfo createFromParcel(Parcel in) {
            return new NoteInfo(in);
        }

        @Override
        public NoteInfo[] newArray(int size) {
            return new NoteInfo[size];
        }
    };

    public int getNoteId(){
        return noteId;
    }
    public CourseInfo getCourse() {
        return mCourse;
    }

    public void setCourse(CourseInfo course) {
        mCourse = course;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        mText = text;
    }

    private String getCompareKey() {
        return mCourse.getmCourseId() + "|" + mTitle + "|" + mText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NoteInfo that = (NoteInfo) o;

        return getCompareKey().equals(that.getCompareKey());
    }

    @Override
    public int hashCode() {
        return getCompareKey().hashCode();
    }

    @Override
    public String toString() {
        return getCompareKey();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(mCourse, flags);
        dest.writeString(mTitle);
        dest.writeString(mText);
        dest.writeInt(noteId);
    }


}
