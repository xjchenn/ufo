package com.example.jerry.ufo;

import java.util.Comparator;

/**
 * Created by Jerry on 2016-11-23.
 */

public class FacilityComparator implements Comparator<Facility>{
    @Override
    public int compare(Facility f1, Facility f2) {
        return f2.getAvailability().ordinal() - f1.getAvailability().ordinal();
    }
}
