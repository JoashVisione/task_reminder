package com.visione.taskreminder.interfaces;

import android.view.View;

import com.visione.taskreminder.activities.MainActivity;
import com.visione.taskreminder.data.ReminderItem;

public interface OnClickListener {
    void onItemLongClick(View view, ReminderItem obj, int pos);
    void onItemClick(View view, MainActivity.ReminderAdapter.ViewHolder holder, int pos);
}
