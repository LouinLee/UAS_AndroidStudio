package com.example.realmapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

public class EditProfileFragment extends Fragment {

    // Realm instance
    private Realm realm;

    // UI elements
    private EditText usernameEditText, emailEditText, ageEditText, editTextPassword;
    private ImageView imageViewUser;
    private TextView saveProfileButton;
    private RadioGroup genderRadioGroup;
    private static final int PICK_IMAGE_REQUEST = 1;
    private File imageFile;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        // Initialize Realm
        realm = Realm.getDefaultInstance();
        // Initialize UI components
        usernameEditText = view.findViewById(R.id.editTextUsername);
        emailEditText = view.findViewById(R.id.editTextEmail);
        ageEditText = view.findViewById(R.id.editTextAge);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        imageViewUser = view.findViewById(R.id.imageViewUser);
        saveProfileButton = view.findViewById(R.id.saveProfileButton);
        genderRadioGroup = view.findViewById(R.id.genderRadioGroup);

        // Set click listener for image button
        imageViewUser.setOnClickListener(v -> chooseImage());

        // Set click listener for save profile button
        saveProfileButton.setOnClickListener(v -> saveProfile());

        // Load profile details
        loadProfileDetails();

        return view;
    }

    // Method to choose an image from gallery
    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                imageViewUser.setImageBitmap(bitmap);
                // Save the selected image to a file
                saveImageToFile(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Method to save the selected image to a file
    private void saveImageToFile(Bitmap bitmap) {
        try {
            if (bitmap == null) {
                // If no image is selected, set a default image
                imageViewUser.setImageResource(R.drawable.ic_profile);
                return;
            }

            // Create a file in the internal storage directory
            imageFile = new File(requireContext().getFilesDir(), "user_image.jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            String newAge = ageEditText.getText().toString().trim();
            String newPassword = editTextPassword.getText().toString().trim();

            // Check for null or empty values
            if (newUsername.isEmpty() || newEmail.isEmpty() || newAge.isEmpty() || newPassword.isEmpty()) {
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
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    ProfileFragment profileFragment = new ProfileFragment();
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, profileFragment)
                            .addToBackStack(null)
                            .commit();
                }, () -> {
                    // Transaction succeeded
                    // Show success message
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
            ageEditText.setText(user.getAge());
            editTextPassword.setText(user.getPassword());
            imageViewUser.setImageResource(user.getImageResourceId());
        } else {
            Toast.makeText(requireContext(), "Profile details not available.", Toast.LENGTH_LONG).show();
        }
    }

    // Close the Realm instance when the fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
