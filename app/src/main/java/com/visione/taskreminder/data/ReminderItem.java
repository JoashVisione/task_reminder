package com.visione.taskreminder.data;

public class ReminderItem {
    public String mTitle;
    public String mDescription;
    public String mDate;
    public String mTime;
    public String mRepeat;
    public String mRepeatNo;
    public String mRepeatType;
    public String mActive;
    public String mDone;

    public ReminderItem(String Title, String Description, String Date, String Time, String Repeat, String RepeatNo, String RepeatType, String Active) {
        this.mTitle = Title;
        this.mDescription = Description;
        this.mDate = Date;
        this.mTime = Time;
        this.mRepeat = Repeat;
        this.mRepeatNo = RepeatNo;
        this.mRepeatType = RepeatType;
        this.mActive = Active;
    }
}