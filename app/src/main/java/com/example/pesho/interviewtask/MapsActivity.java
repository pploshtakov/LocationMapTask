package com.example.pesho.interviewtask;

import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;


import com.example.pesho.interviewtask.model.DBManager;
import com.google.android.gms.identity.intents.Address;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {


    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        DBManager.getInstance(MapsActivity.this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        ArrayList<com.example.pesho.interviewtask.model.Marker> markers = DBManager.getInstance(MapsActivity.this).getMarkers();
        if (markers.size() > 0) {
            MarkerOptions markerOptions = new MarkerOptions();
            for (com.example.pesho.interviewtask.model.Marker marker : markers) {
                markerOptions.position(new LatLng(marker.getLatitude(), marker.getLongitude()));
                markerOptions.title(String.valueOf(marker.getMarkerID()));
                mMap.addMarker(markerOptions).setDraggable(true);
            }
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                String country = getCountryName(MapsActivity.this, latLng.latitude, latLng.longitude);
                com.example.pesho.interviewtask.model.Marker marker
                        = new com.example.pesho.interviewtask.model.Marker(
                        getAddress(MapsActivity.this, latLng.latitude, latLng.longitude), country, latLng );
                int id = DBManager.getInstance(MapsActivity.this).addMarker(marker);
                marker.setMarkerID(id);
                markerOptions.title(String.valueOf(id));
                mMap.addMarker(markerOptions).setDraggable(true);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Intent intent = new Intent(MapsActivity.this, MarkerActivity.class);
                intent.putExtra("markerID", marker.getTitle());
                startActivity(intent);
                return false;
            }
        });



        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            LatLng previousPosition;
            com.example.pesho.interviewtask.model.Marker myMarker;
            @Override
            public void onMarkerDragStart(Marker marker) {
                previousPosition = marker.getPosition();

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                myMarker = DBManager.getInstance(MapsActivity.this).getMarkerById(Integer.valueOf(marker.getTitle()));
                myMarker.setLatitude(marker.getPosition().latitude);
                myMarker.setLongitude(marker.getPosition().longitude);
                myMarker.setCountry(getCountryName(MapsActivity.this, myMarker.getLatitude(), myMarker.getLongitude()));
                myMarker.setAddress(getAddress(MapsActivity.this,myMarker.getLatitude(), myMarker.getLongitude() ));
                DBManager.getInstance(MapsActivity.this).updateMarker(myMarker);
            }
        });
    }

    public static String getCountryName(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<android.location.Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getCountryName();
            }
            return null;
        } catch (IOException ignored) {
            //do something
        }
        return null;
    }

    public static String getAddress(Context context, double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<android.location.Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address result;

            if (addresses != null && !addresses.isEmpty()) {
                return addresses.get(0).getFeatureName() + ", " + addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea();
            }
            return null;
        } catch (IOException ignored) {
            //do something
        }
        return null;
    }


}
