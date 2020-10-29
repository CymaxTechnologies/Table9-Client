package com.example.menuapp.DataBase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.menuapp.Resturant;

import java.util.List;

@Dao
public interface ResturantDAO {
    @Query("select * from resturant")
    public List<Resturant> getAll();
    @Query("delete  from resturant")
    public void deleAll();
    @Insert
    public void insert(Resturant r);
}
