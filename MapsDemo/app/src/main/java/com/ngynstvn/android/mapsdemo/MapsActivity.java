package com.ngynstvn.android.mapsdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback {

    private static final String TAG = "Test: " + MapsActivity.class.getSimpleName() + ": ";

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient googleApiClient;

    private Location location;
    private static double latitude;
    private static double longitude;
    private static float zoom;
    private static int counter = 0;

    private CameraPosition cameraPosition;

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;

    private NotificationManager notificationManager;
    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        geofenceList = new ArrayList<>();

        if(savedInstanceState != null) {
            latitude = savedInstanceState.getDouble("latitude");
            longitude = savedInstanceState.getDouble("longitude");
            zoom = savedInstanceState.getFloat("zoom");
            counter = savedInstanceState.getInt("counter");
            Log.v(TAG, "Retrieved: (" + latitude + ", " + longitude + ") | zoom =" + zoom);
        }

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        Log.e(TAG, "onResume() called");
        super.onResume();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom), 1000, null);
    }

    @Override
    protected void onPause() {
        Log.e(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.e(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);

        cameraPosition = mMap.getCameraPosition();
        latitude = cameraPosition.target.latitude;
        longitude = cameraPosition.target.longitude;
        zoom = cameraPosition.zoom;

        outState.putDouble("latitude", latitude);
        outState.putDouble("longitude", longitude);
        outState.putFloat("zoom", zoom);
        outState.putInt("counter", counter);

        Log.v(TAG, "Saved position: (" + latitude + ", " + longitude + ") zoom= " + zoom);
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy() called");
        super.onDestroy();
    }

    private void setUpMapIfNeeded() {
        Log.v(TAG, "setUpMapIfNeeded() called");
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }

        // Place this method here to ensure restoring state works properly

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    @Override
                    public void onCameraChange(CameraPosition cameraPosition) {
                        latitude = cameraPosition.target.latitude;
                        longitude = cameraPosition.target.longitude;
                        zoom = cameraPosition.zoom;

                        Log.v(TAG, "Current position: (" + latitude + ", " + longitude + ") | zoom = " + zoom);
                    }
                });
            }
        });

    }

    // Only animate the map if it is first time opening the app

    private void setUpMap(double latitude, double longitude, float zoom) {
        Log.v(TAG, "setUpMap() called");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // Add an address here
        addPOI("100 Awesome Street", "Cary", "NC", 400);

        LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
                getGeofencePendingIntent()).setResultCallback(this);

        if(counter <= 1) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom), 1000, null);
        }
        else {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), zoom));
        }
    }

    // Connect to Google Play Services

    protected synchronized void buildGoogleApiClient() {
        Log.v(TAG, "buildGoogleApiClient() called");
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        if (googleApiClient != null) {
            Log.v(TAG, "googleApiClient is connected");
            googleApiClient.connect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.v(TAG, "onConnected() called");

        counter++;

        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if(counter <= 1) {

            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                Log.v(TAG, "Your current location: (" + latitude + "," + longitude + ")");
                setUpMap(latitude, longitude, 14);
            }
            else {
                setUpMap(latitude, longitude, zoom);
            }
        } else if (counter > 1) {
            Log.v(TAG, "Your current location: (" + latitude + "," + longitude + ")");
            setUpMap(latitude, longitude, zoom);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "onConnectionSuspended() called");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "onConnectionFailed() called");
    }

    private void addPOI(String address, String city, String state, int radius) {
        // radius = geofence radius and is in units of meters

        Geocoder geocoder = new Geocoder(this);
        List<Address> list;
        Address addressObj;

        try {
            list = geocoder.getFromLocationName(address + " " + city + " " + state, 1);
            addressObj = list.get(0);

            latitude = addressObj.getLatitude();
            longitude = addressObj.getLongitude();
        } catch (Exception e) {
            Log.v(TAG, "Unable to parse address.");
        }

        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Dummy Location"));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(latitude, longitude))
                .radius(radius)
                .strokeColor(getResources().getColor(R.color.red_50))
                .strokeWidth(2.00f));

        if (geofenceList != null) {
            geofenceList.add(new Geofence.Builder()
                    .setRequestId(String.valueOf(1))
                    .setCircularRegion(latitude, longitude, radius)
                    .setExpirationDuration(Geofence.NEVER_EXPIRE)
                    .setLoiteringDelay(10000)
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_DWELL)
                    .build());
        }
    }

    /**
     * Geofencing Material
     */

    private GeofencingRequest getGeofencingRequest() {
        Log.v(TAG, "getGeofencingRequest() called");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Log.v(TAG, "getGeofencePendingIntent() called");
        // Reuse the PendingIntent if we already have it.

        if (geofencePendingIntent != null) {
            return geofencePendingIntent;
        }

        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Result result) {

    }

}
