package com.example.menuapp.Others;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecuter {
    private static AppExecuter instance;
    private Executor diskio;
    private Executor ui;
    private AppExecuter(Executor executors,Executor executorUI)
    {
        diskio=executors;
        ui=executorUI;
    }
    public static AppExecuter getInstance()
    {
        if (instance==null)
        {
            instance=new AppExecuter(Executors.newSingleThreadExecutor(),Executors.newSingleThreadExecutor());
        }
        return instance;
    }
    public Executor getUi()
    {
        return  ui;
    }
    public Executor getDiskio()
    {
        return diskio;
    }


}
