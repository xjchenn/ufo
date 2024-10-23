package com.example.jerry.ufo;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class facilityMap extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_map);
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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Model.getInstance().CENTER_LAT,
                Model.getInstance().CENTRE_LNG),15));
        update();

    }
    /** Called when the user clicks a marker. */
    @Override
    public boolean onMarkerClick(final Marker marker) {
        // Retrieve the data from the marker.
        String title = marker.getTitle();
        Intent queryFacilities = new Intent(facilityMap.this, FacilityList.class);
        queryFacilities.putExtra("query",  title);
        queryFacilities.putExtra("type", 1);
        startActivity(queryFacilities);
        return false;
    }

    @Override
    public void update() {
        // Add a marker in UWP and move the camera
        List<Building> buildingList = Model.getInstance().getBuilding();
        for(Building building: buildingList) {
            LatLng latLng = new LatLng(building.getLat(), building.getLng());
            Marker mUwp = mMap.addMarker(new MarkerOptions().position(latLng).title(building.getName()));
        }
        mMap.setOnMarkerClickListener(this);
    }
}
