package com.visione.taskreminder.data;

public class Reminder {
    private int mID;
    private String mTitle;

    private String mDescription;
    private String mDate;
    private String mTime;
    private String mRepeat;
    private String mRepeatNo;
    private String mRepeatType;
    private String mActive;
    private String mDone;


    Reminder(int ID, String Title, String description, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String Done){
        mID = ID;
        mTitle = Title;
        mDate = Date;
        mDescription = description;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mActive = Active;
        mDone = Done;
    }

    public Reminder(String Title, String description, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active, String Done){
        mTitle = Title;
        mDescription = description;
        mDate = Date;
        mTime = Time;
        mRepeat = Repeat;
        mRepeatNo = RepeatNo;
        mRepeatType = RepeatType;
        mActive = Active;
        mDone = Done;
    }

    Reminder(){}

    public int getID() {
        return mID;
    }

    void setID(int ID) {
        mID = ID;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mDescription) {
        this.mDescription = mDescription;
    }

    public String getDate() {
        return mDate;
    }

    public void setDate(String date) {
        mDate = date;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(String repeatType) {
        mRepeatType = repeatType;
    }

    public String getRepeatNo() {
        return mRepeatNo;
    }

    public void setRepeatNo(String repeatNo) {
        mRepeatNo = repeatNo;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String repeat) {
        mRepeat = repeat;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String active) {
        mActive = active;
    }

    public String getDone() {
        return mDone;
    }

    public void setDone(String mDone) {
        this.mDone = mDone;
    }
}
