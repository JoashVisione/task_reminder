package com.visione.taskreminder.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bignerdranch.android.multiselector.MultiSelector;
import com.bignerdranch.android.multiselector.SwappingHolder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.visione.taskreminder.R;
import com.visione.taskreminder.data.Reminder;
import com.visione.taskreminder.data.ReminderDatabase;
import com.visione.taskreminder.data.ReminderItem;
import com.visione.taskreminder.interfaces.OnClickListener;
import com.visione.taskreminder.receivers.AlarmReceiver;
import com.visione.taskreminder.session.ResetPin;
import com.visione.taskreminder.session.SessionManager;
import com.visione.taskreminder.utils.DateTimeComparator;
import com.visione.taskreminder.utils.DateTimeSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.visione.taskreminder.data.ReminderDatabase.DONE;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private ReminderAdapter mAdapter;
    private TextView mNoReminderView;
    public LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    private ReminderDatabase rb;
    private MultiSelector mMultiSelector = new MultiSelector();
    private AlarmReceiver mAlarmReceiver;
    SessionManager session;
    private  boolean doubleBackToExitPressedOnce = false;
    Toolbar toolbar;

    //added
    private ActionModeCallback actionModeCallback;
    private ActionMode actionMode;
    private MenuItem doneRemindersTxt;
    ArrayList<ReminderItem> filterLists;
    public ArrayList<ReminderItem> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        //session management
        session = new SessionManager(getApplicationContext());
        session.checkLogin();

        //check intro
        if(session.isLoggedIn()) {
            if (!restorePrefData()) {
                Intent mainActivity = new Intent(getApplicationContext(), IntroActivity.class);
                startActivity(mainActivity);
                finish();
            }
        }

        // Initialize reminder database
        rb = new ReminderDatabase(this);

        // Initialize views
        FloatingActionButton fab = findViewById(R.id.fab);
        RecyclerView mList = findViewById(R.id.reminder_list);
        mNoReminderView = findViewById(R.id.no_reminder_text);

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

//        if (mTest.isEmpty()) {
  //          mNoReminderView.setVisibility(View.VISIBLE);
    //    }
        // Create recycler view
        mList.setLayoutManager(getLayoutManager());
        registerForContextMenu(mList);
        mAdapter = new ReminderAdapter();
        mAdapter.setItemCount(getDefaultItemCount());
        mList.setAdapter(mAdapter);

        // On clicking the floating action button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ReminderAddActivity.class);
                startActivity(intent);
            }
        });

        //added
        actionModeCallback = new ActionModeCallback();

        mAdapter.setOnClickListener(new OnClickListener() {
            @Override
            public void onItemLongClick(View view, ReminderItem obj, int pos) {
                enableActionMode(pos);
            }

            @Override
            public void onItemClick(View view, ReminderAdapter.ViewHolder holder, int pos) {
                if (!mMultiSelector.tapSelection(holder)) {
                    int mReminderClickID = IDmap.get(pos);
                    selectReminder(mReminderClickID);

                } else if(mMultiSelector.getSelectedPositions().isEmpty()){
                  mAdapter.setItemCount(getDefaultItemCount());
                }
            }
        });

        //end

        // Initialize alarm
        mAlarmReceiver = new AlarmReceiver();
        //show done task on navigation view

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        //drawer
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu navMenu = navigationView.getMenu();
        doneRemindersTxt = navMenu.findItem(R.id.nav_completed_tasks);

        countUndoneTasks();
        //end
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);

            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                //super.onBackPressed();
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    return;
                }
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(MainActivity.this, "Press back again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        filterLists = new ArrayList<>(mItems);//copy mitems to filter list
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                //mAdapter.getFilter().filter(s);
                //Toasty.success(getApplicationContext(), mAdapter.getFilter().convertResultToString(s), Toasty.LENGTH_SHORT).show();
                final ArrayList<ReminderItem> filterModeList = filter(filterLists, s);
                mAdapter.setFilter(filterModeList);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            session.logoutUser();
            finish();
            return true;
        } else if(id == R.id.action_delete_all){
            //confirm before deleting all tasks
            confirmDeletion();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_completed_tasks) {
            startActivity(new Intent(getApplicationContext(), DoneActivity.class));
        } else if (id == R.id.nav_change_pin) {
            startActivity(new Intent(getApplicationContext(), ResetPin.class));
        } else if (id == R.id.nav_exit) {
            confirmExit();
        } else if (id == R.id.nav_share) {
            shareIt();
        }  else if (id == R.id.nav_task_analysis) {
            startActivity(new Intent(getApplicationContext(), TaskHistory.class));
        } else if (id == R.id.nav_about) {
            changelog();
        }  else if (id == R.id.nav_help) {
            startActivity(new Intent(getApplicationContext(), IntroActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // On clicking a reminder item
    private void selectReminder(int mClickID) {
        String mStringClickID = Integer.toString(mClickID);

        // Create intent to edit the reminder
        // Put reminder id as extra
        Intent i = new Intent(this, ReminderEditActivity.class);
        i.putExtra(ReminderEditActivity.EXTRA_REMINDER_ID, mStringClickID);
        startActivityForResult(i, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.setItemCount(getDefaultItemCount());
    }

    // Recreate recycler view
    // This is done so that newly created reminders are displayed
    @Override
    public void onResume(){
        super.onResume();

        // To check is there are saved reminders
        // If there are no reminders display a message asking the user to create reminders
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }

        mAdapter.setItemCount(getDefaultItemCount());
        countUndoneTasks();
    }

    // Layout manager for recycler view
    protected RecyclerView.LayoutManager getLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        return linearLayoutManager;
    }

    protected int getDefaultItemCount() {
        return 100;
    }

    private void confirmDeletion(){
        if(rb.getAllReminders().size() < 1) {
            Toasty.info(MainActivity.this, "Please make sure you add reminders before deleting", Toasty.LENGTH_SHORT).show();
        } else {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Do you really want to delete all your tasks? Reminders will be deleted too. This action is irreversible.");
        builder.setCancelable(false);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    rb.deleteUndoneReminders();
                    Toasty.success(MainActivity.this, "All tasks and reminders deleted", Toasty.LENGTH_SHORT).show();
                    recreateActivityCompat(MainActivity.this);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    }
    //sharing option
    private void shareIt() {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Task Reminder");
        sharingIntent.putExtra(Intent.EXTRA_TEXT, "Click on the this link to download this app http://www.visione.com");
        startActivity(Intent.createChooser(sharingIntent, "Share Via"));

    }
    //refresh activity
    public  final  void recreateActivityCompat(final Activity activity){
        final Intent intent = activity.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    public boolean restorePrefData() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("myPrefs", MODE_PRIVATE);
        return pref.getBoolean("isIntroOpnend",false);
    }

    //added callback class
    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.delete_selected_items, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.action_delete_all) {
                deleteReminder();
                mode.finish();
                return true;
            } else if(id == R.id.action_done){
                updateReminder();
                Toasty.success(getApplicationContext(),
                        "Task moved to completed tasks",
                        Toasty.LENGTH_SHORT).show();
                mode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelections();
            actionMode = null;
        }
    }

    private void deleteReminder() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            int id = IDmap.get(i);
            Reminder r = rb.getReminder(id);
            rb.deleteReminder(r);
            mAlarmReceiver.cancelAlarm(getApplicationContext(), i);
            mAdapter.removeData(selectedItemPositions.get(i));
        }
        mAdapter.notifyDataSetChanged();
        Toasty.success(getApplicationContext(),
                "Reminder Deleted",
                Toasty.LENGTH_SHORT).show();
        checkReminders();
    }

    private void updateReminder() {
        List<Integer> selectedItemPositions = mAdapter.getSelectedItems();
        for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
            int id = IDmap.get(i);
            Reminder r = rb.getReminder(id);
            r.setDone(DONE);
            //if(rb.updateDoneReminder(r)> -1){
            rb.updateDoneReminder(r);
            mAlarmReceiver.cancelAlarm(getApplicationContext(), id);
            mAdapter.removeData(selectedItemPositions.get(i));
            //}
        }
        mAdapter.notifyDataSetChanged();
        checkReminders();
        countUndoneTasks();

    }

    //added methods
    private void enableActionMode(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        mAdapter.toggleSelection(position);
        int count = mAdapter.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
        } else {
            actionMode.setTitle(count + " selected");
            actionMode.invalidate();
        }
    }

    private void checkReminders(){
        List<Reminder> mTest = rb.getAllReminders();

        if (mTest.isEmpty()) {
            mNoReminderView.setVisibility(View.VISIBLE);
        } else {
            mNoReminderView.setVisibility(View.GONE);
        }
    }

    public void countUndoneTasks(){
        int counter = rb.getDoneReminders().size();
        if(counter == 100)
            doneRemindersTxt.setTitle("Completed Tasks     (99+)");
        else
            doneRemindersTxt.setTitle("Completed Tasks      (" + String.valueOf(counter) + ")");
    }

    //end

    //start filetring
    private ArrayList<ReminderItem> filter(ArrayList<ReminderItem> rList, String query){
        query = query.toLowerCase();
        final ArrayList<ReminderItem> filterModeList = new ArrayList<>();
        for(ReminderItem item : rList){
            final String filterPattern = item.mTitle.toLowerCase();
            if(filterPattern.contains(query)){
                filterModeList.add(item);
            }
        }
        return filterModeList;
    }
    //filter end

    //display changelog
    private void changelog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setCancelable(true);
        dialog.setTitle("Changelog");
        dialog.setContentView(R.layout.changelog_layout);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
        //..................

    }

    public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> /*implements Filterable*/ {
        ViewHolder holder;
        private OnClickListener onClickListener = null;
        ReminderDatabase rb;

        private SparseBooleanArray selected_items;
        private int current_selected_idx = -1;

        private void setOnClickListener(OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }

        ReminderAdapter() {
            mItems = new ArrayList<>();
            selected_items = new SparseBooleanArray();
        }

        public void setItemCount(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
            notifyDataSetChanged();
        }

        public void onDeleteItem(int count) {
            mItems.clear();
            mItems.addAll(generateData(count));
        }

        public void removeItemSelected(int selected) {
            if (mItems.isEmpty()) return;
            mItems.remove(selected);
            notifyItemRemoved(selected);
        }

        //for filtering
        public void setFilter(ArrayList<ReminderItem> listItems){
            mItems = new ArrayList<>();
            mItems.addAll(listItems);
            notifyDataSetChanged();
        }

        // View holder for recycler view items
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View root = inflater.inflate(R.layout.recycle_items, parent, false);
            holder = new ViewHolder(root, this);

            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder itemHolder, final int position) {
            final ReminderItem item = mItems.get(position);
            itemHolder.setReminderTitleAndDescription(item.mTitle, item.mDescription);
            itemHolder.setReminderDateTime(item.mDate, item.mTime);
            itemHolder.setReminderRepeatInfo(item.mRepeat, item.mRepeatNo, item.mRepeatType);
            itemHolder.setActiveImage(item.mActive);
            toggleCheckedIcon(itemHolder, position);

            itemHolder.relativeLayout.setActivated(selected_items.get(position, false));

            itemHolder.mThumbnailImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickListener != null) {
                        onClickListener.onItemClick(v, holder, position);
                    }
                }
            });

            itemHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (onClickListener != null)
                        onClickListener.onItemLongClick(view, item, position);
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

        //added
        private void toggleCheckedIcon(ViewHolder holder, int position) {
            if (selected_items.get(position, false)) {
                holder.selectedImage.setVisibility(View.VISIBLE);
                holder.mActiveImage.setVisibility(View.GONE);
                if (current_selected_idx == position)
                    resetCurrentIndex();
            } else {
                holder.selectedImage.setVisibility(View.GONE);
                holder.mActiveImage.setVisibility(View.VISIBLE);
                if (current_selected_idx == position)
                    resetCurrentIndex();
            }
        }

        public ReminderItem getItem(int position) {
            return mItems.get(position);
        }

        void toggleSelection(int pos) {
            current_selected_idx = pos;
            if (selected_items.get(pos, false)) {
                selected_items.delete(pos);
            } else {
                selected_items.put(pos, true);
            }
            notifyItemChanged(pos);
        }

        void clearSelections() {
            selected_items.clear();
            notifyDataSetChanged();
        }

        int getSelectedItemCount() {
            return selected_items.size();
        }

        List<Integer> getSelectedItems() {
            List<Integer> items = new ArrayList<>(selected_items.size());
            for (int i = 0; i < selected_items.size(); i++) {
                items.add(selected_items.keyAt(i));
            }
            return items;
        }

        void removeData(int position) {
            mItems.remove(position);
            resetCurrentIndex();
        }

        private void resetCurrentIndex() {
            current_selected_idx = -1;
        }

        // UI and data class for recycler view items
        public  class ViewHolder extends SwappingHolder /*implements View.OnClickListener, View.OnLongClickListener */{
            private TextView mTitleText, mDescriptionText, mDateText, mTimeText, mRepeatInfoText;
            private ImageView mActiveImage , mThumbnailImage, selectedImage;
            private ColorGenerator mColorGenerator = ColorGenerator.DEFAULT;
            private TextDrawable mDrawableBuilder;
            private RelativeLayout relativeLayout;

            ViewHolder(View itemView, ReminderAdapter adapter) {
                super(itemView, mMultiSelector);

                // Initialize adapter for the items
                mAdapter = adapter;

                // Initialize views
                mTitleText = itemView.findViewById(R.id.recycle_title);
                mDescriptionText = itemView.findViewById(R.id.recycle_description);
                mDateText = itemView.findViewById(R.id.recycle_date);
                mTimeText = itemView.findViewById(R.id.recycle_time);
                mRepeatInfoText = itemView.findViewById(R.id.recycle_repeat_info);
                mActiveImage = itemView.findViewById(R.id.active_image);
                mThumbnailImage = itemView.findViewById(R.id.thumbnail_image);
                selectedImage = itemView.findViewById(R.id.selected_image);
                relativeLayout = itemView.findViewById(R.id.relative_layout);

            }

            // Set reminder title view
            void setReminderTitleAndDescription(String title, String description) {
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
            void setReminderDateTime(String date, String time) {
                mDateText.setText(date);
                mTimeText.setText(time);
            }

            // Set repeat views
            void setReminderRepeatInfo(String repeat, String repeatNo, String repeatType) {
                if(repeat.equals("true")){
                    mRepeatInfoText.setText("Every " + repeatNo + " " + repeatType + "(s)");
                }else if (repeat.equals("false")) {
                    mRepeatInfoText.setText("Off");
                }
            }

            // Set active image as on or off
            void setActiveImage(String active){
                if(active.equals("true")){
                    mActiveImage.setImageResource(R.drawable.ic_notification_on);
                }else if (active.equals("false")) {
                    mActiveImage.setImageResource(R.drawable.ic_notifications_off);
                }
            }
        }//end class


        // Generate real data for each item
        List<ReminderItem> generateData(int count) {
            ArrayList<ReminderItem> items = new ArrayList<>();

            // Get all reminders from the database
            ReminderDatabase reminderDatabase = new ReminderDatabase(getApplicationContext());
            List<Reminder> reminders = reminderDatabase.getAllReminders();

            // Initialize lists
            List<String> Titles = new ArrayList<>();
            List<String> Descriptions = new ArrayList<>();
            List<String> Repeats = new ArrayList<>();
            List<String> RepeatNos = new ArrayList<>();
            List<String> RepeatTypes = new ArrayList<>();
            List<String> Actives = new ArrayList<>();
            List<String> Dates = new ArrayList<>();
            List<String> Times = new ArrayList<>();
            List<String> DateAndTimes = new ArrayList<>();
            List<Integer> IDList= new ArrayList<>();
            List<DateTimeSorter> DateTimeSortList = new ArrayList<>();

            // Add details of all reminders in their respective lists
            for (Reminder r : reminders) {
                Titles.add(r.getTitle());
                Descriptions.add(r.getDescription());
                Dates.add(r.getDate());
                Times.add(r.getTime());
                DateAndTimes.add(r.getDate() + " " + r.getTime());
                Repeats.add(r.getRepeat());
                RepeatNos.add(r.getRepeatNo());
                RepeatTypes.add(r.getRepeatType());
                Actives.add(r.getActive());
                IDList.add(r.getID());
            }

            int key = 0;

            // Add date and time as DateTimeSorter objects
            for(int k = 0; k<Titles.size(); k++){
                DateTimeSortList.add(new DateTimeSorter(key, DateAndTimes.get(k)));
                key++;
            }

            // Sort items according to date and time in ascending order
            Collections.sort(DateTimeSortList, new DateTimeComparator());

            int k = 0;

            // Add data to each recycler view item
            for (DateTimeSorter item : DateTimeSortList) {
                int i = item.getIndex();
                items.add(new ReminderItem(Titles.get(i), Descriptions.get(i), Dates.get(i), Times.get(i), Repeats.get(i),
                        RepeatNos.get(i), RepeatTypes.get(i), Actives.get(i)));
                IDmap.put(k, IDList.get(i));

                k++;
            }
            return items;
        }
    }

    private void confirmExit(){
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Confirm Exit");
        builder.setMessage("Do you really want to close?");
        builder.setCancelable(false);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
               finish();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
