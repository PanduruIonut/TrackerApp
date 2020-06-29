package com.tracker.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    GoogleMap map;
    public static int myHomeDevices;
    public static Context context;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        context = this;
        CardView city1 = findViewById(R.id.city1);
        CardView city2 = findViewById(R.id.city2);
        CardView city3 = findViewById(R.id.city3);
        CardView city4 = findViewById(R.id.city4);
        CardView utilityBtn = findViewById(R.id.utilityBtn);
        city1.setOnClickListener(this);
        city2.setOnClickListener(this);
        city3.setOnClickListener(this);
        city4.setOnClickListener(this);
        utilityBtn.setOnClickListener(this);

        checkPermission();


        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.100.41:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {

                    Log.d("MainActivity", "onSuccess");

                    String subtopic = "Sniffer";
                    int qos = 1;
                    try {
                        IMqttToken subToken = client.subscribe(subtopic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {

                            }
                        });
                    } catch (MqttSecurityException e) {
                        e.printStackTrace();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d("MainActivity", "onFailure");

                }
            });
        } catch (MqttException e) {
            Log.d("error", "error");
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                Toast.makeText(MainActivity.this, new String("Connection lost"), Toast.LENGTH_LONG).show();

            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast.makeText(MainActivity.this, new String(message.getPayload()), Toast.LENGTH_LONG).show();
                JSONObject messageJson = new JSONObject(new String(message.getPayload()));
                int devicesNr = messageJson.getJSONArray("MAC").length();
                myHomeDevices = devicesNr;
                ManageDevices.computeDevices(messageJson);
                Message.obtain(ItemsPreviewActivity.CityLocationHandler, 1, devicesNr).sendToTarget();
                Message.obtain(CityScreen.CityScreenHandler, 1, devicesNr).sendToTarget();
                Log.d("MainActivity", "message received json :" + messageJson);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.utilityBtn) {
            Intent i = new Intent(this, FreeRooam.class);
            startActivity(i);
        } else {
            Intent intent = new Intent(this, ItemsPreviewActivity.class);
            startActivity(intent);

        }
    }

    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }
}
