package com.visione.taskreminder.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarInstance {
    public CalendarInstance() {
    }

    public Calendar getCalendar(String date, String time){
        SimpleDateFormat currentTimeFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        Date alarmDate;
        try {
            alarmDate = currentTimeFormat.parse(date + " " + time);
            calendar.setTime(alarmDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return calendar;
    }
}
