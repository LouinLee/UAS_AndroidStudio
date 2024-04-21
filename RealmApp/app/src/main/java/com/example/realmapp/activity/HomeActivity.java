/*
 * HomeActivity.java
 * This class represents the main activity of the RealmApp application. It sets up the bottom navigation view
 * and handles the selection of different fragments based on user interaction.
 */

package com.example.realmapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.example.realmapp.R;
import com.example.realmapp.fragment.ClassFragment;
import com.example.realmapp.fragment.HomeFragment;
import com.example.realmapp.fragment.ProfileFragment;
import com.example.realmapp.fragment.TrainerFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize the bottom navigation view
        BottomNavigationView navView = findViewById(R.id.navigation);

        // Set the listener for bottom navigation view items
        navView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            // Determine which fragment to show based on the selected bottom navigation view item
            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_class) {
                selectedFragment = new ClassFragment();
            } else if (itemId == R.id.navigation_trainer) {
                selectedFragment = new TrainerFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            // Replace the current fragment with the selected one
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();
            return true;
        });

        // Set default selection
        navView.setSelectedItemId(R.id.navigation_home);  // This will trigger the item selection listener
    }
}
