package com.example.zachcheu.carboncredit;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.w3c.dom.Text;
import java.util.List;

/**
 * Created by DevWork on 10/27/16.
 */

public class MapFragment extends Fragment implements LocationListener, PointMatcherCallback {
    public static final String ARG_OBJECT = "object";

    public MapView mapView;
    public LocationManager lm;
    public Context mContext;

    public TextView textView;
    public TextView texter;

    public View custom;
    public CustomView customView;

    public float endTime;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MapboxAccountManager.getInstance();
        View rootView = inflater.inflate(R.layout.map_layout, container, false);
        Bundle args = getArguments();
        int index = args.getInt(ARG_OBJECT);

        //textView = (TextView) rootView.findViewById(R.id.speedText);
        //texter = (TextView) rootView.findViewById(R.id.texter);
        lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);

        Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Regular.ttf");
        Typeface typeface1 = Typeface.createFromAsset(mContext.getAssets(), "fonts/Lato-Bold.ttf");
        //texter.setTypeface(typeface1);
        // texter.setLetterSpacing(0.5f);
        //textView.setLetterSpacing(0.5f);
        //System.out.println(texter.getLetterSpacing());
        // textView.setTypeface(typeface);

        //custom = (View) rootView.findViewById(R.id.v);
        customView = (CustomView) rootView.findViewById(R.id.v);

        mapView = (MapView) rootView.findViewById(R.id.mapview);
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

                mapboxMap.getUiSettings().setTiltGesturesEnabled(false);
                //mapboxMap.easeCamera(CameraUpdateFactory.newCameraPosition());
            }
        });

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 1, this);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                float startTime = SystemClock.uptimeMillis();
                while (true) {
                    endTime = SystemClock.uptimeMillis() - startTime;
                    if ((int)(endTime / 1000) >= 0) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (endTime / 1000 >= 0) {
                                    customView.setCreditActive(String.valueOf((int) (endTime / 1000)));
                                    //System.out.println(String.valueOf((int) (endTime / 1000)));
                                }

                            }
                        });
                    }

                }

            }
        };
        new Thread(r).start();
        // setTextLocation(custom);
        return rootView;
    }

    @Override
    public void addLines(final List<DrivePointPair> lines) {
        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(MapboxMap mapboxMap) {
                for (DrivePointPair pair : lines) {
                    if (pair.from == null)
                        continue;

                    if (pair.to == null)
                        continue;

                    int speed1 = pair.from.getSpeed();
                    int speed2 = pair.to.getSpeed();

                    // calculate average speed between two points
                    int averageSpeed = (speed1 + speed2) / 2;

                    final int color;

                    /*
                     * Determine color condition
                     */
                    if (averageSpeed >= 0 && averageSpeed < 25) {
                        color = Color.parseColor("#2ecc71");
                    } else if (averageSpeed >= 25 && averageSpeed <= 40) {
                        color = Color.parseColor("#e67e22");
                    } else {
                        color = Color.parseColor("#c0392b");
                    }
                    mapboxMap.addPolyline(new PolylineOptions()
                            .add(new LatLng(pair.from.getmLocation().getLatitude(), pair.to.getmLocation().getLongitude()))
                            .add(new LatLng(pair.to.getmLocation().getLatitude(), pair.from.getmLocation().getLongitude()))
                            .add(new LatLng(pair.from.getmLocation().getLatitude(), pair.to.getmLocation().getLongitude()))
                            .width(5)
                            .color(color));
                }
            }
        });
    }

    /*private void addLine(DrivePoint from, DrivePoint to) {



        Runnable r = new Runnable() {
            @Override
            public void run() {
                float startTime = SystemClock.uptimeMillis();
                while (true) {
                    endTime = SystemClock.uptimeMillis() - startTime;
                    if ((int)(endTime / 1000) >= 0) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (endTime / 1000 >= 0) {
                                    customView.setCreditActive(String.valueOf((int) (endTime / 1000)));
                                    //System.out.println(String.valueOf((int) (endTime / 1000)));
                                }

                            }
                        });
                    }

                }

            }
        };
        new Thread(r).start();
    }*/

    @Override
    public void onLocationChanged(final Location location) {

        //@ param 1 : time -> we still need to calculate it properly...default is 0 for now
        //@ last param: isHighway...still need google maps api for this to work
        final Location temp = location;

        addLine();

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (location.hasSpeed()) {
                    customView.setSpeedActive(String.valueOf((int) (temp.getSpeed() * 2.2369)));
                } else {
                    customView.setSpeedActive("0");
                }
                //if (location.hasSpeed())
                //texter.setText(String.valueOf((int) (temp.getSpeed() * 2.2369)) + " mph");
                // else
                //texter.setText("0 mph");
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

    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    public void setContext(Context m) {
        this.mContext = m;
    }

    public void setTextLocation(View v) {
        int[] coordinate = new int[2];
        v.getLocationOnScreen(coordinate);
        System.out.println("LALAAAL: " + v.getTop() + ", " + v.getLeft());
    }
}
