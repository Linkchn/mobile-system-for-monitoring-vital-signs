package com.grp.application.polar;

import android.content.Context;

import androidx.annotation.NonNull;

import com.grp.application.Application;
import com.grp.application.MainActivity;
import com.grp.application.monitor.Monitor;

import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.PolarBleApiDefaultImpl;
import polar.com.sdk.api.model.PolarDeviceInfo;


public class PolarDevice {
    private String deviceId;
    private PolarBleApi polarApi;

    private static PolarDevice instance;

    private PolarDevice() {
        polarApi = PolarBleApiDefaultImpl.defaultImplementation(Application.context, PolarBleApi.ALL_FEATURES);
    }

    public static PolarDevice getInstance() {
        if (instance == null) {
            instance = new PolarDevice();
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
}
