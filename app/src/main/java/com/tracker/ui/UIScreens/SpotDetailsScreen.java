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
import com.tracker.ui.R;

import java.util.ArrayList;
import java.util.Map;

import static com.tracker.ui.Managers.ManageDevices.getLast5DaysDate;
import static com.tracker.ui.Managers.ManageDevices.getLastWeekRaport;
import static java.lang.Integer.parseInt;

public class SpotDetailsScreen extends AppCompatActivity {

    BarChart barChart;
    TextView placeTitleTV;
    TextView aviTV;
    ProgressBar percentagePB;
    String title;
    String percentage;
    String statusAvi;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot_details);
        barChart = findViewById(R.id.busynessChart);
        barChart.getDescription().setEnabled(false);
        setData(5);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            title = "error";
        } else {
            title = extras.getString("placeName");
            percentage = extras.getString("percentage");
            statusAvi = extras.getString("percentageDesc");

        }
        placeTitleTV = findViewById(R.id.spotName);
        percentagePB = findViewById(R.id.percentageBar);
        aviTV = findViewById(R.id.main_percentage_text);
        placeTitleTV.setText(title);
        percentagePB.setProgress(parseInt(percentage));
        aviTV.setText(statusAvi + " - " + percentage + "%");

        if (parseInt(percentage) <= 35) {
            percentagePB.setProgressTintList(ColorStateList.valueOf(Color.GREEN));
            ;

        } else if (parseInt(percentage) >= 35 && parseInt(percentage) <= 75) {
            percentagePB.setProgressTintList(ColorStateList.valueOf(Color.YELLOW));

        } else if (parseInt(percentage) >= 75) {
            percentagePB.setProgressTintList(ColorStateList.valueOf(Color.RED));
        }

    }

    public void setData(int count) {
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
}
