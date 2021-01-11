package com.grp.application.monitor;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.androidplot.util.PixelUtils;
import com.grp.application.polar.Plotter;
import com.grp.application.polar.PolarDevice;
import com.grp.application.polar.TimePlotter;

import org.reactivestreams.Publisher;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Function;
import polar.com.sdk.api.model.PolarEcgData;
import polar.com.sdk.api.model.PolarSensorSetting;


public class Monitor {

    private MonitorState monitorState;
    private ViewSetter viewSetter;

    private PolarDevice polarDevice;
    private Toast toast;

    private static Monitor instance;
    private Context context;

    private TimePlotter plotterHR;
    private Plotter plotterECG;
    private double weight;
    private Disposable ecgDisposable = null;


    private Monitor(Context context) {
        this.context = context;
        monitorState = MonitorState.getInstance();
        viewSetter = ViewSetter.getInstance(context);
        polarDevice = PolarDevice.getInstance();
        PixelUtils.init(context);
        plotterHR = new TimePlotter();
        plotterECG = new Plotter("ECG");
    }

    public static void createInstance(Context context) {
        if (instance != null) {
            return;
        }

        instance = new Monitor(context);
    }

    public static Monitor getInstance() {
        if (instance == null) {
            throw new RuntimeException("No Monitor instance created");
        }

        return instance;
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

    public Context getContext() {
        return context;
    }

    public MonitorState getMonitorState() {
        return  monitorState;
    }

    public ViewSetter getViewSetter() {
        return viewSetter;
    }

    public void showToast(String text){
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        toast.show();
    }

    public TimePlotter getPlotterHR() {
        return plotterHR;
    }

    public Plotter getPlotterECG() {
        return plotterECG;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
}
