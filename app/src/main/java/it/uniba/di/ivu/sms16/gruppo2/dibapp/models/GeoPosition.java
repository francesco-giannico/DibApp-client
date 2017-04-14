package it.uniba.di.ivu.sms16.gruppo2.dibapp.models;


import java.io.Serializable;

public class GeoPosition implements Serializable {
    private static final long serialVersionUID = 1L;

    public double lat;
    public double lng;
    public int range;
    public String placeName;

    public GeoPosition() {

    }

    public GeoPosition(double lat, double lng, String placeName) {
        this.lat = lat;
        this.lng = lng;
        this.placeName = placeName;
    }

    public GeoPosition(double lat, double lng, int range, String placeName) {
        this.lat = lat;
        this.lng = lng;
        this.range = range;
        this.placeName = placeName;
    }
}