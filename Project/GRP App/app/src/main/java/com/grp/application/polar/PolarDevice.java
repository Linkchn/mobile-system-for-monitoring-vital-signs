package com.grp.application.polar;

import com.grp.application.Application;

import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.PolarBleApiDefaultImpl;

/**
 * Class {@code PolarDevice} is the class to represent polar device.
 *
 * @author UNNC GRP G19 modified from JOikarinen@GitHub
 * @version 1.0
 */
public class PolarDevice {
    private String deviceId;
    private PolarBleApi polarApi;

    private static PolarDevice instance;

    /**
     * Private constructor.
     */
    private PolarDevice() {
        polarApi = PolarBleApiDefaultImpl.defaultImplementation(Application.context, PolarBleApi.ALL_FEATURES);
    }

    /**
     * Get the unique instance of PolarDevice.
     * @return instance of PolarDevice
     */
    public static PolarDevice getInstance() {
        if (instance == null) {
            instance = new PolarDevice();
        }
        return instance;
    }

    /**
     * Call for Polar API.
     * @return Polar API
     */
    public PolarBleApi api() {
        return polarApi;
    }

    /**
     * Get device ID
     * @return device ID
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * Set device ID
     * @param deviceId device ID
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
