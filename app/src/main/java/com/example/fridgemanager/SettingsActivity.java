package com.example.fridgemanager;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Settings Activity
 */

public class SettingsActivity extends AppCompatActivity  {

    TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        FloatingActionButton home = findViewById(R.id.fab_Home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goHome = new Intent(getApplicationContext(), DashboardNavigation.class);
                startActivity(goHome);
            }
        });

    }


}