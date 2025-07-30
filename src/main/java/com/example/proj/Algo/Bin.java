package com.example.proj.Algo;

public class Bin {
    //Variables
    public int ID;
    public double lat;
    public double lon;
    public int fillpercent;

    //Constructor
    public Bin(int id, double lat, double lon , int fillpercent){
        ID = id;
        this.lat = lat;
        this.lon = lon;
        this.fillpercent = fillpercent;
    }

    //Getters
    public int getId() { return ID; }
    public double getLatitude() { return lat; }
    public double getLongitude() { return lon; }
    public int getFillLevel() { return fillpercent; }
}
