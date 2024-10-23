package com.example.jerry.ufo;

import android.content.Intent;
import android.os.Bundle;
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

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, com.example.jerry.ufo.View {

    Button findSport;
    Button showMap;
    Button showSubscribed;

    public String loadJSONFromAsset(String filename) {
        String json = null;
        try {
            InputStream is = this.getAssets().open(filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        Model.getInstance().buildnotification(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        findSport = (Button) findViewById(R.id.findsport);

        findSport.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this, SportsList.class);
                startActivity(myIntent);
            }
        });

        showMap = (Button) findViewById(R.id.showMap);

        showMap.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this, facilityMap.class);
                startActivity(myIntent);
            }
        });

        showSubscribed = (Button) findViewById(R.id.subscription);

        showSubscribed.setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                Intent myIntent = new Intent(MainActivity.this, FacilityList.class);
                myIntent.putExtra("type", 2);
                startActivity(myIntent);
            }
        });

        API api = new API(this);
        api.authenticate();

        Model.getInstance().mSocket.connect(); // open socket
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.findsport) {
            Intent myIntent = new Intent(MainActivity.this, SportsList.class);
            startActivity(myIntent);
        } else if (id == R.id.findbuilding) {
            Intent myIntent = new Intent(MainActivity.this, facilityMap.class);
            startActivity(myIntent);
        } else if (id == R.id.findsubcribed) {
            Intent myIntent = new Intent(MainActivity.this, FacilityList.class);
            myIntent.putExtra("type", 2);
            startActivity(myIntent);
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void update() {
        findSport.setClickable(true);
        showMap.setClickable(true);
        showSubscribed.setClickable(true);
    }
}
