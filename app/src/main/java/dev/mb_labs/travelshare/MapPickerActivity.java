package dev.mb_labs.travelshare;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapPickerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LatLng selectedLocation;
    private Marker currentMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_picker);

        double lat = getIntent().getDoubleExtra("LATITUDE", 0);
        double lng = getIntent().getDoubleExtra("LONGITUDE", 0);
        if (lat != 0 && lng != 0) {
            selectedLocation = new LatLng(lat, lng);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button btnConfirm = findViewById(R.id.btn_confirm_location);
        btnConfirm.setOnClickListener(v -> {
            if (selectedLocation != null) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("LATITUDE", selectedLocation.latitude);
                resultIntent.putExtra("LONGITUDE", selectedLocation.longitude);
                setResult(RESULT_OK, resultIntent);
                finish();
            } else {
                Toast.makeText(this, "Please tap on the map to select a location", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (selectedLocation != null) {
            placeMarker(selectedLocation);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15));
        } else {
            //default to France/Montpellier if no location set
            LatLng defaultLoc = new LatLng(46.22, 2.21);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLoc, 5));
        }

        mMap.setOnMapClickListener(latLng -> {
            selectedLocation = latLng;
            placeMarker(latLng);
        });
    }

    private void placeMarker(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title("Selected Location"));
    }
}