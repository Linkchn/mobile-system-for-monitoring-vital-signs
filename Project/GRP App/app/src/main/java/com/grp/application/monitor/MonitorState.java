package com.grp.application.monitor;

/**
 * Class {@code MonitorState} is the class to maintain monitor states based on
 * devices connection and on/off of switches.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class MonitorState {

    /** {@code True} if the switch of "start capture data" is on, {@code false} otherwise. */
    private static boolean startCaptureDataEnabled = false;
    /** {@code True} if the switch of "simulation" is on, {@code false} otherwise. */
    private static boolean simulationEnabled = false;
    /** {@code True} if the simulator is on, {@code false} otherwise. */
    private static boolean simulationOn = false;
    /** {@code True} if the switch of "receive message when not wear device" is on, {@code false} otherwise. */
    private static boolean msgOnNotWearDeviceEnabled = false;
    /** {@code True} if the switch of "receive warning when not capture data" is on, {@code false} otherwise. */
    private static boolean msgOnNotCaptureDataEnabled = false;
    /** {@code True} if the switch of "receive alert when report generated" is on, {@code false} otherwise. */
    private static boolean msgOnReportGeneratedEnabled = false;
    /** {@code True} if heart rate device is connected, {@code false} otherwise. */
    private static boolean hrDeviceConnected = false;
    /** {@code True} if scale device is connected, {@code false} otherwise. */
    private static boolean scaleDeviceConnected = false;
    /** {@code True} if age is set, {@code false} otherwise */
    private static boolean ageSet = false;

    private static MonitorState instance;

    /**
     * Private constructor.
     */
    private MonitorState() {}

    /**
     * Get the unique instance of MonitorState.
     * @return instance of MonitorState
     */
    protected static MonitorState getInstance() {
        if (instance == null) {
            instance = new MonitorState();
        }
        return instance;
    }

    public void enableStartCaptureData() {
        startCaptureDataEnabled = true;
    }

    public void disableStartCaptureData() {
        startCaptureDataEnabled = false;
    }

    public boolean isStartCaptureDataEnabled() {
        return startCaptureDataEnabled;
    }

    public void enableSimulation() {
        simulationEnabled = true;
    }

    public void disableSimulation() {
        simulationEnabled = false;
    }

    public boolean isSimulationEnabled() {
        return simulationEnabled;
    }

    public void simulationOn() {
        simulationOn = true;
    }

    public void simulationOff() {
        simulationOn = false;
    }

    public boolean isSimulationOn() {
        return simulationOn;
    }

    public void enableMsgOnNotWearDevice() {
        msgOnNotWearDeviceEnabled = true;
    }

    public void disableMsgOnNotWearDevice() {
        msgOnNotWearDeviceEnabled = false;
    }

    public boolean isMsgOnNotWearDeviceEnabled() {
        return msgOnNotWearDeviceEnabled;
    }

    public void enableMsgOnNotCaptureData() {
        msgOnNotCaptureDataEnabled = true;
    }

    public void disableMsgOnNotCaptureData() {
        msgOnNotCaptureDataEnabled = false;
    }

    public boolean isMsgOnNotCaptureDataEnabled() {
        return msgOnNotCaptureDataEnabled;
    }

    public void enableMsgOnReportGenerated() {
        msgOnReportGeneratedEnabled = true;
    }

    public void disableMsgOnReportGenerated() {
        msgOnReportGeneratedEnabled = false;
    }

    public boolean isMsgOnReportGeneratedEnabled() {
        return msgOnReportGeneratedEnabled;
    }

    public void connectHRDevice () {
        hrDeviceConnected = true;
    }

    public void disconnectHRDevice() {
        hrDeviceConnected = false;
    }

    public boolean isHRDeviceConnected() {
        return hrDeviceConnected;
    }

    public void connectScaleDevice () {
        scaleDeviceConnected = true;
    }

    public void disconnectScaleDevice() {
        scaleDeviceConnected = false;
    }

    public boolean isScaleDeviceConnected() {
        return scaleDeviceConnected;
    }

    public void setAge() {
        ageSet = true;
    }

    public boolean isAgeSet() {
        return ageSet;
    }
}
