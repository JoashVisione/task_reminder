package com.visione.taskreminder.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.visione.taskreminder.R;
import com.visione.taskreminder.adapter.ReminderDoneAdapter;
import com.visione.taskreminder.data.Reminder;
import com.visione.taskreminder.data.ReminderDatabase;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class DoneActivity extends AppCompatActivity {
    private List<Reminder> doneReminderList;
    private ReminderDatabase rb;
    TextView noReminder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done);

        Toolbar toolbar = findViewById(R.id.done_toolbar);
        toolbar.setTitle(R.string.done_reminders);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rb = new ReminderDatabase(this);
        doneReminderList = rb.getDoneReminders();
        ReminderDoneAdapter adapter = new ReminderDoneAdapter(this, doneReminderList);

        noReminder = findViewById(R.id.no_done_reminder_text);

        if(doneReminderList.isEmpty()){
            noReminder.setVisibility(View.VISIBLE);
        } else {
            noReminder.setVisibility(View.GONE);
        }

        RecyclerView recyclerView = findViewById(R.id.reminder_list_done);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.done_delete_all) {
            if(!doneReminderList.isEmpty()) {
                confirmDeletion();
            } else {
                Toasty.info(getApplicationContext(), "Make sure done reminders are available before deleting them", Toasty.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void confirmDeletion(){
        AlertDialog.Builder builder = new AlertDialog.Builder(DoneActivity.this);
        builder.setTitle("Confirm Deletion");
        builder.setMessage("Do you really want to delete all done tasks?");
        builder.setCancelable(false);
        builder.setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                rb.deleteDoneReminders();
                recreateActivityCompat(DoneActivity.this);
                Toasty.success(getApplicationContext(), "All Done tasks deleted", Toasty.LENGTH_SHORT).show();
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
    //refresh activity
    public  final  void recreateActivityCompat(final Activity activity){
        final Intent intent = activity.getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        activity.finish();
        activity.overridePendingTransition(0, 0);
        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }
}
