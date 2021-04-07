package com.grp.application.pages;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.google.android.material.textfield.TextInputEditText;
import com.grp.application.Application;
import com.grp.application.Constants;
import com.grp.application.GlobalData;
import com.grp.application.HeartRateData;
import com.grp.application.MainActivity;
import com.example.application.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.grp.application.database.Dao;
import com.grp.application.export.FileLog;
import com.grp.application.monitor.Monitor;
import com.grp.application.GRPNotification.GRPNotification;
import com.grp.application.polar.Plotter;
import com.grp.application.polar.PlotterListener;
import com.grp.application.polar.PolarDevice;
import com.grp.application.polar.TimePlotter;
import com.grp.application.scale.Scale;
import com.grp.application.scale.bluetooth.BluetoothCommunication;
import com.grp.application.scale.datatypes.ScaleMeasurement;
import com.grp.application.simulation.HrSimulator;
import com.grp.application.simulation.WeightSimulator;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;


import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.model.PolarDeviceInfo;
import polar.com.sdk.api.model.PolarHrData;
import timber.log.Timber;

/**
 * {@code HomeFragment} is class to maintain UI elements and functions of home page.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class HomeFragment extends Fragment implements PlotterListener {
    private Monitor monitor;
    private MainActivity mainActivity;
    private PolarDevice polarDevice;
    private HrSimulator hrSimulator;
    private WeightSimulator weightSimulator;

    private XYPlot plotHR;
    private XYPlot plotECG;
    private TextView textViewHR;
    SwitchMaterial startCaptureDataSwitch;
    SwitchMaterial simulationSwitch;
    SwitchMaterial receiveWarningSwitch;
    TextInputEditText weightText;
    Button measureButton;

    Button recordingHrButton;
    Button stopRecordingHrButton;
    Button viewRecordingHrButton;
    Button recordingECGButton;
    Button stopRecordingECGButton;
    Button viewRecordingECGButton;
    Button recordingAccButton;
    Button stopRecordingAccButton;
    Button viewRecordingAccButton;

    private Boolean hrStatus = false;
    private Boolean ecgStatus = false;
    private String hrData = "";
    private  String ecgData = "";
    private TimePlotter plotterHR;
    private Plotter plotterECG;

    private Handler simHandler = new Handler();
    Runnable simulate;

    private GRPNotification grpNotification;

    private ArrayList<HeartRateData> heartRateDataList; // Store recorded hr data for temp
    private long lastTimestamp;    // record last time
    private final long ONE_MIN = 60*1000;


    public HomeFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        monitor = Monitor.getInstance();
        mainActivity = (MainActivity) getActivity();
        startCaptureDataSwitch = root.findViewById(R.id.switch_start_capture_data);
        simulationSwitch = root.findViewById(R.id.switch_simulation);
        weightText = root.findViewById(R.id.text_field_weight);
        measureButton = root.findViewById(R.id.button_measure_weight);
        hrSimulator = HrSimulator.getInstance();
        weightSimulator = WeightSimulator.getInstance();


        recordingHrButton = root.findViewById(R.id.button_start_recording_hr);
        recordingECGButton = root.findViewById(R.id.button_start_recording_ecg);
        recordingAccButton = root.findViewById(R.id.button_start_recording_acc);
        receiveWarningSwitch = root.findViewById(R.id.switch_msg_not_capture_data);


        polarDevice = PolarDevice.getInstance();
        plotHR = root.findViewById(R.id.plot_hr);
        plotECG = root.findViewById(R.id.plot_ecg);
        textViewHR = root.findViewById(R.id.number_heart_rate);
        grpNotification = GRPNotification.getInstance(mainActivity);
        plotterHR = monitor.getPlotterHR();
        plotterECG = monitor.getPlotterECG();
        plotterHR.setListener(this);
        plotterECG.setListener(this);

        heartRateDataList = new ArrayList<>();

        // Set hr simulator
        simulate = new Runnable() {
            @Override
            public void run() {
                try {
                    PolarHrData data = hrSimulator.getNextHrData();
                    if(data.hr <= 0){
                        grpNotification.sendNotification(mainActivity);
                        // TODO: need to fix bug here
//                            if(receiveWarningSwitch.isChecked()){
//
//                            }
                    }
                    else {
                        textViewHR.setText("Current Heart Rate: " + data.hr);
                        loadHrValue(data);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                simHandler.postDelayed(this, 1000);
            }
        };

        if (!monitor.getMonitorState().isSimulationEnabled()) {
            simHandler.removeCallbacks(simulate);
            stopPlot();
        }
        initDevice();
        initUI();
        if (monitor.getMonitorState().isStartCaptureDataEnabled()) {
            startPlot();
            if (monitor.getMonitorState().isSimulationEnabled()) {
                simHandler.postDelayed(simulate, 1000);
            }
        }



        // Set action for "start capture data" switch
        startCaptureDataSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                monitor.showToast("Start Capture Data");
                monitor.getMonitorState().enableStartCaptureData();
                startPlot();
                if (monitor.getMonitorState().isSimulationEnabled()) {
                    simHandler.postDelayed(simulate, 1000);
                    monitor.getMonitorState().simulationOn();
                }
                GlobalData.isStartRecord = true;
                lastTimestamp = System.currentTimeMillis();
            } else {
                monitor.showToast("Stop Capture Data");
                monitor.getMonitorState().disableStartCaptureData();
                simHandler.removeCallbacks(simulate);
                monitor.getMonitorState().simulationOff();
                stopPlot();
                clearPlot();
                Dao dao = new Dao(getContext());    // Dao
                dao.insertHRdata(heartRateDataList);
                GlobalData.isStartRecord = false;
                heartRateDataList.clear();
            }
            setButtonGroup(isChecked);
        });

        // Set action for "measure" button
        measureButton.setOnClickListener((view) -> {
            if (monitor.getMonitorState().isSimulationEnabled()) {
                try {
                    float weight = weightSimulator.readNextWeightData();
                    if(weight <= 0) {
                        if (receiveWarningSwitch.isChecked()) {
                            grpNotification.sendNotification(mainActivity);
                        }
                    }
                    else {
                            weightText.setText(String.format("%.2f", weight));
                            Dao dao = new Dao(getContext());
                            dao.insert(System.currentTimeMillis(), (long) weight, Constants.WEIGHT_TABLE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                monitor.showToast("Simulate Weight Measurement");
            } else {
                invokeConnectToBluetoothDevice(view);
            }
        });


        /**
         * A button listener that monitors the RecordingHrButton reacting differently in
         * different situations.
         *
         * <ul>
         *     <li>
         *         When the first time this button is clicked and there has a connected device,
         *         the recording is starting and a dialog will show "Recording". The text of the
         *         button will turn to red.
         *     </li>
         *     <li>
         *         If user constantly clicks the button while the recording, an alert will appear to
         *         warn "Already recording".
         *     </li>
         *     <li>
         *          If there is no an available device but the user clicks the button, an alert will
         *          show "No Device Connected".
         *     </li>
         *     <li>
         *         If the connected device is not suitable but the user clicks the button, an alert
         *         will show "Device Not Supported".
         *     </li>
         * </ul>
         *
         * @param hrStatus represents whether the recording is on or not
         *
         */
        recordingECGButton.setOnClickListener((view) -> {
            if ( polarDevice.getDeviceId() == null) {
                alertDialog("Problem", "No device connected!");
                return;
            }
            if (monitor.isEcgStatus()) {
                // already recording
                exportECG();
                monitor.setEcgStatus(false);
                recordingECGButton.setText("Start Recording ECG");
                recordingECGButton.setBackgroundColor(Color.rgb(255,255,255));
                recordingECGButton.setTextColor(Color.rgb(21,131,216));
            }else {
                // start recording
                monitor.setEcgStatus(true);
                recordingECGButton.setText("Stop Recording ECG");
                recordingECGButton.setBackgroundColor(Color.rgb(21,131,216));
                recordingECGButton.setTextColor(Color.rgb(255,255,255));
                Toast.makeText(Application.context, "Start recording ECG data", Toast.LENGTH_LONG).show();
            }
        });




        /**
         * A button listener that every time it is clicked, the directory that contains recording
         * data will be opened.
         */
//        viewRecordingECGButton.setOnClickListener((view) -> {
//            openAssignFolder("%2fECG%2f");
//        });


        // ACC
        recordingAccButton.setOnClickListener((view) -> {
            if ( polarDevice.getDeviceId() == null) {
                alertDialog("Problem", "No device connected!");
                return;
            }
            if (monitor.isEcgStatus()) {
                // already recording
                exportACC();
                monitor.setAccStatus(false);
                recordingAccButton.setText("Start Recording Acc");
                recordingAccButton.setBackgroundColor(Color.rgb(255,255,255));
                recordingAccButton.setTextColor(Color.rgb(21,131,216));
            }else {
                // start recording
                monitor.setAccStatus(true);
                recordingAccButton.setText("Stop Recording Acc");
                recordingAccButton.setBackgroundColor(Color.rgb(21,131,216));
                recordingAccButton.setTextColor(Color.rgb(255,255,255));
                Toast.makeText(Application.context, "Start recording acceleration", Toast.LENGTH_LONG).show();
            }
        });

        /**
         * A button listener that every time it is clicked, the directory that contains recording
         * data will be opened.
         */
//        viewRecordingAccButton.setOnClickListener((view) -> {
//            openAssignFolder("%2fACC%2f");
//        });



        /**
         * A button listener that monitors the RecordingHrButton reacting differently in
         * different situations.
         *
         * <ul>
         *     <li>
         *         When the first time this button is clicked and there has a connected device,
         *         the recording is starting and a dialog will show "Recording". The text of the
         *         button will turn to red.
         *     </li>
         *     <li>
         *         If user constantly clicks the button while the recording, an alert will appear to
         *         warn "Already recording".
         *     </li>
         *     <li>
         *          If there is no an available device but the user clicks the button, an alert will
         *          show "No Device Connected".
         *     </li>
         *     <li>
         *         If the connected device is not suitable but the user clicks the button, an alert
         *         will show "Device Not Supported".
         *     </li>
         * </ul>
         *
         * @param hrStatus represents whether the recording is on or not
         *
         */
        recordingHrButton.setOnClickListener((view) -> {
            if ( !monitor.getMonitorState().isSimulationEnabled() && polarDevice.getDeviceId() == null) {
                alertDialog("Problem", "No device connected!");
                return;
            }
            if (monitor.isHrStatus()) {
                // already recording
                exportHR();
                monitor.setHrStatus(false);
                recordingHrButton.setText("Start Recording Hr");
                recordingHrButton.setBackgroundColor(Color.rgb(255,255,255));
                recordingHrButton.setTextColor(Color.rgb(21,131,216));
            }else {
                // start recording
                monitor.setHrStatus(true);
                recordingHrButton.setText("Stop Recording Hr");
                recordingHrButton.setBackgroundColor(Color.rgb(21,131,216));
                recordingHrButton.setTextColor(Color.rgb(255,255,255));
                Toast.makeText(Application.context, "Start recording heart rate data", Toast.LENGTH_LONG).show();
            }

        });



        /**
         * A button listener that every time it is clicked, the directory that contains recording
         * data will be opened.
         */
//        viewRecordingHrButton.setOnClickListener((view) -> {
//            openAssignFolder("%2fHR%2f");
//
//        });


        // updated at 2/17. Problems occur in android simulator.
        return root;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (monitor.getMonitorState().isSimulationEnabled()) {
            stopPlot();
            simHandler.removeCallbacks(simulate);
        }
    }

    /**
     * generate the alert dialog
     * @param problem the title of alert
     * @param s the problem content
     */
    private void alertDialog(String problem, String s) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setTitle(problem)
                .setMessage(s)
                .setIcon(R.mipmap.ic_launcher)
                .create();
        alertDialog.show();
    }

    private void setButtonGroup (boolean enable) {
        recordingAccButton.setEnabled(enable);
        recordingECGButton.setEnabled(enable);
        recordingHrButton.setEnabled(enable);
        if(enable){
            recordingAccButton.setTextColor(Color.rgb(21,131,216));
            recordingECGButton.setTextColor(Color.rgb(21,131,216));
            recordingHrButton.setTextColor(Color.rgb(21,131,216));
        }else{
            recordingAccButton.setTextColor(Color.rgb(192,192,192));
            recordingECGButton.setTextColor(Color.rgb(192,192,192));
            recordingHrButton.setTextColor(Color.rgb(192,192,192));
        }
    }

    private void exportECG() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            // if clicked, changes the status to "False"
            monitor.setEcgStatus(false);
            String fileName = "ECG_Recording_" + new Date().getTime();
            try {
                FileLog.saveLog(monitor.getEcgValue(),fileName,"VitalSigns/ECG");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(Application.context, "ECG Export successfully!", Toast.LENGTH_LONG).show();
            monitor.stopECG();
        }
    }

    private void exportHR() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            // if clicked, changes the status to "False"
            monitor.setHrStatus(false);
            String fileName = "HR_Recording_" + new Date().getTime();
            try {
                FileLog.saveLog(hrData,fileName,"VitalSigns/HR");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(Application.context, "HR Export successfully!", Toast.LENGTH_LONG).show(); // <--
            monitor.stopHr();
        }
    }

    private void exportACC() {
        if (ContextCompat.checkSelfPermission(mainActivity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        } else {
            // if clicked, changes the status to "False"
            monitor.setAccStatus(false);
            String fileName = "ACC_Recording_" + new Date().getTime();
            try {
                FileLog.saveLog(monitor.getAccValue(),fileName,"VitalSigns/ACC");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(Application.context, "ACC Export successfully!", Toast.LENGTH_LONG).show();
            monitor.stopACC();
        }
    }

    /**
     * A method that used to detect whether the device is suitable or not.
     * True will be returned if the device is suitable otherwise false.
     * @param view view
     * @return
     */
    private boolean detectDeviceSupport(View view){
        final Scale scale = Scale.getInstance();

        String hwAddress = scale.getHwAddress();

        if(!BluetoothAdapter.checkBluetoothAddress(hwAddress)){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * A method that used to detect whether the device is connected or not.
     * True will be returned if the device is connected otherwise false.
     * @param view view
     * @return
     */
    private boolean detectDeviceConnect(View view){
        final Scale scale = Scale.getInstance();

        String deviceName = scale.getDeviceName();
        String hwAddress = scale.getHwAddress();

        if(!BluetoothAdapter.checkBluetoothAddress(hwAddress)){
            return false;
        }
        else{
            return true;
        }
    }

    /**
     * Start connection to scale device.
     * @param view view
     */
    private void invokeConnectToBluetoothDevice(View view) {

        final Scale scale = Scale.getInstance();

        String deviceName = scale.getDeviceName();
        String hwAddress = scale.getHwAddress();

        if (!BluetoothAdapter.checkBluetoothAddress(hwAddress)) {
            monitor.showToast("No Device Set");
            return;
        }

        monitor.showToast("Connect to " + deviceName);

        if (!scale.connectToBluetoothDevice(deviceName, hwAddress, callbackBtHandler)) {
            monitor.showToast("Device not support");
        }
    }

    /**
     * Set callback handler for scale operation.
     */
    private final Handler callbackBtHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            BluetoothCommunication.BT_STATUS btStatus = BluetoothCommunication.BT_STATUS.values()[msg.what];

            switch (btStatus) {
                case RETRIEVE_SCALE_DATA:
                    ScaleMeasurement scaleBtData = (ScaleMeasurement) msg.obj;

                    Scale scale = Scale.getInstance();
                    scale.addScaleMeasurement(scaleBtData);
                    if(monitor.getWeight()<=0){
                        if(receiveWarningSwitch.isChecked()){
                        grpNotification.sendNotification(mainActivity);
                        }
                    }
                    weightText.setText(String.format("%.2f", monitor.getWeight()));
                    break;
                case INIT_PROCESS:
                    monitor.showToast("Bluetooth initializing");
                    Timber.d("Bluetooth initializing");
                    break;
                case CONNECTION_LOST:
                    monitor.showToast("Bluetooth connection lost");
                    Timber.d("Bluetooth connection lost");
                    break;
                case NO_DEVICE_FOUND:
                    monitor.showToast("No Bluetooth device found");
                    Timber.e("No Bluetooth device found");
                    break;
                case CONNECTION_RETRYING:
                    monitor.showToast("No Bluetooth device found retrying");
                    Timber.e("No Bluetooth device found retrying");
                    break;
                case CONNECTION_ESTABLISHED:
                    monitor.showToast("Bluetooth connection successful established");
                    Timber.d("Bluetooth connection successful established");
                    break;
                case CONNECTION_DISCONNECT:
                    monitor.showToast("Bluetooth connection successful established");
                    Timber.d("Bluetooth connection successful disconnected");
                    break;
                case UNEXPECTED_ERROR:
                    monitor.showToast("Bluetooth unexpected error: " + msg.obj);
                    Timber.e("Bluetooth unexpected error: %s", msg.obj);
                    break;
                case SCALE_MESSAGE:
                    try {
                        String toastMessage = String.format(getResources().getString(msg.arg1), msg.obj);
                        monitor.showToast(toastMessage);
                        Timber.d("Bluetooth scale message: " + toastMessage);
                    } catch (Exception ex) {
                        Timber.e("Bluetooth scale message error: " + ex);
                    }
                    break;
            }
        }
    };

    /**
     * Initial device.
     */
    private void initDevice() {
        polarDevice.api().setApiCallback(new PolarBleApiCallback() {
            @Override
            public void deviceConnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                monitor.showToast(polarDeviceInfo.deviceId + " is Connected");
                monitor.getMonitorState().connectHRDevice();
                initUI();
            }

            @Override
            public void hrNotificationReceived(@NonNull String identifier, @NonNull PolarHrData data) {
                if (monitor.getMonitorState().isStartCaptureDataEnabled()) {
                    textViewHR.setText("Current Heart Rate: " + String.valueOf(data.hr));
                } else {
                    textViewHR.setText("No HR Signal");
                }
                if(data.hr <= 0){
                    if(receiveWarningSwitch.isChecked()){
                    grpNotification.sendNotification(mainActivity);
                    }
                }
                loadHrValue(data);
            }

            @Override
            public void deviceDisconnected(@NonNull PolarDeviceInfo polarDeviceInfo) {
                monitor.showToast(polarDeviceInfo.deviceId + "is lost");
                monitor.getMonitorState().disconnectHRDevice();
                stopPlot();
                clearPlot();
                monitor.getMonitorState().disableStartCaptureData();
                startCaptureDataSwitch.setEnabled(false);
            }

            @Override
            public void ecgFeatureReady(@NonNull String identifier) {
                monitor.streamECG();
            }

            @Override
            public void accelerometerFeatureReady(@NonNull String identifier) {
                monitor.streamACC();
            }
        });
    }

    /**
     * Initial UI.
     */
    private void initUI() {
        monitor.getViewSetter().setSwitchView(startCaptureDataSwitch, monitor.getMonitorState().isStartCaptureDataEnabled(),
                monitor.getMonitorState().isHRDeviceConnected() || monitor.getMonitorState().isSimulationEnabled());

        plotHR.setRangeBoundaries(50, 100, BoundaryMode.AUTO);
        plotHR.setDomainBoundaries(0, 360000, BoundaryMode.AUTO);
        // Left labels will increment by 10
        plotHR.setRangeStep(StepMode.INCREMENT_BY_VAL, 10);
        plotHR.setDomainStep(StepMode.INCREMENT_BY_VAL, 60000);
        // Make left labels be an integer (no decimal places)
        plotHR.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT).
                setFormat(new DecimalFormat("#"));
        // These don't seem to have an effect
        plotHR.setLinesPerRangeLabel(2);

        plotECG.setRangeBoundaries(-3.3, 3.3, BoundaryMode.FIXED);
        plotECG.setRangeStep(StepMode.INCREMENT_BY_FIT, 0.55);
        plotECG.setDomainBoundaries(0, 500, BoundaryMode.GROW);
        plotECG.setLinesPerRangeLabel(2);

        setButtonGroup(startCaptureDataSwitch.isChecked());
        monitor.getViewSetter().setButtonView(measureButton,
                monitor.getMonitorState().isScaleDeviceConnected() || monitor.getMonitorState().isSimulationEnabled());
    }

    /**
     * Start plotting.
     */
    private void startPlot() {
        plotHR.addSeries(plotterHR.getHrSeries(), plotterHR.getHrFormatter());
        plotECG.addSeries(plotterECG.getSeries(), plotterECG.getFormatter());
    }

    /**
     * Stop plotting.
     */
    private void stopPlot() {
        plotHR.removeSeries(plotterHR.getHrSeries());
        plotECG.removeSeries(plotterECG.getSeries());
    }

    private void clearPlot() {
        plotterHR.clearVal();
        plotterECG.clearVal();
    }

    /**
     * Update UI.
     */
    @Override
    public void update() {
        mainActivity.runOnUiThread(() -> {
            plotHR.redraw();
            plotECG.redraw();
        });
    }



    private void loadHrValue(PolarHrData data) {
        long currentTime = System.currentTimeMillis();
        int hr = data.hr;
        heartRateDataList.add(new HeartRateData((long)hr, currentTime));
        if(monitor.isHrStatus()){
            hrData = hrData + currentTime + "," + hr + ",\n";
        }

        // store data into database every 5 mins
        if(currentTime - lastTimestamp >= ONE_MIN*5) {
            Dao dao = new Dao(getContext());
            lastTimestamp = currentTime;
            dao.insertHRdata(heartRateDataList);
            heartRateDataList.clear();
        }

        monitor.getPlotterHR().addValues(data);
    }

}