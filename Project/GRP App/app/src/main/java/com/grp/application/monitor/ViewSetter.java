package com.grp.application.monitor;

import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import com.example.application.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

public class ViewSetter {

    private Context context;

    private static ViewSetter instance;

    private ViewSetter(Context context) {
        this.context = context;
    }

    protected static ViewSetter getInstance(Context context) {
        if (instance == null) {
            instance = new ViewSetter(context);
        }
        return instance;
    }

    public void setSwitchView(SwitchMaterial switchButton, boolean isEnabled) {
        switchButton.setChecked(isEnabled);
    }

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

    public void setButtonView(Button button, boolean isEnabled) {
        button.setEnabled(isEnabled);
    }
}
