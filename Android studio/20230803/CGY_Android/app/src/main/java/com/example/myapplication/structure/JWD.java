package com.example.myapplication.structure;

public class JWD {
    public double longitude;
    public double latitude;
    public double orientation;

    public JWD(double longitude, double latitude, double orientation) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.orientation = orientation;
    }

    @Override
    public String toString() {
        return "JWD{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", orientation=" + orientation +
                '}';
    }
}
