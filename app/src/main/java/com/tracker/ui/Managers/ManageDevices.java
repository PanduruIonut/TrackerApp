package com.tracker.ui.Managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tracker.ui.MainActivity;
import com.tracker.ui.R;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class ManageDevices {

    public static void computeDevices(JSONObject devices) {
        try {

            Date c = Calendar.getInstance().getTime();
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
            String formattedDate = df.format(c);
            SharedPreferences prefs = MainActivity.context.getSharedPreferences(formattedDate, MODE_PRIVATE);
            ArrayList<String> knownDev = new ArrayList<>();
            knownDev = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString(formattedDate, ObjectSerializer.serialize(new ArrayList<String>())));

            ArrayList<String> devicesArray = new ArrayList<>();
            JSONObject jo = new JSONObject(String.valueOf(devices));
            for (int i = 0; i < jo.length(); i++) {
                devicesArray.add(jo.getString("MAC"));
            }

            if (knownDev != null) {
                if (knownDev.size() == 0) {
                    knownDev = devicesArray;
                }

                for (int j = 0; j < devicesArray.size(); j++) {
                    if (!knownDev.contains(devicesArray.get(j))) {
                        knownDev.add(devicesArray.get(j));
                    }
                    SharedPreferences.Editor editor = MainActivity.context.getSharedPreferences(formattedDate, MODE_PRIVATE).edit();
                    editor.putString(formattedDate, ObjectSerializer.serialize(devicesArray)).apply();
                }
                saveHistory(devicesArray);
            }

        } catch (Exception e) {
            Log.d("error", "error");
        }
    }

    public static Map<String, String> getLastWeekRaport(ArrayList<String> latestDaysArray) {
        Map<String, String> map = new HashMap<String, String>();
        for (int i = 0; i < latestDaysArray.size(); i++) {
            SharedPreferences prefs = MainActivity.context.getSharedPreferences(latestDaysArray.get(i), MODE_PRIVATE);
            ArrayList<String> devicesPerDay = new ArrayList<>();
            try {
                devicesPerDay = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString(latestDaysArray.get(i), ObjectSerializer.serialize(new ArrayList<String>())));
                map.put(latestDaysArray.get(i), String.valueOf(devicesPerDay.get(0).split(",").length));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    public static ArrayList<String> getLast5DaysDate() {
        ArrayList<String> lastDays = new ArrayList<>();
        try {
            for (int i = 1; i <= 5; i++) {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, -i);
                Date date = calendar.getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String formattedDate = df.format(date);
                lastDays.add(formattedDate);
            }
            Log.d("last5Days:", String.valueOf(lastDays));
        } catch (Exception e) {
            Log.d("error", String.valueOf(e));
        }
        return lastDays;
    }

    public static void saveDevices(Context context, int devices) {
        SharedPreferences.Editor editor = context.getSharedPreferences("devices", MODE_PRIVATE).edit();
        editor.putString("devices", "");
        SharedPreferences.Editor devicesSP = editor.putInt("devices", devices);
        editor.apply();
    }

    public static ArrayList<String> getDevices(Context context, String key) {
        SharedPreferences prefs = context.getSharedPreferences(key, MODE_PRIVATE);
        ArrayList<String> devices = new ArrayList<>();
        try {
            devices = (ArrayList<String>) ObjectSerializer.deserialize(prefs.getString(key, ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices;
    }

    public static String calculateCrowdedness(int roomName, int currentDevices) {
        switch (roomName) {
            case R.string.myHome:
                return getStatusPercentageBased(currentDevices, MainActivity.context.getResources().getInteger(R.integer.myHome_maxSpots));
            case R.string.UlbsStiinte:
                return getStatusPercentageBased(currentDevices, MainActivity.context.getResources().getInteger(R.integer.UlbsStiinte_maxSpots));
            case R.string.BibliotecaJudeteana:
                return getStatusPercentageBased(currentDevices, MainActivity.context.getResources().getInteger(R.integer.BibliotecaJudeteana_maxSpots));
            case R.string.UlbsDecanat:
                return getStatusPercentageBased(currentDevices, MainActivity.context.getResources().getInteger(R.integer.UlbsDecanat_maxSpots));
            case R.string.Starbucks:
                return getStatusPercentageBased(currentDevices, MainActivity.context.getResources().getInteger(R.integer.Starbucks_maxSpots));
            case R.string.PostaRomana:
                return getStatusPercentageBased(currentDevices, MainActivity.context.getResources().getInteger(R.integer.PostaRomana_maxSpots));
            default:
                return "error";
        }
    }

    private static String getStatusPercentageBased(int devices, float personLimit) {
        float percent = getPercentage(devices, personLimit);
        if (percent <= 35) {
            return "Not Busy";
        } else if (percent >= 35 && percent <= 75) {
            return "Crowded";
        } else if (percent >= 75) {
            return "Full ";
        }
        return "";
    }

    public static float getPercentage(int devices, float personLimit) {
        return (devices / personLimit) * 100;
    }

    public static void saveHistory(ArrayList<String> devicesArray) {
        try {
            SharedPreferences.Editor editor0 = MainActivity.context.getSharedPreferences("23-Jun-2020", MODE_PRIVATE).edit();
            editor0.putString("23-Jun-2020", ObjectSerializer.serialize(devicesArray)).apply();
            SharedPreferences.Editor editor = MainActivity.context.getSharedPreferences("24-Jun-2020", MODE_PRIVATE).edit();
            editor.putString("24-Jun-2020", ObjectSerializer.serialize(devicesArray)).apply();
            SharedPreferences.Editor editor2 = MainActivity.context.getSharedPreferences("25-Jun-2020", MODE_PRIVATE).edit();
            editor2.putString("25-Jun-2020", ObjectSerializer.serialize(devicesArray)).apply();
            SharedPreferences.Editor editor3 = MainActivity.context.getSharedPreferences("26-Jun-2020", MODE_PRIVATE).edit();
            editor3.putString("26-Jun-2020", ObjectSerializer.serialize(devicesArray)).apply();
            SharedPreferences.Editor editor4 = MainActivity.context.getSharedPreferences("27-Jun-2020", MODE_PRIVATE).edit();
            editor4.putString("27-Jun-2020", ObjectSerializer.serialize(devicesArray)).apply();
            SharedPreferences.Editor editor5 = MainActivity.context.getSharedPreferences("28-Jun-2020", MODE_PRIVATE).edit();
            editor5.putString("28-Jun-2020", ObjectSerializer.serialize(devicesArray)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
