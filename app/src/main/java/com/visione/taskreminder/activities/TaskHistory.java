package com.visione.taskreminder.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.visione.taskreminder.R;
import com.visione.taskreminder.data.Reminder;
import com.visione.taskreminder.data.ReminderDatabase;
import com.visione.taskreminder.utils.DecimalRemover;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static com.github.mikephil.charting.animation.Easing.EaseInOutCubic;

public class TaskHistory extends AppCompatActivity {
    BarChart barChart;
    LineChart lineChart;
    PieChart pieChart;
    public LinkedHashMap<Integer, Integer> IDmap = new LinkedHashMap<>();
    private ReminderDatabase rb;
    private int allTasks;
    private int doneTasks;
    private int undoneTasks;
    Reminder reminder;
    private TextView progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_history);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rb = new ReminderDatabase(this);

        allTasks = rb.getAllTasksCreated().size();// e.g 10
        doneTasks = rb.getDoneReminders().size();//e.g. 5
        undoneTasks = rb.getAllReminders().size();

        barChart = findViewById(R.id.bar_chart);
        lineChart = findViewById(R.id.line_chart);
        pieChart = findViewById(R.id.pie_chart);

        setBarData();

        progress = findViewById(R.id.progress);

        if(doneTasks > undoneTasks){
            progress.setText("Analysis is complete. \nYou are doing well, Keep it up");
            progress.setVisibility(View.VISIBLE);
        } else if(doneTasks == allTasks){
            progress.setText("Analysis is complete. \nExcellent! Your performance is according to schedule.");
            progress.setVisibility(View.VISIBLE);
        } else if(doneTasks < undoneTasks){
            progress.setText("Analysis is complete. \nPlease make sure you do your tasks as planned. You are behind schedule.");
            progress.setVisibility(View.VISIBLE);
        } else if(doneTasks == undoneTasks){
            progress.setText("Analysis is complete. \nSeems everything is under control and normal.");
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    private void setLineData() {
        ArrayList<Entry> yValuesDone = new ArrayList<>();
        ArrayList<Entry> yValuesAll = new ArrayList<>();
        ArrayList<Entry> yValuesUndone = new ArrayList<>();

        int k = 0;
        int m = 0;
        int n = 0;
        for(int j = 0 ; j <= doneTasks; j++) {
            yValuesDone.add(new BarEntry(k , j));
            k += 1;
        }

        for(int j = 0 ; j <= allTasks; j++) {
            yValuesAll.add(new BarEntry(m, j));
            m += 3;
        }

         for(int j = 0 ; j <= undoneTasks; j++) {
          yValuesUndone.add(new BarEntry(n, j));
          n += 2;
        }


        LineDataSet lineDataSetDone = new LineDataSet(yValuesDone, "Completed Tasks");
        lineDataSetDone.setColor(getResources().getColor(R.color.green));
        lineDataSetDone.setDrawCircles(true);
        lineDataSetDone.setLineWidth(3f);

        LineDataSet lineDataSetUndone = new LineDataSet(yValuesUndone, "Uncompleted Tasks");
        lineDataSetUndone.setColor(getResources().getColor(R.color.secondaryBlack));
        lineDataSetUndone.setDrawCircles(true);
        lineDataSetUndone.setLineWidth(3f);

        LineDataSet lineDataSetAll = new LineDataSet(yValuesAll, "All Tasks");
        lineDataSetAll.setColor(getResources().getColor(R.color.colorAccent));
        lineDataSetAll.setDrawCircles(true);
        lineDataSetAll.setLineWidth(3f);

        lineDataSetAll.setDrawCircles(true);
        lineDataSetAll.setCircleColor(getResources().getColor(R.color.colorPrimary));
        lineDataSetDone.setDrawCircles(true);
        lineDataSetDone.setCircleColor(getResources().getColor(R.color.colorPrimary));
        lineDataSetUndone.setDrawCircles(true);
        lineDataSetUndone.setCircleColor(getResources().getColor(R.color.colorPrimary));


        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSetDone);
        dataSets.add(lineDataSetAll);
        dataSets.add(lineDataSetUndone);

        LineData lineData = new LineData(dataSets);
        lineData.setValueFormatter(new DecimalRemover());


        lineChart.getDescription().setEnabled(false);
        lineChart.setData(lineData);
        lineChart.getXAxis().setDrawAxisLine(true);
        lineChart.getXAxis().setDrawGridLines(true);
        lineChart.getAxisLeft().setStartAtZero(true);
        lineChart.getAxisRight().setStartAtZero(true);



        lineChart.animateX(1000);
        barChart.setVisibility(View.GONE);
        lineChart.setVisibility(View.VISIBLE);
        pieChart.setVisibility(View.GONE);
    }

    private void setBarData() {

        ArrayList<BarEntry> yValuesDone = new ArrayList<>();
        ArrayList<BarEntry> yValuesAll = new ArrayList<>();
        ArrayList<BarEntry> yValuesUndone = new ArrayList<>();

            yValuesDone.add(new BarEntry(1,  doneTasks));

            yValuesAll.add(new BarEntry(2,  allTasks));

            yValuesUndone.add(new BarEntry(3,  undoneTasks));

        BarDataSet barDataSetDone = new BarDataSet(yValuesDone, "Completed Tasks");
        barDataSetDone.setColors(getResources().getColor(R.color.green));
        barDataSetDone.setDrawValues(true);

        BarDataSet barDataSetUndone = new BarDataSet(yValuesUndone, "Uncompleted Tasks");
        barDataSetUndone.setColors(getResources().getColor(R.color.secondaryBlack));
        barDataSetUndone.setDrawValues(true);

        BarDataSet barDataSetAll = new BarDataSet(yValuesAll, "All Tasks");
        barDataSetAll.setColors(getResources().getColor(R.color.colorAccent));
        barDataSetAll.setDrawValues(true);

        ArrayList<IBarDataSet> barDataSets = new ArrayList<>();
        barDataSets.add(barDataSetDone);
        barDataSets.add(barDataSetUndone);
        barDataSets.add(barDataSetAll);


        BarData barData = new BarData(barDataSets);
        barData.setValueFormatter(new DecimalRemover());

        barChart.getDescription().setEnabled(false);
        barChart.invalidate();
        barChart.animateY(4000);
        barChart.setFitBars(true);
        barChart.getAxisLeft().setStartAtZero(true);
        barChart.getAxisRight().setStartAtZero(true);

        barChart.setData(barData);

        barChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.GONE);
        pieChart.setVisibility(View.GONE);
    }

    private void setPieData() {

        ArrayList<PieEntry> values = new ArrayList<>();

        values.add(new PieEntry(doneTasks, "Completed Tasks"));

        values.add(new PieEntry(undoneTasks,  "Uncompleted Tasks"));

        values.add(new PieEntry(allTasks,  "All Tasks"));

        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(getResources().getColor(R.color.green));
        colors.add(getResources().getColor(R.color.secondaryBlack));
        colors.add(getResources().getColor(R.color.colorAccent));

        PieDataSet pieDataSet = new PieDataSet(values, "");
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(3f);
        pieDataSet.setSelectionShift(5f);
        pieDataSet.setDrawValues(true);
        pieDataSet.setValueFormatter(new DecimalRemover());

        PieData pieData = new PieData( pieDataSet);
        pieData.setValueTextSize(10f);
        pieData.setValueTextColor(getResources().getColor(R.color.white));

        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.99f);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.WHITE);
        pieChart.setTransparentCircleRadius(36f);
        pieChart.setUsePercentValues(false);
        pieChart.setHoleRadius(25f);
        pieChart.animateY(4000, EaseInOutCubic);


        pieChart.setData(pieData);

        pieChart.setVisibility(View.VISIBLE);
        lineChart.setVisibility(View.GONE);
        barChart.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chart_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_bar_chart) {
            //show bar chart
            setBarData();
            return true;
        } else if(id == R.id.action_line_chart){
            //show line chart
            setLineData();
            return true;
        }  else if(id == R.id.action_pie_chart){
            //show pie chart
            setPieData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
