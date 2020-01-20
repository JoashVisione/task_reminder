package com.visione.taskreminder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.visione.taskreminder.data.Reminder;
import com.visione.taskreminder.data.ReminderDatabase;
import com.visione.taskreminder.utils.CalendarInstance;

import java.util.Calendar;
import java.util.List;


public class BootReceiver extends BroadcastReceiver {

    private long mRepeatTime;


    // Constant values in milliseconds
    private static final long milMinute = 60000L;
    private static final long milHour = 3600000L;
    private static final long milDay = 86400000L;
    private static final long milWeek = 604800000L;
    private static final long milMonth = 2592000000L;


    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

            ReminderDatabase reminderDatabase = new ReminderDatabase(context);

            AlarmReceiver mAlarmReceiver = new AlarmReceiver();

            List<Reminder> reminders = reminderDatabase.getAllReminders();

            for (Reminder reminder : reminders) {
                int mReceivedID = reminder.getID();
                String mRepeat = reminder.getRepeat();
                String mRepeatNo = reminder.getRepeatNo();
                String mRepeatType = reminder.getRepeatType();
                String mActive = reminder.getActive();
                String mDate = reminder.getDate();
                String mTime = reminder.getTime();

                // Cancel existing notification of the reminder by using its ID
                mAlarmReceiver.cancelAlarm(context, mReceivedID);

                // Check repeat type
                switch (mRepeatType) {
                    case "Minute":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milMinute;
                        break;
                    case "Hour":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milHour;
                        break;
                    case "Day":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milDay;
                        break;
                    case "Week":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milWeek;
                        break;
                    case "Month":
                        mRepeatTime = Integer.parseInt(mRepeatNo) * milMonth;
                        break;
                }

                // Create a new notification

                Calendar calendar = new CalendarInstance().getCalendar(mDate, mTime);
                Calendar current = Calendar.getInstance();
                if (calendar.compareTo(current) > 0) {
                    if (mActive.equals("true")) {
                        if (mRepeat.equals("true")) {
                            mAlarmReceiver.setRepeatAlarm(context, calendar, mReceivedID, mRepeatTime);
                        } else if (mRepeat.equals("false")) {
                            mAlarmReceiver.setAlarm(context, calendar, mReceivedID);
                        }
                    }
                }
            }
        }
    }
}