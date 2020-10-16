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



    HashMap<String, ArrayList<Cuisine>> cuise;

    public HashMap<String, ArrayList<Integer>> getCount() {
        return count;
    }

    public void setCount(HashMap<String, ArrayList<Integer>> count) {
        this.count = count;
    }

    HashMap<String, ArrayList<Integer>> count;


}//End Class
