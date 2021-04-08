package com.grp.application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.grp.application.R;
import com.grp.application.pages.BluetoothSettingsFragment;

/**
 * {@code ScaleSearchActivity} is the activity for searching scales.
 *
 * @author UNNC GRP G19
 * @version 1.0
 */
public class ScaleSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_search);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container_scale_search, new BluetoothSettingsFragment())
                .commit();
    }
}