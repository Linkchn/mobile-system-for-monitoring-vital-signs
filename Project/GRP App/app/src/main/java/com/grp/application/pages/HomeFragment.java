package com.grp.application.pages;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
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
import com.grp.application.GRPNotification.GRPNotification;
import com.grp.application.Application;
import com.grp.application.MainActivity;
import com.example.application.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.grp.application.export.FileLog;
import com.grp.application.monitor.Monitor;
import com.grp.application.polar.Plotter;
import com.grp.application.polar.PlotterListener;
import com.grp.application.polar.PolarDevice;
import com.grp.application.polar.TimePlotter;
import com.grp.application.scale.Scale;
import com.grp.application.scale.bluetooth.BluetoothCommunication;
import com.grp.application.scale.datatypes.ScaleMeasurement;
import com.grp.application.simulation.HrSimulator;
import com.grp.application.simulation.WeightSimulator;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;

import polar.com.sdk.api.PolarBleApiCallback;
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
    TextInputEditText weightText;
    Button measureButton;

    Button startRecordingHrButton;
    Button stopRecordingHrButton;
    Button viewRecordingHrButton;

    private Boolean hrStatus = false;
    private String hrData = "";

    private TimePlotter plotterHR;
    private Plotter plotterECG;

    private GRPNotification grpNotification;


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

        startRecordingHrButton = root.findViewById(R.id.button_start_recording_hr);
        stopRecordingHrButton = root.findViewById(R.id.button_stop_recording_hr);
        viewRecordingHrButton = root.findViewById(R.id.button_view_recording_hr);



        polarDevice = PolarDevice.getInstance();
        plotHR = root.findViewById(R.id.plot_hr);
        plotECG = root.findViewById(R.id.plot_ecg);
        textViewHR = root.findViewById(R.id.number_heart_rate);
        grpNotification = GRPNotification.getInstance(mainActivity);

        // Set hr simulator
        Handler simHandler = new Handler();
        Runnable simulate = new Runnable() {
            @Override
            public void run() {
                try {
                    PolarHrData data = hrSimulator.getNextHrData();
                    if(data.hr <= 0){
                        grpNotification.sendNotification(mainActivity);
                    }
                    textViewHR.setText("Current Heart Rate: " + String.valueOf(data.hr));
                    loadHrValue(data);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                simHandler.postDelayed(this, 1000);
            }
        };

        plotterHR = monitor.getPlotterHR();
        plotterECG = monitor.getPlotterECG();
        plotterHR.setListener(this);
        plotterECG.setListener(this);

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
                }
            } else {
                monitor.showToast("Stop Capture Data");
                monitor.getMonitorState().disableStartCaptureData();
                simHandler.removeCallbacks(simulate);
                stopPlot();
            }
        });

        // Set action for "measure" button
        measureButton.setOnClickListener((view) -> {
            if (monitor.getMonitorState().isSimulationEnabled()) {
                try {
                    float weight = weightSimulator.readNextWeightData();
                    if(weight <= 0){
                        grpNotification.sendNotification(mainActivity);
                    }
                    weightText.setText(String.format("%.2f", weight));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                monitor.showToast("Simulate Weight Measurement");
            } else {
                invokeConnectToBluetoothDevice(view);
            }
        });


        // startRecordHr updated at 2/14
        startRecordingHrButton.setOnClickListener((view) -> {
//            Toast.makeText(Application.context, "!!!!!!", Toast.LENGTH_LONG).show();

            if(hrStatus == true){
                AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                        .setTitle("Problem")
                        .setMessage("Already recording ")
                        .setIcon(R.mipmap.ic_launcher)
                        .create();
                alertDialog1.show();
                startRecordingHrButton.setTextColor(Color.rgb(244,67,54));
            }else{
                if (monitor.getMonitorState().isSimulationEnabled()){
                    AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                            .setTitle("Problem")
                            .setMessage("Recording")
                            .setIcon(R.mipmap.ic_launcher)
                            .create();
                    alertDialog1.show();
                    startRecordingHrButton.setTextColor(Color.rgb(244,67,54));
                    hrStatus = true;
                } else {
                    if(detectDeviceConnect(view) == false){
                        AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                                .setTitle("Problem")
                                .setMessage("No Device Connected ")
                                .setIcon(R.mipmap.ic_launcher)
                                .create();
                        alertDialog1.show();
                    }
                    else if(detectDeviceSupport(view) == false){
                        AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                                .setTitle("Problem")
                                .setMessage("Device Not Supported")
                                .setIcon(R.mipmap.ic_launcher)
                                .create();
                        alertDialog1.show();
                    }
                }

            }

        });

        // stopRecordHr
        stopRecordingHrButton.setOnClickListener((view) -> {

            if(monitor.getMonitorState().isSimulationEnabled()){
                if (hrStatus == true){

                    AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                            .setTitle("Recording")
                            .setMessage("Recording ends")
                            .setIcon(R.mipmap.ic_launcher)
                            .create();
                    alertDialog1.show();
                    startRecordingHrButton.setTextColor(Color.rgb(21,131,216));

                    final int REQUEST_EXTERNAL_STORAGE = 1;
                    String[] PERMISSIONS_STORAGE = {
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS};
                    if (ContextCompat.checkSelfPermission(mainActivity,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(mainActivity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
                    } else {
                        // if clicked, changes the status to "False"
                        hrStatus = false;
                        String fileName = "HR_Recording_" + new Date().getTime();
                        try {
                            FileLog.saveLog("Heart Beat per Minute",hrData,fileName);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(Application.context, "Export successfully!", Toast.LENGTH_LONG).show(); // <--
                        hrData = "";
                        hrStatus = false;
                    }
                }

            } else {
                AlertDialog alertDialog1 = new AlertDialog.Builder(getContext())
                        .setTitle("Problem")
                        .setMessage("No Recording")
                        .setIcon(R.mipmap.ic_launcher)
                        .create();
                alertDialog1.show();
            }
        });

        // viewRecordHr
        viewRecordingHrButton.setOnClickListener((view) -> {
//            Toast.makeText(Application.context, "view", Toast.LENGTH_LONG).show();
            openAssignFolder(Environment.getExternalStorageDirectory() + "/HR");
            if(monitor.getMonitorState().isSimulationEnabled()){
            }
        });


        // updated at 2/17. Problems occur in android simulator.
        return root;
    }

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
                        grpNotification.sendNotification(mainActivity);
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
            public void hrNotificationReceived(@NonNull String identifier, @NonNull PolarHrData data) {
                if (monitor.getMonitorState().isStartCaptureDataEnabled()) {
                    textViewHR.setText("Current Heart Rate: " + String.valueOf(data.hr));
                } else {
                    textViewHR.setText("No HR Signal");
                }
                if(data.hr <= 0){
                    grpNotification.sendNotification(mainActivity);
                }
                loadHrValue(data);
            }

            @Override
            public void ecgFeatureReady(@NonNull String identifier) {
                monitor.streamECG();
            }
        });
    }

    /**
     * Initial UI.
     */
    private void initUI() {
        monitor.getViewSetter().setSwitchView(startCaptureDataSwitch, monitor.getMonitorState().isStartCaptureDataEnabled());

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

    private void openAssignFolder(String path) {
//        File file = new File(path);
//        if (null == file || !file.exists()) {
//            return;
//        }
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
////        intent.addCategory(Intent.CATEGORY_OPENABLE);
////        intent.setType("csv/*");
//
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
////        intent.setDataAndType(Uri.fromFile(file), "csv/*");
//        intent.setType("csv/*");
//
//        try {
//            startActivity(intent);
//        } catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//        }

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(path);
        intent.setDataAndType(uri, "*/*");
        startActivity(Intent.createChooser(intent, "Open folder"));
    }

    private void loadHrValue(PolarHrData data) {
        if(hrStatus){
            hrData = hrData + System.currentTimeMillis() + "," + data + ",\n";
        }
        monitor.getPlotterHR().addValues(data);
    }

}