package com.example.zachcheu.carboncredit;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;

import com.mapbox.mapboxsdk.MapboxAccountManager;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.constants.Style;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Drive extends Activity implements LocationListener {

    // gets context location!
    private LocationManager lm;
    private TextView textView;
    public MapView mapView;
    public DrivePointManager drivePointManager;
    File file;
    FileWriter fw;
    BufferedWriter bw;

    public Drive() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MapboxAccountManager.start(this, getString(R.string.access_token));

        setContentView(R.layout.drive);
        drivePointManager = new DrivePointManager();
        textView = (TextView) findViewById(R.id.speedView);
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "gothic.ttf");
        textView.setTypeface(typeface);
        try{
            file = new File(Environment.getExternalStorageDirectory()+File.separator+"file.txt");
            if(file.createNewFile()){
                System.out.println("EXISTS");
            }else{
                System.out.println("GONE");
            }
            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
        }catch(Exception e){
            Log.d("Exception",e.toString());
        }
        mapView = (MapView) findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                // this is where we interact with the map
                mapboxMap.setStyleUrl(Style.MAPBOX_STREETS);
                //mapboxMap.setMaxZoom(10);
                mapboxMap.setMyLocationEnabled(true);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(mapboxMap.getMyLocation().getLatitude(), mapboxMap.getMyLocation().getLongitude()))
                        .zoom(13)
                        .bearing(180)
                        .tilt(30)
                        .build();
                mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition), 5000);
                addPoints(mapboxMap);

                mapboxMap.getUiSettings().setTiltGesturesEnabled(false);
                //mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition());
            }
        });

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        System.currentTimeMillis();

    }

    //drawing points test
    private void addPoints(final MapboxMap mapboxMap) {


        final List<LatLng> c = new ArrayList<>();
        c.add(new LatLng((double) (47.673988), (double) (-122.121513)));
        c.add(new LatLng((double) (40.730610), (double) (-73.935242)));
        c.add(new LatLng((double) (47.673988), (double) (-122.121513)));


        Drive.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapboxMap.addPolyline(new PolylineOptions()
                        .addAll(c)
                        .width(10)
                        .color(Color.parseColor("#3498db")));
            }
        });
    }

    private void addLine() {

        final DrivePoint lastPoint = drivePointManager.getLastPoint();
        final DrivePoint secondtoLastPoint = drivePointManager.getSecondToLastPoint();
        if (lastPoint == null) {
            return;
        }else{

        }
        if (secondtoLastPoint == null) {
            return;
        }else{

        }
        int speed1 = lastPoint.getSpeed();
        int speed2 = secondtoLastPoint.getSpeed();

        // calculate average speed between two points
        int averageSpeed = (speed1 + speed2)/2;

        final int color;

        /*
         * Determine color condition
         */
        if(averageSpeed >= 0 && averageSpeed < 25){
            color = Color.parseColor("#2ecc71");
        }else if (averageSpeed >= 25 && averageSpeed <= 40){
            color = Color.parseColor("#e67e22");
        }else{
            color = Color.parseColor("#c0392b");
        }


        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                mapboxMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(secondtoLastPoint.getmLocation().getLatitude(), secondtoLastPoint.getmLocation().getLongitude()))
                        .add(new LatLng(lastPoint.getmLocation().getLatitude(), lastPoint.getmLocation().getLongitude()))
                        .add(new LatLng(secondtoLastPoint.getmLocation().getLatitude(), secondtoLastPoint.getmLocation().getLongitude()))
                        .width(5)
                        .color(color));
            }
        });
    }


    @Override
    public void onLocationChanged(final Location location) {
        //@ param 1 : time -> we still need to calculate it properly...default is 0 for now
        //@ last param: isHighway...still need google maps api for this to work
        drivePointManager.addPoint(new DrivePoint(
                0.0f,
                (int) (location.getSpeed() * 2.2369),
                location,
                false));
        try{
            bw.write(""+(location.getSpeed()*2.2369)+","+new PointsToCred(drivePointManager.getDriveList()).getCarbonCredit());
        }catch(Exception e){
            Log.d("Exception",e.toString());
        }

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                System.out.println("test1");
            }
        });

        /*
         * adds line to map view
         */
        addLine();

        Drive.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                textView.setText(String.valueOf((int) (location.getSpeed() * 2.2369)));

            }
        });
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {
        //TO DO!
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    public void startTimer() {

    }
    public void stopTimer() {
        SystemClock s;
    }
}