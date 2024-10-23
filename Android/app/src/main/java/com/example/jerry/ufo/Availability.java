package com.example.jerry.ufo;

/**
 * Created by Jaden on 10/16/2016.
 */

public enum Availability {
    CLOSED("Closed"),
    LONG_WAITING("Long Wait"),
    FULL("Full"),
    ALMOST_FULL("Almost Full"),
    AVAILABLE("Available"),
    EMPTY("Empty");

    private String text;

    Availability(String text) {
        this.text = text;
    }

    public String text() {
        return text;
    }
}
