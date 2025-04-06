package com.example.proj.Algo;

public class Truck {
   public int capacity;
   public int currentlaod;

   public Truck(int capacity){
       this.capacity = capacity;
       currentlaod = 0;
   }

   public boolean canLoad(Bin bin){
       return (currentlaod + bin.fillpercent) <= capacity;
   }

   public void load(Bin bin){
       currentlaod += bin.fillpercent;
   }

   public void unload(){
       currentlaod = 0;
   }
   public int getRemainingCapacity(){
       return capacity - currentlaod;
   }

}
