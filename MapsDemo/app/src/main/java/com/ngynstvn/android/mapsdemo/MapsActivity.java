package com.ngynstvn.android.mapsdemo;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
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
    private double latitude;
    private double longitude;
    private static int counter = 0;

    private List<Geofence> geofenceList;
    private PendingIntent geofencePendingIntent;

    private NotificationManager notificationManager;
    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        buildGoogleApiClient();
        geofenceList = new ArrayList<>();
        setUpMapIfNeeded();


    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume() called");
        super.onResume();
        counter++;
        Log.v(TAG, "onResume() call counter: " + counter);
        if (mMap != null && counter == 2) {
            setUpMap();

//            LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
//                    getGeofencePendingIntent()).setResultCallback(this);

        } else if (mMap != null && counter > 2) {
            reSetUpMap();

//            LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
//                    getGeofencePendingIntent()).setResultCallback(this);
        }
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause() called");
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState() called");
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        Log.v(TAG, "onDestroy() called");
        super.onDestroy();

        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    private void setUpMapIfNeeded() {
        Log.v(TAG, "setUpMapIfNeeded() called");
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
        }
    }

    // Only animate the map if it is first time opening the app

    private void setUpMap() {
        Log.v(TAG, "setUpMap() called");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
        mMap.addMarker(new MarkerOptions().position(new LatLng(34.144447, -118.257028)).title("Marker"));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(34.144447, -118.257028))
                .radius(400)
                .fillColor(getResources().getColor(R.color.red_50))
                .strokeColor(getResources().getColor(R.color.red_50))
                .strokeWidth(0.00f));

        geofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(1))
                .setCircularRegion(34.144447, -118.257028, 400)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        mMap.addMarker(new MarkerOptions().position(new LatLng(34.159260, -118.255139)).title("Marker"));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(34.159260, -118.255139))
                .radius(400)
                .fillColor(getResources().getColor(R.color.red_50))
                .strokeColor(getResources().getColor(R.color.red_50))
                .strokeWidth(0.00f));

        geofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(1))
                .setCircularRegion(34.159260, -118.255139, 400)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
                getGeofencePendingIntent()).setResultCallback(this);
    }

    // Method to disable animation if the map has already been set up first time

    private void reSetUpMap() {
        Log.v(TAG, "reSetUpMap() called");
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 14));
        mMap.addMarker(new MarkerOptions().position(new LatLng(34.144447, -118.257028)).title("Marker"));
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        mMap.addCircle(new CircleOptions()
                .center(new LatLng(34.144447, -118.257028))
                .radius(400)
                .fillColor(getResources().getColor(R.color.red_50))
                .strokeColor(getResources().getColor(R.color.red_50))
                .strokeWidth(0.00f));

        geofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(1))
                .setCircularRegion(34.144447, -118.257028, 400)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        mMap.addMarker(new MarkerOptions().position(new LatLng(34.159260, -118.255139)).title("Marker"));
        mMap.addCircle(new CircleOptions()
                .center(new LatLng(34.159260, -118.255139))
                .radius(400)
                .fillColor(getResources().getColor(R.color.red_50))
                .strokeColor(getResources().getColor(R.color.red_50))
                .strokeWidth(0.00f));

        geofenceList.add(new Geofence.Builder()
                .setRequestId(String.valueOf(2))
                .setCircularRegion(34.159260, -118.255139, 400)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                        Geofence.GEOFENCE_TRANSITION_EXIT)
                .build());

        LocationServices.GeofencingApi.addGeofences(googleApiClient, getGeofencingRequest(),
                getGeofencePendingIntent()).setResultCallback(this);
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
        location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            Log.v(TAG, "Your current location: (" + latitude + "," + longitude + ")");
        } else {
            Log.v(TAG, "Location is null. Unable to get location");
        }

        onResume();

    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.v(TAG, "onConnectionSuspended() called");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.v(TAG, "onConnectionFailed() called");
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

        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onResult(Result result) {

    }

}
