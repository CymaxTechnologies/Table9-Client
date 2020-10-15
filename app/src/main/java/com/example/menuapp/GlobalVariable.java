package com.example.menuapp;

import android.app.Application;

import java.util.ArrayList;
import java.util.HashMap;

public class GlobalVariable extends Application
{
    public HashMap<String, ArrayList<Cuisine>> getCuise() {
        return cuise;
    }

    public void setCuise(HashMap<String, ArrayList<Cuisine>> cuise) {
        this.cuise = cuise;
    }



    HashMap<String, ArrayList<Cuisine>> cuise=new HashMap<>();
    HashMap<String, ArrayList<Integer>> count=new HashMap<>();


}//End Class
