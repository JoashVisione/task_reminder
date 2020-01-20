package com.visione.taskreminder.data;

public class DoneReminder {
    public String mTitle;
    public String mDescription;
    public String mDate;
    public String mTime;
    public String mRepeat;
    public String mRepeatNo;
    public String mRepeatType;
    public String mActive;
    public String mDone;

    public DoneReminder(String mTitle, String mDescription, String mDate, String mTime, String mRepeat, String mRepeatNo, String mRepeatType, String mActive, String mDone) {
        this.mTitle = mTitle;
        this.mDescription = mDescription;
        this.mDate = mDate;
        this.mTime = mTime;
        this.mRepeat = mRepeat;
        this.mRepeatNo = mRepeatNo;
        this.mRepeatType = mRepeatType;
        this.mActive = mActive;
        this.mDone = mDone;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
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

    public void setDate(String mDate) {
        this.mDate = mDate;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String mTime) {
        this.mTime = mTime;
    }

    public String getRepeat() {
        return mRepeat;
    }

    public void setRepeat(String mRepeat) {
        this.mRepeat = mRepeat;
    }

    public String getRepeatNo() {
        return mRepeatNo;
    }

    public void setRepeatNo(String mRepeatNo) {
        this.mRepeatNo = mRepeatNo;
    }

    public String getRepeatType() {
        return mRepeatType;
    }

    public void setRepeatType(String mRepeatType) {
        this.mRepeatType = mRepeatType;
    }

    public String getActive() {
        return mActive;
    }

    public void setActive(String mActive) {
        this.mActive = mActive;
    }

    public String getDone() {
        return mDone;
    }

    public void setDone(String mDone) {
        this.mDone = mDone;
    }
}
