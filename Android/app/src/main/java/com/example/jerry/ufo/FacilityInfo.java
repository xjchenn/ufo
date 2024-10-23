package com.example.jerry.ufo;

import android.Manifest;
import android.content.Context;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class FacilityInfo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, com.example.jerry.ufo.View {

    Building curBuilding;
    Button subscribe;
    Facility curFacility;
    RatingBar ratingbar;
    TextView availability;
    API api;
    private LocationManager locationManager;
    private LocationListener listener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facility_info);






        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        api = new API(FacilityInfo.this);
        
        final String facilityname= getIntent().getStringExtra("curfacility");
        final String buildingname = getIntent().getStringExtra("curbuilding");
        boolean isSubscribed = getIntent().getBooleanExtra("isSubscribed", false);

        curFacility = Model.getInstance().findFacilityByName(facilityname);
        curBuilding = Model.getInstance().findBuildingByBuildingName(buildingname); // to call map

        Model.getInstance().setView(this);
        toolbar.setTitle(facilityname);
        setSupportActionBar(toolbar);

        ImageView facilitypicture = (ImageView) findViewById(R.id.image);
        int id = getResources().getIdentifier(curFacility.getSport().getName().replaceAll(" ",""), "drawable", getPackageName());
        facilitypicture.setImageResource(id);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Model.getInstance().userLocation = new Location("");
        Model.getInstance().userLocation.setLatitude(Model.getInstance().CENTER_LAT);
        Model.getInstance().userLocation.setLongitude(Model.getInstance().CENTRE_LNG);

        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.v("a","------------Got new location");
                Model.getInstance().userLocation = location;
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.v("a","------------Got new location");
            }

            @Override
            public void onProviderEnabled(String provider) {
                Log.v("a","------------Got new location");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.v("a","------------Got new location");
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.v("e","check failed");
            //return;
        }
        else {
            Log.v("a", "-------trying to query location manager");
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
        }
        subscribe = (Button) findViewById(R.id.subscribebutton);
        subscribe.setText(isSubscribed ? "Unsubscribe" : "Subscribe");
        subscribe.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                if(subscribe.getText().equals("Subscribe")) {
                    api.subscribe(Model.getInstance().findFacilityByName(facilityname),true);
                    subscribe.setText("Unsubscribe");
                } else {
                    api.unsubscribe(facilityname);
                    subscribe.setText("Subscribe");
                }
            }
        });

        availability = (TextView) findViewById(R.id.AvailabilityTextView);
        availability.setText(curFacility.getAvailability().text());

        availability.setTextColor(Color.rgb((int)Math.round(255*((5-curFacility.getAvailability().ordinal())/5.0)),(int)Math.round(255*(curFacility.getAvailability().ordinal()/5.0)),0));


        ratingbar = (RatingBar) findViewById(R.id.ratingBar);
        ratingbar.setIsIndicator(false);
        ratingbar.setRating((float) curFacility.getRating());
        ratingbar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b){
                    api.rate(v,facilityname);
                    ratingbar.setIsIndicator(true);//user can only vote once per opening this page
                }
            }
        });

        Button directions = (Button) findViewById(R.id.directionbutton);
        directions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                callGoogleMaps();
            }
        });
        update();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.findsport) {
            Intent myIntent = new Intent(FacilityInfo.this, SportsList.class);
            startActivity(myIntent);
        } else if (id == R.id.findbuilding) {
            Intent myIntent = new Intent(FacilityInfo.this, facilityMap.class);
            startActivity(myIntent);
        } else if (id == R.id.findsubcribed) {
            Intent myIntent = new Intent(FacilityInfo.this, FacilityList.class);
            myIntent.putExtra("type", 2);
            startActivity(myIntent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void callGoogleMaps() {
        String currentLat = Double.toString(Model.getInstance().userLocation.getLatitude());
        String currentLng = Double.toString(Model.getInstance().userLocation.getLongitude());
        String targetLat = Double.toString(curBuilding.getLat());
        String targetLng = Double.toString(curBuilding.getLng());
        String url = "http://maps.google.com/maps?saddr="+currentLat+","+
                currentLng+"&daddr="+targetLat+","+targetLng;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
        startActivity(intent);
    }

    @Override
    public void update() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // This code will always run on the UI thread, therefore is safe to modify UI elements.
                availability.setText(curFacility.getAvailability().text());
                ratingbar.setRating((float) curFacility.getRating());
            }
        });
    }
}
