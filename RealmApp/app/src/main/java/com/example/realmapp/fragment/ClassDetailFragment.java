/*
 * ClassDetailFragment.java
 * This fragment displays details of a specific gym class and allows users to book the class.
 */

package com.example.realmapp.fragment;

import android.content.Context;
import android.content.Intent;
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

import com.example.realmapp.R;
import com.example.realmapp.activity.HomeActivity;
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class ClassDetailFragment extends Fragment {

    // Realm instance
    private Realm realm;

    // UI elements
    private TextView title, description, time;
    private ImageView imageView;
    private Button bookButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize Realm
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class_detail, container, false);

        // Initialize UI components
        title = view.findViewById(R.id.textViewTitle);
        description = view.findViewById(R.id.textViewDescription);
        time = view.findViewById(R.id.textViewTime);
        imageView = view.findViewById(R.id.imageViewClass);
        bookButton = view.findViewById(R.id.bookClassButton);

        // Load class details
        loadClassDetails();

        // Set click listener for book button
        bookButton.setOnClickListener(this::attemptToBookClass);
        return view;
    }

    // Method to load class details
    private void loadClassDetails() {
        // Get class ID from arguments
        String classId = getArguments() != null ? getArguments().getString("classId", "") : "";
        // Retrieve gym class from Realm database
        GymClass gymClass = realm.where(GymClass.class).equalTo("id", classId).findFirst();

        // Display class details if found, otherwise show a toast message
        if (gymClass != null) {
            title.setText(gymClass.getTitle());
            description.setText(gymClass.getDescription());
            time.setText(gymClass.getTime());
            imageView.setImageResource(gymClass.getImageResourceId());
        } else {
            Toast.makeText(getContext(), "Class details not available.", Toast.LENGTH_LONG).show();
        }
    }

    // Method to attempt booking the class
    private void attemptToBookClass(View view) {
        // Ensure context is not null
        if (getContext() == null) return;

        // Get user ID from SharedPreferences
        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("ClassDetailFragment", "User ID from SharedPreferences: " + userId);

        // If user ID is not found, show a toast message and return
        if (userId == null) {
            Toast.makeText(getContext(), "User ID not found, please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        // Execute Realm transaction asynchronously to book the class
        realm.executeTransactionAsync(r -> {
            // Retrieve user and gym class objects
            User user = r.where(User.class).equalTo("id", userId).findFirst();
            String classId = getArguments() != null ? getArguments().getString("classId", "") : "";
            GymClass managedGymClass = r.where(GymClass.class).equalTo("id", classId).findFirst();

            // Check if user and gym class objects exist
            if (user != null && managedGymClass != null) {
                // Check if the class is already booked
                if (!user.getBookedClasses().contains(managedGymClass)) {
                    // Book the class
                    user.getBookedClasses().add(managedGymClass);
                    // Show toast message for successful booking
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Class booked successfully", Toast.LENGTH_SHORT).show()
                        );
                        // Navigate to the home activity
                        goToHomeActivity();
                    }
                } else {
                    // Show toast message if the class is already booked
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "This class is already booked.", Toast.LENGTH_SHORT).show()
                        );
                    }
                    // Cancel the transaction if the class is already booked
                    r.cancelTransaction();
                }
            } else {
                // Throw exceptions if user or gym class objects are not found
                if (user == null) {
                    Log.e("Realm", "User not found");
                    throw new IllegalStateException("User not found.");
                }
                if (managedGymClass == null) {
                    Log.e("Realm", "Class not found");
                    throw new IllegalStateException("Class not found.");
                }
            }
        }, () -> {
            // Success handler will not be called here; moved inside transaction logic
        }, this::onError);
    }

    // Error handling for Realm transactions
    private void onError(Throwable error) {
        if (getContext() == null) return;
        Toast.makeText(getContext(), "Failed to book class: " + error.getMessage(), Toast.LENGTH_LONG).show();
    }

    // Method to navigate to the home activity
    private void goToHomeActivity() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    // Close the Realm instance when the fragment is destroyed
    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
