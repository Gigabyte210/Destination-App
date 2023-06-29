package com.tseopela.destination;

public class LocationClass {
    double lat;
    double lon;
    String address;

    public LocationClass(double lat, double lon, String address) {
        this.lat = lat;
        this.lon = lon;
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public String getAddress() {
        return address;
    }
}