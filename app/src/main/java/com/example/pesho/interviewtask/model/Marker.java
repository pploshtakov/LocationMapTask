package com.example.pesho.interviewtask.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayOutputStream;

/**
 * Created by Pesho on 11/5/2016.
 */

public class Marker {
    private int markerID;
    private String address;
    private String country;
    private double latitude;
    private double longitude;
    private Bitmap attachment;

    public Marker(String address, String country, LatLng latLng) {
        this.address = address;
        this.country = country;
        this.latitude = latLng.latitude;
        this.longitude = latLng.longitude;
        //markerID = DBManager.getInstance(context).addMarker(this);
    }

    public void setMarkerID(int markerID) {
        this.markerID = markerID;
    }

    public String getAddress() {
        return address;
    }

    public String getCountry() {
        return country;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public Bitmap getAttachment() {
        return attachment;
    }

    public int getMarkerID() {
        return markerID;
    }

    public void setAttachment(Bitmap attachment) {
        this.attachment = attachment;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public byte[] getByteArrayOfImage() {
        return getBytes(this.attachment);
    }

    // convert from bitmap to byte array
    private static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public void setImageFromByteArray(byte[] arr) {
        this.attachment = getImage(arr);
    }

    // convert from byte array to bitmap
    private static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
