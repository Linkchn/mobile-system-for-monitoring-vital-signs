package com.grp.application.scale;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.format.DateFormat;

import com.grp.application.monitor.Monitor;
import com.grp.application.scale.bluetooth.BluetoothCommunication;
import com.grp.application.scale.bluetooth.BluetoothFactory;
import com.grp.application.scale.datatypes.ScaleMeasurement;

import java.util.Date;

import timber.log.Timber;

public class Scale {
    private BluetoothCommunication btDeviceDriver;

    private static Scale instance;

    private String deviceName;
    private String hwAddress;
    private Context context;

    private Scale(Context context) {
        this.context = context;
        btDeviceDriver = null;
    }

    public static void createInstance(Context context) {
        if (instance != null) {
            return;
        }

        instance = new Scale(context);
    }

    public static Scale getInstance() {
        if (instance == null) {
            throw new RuntimeException("No Scale instance created");
        }

        return instance;
    }



    public boolean connectToBluetoothDevice(String deviceName, String hwAddress, Handler callbackBtHandler) {
        Timber.d("Trying to connect to bluetooth device [%s] (%s)", hwAddress, deviceName);

        disconnectFromBluetoothDevice();

        btDeviceDriver = BluetoothFactory.createDeviceDriver(context, deviceName);
        if (btDeviceDriver == null) {
            return false;
        }

        btDeviceDriver.registerCallbackHandler(callbackBtHandler);
        btDeviceDriver.connect(hwAddress);

        return true;
    }

    public boolean disconnectFromBluetoothDevice() {
        if (btDeviceDriver == null) {
            return false;
        }

        Timber.d("Disconnecting from bluetooth device");
        btDeviceDriver.disconnect();
        btDeviceDriver = null;

        return true;
    }

    public void addScaleMeasurement(final ScaleMeasurement scaleMeasurement) {
        Monitor monitor = Monitor.getInstance();
        monitor.setWeight(scaleMeasurement.getWeight());
    }

    public String getHwAddress() {
        return hwAddress;
    }

    public void setHwAddress(String hwAddress) {
        this.hwAddress = hwAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
