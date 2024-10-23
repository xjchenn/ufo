package com.example.jerry.ufo;

/**
 * Created by charles on 19/11/16.
 */

public class Building {
    private String buildingName;
    private double lat;
    private double lng;

    public Building(String buildingName, double lat, double lng) {
        this.buildingName = buildingName;
        this.lat = lat;
        this.lng = lng;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() {
        return lng;
    }
    public String getName() {
        return buildingName;
    }
}
