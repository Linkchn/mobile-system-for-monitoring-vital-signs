package com.grp.application.monitor;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.grp.application.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

/**
 * Class {@code ViewSetter} is the class to set UI elements based on states of monitor.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class ViewSetter {

    private Context context;

    private static ViewSetter instance;

    /**
     * Private constructor.
     * @param context context of the application
     */
    private ViewSetter(Context context) {
        this.context = context;
    }

    /**
     * Get the unique instance of ViewSetter.
     * @return instance of ViewSetter
     */
    protected static ViewSetter getInstance(Context context) {
        if (instance == null) {
            instance = new ViewSetter(context);
        }
        return instance;
    }

    /**
     * Set switch view to on/off based on relative state of monitor.
     * @param switchButton target switch
     * @param isChecked boolean parameter whether the switch is on
     */
    public void setSwitchView(SwitchMaterial switchButton, boolean isChecked) {
        switchButton.setChecked(isChecked);
    }

    /**
     * Set switch view to on/off and enabled/disabled based on relative state of monitor.
     * @param switchButton target switch
     * @param isChecked boolean parameter whether the switch is on
     * @param isEnabled boolean parameter whether the switch is enabled
     */
    public void setSwitchView(SwitchMaterial switchButton, boolean isChecked, boolean isEnabled) {
        switchButton.setEnabled(isEnabled);
        switchButton.setChecked(isChecked);
    }

    /**
     * Set device connection button view to enabled/disabled based on the connection of the device.
     * @param symbol target symbol to show the connection state
     * @param connectButton button to connect device
     * @param disconnectButton button to disconnect device
     * @param isConnected boolean parameter whether the device is connected
     * @param isSimulationEnabled boolean parameter whether the simulation is enabled
     */
    public void setDeviceView(ImageView symbol, Button connectButton, Button disconnectButton, boolean isConnected, boolean isSimulationEnabled) {
        if (isConnected || isSimulationEnabled) {
            symbol.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24));
            connectButton.setText(R.string.text_device_connected);
            if (!isSimulationEnabled) {
                disconnectButton.setVisibility(View.VISIBLE);
            }
        } else {
            symbol.setImageDrawable(context.getDrawable(R.drawable.ic_outline_cancel_24));
            connectButton.setText(R.string.text_device_not_connected);
            disconnectButton.setVisibility(View.INVISIBLE);
        }
        connectButton.setEnabled(!isConnected && !isSimulationEnabled);
    }

    /**
     * Set button view to enabled/disabled based on relative state of monitor.
     * @param button target button
     * @param isEnabled boolean parameter whether the button is enabled
     */
    public void setButtonView(Button button, boolean isEnabled) {
        button.setEnabled(isEnabled);
    }

    /**
     * Set age view to  based on relative state of monitor.
     * @param setButton age set button
     * @param ageText displayed age number
     * @param isAgeSet boolean parameter whether age is set
     */
    public void setAgeView(Button setButton, TextView ageText, boolean isAgeSet) {
        if (isAgeSet) {
            setButton.setText(R.string.age_set);
            ageText.setText(String.valueOf(Monitor.getInstance().getAge()));
        }
    }
}
