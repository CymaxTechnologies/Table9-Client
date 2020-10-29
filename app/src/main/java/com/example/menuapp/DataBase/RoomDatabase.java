package com.example.menuapp.DataBase;

import android.content.Context;
import android.net.http.SslCertificate;

import androidx.room.Database;
import androidx.room.Room;

import com.example.menuapp.Resturant;

@Database(entities = Resturant.class,version = 2)
public abstract  class RoomDatabase extends androidx.room.RoomDatabase {
    private static String DB_NAME="My_DB";
 public static    RoomDatabase roomDatabase;
    public abstract  ResturantDAO resturantDAO();
    public static synchronized RoomDatabase getInstance(Context context)
    {
        if(roomDatabase==null)
        {
            roomDatabase= Room.databaseBuilder(context.getApplicationContext(),RoomDatabase.class, DB_NAME).fallbackToDestructiveMigration().build();
        }
        return roomDatabase;
    }
}
