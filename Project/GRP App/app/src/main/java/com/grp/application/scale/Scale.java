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

/**
 * Class {@code Scale} is the class to represent scale device.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class Scale {
    private BluetoothCommunication btDeviceDriver;

    private static Scale instance;

    private String deviceName;
    private String hwAddress;
    private Context context;

    /**
     * Private constructor.
     * @param context context of the application
     */
    private Scale(Context context) {
        this.context = context;
        btDeviceDriver = null;
    }

    /**
     * Initial Scale instance.
     * @param context context of the application
     */
    public static void createInstance(Context context) {
        if (instance != null) {
            return;
        }

        instance = new Scale(context);
    }

    /**
     * Get the unique instance of Scale.
     * @return instance of Scale
     */
    public static Scale getInstance() {
        if (instance == null) {
            throw new RuntimeException("No Scale instance created");
        }

        return instance;
    }

    /**
     * Set connection of scale device.
     * @param deviceName name of target device
     * @param hwAddress bluetooth MAC address of target device
     * @param callbackBtHandler callback handler of scale device
     * @return {@code true} if target scale device can be connected, {@code false} otherwise
     */
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

    /**
     * Disconnect from current scale device.
     * @return {@code true} if operation managed, {@code false} otherwise.
     */
    public boolean disconnectFromBluetoothDevice() {
        if (btDeviceDriver == null) {
            return false;
        }

        Timber.d("Disconnecting from bluetooth device");
        btDeviceDriver.disconnect();
        btDeviceDriver = null;

        return true;
    }

    /**
     * Add measured data to monitor
     * @param scaleMeasurement measured data
     */
    public void addScaleMeasurement(final ScaleMeasurement scaleMeasurement) {
        Monitor monitor = Monitor.getInstance();
        monitor.setWeight(scaleMeasurement.getWeight());
    }

    /**
     * Get bluetooth MAC address.
     * @return bluetooth MAC address
     */
    public String getHwAddress() {
        return hwAddress;
    }

    /**
     * Set bluetooth MAC address
     * @param hwAddress bluetooth MAC address
     */
    public void setHwAddress(String hwAddress) {
        this.hwAddress = hwAddress;
    }

    /**
     * Get device name.
     * @return device name
     */
    public String getDeviceName() {
        return deviceName;
    }

    /**
     * Set device name.
     * @param deviceName device name
     */
    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
