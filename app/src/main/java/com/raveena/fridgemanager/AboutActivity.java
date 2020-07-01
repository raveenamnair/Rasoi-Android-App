package com.raveena.fridgemanager;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * This class will host the About Page will gives a description of the creater of Rasoi and
 * what the app is about
 * **/

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Button contact = findViewById(R.id.contact_btn);
        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToContactActivity = new Intent(getApplicationContext(), ContactActivity.class);
                startActivity(goToContactActivity);
            }
        });

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
