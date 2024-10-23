package com.example.jerry.ufo;

/**
 * Created by Jaden on 10/16/2016.
 */

public class Facility {

    private Sport sport;
    private Building building;
    private Availability availability;
    private double rating;
    private String name;

    public Facility(Sport sport, Building building, Availability availability, String name, double rating) {
        this.sport = sport;
        this.building = building;
        this.availability = availability;
        this.name = name;
        this.rating = rating;
    }

    public Sport getSport() {
        return sport;
    }

    public Building getBuilding() {
        return building;
    }

    public String getName(){
        return name;
    }

    public Availability getAvailability() {
        return availability;
    }

    public void setAvailability(int avil) {
        this.availability=Availability.values()[avil];
    }

    public void setRating(double rating){
        this.rating = rating;
    }

    public double getRating() {
        return rating;
    }

//    public boolean equals(Facility f) {
//        if (this.name.equals(f.getName())){
//            return true;
//        }
//        return false;
//    }
}
