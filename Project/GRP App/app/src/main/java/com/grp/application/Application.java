package com.grp.application;


import android.content.Context;

import com.grp.application.monitor.Monitor;
import com.grp.application.scale.Scale;

/**
 * {@code Application} is the base class for maintaining global application state.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class Application extends android.app.Application {

    Monitor monitor;

    public static Context context; //Global context

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Scale.createInstance(context); //Initial Scale
        Monitor.createInstance(context); // Initial Monitor
        monitor = Monitor.getInstance();
    }
}

