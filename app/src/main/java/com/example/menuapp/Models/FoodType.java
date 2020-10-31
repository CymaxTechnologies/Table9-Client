package com.example.menuapp.Models;

import com.example.menuapp.Cuisine;

import java.util.ArrayList;

public class FoodType {
    String name;
    ArrayList<Cuisine> cuisines;
   public FoodType()
    {
        cuisines=new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Cuisine> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<Cuisine> cuisines) {
        this.cuisines = cuisines;
    }



}
