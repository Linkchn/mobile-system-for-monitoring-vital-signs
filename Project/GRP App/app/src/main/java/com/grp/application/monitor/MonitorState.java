package com.grp.application.monitor;

public class MonitorState {
    private static boolean startCaptureDataEnabled = false;
    private static boolean simulationEnabled = false;
    private static boolean msgOnNotWearDeviceEnabled = false;
    private static boolean msgOnNotCaptureDataEnabled = true;
    private static boolean msgOnReportGeneratedEnabled = false;
    private static boolean hrDeviceConnected = false;
    private static boolean scaleDeviceConnected = false;

    private static MonitorState instance;

    private MonitorState() {}

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
}
