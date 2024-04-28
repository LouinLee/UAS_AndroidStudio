package com.example.realmapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.realmapp.R;
import com.example.realmapp.model.User;

import java.io.File;
import java.util.Calendar;

import io.realm.Realm;

public class EditProfileFragment extends Fragment {

    // Realm instance
    private Realm realm;

    // UI elements
    private EditText usernameEditText, emailEditText, editTextPassword;
    private ImageView imageViewUser;
    private TextView saveProfileButton;
    private RadioGroup genderRadioGroup;
    private static final int PICK_IMAGE_REQUEST = 1;
    private File imageFile;
    private DatePicker datePicker;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        // Initialize Realm
        realm = Realm.getDefaultInstance();
        // Initialize UI components
        usernameEditText = view.findViewById(R.id.editTextUsername);
        emailEditText = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        imageViewUser = view.findViewById(R.id.imageViewUser);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);
        datePicker = view.findViewById(R.id.agePicker);

        // Load profile details
        loadProfileDetails();
        // Set click listener for image button
        imageViewUser.setOnClickListener(v -> chooseImage());

        // Set click listener for save profile button
        saveProfileButton.setOnClickListener(v -> saveProfile());
        return view;
    }

    // Method to choose an image from gallery
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    // Method to save profile changes
    private void saveProfile() {
        // Get user ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("EditProfileFragment", "User ID from SharedPreferences: " + userId);

        // Retrieve user from Realm database
        User user = realm.where(User.class).equalTo("id", userId).findFirst();

        // Update user details if found, otherwise show a toast message
        if (user != null) {
            // Get the new values entered by the user
            String newUsername = usernameEditText.getText().toString().trim();
            String newEmail = emailEditText.getText().toString().trim();
            String newPassword = editTextPassword.getText().toString().trim();

            // Extract selected date from DatePicker
            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // Month is 0-based, so add 1
            int year = datePicker.getYear();

            // Calculate age based on selected date and system's current date
            int age = calculateAge(year, month, day);
            String newAge = String.valueOf(age);

            // Format birth date
            String newBirthDate = String.format("%02d-%02d-%04d", day, month, year);

            // Check for null or empty values
            if (newUsername.isEmpty() || newEmail.isEmpty() || newPassword.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            // Validate email format
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()) {
                Toast.makeText(requireContext(), "Invalid email address", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if any changes have been made
            int selectedGenderId = genderRadioGroup.getCheckedRadioButtonId();
            if (!newUsername.equals(user.getUsername())
                    || !newEmail.equals(user.getEmail())
                    || !newBirthDate.equals(user.getBirthdate())
                    || !newAge.equals(user.getAge())
                    || !newPassword.equals(user.getPassword())
                    || selectedGenderId != -1  // Check if a radio button is selected
                    || imageFile != null) {
                // Changes detected, proceed with updating user details
                realm.executeTransactionAsync(realm -> {
                    // Get the user object again in the background thread
                    User backgroundUser = realm.where(User.class).equalTo("id", userId).findFirst();
                    if (backgroundUser != null) {
                        // Update user details
                        backgroundUser.setUsername(newUsername);
                        backgroundUser.setEmail(newEmail);
                        backgroundUser.setBirthdate(newBirthDate);
                        backgroundUser.setAge(newAge);
                        backgroundUser.setPassword(newPassword);
                        // Set gender based on selected radio button
                        if (selectedGenderId != -1) {
                            if (selectedGenderId == R.id.gender1) {
                                backgroundUser.setGender("Male");
                            } else if (selectedGenderId == R.id.gender2) {
                                backgroundUser.setGender("Female");
                            }
                        }
                    }
                }, () -> {
                    // Transaction succeeded
                    // Show success message
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    ProfileFragment profileFragment = new ProfileFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, profileFragment)
                            .addToBackStack(null)
                            .commit();
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                }, error -> {
                    // Transaction failed
                    Log.e("EditProfileFragment", "Failed to update profile: " + error.getMessage());
                    Toast.makeText(requireContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                });
            } else {
                // No changes detected, show a message to the user
                Toast.makeText(requireContext(), "No changes detected", Toast.LENGTH_SHORT).show();
            }
        } else {
            // User not found, show a message
            Toast.makeText(requireContext(), "Profile details not found", Toast.LENGTH_LONG).show();
        }
    }

    // Method to load profile details
    private void loadProfileDetails() {
        // Get user ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("EditProfileFragment", "User ID from SharedPreferences: " + userId);

        // Retrieve user from Realm database
        User user = realm.where(User.class).equalTo("id", userId).findFirst();

        // Display profile details if found, otherwise show a toast message
        if (user != null) {
            usernameEditText.setText(user.getUsername());
            emailEditText.setText(user.getEmail());
            editTextPassword.setText(user.getPassword());
            // Set the date on DatePicker
            String[] birthdateParts = user.getBirthdate().split("-");
            int day = Integer.parseInt(birthdateParts[0]);
            int month = Integer.parseInt(birthdateParts[1]) - 1; // Month is 0-based, so subtract 1
            int year = Integer.parseInt(birthdateParts[2]);
            datePicker.init(year, month, day, null);
            imageViewUser.setImageResource(user.getImageResourceId());
        } else {
            Toast.makeText(requireContext(), "Profile details not available.", Toast.LENGTH_LONG).show();
        }
    }

    // Method to calculate age based on birthdate
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

    // Close the Realm instance when the fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
