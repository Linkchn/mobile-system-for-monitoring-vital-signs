package com.grp.application.monitor;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import com.example.application.R;
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
     * @param button target button
     * @param isConnected boolean parameter whether the device is connected
     */
    public void setDeviceView(ImageView symbol, Button button, boolean isConnected) {
        if (isConnected) {
            symbol.setImageDrawable(context.getDrawable(R.drawable.ic_baseline_check_circle_outline_24));
            button.setText(R.string.text_device_connected);
        } else {
            symbol.setImageDrawable(context.getDrawable(R.drawable.ic_outline_cancel_24));
            button.setText(R.string.text_device_not_connected);
        }
        button.setEnabled(!isConnected);
    }

    /**
     * Set button view to enabled/disabled based on relative state of monitor.
     * @param button target button
     * @param isEnabled boolean parameter whether the button is enabled
     */
    public void setButtonView(Button button, boolean isEnabled) {
        button.setEnabled(isEnabled);
    }
}
