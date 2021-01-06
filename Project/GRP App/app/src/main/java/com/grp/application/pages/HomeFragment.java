package com.grp.application.pages;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.grp.application.MainActivity;
import com.example.application.R;
import com.grp.application.components.Devices;
import com.grp.application.components.Switches;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.grp.application.polar.Plotter;
import com.grp.application.polar.PlotterListener;
import com.grp.application.polar.PolarDevice;
import com.grp.application.polar.TimePlotter;

import org.reactivestreams.Publisher;

import java.text.DecimalFormat;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import polar.com.sdk.api.PolarBleApiCallback;
import polar.com.sdk.api.model.PolarEcgData;
import polar.com.sdk.api.model.PolarHrData;
import polar.com.sdk.api.model.PolarSensorSetting;

public class HomeFragment extends Fragment implements PlotterListener {

    private MainActivity mainActivity;
    private PolarDevice polarDevice;
    private XYPlot plotHR;
    private XYPlot plotECG;
    private TimePlotter plotterHR;
    private Plotter plotterECG;
    private TextView textViewHR;

    private Disposable ecgDisposable = null;

    public HomeFragment() {}

    public View onCreateView(@NonNull LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);
        mainActivity = (MainActivity) getActivity();
        SwitchMaterial startCaptureDataSwitch = root.findViewById(R.id.startCaptureDataSwitch);

        polarDevice = PolarDevice.getInstance(mainActivity);
        plotHR = root.findViewById(R.id.plot_hr);
        plotECG = root.findViewById(R.id.plot_ecg);
        textViewHR = root.findViewById(R.id.number_heart_rate);

        plotterHR = new TimePlotter();
        plotterECG = new Plotter("ECG");
        plotterHR.setListener(this);
        plotterECG.setListener(this);

        initialDevice();
        plot();


        mainActivity.setSwitchView(startCaptureDataSwitch, Switches.START_CAPTURE_DATA);
        startCaptureDataSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mainActivity.showToast("Start Capture Data");
            } else {
                mainActivity.showToast("Stop Capture Data");
            }
            mainActivity.toggleSwitch(Switches.START_CAPTURE_DATA);
        });

        return root;
    }

    private void initialDevice() {
        polarDevice.api().setApiCallback(new PolarBleApiCallback() {
            @Override
            public void hrNotificationReceived(@NonNull String identifier, @NonNull PolarHrData data) {
                if (mainActivity.isSwitchChecked(Switches.START_CAPTURE_DATA)) {
                    textViewHR.setText(String.valueOf(data.hr));
                    plotterHR.addValues(data);
                }
            }

            @Override
            public void ecgFeatureReady(@NonNull String identifier) {

                    streamECG();

            }
        });
    }

    private void plot() {
        plotHR.addSeries(plotterHR.getHrSeries(), plotterHR.getHrFormatter());
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

        plotECG.addSeries(plotterECG.getSeries(), plotterECG.getFormatter());
        plotECG.setRangeBoundaries(-3.3, 3.3, BoundaryMode.FIXED);
        plotECG.setRangeStep(StepMode.INCREMENT_BY_FIT, 0.55);
        plotECG.setDomainBoundaries(0, 500, BoundaryMode.GROW);
        plotECG.setLinesPerRangeLabel(2);
    }

    public void streamECG() {
        if (ecgDisposable == null) {
            ecgDisposable =
                    polarDevice.api().requestEcgSettings(polarDevice.getDeviceId())
                            .toFlowable()
                            .flatMap((Function<PolarSensorSetting, Publisher<PolarEcgData>>) sensorSetting ->
                                    polarDevice.api().startEcgStreaming(polarDevice.getDeviceId(), sensorSetting.maxSettings()))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    polarEcgData -> {
                                        for (Integer data : polarEcgData.samples) {
                                            plotterECG.sendSingleSample((float) ((float) data / 1000.0));
                                        }
                                    },
                                    throwable -> {
                                        ecgDisposable = null;
                                    }
                            );
        } else {
            // NOTE stops streaming if it is "running"
            ecgDisposable.dispose();
            ecgDisposable = null;
        }
    }

    @Override
    public void update() {
        mainActivity.runOnUiThread(() -> {
            plotHR.redraw();
            plotECG.redraw();
        });
    }
}