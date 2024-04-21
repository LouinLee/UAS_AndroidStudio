/*
 * LoginActivity.java
 * This class represents the login activity of the RealmApp application. It allows users to log in
 * using their username and password. Upon successful login, it saves the user's ID and navigates
 * to the home activity.
 */

package com.example.realmapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmapp.R;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView loginButton;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Realm
        realm = Realm.getDefaultInstance();

        // Initialize UI elements
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);

        // Set click listener for login button
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve username and password from EditText fields
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                // Attempt to log in
                loginUser(username, password);
            }
        });
    }

    // Method to authenticate user login
    private void loginUser(String username, String password) {
        // Find the user in the Realm database by username
        User user = realm.where(User.class).equalTo("username", username).findFirst();

        // Check if the user exists and the password is correct
        if (user != null && user.getPassword().equals(password)) {

            // Save user ID to SharedPreferences
            saveUserData(user.getId());

            // Show login success message
            Toast.makeText(LoginActivity.this, "Login Successful!", Toast.LENGTH_LONG).show();

            // Navigate to the home activity
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Show invalid login message
            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
        }
    }

    // Method to save user data (user ID) to SharedPreferences
    private void saveUserData(String userId) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.apply();
    }

    // Close the Realm instance when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
