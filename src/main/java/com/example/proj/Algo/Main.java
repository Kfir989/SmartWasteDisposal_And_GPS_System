package com.example.proj.Algo;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Bin> bins = Arrays.asList(
                new Bin(1, 10.0, 20.0, 100),
                new Bin(2, 11.0, 21.0, 80),
                new Bin(3, 12.0, 22.0, 50),
                new Bin(4, 13.0, 23.0, 30),
                new Bin(5, 14.0, 24.0, 90)
        );
        Bin startbin = new Bin(0,0,0,0);
        TSCPsolve so = new TSCPsolve(bins,startbin);
        Route route = so.Solve();
        for (int i = 0; i < route.bins.size(); i++){
            System.out.println(route.bins.get(i).ID);
        }
        System.out.println("Total Load: " + so.truck.currentlaod);
        System.out.println("Total Dist: " + route.calculatetotaldistance());
    }
}
