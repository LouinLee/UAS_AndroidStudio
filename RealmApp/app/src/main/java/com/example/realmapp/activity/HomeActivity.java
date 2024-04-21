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

        BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.navigation_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.navigation_class) {
                selectedFragment = new ClassFragment();
            } else if (itemId == R.id.navigation_trainer) {
                selectedFragment = new TrainerFragment();
            } else if (itemId == R.id.navigation_profile) {
                selectedFragment = new ProfileFragment();
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, selectedFragment).commit();
            return true;
        });

        // Set default selection
        navView.setSelectedItemId(R.id.navigation_home);  // This will trigger the item selection listener
    }
}
