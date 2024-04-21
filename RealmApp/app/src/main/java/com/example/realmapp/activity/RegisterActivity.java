package com.example.realmapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmapp.R;
import com.example.realmapp.model.User;

import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.exceptions.RealmPrimaryKeyConstraintException;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private TextView registerButton;
    private Realm realm;
    private String temporaryUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        realm = Realm.getDefaultInstance();

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        registerButton = findViewById(R.id.registerButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerUser(usernameEditText.getText().toString(), passwordEditText.getText().toString());
            }
        });
    }

    private void registerUser(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        realm.executeTransactionAsync(realm -> {
            // Generate a unique ID for each user
            String userId = UUID.randomUUID().toString();
            User user = realm.createObject(User.class, userId); // use id as primary key
            user.setUsername(username);
            user.setPassword(password);
            temporaryUserId = userId; // Store the userId to use after transaction
        }, () -> {
            saveUserData(temporaryUserId); // Now use the stored userId here
            logAllUsers(); // Log all users to debug
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, error -> {
            Toast.makeText(RegisterActivity.this, "Registration failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
    }

    private void logAllUsers() {
        realm.executeTransactionAsync(realm -> {
                    List<User> users = realm.where(User.class).findAll();
                    for (User user : users) {
                        Log.d("RegisterActivity", "User: " + user.getUsername() + " ID: " + user.getId());
                    }
                }, () -> Log.d("RegisterActivity", "All users logged successfully"),
                error -> Log.e("RegisterActivity", "Error logging users: " + error.getMessage()));
    }

    private void saveUserData(String userId) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.apply();
        Log.d("SharedPreferences", "Saved User ID: " + userId);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
