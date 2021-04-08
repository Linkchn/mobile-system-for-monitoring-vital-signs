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

    public static final String POLAR_KEY = "polar_device_id";
    public static final String SCALE_ADDRESS_KEY = "scale_device_address";
    public static final String SCALE_NAME_KEY = "scale_device_name";
    public static final String MESSAGE_KEY = "message_state";
    public static final String WARNING_KEY = "warning_state";
    public static final String ALERT_KEY = "alert_state";
    public static final String AGE_KEY = "age";

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Scale.createInstance(context); //Initial Scale
        Monitor.createInstance(context); // Initial Monitor
        monitor = Monitor.getInstance();
    }
}

