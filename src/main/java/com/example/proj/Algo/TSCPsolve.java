package com.example.proj.Algo;

import java.util.ArrayList;
import java.util.List;

public class TSCPsolve {

    public final int MAXCAPACITY = 300;
    Bin Startpoint;
    List<Bin> fullbins = new ArrayList<>();
    List<Bin> over70bins = new ArrayList<>();
    List<Bin> otherbins = new ArrayList<>();
    List<Bin> temproute = new ArrayList<>();
    Truck truck;
    Route route;

    public TSCPsolve(List<Bin> bins, Bin start){
        for (Bin bin : bins){
            if (bin.fillpercent == 100){fullbins.add(bin);}
            else if (bin.fillpercent > 70){over70bins.add(bin);}
            else if (bin.fillpercent > 0){otherbins.add(bin);}
        }
        truck = new Truck(MAXCAPACITY);
        route = new Route();
        Startpoint = start;
    }

    public Bin NearestNeighbour(List<Bin> bins,Bin current){
        double mindistance = 999999999.9;
        Bin nearestbin = null;
        for (Bin bin : bins){
            double curdistance = route.distance(current, bin);
            if (curdistance < mindistance){
                mindistance = curdistance;
                nearestbin = bin;
            }
        }
        return nearestbin;
    }

    public Route Solve(){
        temproute.add(Startpoint);
        while (!fullbins.isEmpty()){
            Bin curr = NearestNeighbour(fullbins,temproute.getLast());
            if (truck.canLoad(curr)){
                truck.load(curr);
                temproute.add(curr);
            }
            fullbins.remove(curr);
        }
        while (!over70bins.isEmpty()){
            Bin curr = NearestNeighbour(over70bins,temproute.getLast());
            if (truck.canLoad(curr)){
                truck.load(curr);
                temproute.add(curr);
            }
            over70bins.remove(curr);
        }
        while (!otherbins.isEmpty()){
            Bin curr = NearestNeighbour(otherbins,temproute.getLast());
            if (truck.canLoad(curr)){
                truck.load(curr);
                temproute.add(curr);
            }
            otherbins.remove(curr);
        }

        route.addBin(Startpoint);
        temproute.remove(Startpoint);
        while (!temproute.isEmpty()){
            Bin curr = NearestNeighbour(temproute,route.bins.getLast());
            temproute.remove(curr);
            route.addBin(curr);
        }
        route.addBin(Startpoint);
        return route;
    }
}
