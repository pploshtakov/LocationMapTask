package com.example.pesho.interviewtask.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Pesho on 11/5/2016.
 */

public class DBManager extends SQLiteOpenHelper {
    private static DBManager instance;
    private static ArrayList<Marker> markers;

    //DB version
    private static final int DB_VERSION = 1;
    //DB name
    private static final String DB_NAME = "markersDB";
    //table name
    private static final String TABLE_LOCATIONS = "locations";
    //column names
    private static final String KEY_ID = "id";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_COUNTRY = "country";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LONG = "long";
    private static final String KEY_IMAGE = "image";

    //table location create statement
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE "
            + TABLE_LOCATIONS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ADDRESS + " TEXT," + KEY_COUNTRY
            + " TEXT," + KEY_LAT + " REAL," + KEY_LONG + " REAL," + KEY_IMAGE + " BLOB" + ");";

    //drop table
    private static final String DROP_TABLE_LOCATIONS = "DROP TABLE IF EXISTS " + TABLE_LOCATIONS;

    private DBManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        markers = new ArrayList<Marker>();
        loadMarkers();
    }

    public static DBManager getInstance(Context context) {
        if (instance == null) {
            instance = new DBManager(context);
        }
        return instance;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCATIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_TABLE_LOCATIONS);
        this.onCreate(db);
    }
    //add new marker in db
    public int addMarker(Marker marker) {
        markers.add(marker);
        ContentValues values = new ContentValues();
        values.put(KEY_ID, markers.size());
        values.put(KEY_ADDRESS, marker.getAddress());
        values.put(KEY_COUNTRY, marker.getCountry());
        values.put(KEY_LAT, marker.getLatitude());
        values.put(KEY_LONG, marker.getLongitude());
        getWritableDatabase().insert(TABLE_LOCATIONS, null, values);
        return markers.size();
    }

    //add image
    public void addImageForMarker(Marker marker) {
        ContentValues values = new ContentValues();
        byte[] image = marker.getByteArrayOfImage();
        values.put(KEY_IMAGE, image);
        getWritableDatabase().update(TABLE_LOCATIONS, values, KEY_ID + "=" + marker.getMarkerID(), null);
    }

    //load markers from db
    public void loadMarkers() {
        Cursor cursor = getWritableDatabase().rawQuery("SELECT " + KEY_ID + ", " + KEY_ADDRESS + ", " + KEY_COUNTRY + ", " + KEY_LAT +
                ", " + KEY_LONG + ", " + KEY_IMAGE + " FROM " + TABLE_LOCATIONS, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
            String address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
            String  country= cursor.getString(cursor.getColumnIndex(KEY_COUNTRY));
            double lat = cursor.getDouble(cursor.getColumnIndex(KEY_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(KEY_LONG));
            byte[] byteArr = cursor.getBlob(cursor.getColumnIndex(KEY_IMAGE));
            Marker marker = new Marker(address, country, new LatLng(lat, lng));
            if (byteArr != null) {
                marker.setImageFromByteArray(byteArr);
            }
            marker.setMarkerID(id);
            markers.add(marker);
        }
        cursor.close();
    }

    public Marker getMarkerById(Integer markerID) {
        Marker marker = null;
        for (Marker mark : markers) {
            if (mark.getMarkerID() == markerID) {
                marker = mark;
            }
        }
        return marker;
    }

    //update marker info
    public void updateMarker(Marker marker) {
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS, marker.getAddress());
        values.put(KEY_COUNTRY, marker.getCountry());
        values.put(KEY_LAT, marker.getLatitude());
        values.put(KEY_LONG, marker.getLongitude());
        getWritableDatabase().update(TABLE_LOCATIONS, values, KEY_ID + "=" + marker.getMarkerID(), null);
    }
}
