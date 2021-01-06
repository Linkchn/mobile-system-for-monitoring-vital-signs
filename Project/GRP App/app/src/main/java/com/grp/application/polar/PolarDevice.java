package com.grp.application.polar;

import android.content.Context;

import androidx.annotation.NonNull;

import com.grp.application.MainActivity;

import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiDefaultImpl;


public class PolarDevice {
    private String deviceId;
    private int heartRate;
    private PolarBleApi polarApi;
    private Context classContext;
    private static PolarDevice instance;

    private PolarDevice(Context classContext) {
        this.classContext = classContext;
        polarApi = PolarBleApiDefaultImpl.defaultImplementation(this.classContext, PolarBleApi.ALL_FEATURES);
    }

    public static PolarDevice getInstance(Context classContext) {
        if (instance == null) {
            instance = new PolarDevice(classContext);
        }
        return instance;
    }

    public PolarBleApi api() {
        return polarApi;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }
}
