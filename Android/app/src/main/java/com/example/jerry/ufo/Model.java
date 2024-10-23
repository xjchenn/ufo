package com.example.jerry.ufo;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Display;

import android.support.v7.app.AppCompatActivity;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Jaden on 10/15/2016.
 */
public class Model {
    private static Model instance = new Model();

    NotificationCompat.Builder notification;
    NotificationManager nm;

    public static Model getInstance() {
        return instance;
    }
    public static final String BASE_URL = "http://34.192.78.219";
    public static final String PORT = "8080";
    public static final double CENTER_LAT = 43.471667;
    public static final double CENTRE_LNG = -80.541944;
    private View view;
    private int mode;
    private List<Sport> sports;
    private List<Facility> facilities;
    private List<Building> buildings;
    private List<Facility> subscriptionList;
    private Building curBuilding;
    public Location userLocation;


    public void setMode(int i){
        mode = i;
    }

    public void setView(View view) {
        this.view = view;
    }

    private void notifyView() {
        if (view!=null) {
            view.update();
        }
    }
    public List<Sport> getSports() {
        return sports;
    }
    public List<Building> getBuilding() { return buildings; }

    public void setSports(List<Sport> sports) {
        this.sports = sports;
    }

    public void setBuildings(List<Building> buildings) {
        this.buildings = buildings;
    }

    public List<Facility> getFacilities() {
        if(facilities != null) {
            Collections.sort(facilities, new FacilityComparator());
        }

        return facilities;
    }

    public List<Facility> getSubscriptionList() {
        Collections.sort(subscriptionList, new FacilityComparator());
        return subscriptionList;
    }

    // consider separate models for facility and sport

    public void setFacilities(List<Facility> facilities) {
        this.facilities = facilities;
        notifyView();
    }

    public Sport findSportByName(String name) {
        for(Sport s : sports) {
            if(s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    public Building findBuildingByName(String name) {
        for(Building b : buildings) {
            if(b.getName().equals(name)) {
                return b;
            }
        }
        return null;
    }

    public Facility findFacilityByName(String name){
        if (mode ==2){
            for (Facility f: subscriptionList){
                if (f.getName().equals(name)){
                    return f;
                }
            }
            return null;
        }
        for (Facility f: facilities){
            if (f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }

    public Facility findSubscription(String name){
        for (Facility f: subscriptionList){
            if (f.getName().equals(name)){
                return f;
            }
        }
        return null;
    }

    public void changeFacilityRating(String fname, double rating){
        findFacilityByName(fname).setRating(rating);
        notifyView();
    }

    public void setSubscriptions (List<Facility> list){
        this.subscriptionList = list;
    }
    /*public Building findBuildingByName(String name) {
        for(Building b : buildings) {
            if(b.getName().equals(name)) {
                return b;
            }
        }
        Building newBuilding = new Building(name, 0.0, 0.0);
        buildings.add(newBuilding);
        return newBuilding;
    }

    public Building createBuilding(String name, double lat, double lng) {
        Building result = findBuildingByName(name);
        if (result == null) {
            result = new Building(name, lat, lng);
            buildings.add(result);
        }
        return result;
    }*/

    public Building getCurBuilding() {
        return curBuilding;
    }

    public void setCurBuilding(Building curBuilding) {
        this.curBuilding = curBuilding;
        notifyView();
    }

    public List<Facility> findFacilitiesBySport(Sport sport) {
        List<Facility> result = new ArrayList<Facility>();
        for(Facility f : facilities) {
            if(f.getSport() == sport) {
                result.add(f);
            }
        }

        return result;
    }
    public List<Facility> findFacilitiesBySportName(String sport) {
        List<Facility> result = new ArrayList<Facility>();
        for(Facility f : facilities) {
            if(f.getSport().getName().equals(sport)) {
                result.add(f);
            }
        }

        return result;
    }

    public List<Facility> findFacilitiesByBuildingName(String buildingName) {
        List<Facility> result = new ArrayList<Facility>();
        for(Facility f : facilities) {
            if(f.getBuilding().equals(buildingName)) {
                result.add(f);
            }
        }

        return result;
    }

    public Building findBuildingByBuildingName(String buildingName) {
        for(Building building: buildings) {
            if (building.getName().equals(buildingName)) {
                return building;
            }
        }
        return null;
    }

    public boolean isSubscribed(Facility facility) {
        for (Facility f : subscriptionList) {
            if(f.getName().equals(facility.getName())) {
                return true;
            }
        }
        return false;
    }

    public boolean isSubscribed(String facilityName){
        for (Facility f : subscriptionList) {
            if(f.getName().equals(facilityName)) {
                return true;
            }
        }
        return false;
    }

    public void buildnotification(Context mainContext){
        notification = new NotificationCompat.Builder(mainContext);
        notification.setAutoCancel(true);
        notification.setSmallIcon(R.drawable.cast_ic_notification_on);
        notification.setTicker("Facility is now available");
        notification.setContentTitle("Facility available");
        notification.setContentText("Click to ride UFO");
        notification.setPriority(Notification.PRIORITY_HIGH);
        notification.setDefaults(Notification.DEFAULT_ALL);

        Intent intent = new Intent(mainContext, MainActivity.class);
        PendingIntent pintent = PendingIntent.getActivity(mainContext, 0 , intent, PendingIntent.FLAG_UPDATE_CURRENT);
        notification.setContentIntent(pintent);

        nm = (NotificationManager) mainContext.getSystemService(NOTIFICATION_SERVICE);
    }
    public Socket mSocket;
    {
        try {
            mSocket = IO.socket(BASE_URL + ":" + PORT);
            mSocket.on("notify", new Emitter.Listener(){
                @Override
                public void call (Object ... args){
                    JSONObject obj = (JSONObject) args[0];
                    String facilityName="Facility";
                    try {
                        facilityName = obj.getString("name");
                        Facility facilityOnPage = Model.getInstance().findSubscription(facilityName);
                        Facility facilitySub = Model.getInstance().findFacilityByName(facilityName);
                        if (facilityOnPage!=null) {
                            facilityOnPage.setAvailability(obj.getInt("availability"));
                        }
                        if (facilitySub!=null) {
                            facilitySub.setAvailability(obj.getInt("availability"));
                        }
                        try {
                            notifyView();
                        }catch (Exception e){
                            Log.v("v", e.getStackTrace().toString());
                        }
                        if (isSubscribed(facilityName) && obj.getInt("availability")>=3) {
                            notification.setContentTitle(facilityName+" available");
                            notification.setWhen(System.currentTimeMillis());
                            nm.notify(12345, notification.build());
                        }
                    }catch (JSONException e){
                        Log.v("v","JSON CAN'T FIND value");
                    }

                }
            } );
        } catch (Exception e) {
            Log.v("v", e.getStackTrace().toString());
        }
    }

    private Model() {
    }
}
