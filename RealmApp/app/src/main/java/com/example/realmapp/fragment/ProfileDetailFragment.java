package com.example.realmapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.realmapp.R;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class ProfileDetailFragment extends Fragment {

    // Realm instance
    private Realm realm;

    // UI elements
    private TextView usernameTextView, emailTextView, genderTextView, ageTextView; // Add any other relevant fields
    private ImageView imageViewUser;
    private Button editProfileButton;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Realm
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_detail, container, false);

        // Initialize UI components
        usernameTextView = view.findViewById(R.id.textViewTitle);
        emailTextView = view.findViewById(R.id.textViewEmail);
        genderTextView = view.findViewById(R.id.textViewGender);
        ageTextView = view.findViewById(R.id.textViewAge);
        imageViewUser = view.findViewById(R.id.imageViewUser);
        editProfileButton = view.findViewById(R.id.editProfileButton);
        // Load profile details
        loadProfileDetails();

        // Set click listener for edit profile button
        editProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Replace the current fragment with EditProfileFragment
                replaceFragment(new EditProfileFragment());
            }
        });

        return view;
    }

    // Method to load profile details
    private void loadProfileDetails() {
        // Get user ID from SharedPreferences
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("ProfileDetailFragment", "User ID from SharedPreferences: " + userId);

        // Retrieve user from Realm database
        User user = realm.where(User.class).equalTo("id", userId).findFirst();

        // Display profile details if found, otherwise show a toast message
        if (user != null) {
            usernameTextView.setText(user.getUsername());
            emailTextView.setText(user.getEmail());
            genderTextView.setText(user.getGender());
            ageTextView.setText(user.getAge());
            imageViewUser.setImageResource(user.getImageResourceId());
            // Add code to display other profile details if needed
        } else {
            Toast.makeText(requireContext(), "Profile details not available.", Toast.LENGTH_LONG).show();
        }
    }

    // Method to replace fragment
    private void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    // Close the Realm instance when the fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
