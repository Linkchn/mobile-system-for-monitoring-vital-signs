package com.grp.application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.application.R;
import com.grp.application.pages.BluetoothSettingsFragment;

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