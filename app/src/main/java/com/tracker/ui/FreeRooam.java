package com.tracker.ui;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static java.lang.Double.parseDouble;

public class FreeRooam extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap mMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Button forwardButton;
    Button backwardsButton;
    int position = 0;

    double ULBSStiinteLat = Double.parseDouble(MainActivity.context.getResources().getString(R.string.UlbsStiinteCoordLat));
    double ULBSStiinteLong = Double.parseDouble(MainActivity.context.getResources().getString(R.string.UlbsStiinteCoordLong));
    double ULBSDecanatLat = Double.parseDouble(MainActivity.context.getResources().getString(R.string.UlbsDecanatCoordLat));
    double ULBSDecanatLong = Double.parseDouble(MainActivity.context.getResources().getString(R.string.UlbsDecanatCoordLong));
    double PrimariaSibiuLat = Double.parseDouble(MainActivity.context.getResources().getString(R.string.PrimariaSibiuCoordLat));
    double PrimariaSibiuLong = Double.parseDouble(MainActivity.context.getResources().getString(R.string.PrimariaSibiuCoordLong));
    double TNRLat = Double.parseDouble(MainActivity.context.getResources().getString(R.string.TeatrulRaduStancaCoordLat));
    double TNRLong = Double.parseDouble(MainActivity.context.getResources().getString(R.string.TeatrulRaduStancaCoordLong));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.free_roam);
        forwardButton = findViewById(R.id.forwardBtn);
        backwardsButton = findViewById(R.id.backBtn);


        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }
        }

        backwardsButton.setOnClickListener(v -> navigate(position - 1));

        forwardButton.setOnClickListener(v -> navigate(position + 1));
        MapFragment mapFragment = (MapFragment)
                getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(task -> {
            Location location = task.getResult();
            if (location != null) {
                Geocoder geocoder = new Geocoder(FreeRooam.this, Locale.getDefault());
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    locate(String.valueOf(addresses.get(0).getLatitude()), String.valueOf(addresses.get(0).getLongitude()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(45.7703424, 24.1623973), 499);
                mMap.moveCamera(cameraUpdate);
            }

        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getLocation();
        setMarkersPerCity(new LatLng(ULBSStiinteLat, ULBSStiinteLong), 75);
        setMarkersPerCity(new LatLng(ULBSDecanatLat, ULBSDecanatLong), 25);
        setMarkersPerCity(new LatLng(PrimariaSibiuLat, PrimariaSibiuLong), 50);
        setMarkersPerCity(new LatLng(TNRLat, TNRLong), 80);
    }

    public void locate(String latitude, String longitude) {
        LatLng coordinates = new LatLng(parseDouble(longitude), parseDouble(latitude));
        mMap.addMarker(new MarkerOptions().position(coordinates));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(parseDouble(longitude), parseDouble((latitude))), 17.0f);
        mMap.moveCamera(cameraUpdate);
    }

    private void setMarkersPerCity(LatLng latLng, int percentage) {
        mMap.addMarker(new MarkerOptions().position(latLng));
        CircleOptions circle = new CircleOptions()
                .center(latLng)
                .strokeWidth(2)
                .radius(20);

        if (percentage <= 35) {
            circle.fillColor(Color.parseColor("#93e993"));
            circle.strokeColor(Color.GREEN);
        } else if (percentage <= 75) {
            circle.fillColor(Color.parseColor("#ffff99"));
            circle.strokeColor(Color.YELLOW);
        } else {
            circle.fillColor(Color.parseColor("#e7756b"));
            circle.strokeColor(Color.RED);
        }
        mMap.addCircle(circle);
    }


    private void navigate(int position) {
        ArrayList<List<String>> myArray = new ArrayList<>();
        myArray.add(Arrays.asList(String.valueOf(ULBSStiinteLat), String.valueOf(ULBSStiinteLong), MainActivity.context.getResources().getString(R.string.UlbsStiinte)));
        myArray.add(Arrays.asList(String.valueOf(ULBSDecanatLat), String.valueOf(ULBSDecanatLong), MainActivity.context.getResources().getString(R.string.UlbsDecanat)));
        myArray.add(Arrays.asList(String.valueOf(PrimariaSibiuLat), String.valueOf(PrimariaSibiuLong), MainActivity.context.getResources().getString(R.string.PrimariaSibiu)));
        myArray.add(Arrays.asList(String.valueOf(TNRLat), String.valueOf(TNRLong), MainActivity.context.getResources().getString(R.string.TeatrulRaduStanca)));

        if (position >= 0 && position <= 3) {
            this.position = position;
            Toast.makeText(this, myArray.get(position).get(2), Toast.LENGTH_LONG).show();
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(parseDouble(String.valueOf(myArray.get(position).get(0))), parseDouble((myArray.get(position).get(1)))), 17.0f);
            mMap.moveCamera(cameraUpdate);
        }
    }
}