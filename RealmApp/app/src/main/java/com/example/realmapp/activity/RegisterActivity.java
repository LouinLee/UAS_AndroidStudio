package com.example.realmapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.realmapp.R;
import com.example.realmapp.model.User;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.realm.Realm;

public class RegisterActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private EditText emailEditText;
    private TextView registerButton;
    private Realm realm;
    private String temporaryUserId;
    private DatePicker datePicker; // Declare the DatePicker outside onCreate

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Realm
        realm = Realm.getDefaultInstance();

        // Initialize elements
        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        emailEditText = findViewById(R.id.email);
        registerButton = findViewById(R.id.registerButton);
        RadioGroup genderRadioGroup = findViewById(R.id.genderRadioGroup);
        datePicker = findViewById(R.id.agePicker); // Initialize DatePicker (assuming you have it in your layout)

        // Set click listener for register button
        registerButton.setOnClickListener(view -> {
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            String newEmail = emailEditText.getText().toString().trim();
            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(RegisterActivity.this, "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }
            if (selectedGenderId == -1) {
                Toast.makeText(RegisterActivity.this, "Please select gender", Toast.LENGTH_SHORT).show();
                return;
            }

            String gender;
            if (selectedGenderId == R.id.gender1) {
                gender = "Male";
            } else {
                gender = "Female";
            }

            // Extract selected date from DatePicker
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth();
            int year = datePicker.getYear();

            // Calculate age based on selected date and system's current date
            int age = calculateAge(year, month, day);

            // Rest of your registerUser logic using the calculated age from ageEditText
            registerUser(usernameEditText.getText().toString(), emailEditText.getText().toString(), gender, String.valueOf(age), passwordEditText.getText().toString());
        });
    }

    public static int calculateAge(int year, int monthOfYear, int dayOfMonth) {
        Calendar birthDate = Calendar.getInstance();
        birthDate.set(year, monthOfYear, dayOfMonth);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age;
    }

    // Method to register a new user
    private void registerUser(String username, String email, String gender, String age, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (email.isEmpty()) {
            Toast.makeText(this, "Email cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        } else if (gender.isEmpty()){
            Toast.makeText(this, "Gender cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }
        // Execute Realm transaction asynchronously
        realm.executeTransactionAsync(realm -> {
            // Generate a unique ID for each user
            String userId = UUID.randomUUID().toString();

            // Create a new User object with the generated ID
            User user = realm.createObject(User.class, userId); // use id as primary key
            user.setUsername(username);
            user.setEmail(email);
            user.setGender(gender);
            // Extract selected date from DatePicker
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // Month is 0-based, so add 1
            int year = datePicker.getYear();

            // Format the birthdate as day-month-year
            String formattedBirthdate = String.format(Locale.getDefault(), "%02d-%02d-%04d", day, month, year);

            // Set the formatted birthdate in the birthdateTextView
            user.setBirthdate(formattedBirthdate);
            user.setAge(age);
            user.setPassword(password);
            user.setImageResourceId(R.drawable.ic_profile);
            temporaryUserId = userId; // Store the userId to use after transaction
        }, () -> {
            saveUserData(temporaryUserId); // Now use the stored userId here
            logAllUsers(); // Log all users to debug

            // Navigate to the home activity
            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }, error -> {
            // If transaction encounters an error
            Toast.makeText(RegisterActivity.this, "Registration failed: " + error.getMessage(), Toast.LENGTH_LONG).show();
            error.printStackTrace();
        });
    }

    // Method to log all users for debugging
    private void logAllUsers() {
        realm.executeTransactionAsync(realm -> {
                    // Retrieve all users from Realm
                    List<User> users = realm.where(User.class).findAll();
                    // Log each user's username and ID
                    for (User user : users) {
                        Log.d("RegisterActivity", "User: " + user.getUsername() + " ID: " + user.getId());
                    }
                }, () -> Log.d("RegisterActivity", "All users logged successfully"),
                error -> Log.e("RegisterActivity", "Error logging users: " + error.getMessage()));
    }

    // Method to save user data (user ID) to SharedPreferences
    private void saveUserData(String userId) {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.apply();
        Log.d("SharedPreferences", "Saved User ID: " + userId);
    }

    // Close the Realm instance when the activity is destroyed
    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
