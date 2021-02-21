package com.grp.application.pages;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.google.android.material.textfield.TextInputEditText;
import com.grp.application.MainActivity;
import com.example.application.R;
import com.google.android.material.switchmaterial.SwitchMaterial;
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

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

    private TimePlotter plotterHR;
    private Plotter plotterECG;
    
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

        polarDevice = PolarDevice.getInstance();
        plotHR = root.findViewById(R.id.plot_hr);
        plotECG = root.findViewById(R.id.plot_ecg);
        textViewHR = root.findViewById(R.id.number_heart_rate);

        // Set hr simulator
        Handler simHandler = new Handler();
        Runnable simulate = new Runnable() {
            @Override
            public void run() {
                try {
                    PolarHrData data = hrSimulator.getNextHrData();
                    monitor.getPlotterHR().addValues(data);
                    textViewHR.setText(String.valueOf(data.hr));
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
                    weightText.setText(String.format("%.2f", weight));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                monitor.showToast("Simulate Weight Measurement");
            } else {
                invokeConnectToBluetoothDevice(view);
            }
        });

        return root;
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
                    textViewHR.setText(String.valueOf(data.hr));
                } else {
                    textViewHR.setText("");
                }
                    monitor.getPlotterHR().addValues(data);
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
}