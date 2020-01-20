package com.visione.taskreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.visione.taskreminder.R;
import com.visione.taskreminder.data.Reminder;

import java.util.List;


public class ReminderDoneAdapter extends RecyclerView.Adapter<ReminderDoneAdapter.MyViewHolder> {
    private TextView mTitleText, mDateText, mTimeText, mDescriptionText, mRepeatInfoText;
    private ImageView mActiveImage , mThumbnailImage;
    private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
    private TextDrawable mDrawableBuilder;

    private Context context;
    private List<Reminder> doneReminderList;

    public ReminderDoneAdapter(Context context, List<Reminder> doneReminderList) {
        this.context = context;
        this.doneReminderList = doneReminderList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycle_items, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        final Reminder task = doneReminderList.get(i);
        String title = task.getTitle();
        String description = task.getDescription();
        String date = task.getDate();
        String time = task.getTime();
        String repeat = task.getRepeat();
        String repeatNo = task.getRepeatNo();
        String repeatType = task.getRepeatType();
        String active = task.getActive();


        setReminderTitleAndDescription(title, description);

        if (date != null && time != null) {
            setReminderDateTime(date, time);
        } else {
            mDateText.setText("Date not set");
            mTimeText.setText("Time not set");
        }

        if (repeat != null) {
            setReminderRepeatInfo(repeat, repeatNo, repeatType);
        } else {
            mRepeatInfoText.setText("Off");
        }
        mActiveImage.setVisibility(View.GONE);

        /*if (active != null) {
            setActiveImage(active);
        } else {
            mActiveImage.setImageResource(R.drawable.ic_notifications_active);
        }*/

    }
    @Override
    public int getItemCount() {
        return doneReminderList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        MyViewHolder(@NonNull View view) {
            super(view);
            mTitleText = (TextView) view.findViewById(R.id.recycle_title);
            mDateText = (TextView) view.findViewById(R.id.recycle_date);
            mTimeText = (TextView) view.findViewById(R.id.recycle_time);
            mDescriptionText = (TextView) view.findViewById(R.id.recycle_description);
            mRepeatInfoText = (TextView) view.findViewById(R.id.recycle_repeat_info);
            mActiveImage = (ImageView) view.findViewById(R.id.active_image);
            mThumbnailImage = (ImageView) view.findViewById(R.id.thumbnail_image);
        }
    }

    // Set reminder title view
    public void setReminderTitleAndDescription(String title, String description) {
        mTitleText.setText(title);
        mDescriptionText.setText(description);
        String letter = "A";

        if(title != null && !title.isEmpty()) {
            letter = title.substring(0, 1);
        }

        int color = mColorGenerator.getRandomColor();

        // Create a circular icon consisting of  a random background colour and first letter of title
        mDrawableBuilder = TextDrawable.builder()
                .buildRound(letter, color);
        mThumbnailImage.setImageDrawable(mDrawableBuilder);
    }

    // Set date and time views
    public void setReminderDateTime(String date, String time) {
        mDateText.setText(date);
        mTimeText.setText(time);
    }

    // Set repeat views
    public void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
        if(repeat.equals("true")){
            mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
        }else if (repeat.equals("false")) {
            mRepeatInfoText.setText("Off");
        }
    }

    // Set active image as on or off
    public void setActiveImage(String active){
        if(active.equals("true")){
            mActiveImage.setImageResource(R.drawable.ic_notification_on);
        }else if (active.equals("false")) {
            mActiveImage.setImageResource(R.drawable.ic_notifications_off);
        }

    }

    public void removeData() {
        notifyDataSetChanged();
    }
}
