package com.example.jerry.ufo;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by charles on 17/11/16.
 */

import com.loopj.android.http.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.protocol.RequestDefaultHeaders;
import cz.msebera.android.httpclient.message.BufferedHeader;

import static android.content.Context.ACCOUNT_SERVICE;
import static android.content.Context.MODE_APPEND;


// write methods to query backend api and populate frontend models
public class API {
    public Context context;
    private String gmail="test@gmail.com";

    public API(Context context) {
        this.context = context;
        AccountManager manager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {}
        Account[] list = manager.getAccountsByType("com.google");

        for(Account account: list)
        {
            if(account.type.equalsIgnoreCase("com.google"))
            {
                gmail= account.name;
                break;
            }
        }
    }

    public void connectionFailure() {
        final AlertDialog alert;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Connection Failure, server could not be reached.\nYour data may be out of date")
                .setCancelable(false)
                .setNeutralButton("OK", new DialogInterface.OnClickListener() { // define the 'Cancel' button
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        alert = builder.create();
        alert.show();
    }

    public void authenticate() {

        RequestParams params = new RequestParams();
        params.put("google_account", gmail);
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String sessionHeader =((BufferedHeader) headers[6]).getBuffer().toString();
                int startIndex = 12;
                int endIndex = sessionHeader.indexOf(";");

                String sessionToken = sessionHeader.substring(12, endIndex);
                APIRestClient.setSession(sessionToken);
                loadSports();
                Log.v("a","success-------------------");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                connectionFailure();
                Log.v("a","failed-------------------");
            }
        };
        Log.v("a","about to run api-------------------");
        APIRestClient.post("login", params, handler);
    }


    // TODO: <url>:<port>/sports
    public void loadSports() {
        final List<Sport> sportsList = new ArrayList<>();
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String body = new String(responseBody, "UTF-8");
                    JSONArray SportsArray = new JSONArray(body);
                    for (int i = 0; i < SportsArray.length(); i++) {
                        String name = SportsArray.getString(i);
                        Sport sport = new Sport(name);
                        sportsList.add(sport);
                    }
                    Model.getInstance().setSports(sportsList);
                    loadBuildings();
                    Log.v("a", "success-------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                connectionFailure();
                Log.v("a","failed-------------------");
            }
        };

        APIRestClient.get("sports", params, handler);
    }

    // TODO: <url>:<port>/buildings
    public void loadBuildings() {
        final List<Building> buildingList = new ArrayList<>();
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String body = new String(responseBody, "UTF-8");
                    JSONArray BuildingArray = new JSONArray(body);
                    for (int i = 0; i < BuildingArray.length(); i++) {
                        JSONObject buildingJson = BuildingArray.getJSONObject(i);
                        String name = buildingJson.getString("name");
                        double lat = buildingJson.optDouble("lat", 0.0);
                        double lng = buildingJson.optDouble("lng", 0.0);
                        Building building = new Building(name, lat, lng);
                        buildingList.add(building);
                    }
                    Model.getInstance().setBuildings(buildingList);
                    loadSubscriptions();
                    Log.v("a", "success-------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                connectionFailure();
                Log.v("a","failed-------------------");
            }
        };
        APIRestClient.get("buildings", params, handler);
    }

    // TODO: <url>:<port>/facility/sport/<sportName>
    public void loadFacilitiesBySport(String sportName) {
        final List<Facility> facilitiesList = new ArrayList<>();
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String body = new String(responseBody, "UTF-8");
                    JSONArray FacilitiesArray = new JSONArray(body);
                    for (int i = 0; i < FacilitiesArray.length(); i++) {
                        JSONObject facilityItem = FacilitiesArray.getJSONObject(i);
                        Sport sport = Model.getInstance().findSportByName(facilityItem.getString("sport"));
                        Building building = Model.getInstance().findBuildingByName(facilityItem.getString("building"));
                        int availability = facilityItem.optInt("availability",5);
                        String name =  facilityItem.getString("name");
                        int rating = facilityItem.optInt("rating", 0);

                        Facility facility = new Facility(sport, building, Availability.values()[availability], name, rating);
                        facilitiesList.add(facility);
                    }
                    Model.getInstance().setFacilities(facilitiesList);
                    Log.v("a", "success-------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                connectionFailure();
                Log.v("a","failed-------------------");
            }
        };

        APIRestClient.get("facility/sport/"+sportName, params, handler);
    }


    public void loadSubscriptions(){
        final List<Facility> facilitiesList = new ArrayList<>();
        Model.getInstance().setSubscriptions(facilitiesList);
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String body = new String(responseBody, "UTF-8");
                    JSONArray FacilitiesArray = new JSONArray(body);
                    for (int i = 0; i < FacilitiesArray.length(); i++) {
                        JSONObject facilityItem = FacilitiesArray.getJSONObject(i);
                        Sport sport = Model.getInstance().findSportByName(facilityItem.getString("sport"));
                        Building building = Model.getInstance().findBuildingByName(facilityItem.getString("building"));
                        int availability = facilityItem.optInt("availability",5);
                        String name =  facilityItem.getString("name");
                        int rating = facilityItem.optInt("rating", 0);

                        Facility facility = new Facility(sport, building, Availability.values()[availability], name, rating);
                        facilitiesList.add(facility);
                        subscribe(facility,false);

                    }
                    Model.getInstance().setSubscriptions(facilitiesList);
                    Log.v("a", "success-------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Model.getInstance().setSubscriptions(new ArrayList<Facility>());
                connectionFailure();
                Log.v("a","failed-------------------");
            }
        };

        APIRestClient.get("getSubscribed", params, handler);
    }

    // TODO: <url>:<port>/facility/building/<buildingName>
    public void loadFacilitiesByBuilding(String buildingName) {
        final List<Facility> facilitiesList = new ArrayList<>();
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String body = new String(responseBody, "UTF-8");
                    JSONArray FacilitiesArray = new JSONArray(body);
                    for (int i = 0; i < FacilitiesArray.length(); i++) {
                        JSONObject facilityItem = FacilitiesArray.getJSONObject(i);
                        Sport sport = Model.getInstance().findSportByName(facilityItem.getString("sport"));
                        Building building = Model.getInstance().findBuildingByName(facilityItem.getString("building"));
                        int availability = facilityItem.getInt("availability");
                        String name = facilityItem.getString("name");
                        double rating = facilityItem.getDouble("rating");
                        Facility facility = new Facility(sport, building, Availability.values()[availability], name, rating);
                        facilitiesList.add(facility);
                    }
                    Model.getInstance().setFacilities(facilitiesList);
                    Log.v("a", "success-------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Model.getInstance().setFacilities(facilitiesList);
                connectionFailure();
                Log.v("a", "failed-------------------");
            }
        };
        APIRestClient.get("facility/building/"+ buildingName, params, handler);
    }
        // TODO: <url>:<port>/facility/building/<buildingName>
    public void loadBuildingFromFacility(String facilityName) {
        final Building building;
        RequestParams params = new RequestParams();
        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String body = new String(responseBody, "UTF-8");
                    JSONObject BuildingsObject = new JSONObject(body);
                    String buildingName = BuildingsObject.getString("name");
                    double lat = BuildingsObject.getDouble("lat");
                    double lng = BuildingsObject.getDouble("lng");
                    Building building = new Building(buildingName, lat, lng);
                    Model.getInstance().setCurBuilding(building);
                    Log.v("a", "success-------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                connectionFailure();
                Log.v("a","failed-------------------");
            }
        };

        APIRestClient.get("building/facility/"+ facilityName, params, handler);
    }

    public void subscribe(Facility facility, boolean add) {
        Model.getInstance().mSocket.emit("subscribe", "{\"username\": \""+gmail+"\", \"facility\": \""+facility.getName()+"\"}");
        if (add)
            Model.getInstance().getSubscriptionList().add(facility);
    }


    public void unsubscribe(String facilityName) {
        Model.getInstance().mSocket.emit("unsubscribe", "{\"username\": \""+gmail+"\", \"facility\": \""+facilityName+"\"}");
        for (Facility f: Model.getInstance().getSubscriptionList()){
            if (f.getName().equals(facilityName)){
                Model.getInstance().getSubscriptionList().remove(f);
                return;
            }
        }
    }

    public void rate(double rate, final String facilityname) {
        RequestParams params = new RequestParams();
        params.put("rating",rate);
        params.put("facility",facilityname);

        AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String body = new String(responseBody, "UTF-8");
                    JSONObject Jo = new JSONObject(body);
                    Double rating = Jo.getDouble("newRating");
                    Model.getInstance().changeFacilityRating(facilityname,rating);
                    Log.v("a", "success-------------------");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println(statusCode);
                connectionFailure();
                Log.v("a","failed-------------------");
            }
        };
        APIRestClient.post("facility/addRating/", params, handler);
    }
}


