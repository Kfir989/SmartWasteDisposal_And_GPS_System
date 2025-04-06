package com.example.proj.Algo;

import java.util.ArrayList;
import java.util.List;

public class Route {
    public List<Bin> bins = new ArrayList<>();


    public void addBin(Bin bin){
        bins.add(bin);
    }
    public double calculatetotaldistance(){
        double totaldistance = 0.0;
        for (int i = 0; i < bins.size() - 1; i++){
            totaldistance += distance(bins.get(i),bins.get(i+1));
        }
        return totaldistance;
    }
    public double distance(Bin bin1, Bin bin2){
        return Math.sqrt(Math.pow(bin2.lon - bin1.lon,2) + Math.pow(bin2.lat- bin1.lat,2));
    }

}
