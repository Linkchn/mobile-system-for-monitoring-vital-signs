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

import java.text.SimpleDateFormat;
import java.util.Date;

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

    public String getHrValue() { return hrValue; }

    public String getEcgValue() {
        return ecgValue;
    }

    public String getAccValue() {
        return accValue;
    }

    private String hrValue;
    private String ecgValue;
    private String accValue;
    private boolean hrStatus;
    private boolean ecgStatus;
    private boolean accStatus;

    private void resetHr() { hrValue = "";}

    private void resetECG(){
        ecgValue = "";
    }

    private void resetACC(){
        accValue = "";

    }

    public boolean isHrStatus() {
        return hrStatus;
    }

    public void setHrStatus(boolean hrStatus) {
        this.hrStatus = hrStatus;
    }

    public boolean isEcgStatus() {
        return ecgStatus;
    }

    public void setEcgStatus(boolean ecgStatus) {
        this.ecgStatus = ecgStatus;
    }

    public boolean isAccStatus() {
        return accStatus;
    }

    public void setAccStatus(boolean accStatus) {
        this.accStatus = accStatus;
    }

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

        /*initialize the ecg and acc recording variable*/
        ecgValue = "";
        accValue = "";
        ecgStatus = false;
        accStatus = false;
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
                                            if (ecgStatus){
                                                ecgValue = ecgValue + polarEcgData.timeStamp/1000000 + "," + data + "\n";
                                            }
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
        if (accDisposable == null) {
            accDisposable =
                    polarDevice.api().requestAccSettings(polarDevice.getDeviceId())
                            .toFlowable()
                            .flatMap((Function<PolarSensorSetting, Publisher<PolarAccelerometerData>>) sensorSetting ->
                                    polarDevice.api().startAccStreaming(polarDevice.getDeviceId(), sensorSetting.maxSettings()))
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    polarAccelerometerData -> {
                                        for (PolarAccelerometerData.PolarAccelerometerSample data : polarAccelerometerData.samples) {
                                            if (accStatus){
                                                System.out.println("ACC: "+ data.x + "," + data.y + "," + data.z + "\n");
                                                accValue = accValue + polarAccelerometerData.timeStamp/1000000 + "," + data.x + "," + data.y + "," + data.z + "\n";
                                            }
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

    public static String stampToDate(long  s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = s;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public void stopHr(){
        this.hrStatus = false;
        resetHr();
    }

    public void stopECG(){
        this.ecgStatus = false;
        resetECG();
    }

    public void stopACC(){
        this.accStatus = false;
        resetACC();
    }
}
