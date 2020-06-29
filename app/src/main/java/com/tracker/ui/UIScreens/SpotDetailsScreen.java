package com.tracker.ui.UIScreens;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.tracker.ui.Managers.ManageDevices;
import com.tracker.ui.R;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

import static com.tracker.ui.Managers.ManageDevices.getLast5DaysDate;
import static com.tracker.ui.Managers.ManageDevices.getLastWeekRaport;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class SpotDetailsScreen extends AppCompatActivity {

    BarChart barChart;
    TextView placeTitleTV;
    ProgressBar percentagePB, percentagePB2;
    String title;
    TextView roomName;
    TextView roomName2;
    TextView percentageDesc, percentageDesc2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint({"WrongConstant", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_details);
        barChart = findViewById(R.id.busynessChart);
        barChart.getDescription().setEnabled(false);
        setData();


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            title = "error";
        } else {
            title = extras.getString("placeName");
        }
        placeTitleTV = findViewById(R.id.spotName);
        percentagePB = findViewById(R.id.roomPercentageBar);
        percentagePB2 = findViewById(R.id.room2PercentageBar);
        placeTitleTV.setText(title);
        roomName = findViewById(R.id.room1Name);
        roomName2 = findViewById(R.id.room2Name);
        percentageDesc = findViewById(R.id.percentageStatus);
        percentageDesc2 = findViewById(R.id.percentageStatus2);
        populateDummyData();
    }

    public void setData() {
        Map<String, String> latestDaysRaport = getLastWeekRaport(getLast5DaysDate());
        ArrayList<BarEntry> yVals = new ArrayList<>();
        Object[] values = latestDaysRaport.values().toArray();

        for (int i = 0; i < latestDaysRaport.keySet().size(); i++) {
            yVals.add(new BarEntry(i, Integer.parseInt(values[i].toString())));
        }
        BarDataSet set = new BarDataSet(yVals, "Previous Days");
        set.setColors(ColorTemplate.MATERIAL_COLORS);
        set.setDrawValues(true);

        BarData barData = new BarData(set);

        barChart.setData(barData);
        barChart.invalidate();
        barChart.animateY(500);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void populateDummyData() {
        Random rand = new Random();
        int percentage = rand.nextInt(100);
        Random rand2 = new Random();
        int percentage2 = rand2.nextInt(100);
        if (title.equals(this.getString(R.string.myHome))) {
            roomName.setText("Living");
            roomName2.setText("Bedroom");
            percentageDesc.setText(ManageDevices.getStatusPerPercent(percentage) + " " + percentage + " " + "%");
            percentageDesc2.setText(ManageDevices.getStatusPerPercent(percentage2) + "" + percentage2 + " " + "%");
        } else if (title.equals(getString(R.string.UlbsStiinte))) {
            roomName.setText("A24");
            roomName2.setText("Laborator 1");
            percentageDesc.setText(ManageDevices.getStatusPerPercent(percentage) + percentage + " " + "%");
            percentageDesc2.setText(ManageDevices.getStatusPerPercent(percentage2) + percentage2 + " " + "%");
        } else if (title.equals(getString(R.string.UlbsDecanat))) {
            roomName.setText("DGA");
            roomName2.setText("Receptie");
            percentageDesc.setText(ManageDevices.getStatusPerPercent(percentage) + percentage + " " + "%");
            percentageDesc2.setText(ManageDevices.getStatusPerPercent(percentage2) + percentage2 + " " + "%");
        } else if (title.equals(getString(R.string.BibliotecaJudeteana))) {
            roomName.setText("Camera 1");
            roomName2.setText("Camera 2");
            percentageDesc.setText(ManageDevices.getStatusPerPercent(percentage) + " " + percentage + "%");
            percentageDesc2.setText(ManageDevices.getStatusPerPercent(percentage2) + " " + percentage2 + "%");
        } else if (title.equals(getString(R.string.Starbucks))) {
            roomName.setText("Lounge");
            roomName2.setText("Terasa");
            percentageDesc.setText(ManageDevices.calculateCrowdedness(R.string.Starbucks, percentage) + percentage + " " + "%");
            percentageDesc2.setText(ManageDevices.getStatusPerPercent(percentage2) + percentage2 + " " + "%");
        } else if (title.equals(getString(R.string.PostaRomana))) {
            roomName.setText("Receptie");
            roomName2.setText("Camera de asteptare");
            percentageDesc.setText(ManageDevices.calculateCrowdedness(R.string.Starbucks, percentage) + percentage + " " + "%");
            percentageDesc2.setText(ManageDevices.getStatusPerPercent(percentage2) + percentage2 + " " + "%");
        }
        percentagePB.setProgress(percentage);
        percentagePB2.setProgress((percentage2));

        if (percentage <= 35) {
            percentagePB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        } else if (percentage <= 75) {
            percentagePB.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        } else {
            percentagePB.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

        if (percentage2 <= 35) {
            percentagePB2.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
        } else if (percentage2 <= 75) {
            percentagePB2.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));
        } else {
            percentagePB2.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

    }
}
