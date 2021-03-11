package com.grp.application.monitor;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.grp.application.polar.AccPlotter;
import com.grp.application.polar.Plotter;
import com.grp.application.polar.PolarDevice;
import com.grp.application.polar.TimePlotter;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import polar.com.sdk.api.PolarBleApi;
import polar.com.sdk.api.model.PolarAccelerometerData;
import polar.com.sdk.api.model.PolarEcgData;
import polar.com.sdk.api.model.PolarSensorSetting;
import timber.log.Timber;

/**
 * Class {@code Monitor} is the class collect monitor data,
 * maintain monitor state, and set UI elements based on monitor state.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class Monitor {

    private MonitorState monitorState;
    private ViewSetter viewSetter;

    private PolarDevice polarDevice;
    private Toast toast;

    private static Monitor instance;
    private Context context;

    private TimePlotter plotterHR;
    private Plotter plotterECG;
    private AccPlotter plotterAcc;
    private float weight;
    private Disposable ecgDisposable = null;
    Disposable accDisposable = null;

    /**
     * Private constructor.
     * @param context context of the application
     */
    private Monitor(Context context) {
        this.context = context;
        monitorState = MonitorState.getInstance();
        viewSetter = ViewSetter.getInstance(context);
        polarDevice = PolarDevice.getInstance();
        PixelUtils.init(context);
        plotterHR = new TimePlotter();
        plotterECG = new Plotter("ECG");
        plotterAcc = new AccPlotter();
    }

    /**
     * Initial Monitor instance.
     * @param context context of the application
     */
    public static void createInstance(Context context) {
        if (instance != null) {
            return;
        }

        instance = new Monitor(context);
    }

    /**
     * Get the unique instance of Monitor.
     * @return instance of Monitor
     */
    public static Monitor getInstance() {
        if (instance == null) {
            throw new RuntimeException("No Monitor instance created");
        }

        return instance;
    }

    /**
     * Collect ECG data from Polar device.
     */
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

    public void streamACC(){
        PolarBleApi api = polarDevice.api();
        if (accDisposable == null) {
            accDisposable = api.requestAccSettings(polarDevice.getDeviceId())
                    .toFlowable()
                    .flatMap((Function<PolarSensorSetting, Publisher<PolarAccelerometerData>>) settings -> {
                        PolarSensorSetting sensorSetting = settings.maxSettings();
                        return api.startAccStreaming(polarDevice.getDeviceId(), sensorSetting);
                    }).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            polarAccelerometerData -> {
                                for (PolarAccelerometerData.PolarAccelerometerSample data : polarAccelerometerData.samples) {
                                    plotterAcc.sendSingleSample(data.x,data.y,data.z);
                                }
                            },
                            throwable -> {
                                accDisposable = null;
                            }
                    );
        } else {
            // NOTE dispose will stop streaming if it is "running"
            accDisposable.dispose();
            accDisposable = null;
        }
    }

    /**
     * Get {@code context}.
     * @return context
     */
    public Context getContext() {
        return context;
    }

    /**
     * Get {@code monitorState}.
     * @return monitorState
     */
    public MonitorState getMonitorState() {
        return  monitorState;
    }

    /**
     * Get {@code viewSetter}.
     * @return viewSetter
     */
    public ViewSetter getViewSetter() {
        return viewSetter;
    }

    /**
     * Show toast in current interface.
     * @param text text to display
     */
    public void showToast(String text){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Get {@code plotterHR}.
     * @return plotterHR
     */
    public TimePlotter getPlotterHR() {
        return plotterHR;
    }

    /**
     * Get {@code plotterECG}.
     * @return plotterECG
     */
    public Plotter getPlotterECG() {
        return plotterECG;
    }

    /**
     * Get {@code weight}.
     * @return weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Get {@code weight}.
     * @param weight measured weight
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }
}
