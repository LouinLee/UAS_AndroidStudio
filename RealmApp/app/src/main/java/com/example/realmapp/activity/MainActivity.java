/*
 * MainActivity.java
 * This class represents the main activity of the RealmApp application. It displays options for users
 * to either login or register. Clicking on the login button navigates the user to the LoginActivity,
 * while clicking on the register button navigates the user to the RegisterActivity.
 */

package com.example.realmapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmapp.R;

public class MainActivity extends AppCompatActivity {

    private TextView buttonLogin;
    private TextView buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize elements
        buttonLogin = findViewById(R.id.buttonLogin);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Set click listener for login button
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        // Set click listener for register button
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to RegisterActivity
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
    }
}
