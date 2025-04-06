package com.example.proj.Algo;

public class Bin {
    public int ID;
    public double lat;
    public double lon;
    public int fillpercent;


    public Bin(int id, double lat, double lon , int fillpercent){
        ID = id;
        this.lat = lat;
        this.lon = lon;
        this.fillpercent = fillpercent;
    }

    public boolean isFull(){
        return fillpercent == 100.0;
    }

    public boolean isOver70(){
        return fillpercent > 70.0;
    }
}
