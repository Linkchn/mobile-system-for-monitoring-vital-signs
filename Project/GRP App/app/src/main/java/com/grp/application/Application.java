package com.grp.application;


import android.content.Context;

import com.grp.application.monitor.Monitor;


public class Application extends android.app.Application {

    Monitor monitor;

    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Monitor.createInstance(context);
        monitor = Monitor.getInstance();
    }
}

