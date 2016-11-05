package com.example.pesho.interviewtask;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pesho.interviewtask.model.DBManager;
import com.example.pesho.interviewtask.model.Marker;

public class MarkerActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    TextView countryTV;
    EditText countryET;
    TextView addressTV;
    EditText addressET;
    TextView coordinatesTV;
    TextView markerIdTV;
    ImageView imageView;
    Marker marker;
    View.OnClickListener chData;
    View.OnLongClickListener longClickListener;
    LinearLayout buttonsLayout;
    Button saveButton;
    Button cancelButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
        final Intent intent = getIntent();
        if (intent.hasExtra("markerID")) {
            marker = DBManager.getInstance(MarkerActivity.this).getMarkerById(Integer.valueOf(intent.getStringExtra("markerID")));
        }
        countryTV = (TextView) findViewById(R.id.country_tv);
        countryET = (EditText) findViewById(R.id.country_et);
        addressTV = (TextView) findViewById(R.id.address_tv);
        addressET = (EditText) findViewById(R.id.address_et);
        coordinatesTV = (TextView) findViewById(R.id.coordinates_tv);
        markerIdTV = (TextView) findViewById(R.id.marker_id_tv);
        imageView = (ImageView) findViewById(R.id.image_view);
        buttonsLayout = (LinearLayout) findViewById(R.id.save_cancel_buttons);
        saveButton = (Button) findViewById(R.id.save_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);





        //OnClickListener for changing data
        chData = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MarkerActivity.this, "Please long click for changing data!", Toast.LENGTH_SHORT).show();
            }
        };
        countryTV.setOnClickListener(chData);
        addressTV.setOnClickListener(chData);
        coordinatesTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MarkerActivity.this, "For changing location, please back to map and move marker!", Toast.LENGTH_SHORT).show();
            }
        });

        //long click listener
        longClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                countryTV.setVisibility(View.GONE);
                countryET.setVisibility(View.VISIBLE);
                countryET.setText(marker.getCountry());

                addressTV.setVisibility(View.GONE);
                addressET.setVisibility(View.VISIBLE);
                addressET.setText(marker.getAddress());

                buttonsLayout.setVisibility(View.VISIBLE);
                return false;
            }
        };

        countryTV.setOnLongClickListener(longClickListener);
        addressTV.setOnLongClickListener(longClickListener);

        //changing data save or cancel changes
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryTV.setVisibility(View.VISIBLE);
                countryET.setVisibility(View.GONE);
                countryTV.setText(countryET.getText().toString());
                marker.setCountry(countryET.getText().toString());

                addressTV.setVisibility(View.VISIBLE);
                addressET.setVisibility(View.GONE);
                addressTV.setText(addressET.getText().toString());
                marker.setAddress(addressET.getText().toString());

                buttonsLayout.setVisibility(View.GONE);

                DBManager.getInstance(MarkerActivity.this).updateMarker(marker);
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countryTV.setVisibility(View.VISIBLE);
                countryET.setVisibility(View.GONE);


                addressTV.setVisibility(View.VISIBLE);
                addressET.setVisibility(View.GONE);


                buttonsLayout.setVisibility(View.GONE);
            }
        });

        if (marker != null) {
            countryTV.setText(marker.getCountry());
            addressTV.setText(marker.getAddress());
            coordinatesTV.setText("lat:\n " + marker.getLatitude() + "\n,long: \n" + marker.getLongitude());
            markerIdTV.setText(intent.getStringExtra("markerID"));
        }

        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (intent2.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent2, REQUEST_IMAGE_CAPTURE);
                }
                return false;
            }
        });

        if (marker != null && marker.getAttachment() != null) {
            imageView.setImageBitmap(marker.getAttachment());
            imageView.setOnClickListener(chData);


        } else {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent1 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent1.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent1, REQUEST_IMAGE_CAPTURE);
                    }
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            marker.setAttachment(imageBitmap);
            DBManager.getInstance(MarkerActivity.this).addImageForMarker(marker);
            imageView.setImageBitmap(imageBitmap);
            imageView.setOnClickListener(chData);
        }
    }
}
