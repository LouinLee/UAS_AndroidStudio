/*
 * ProfileFragment.java
 * This fragment displays the user's profile information and their booked classes and trainers.
 * It allows users to log out from their account.
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realmapp.R;
import com.example.realmapp.activity.MainActivity;
import com.example.realmapp.adapter.ClassAdapter;
import com.example.realmapp.adapter.TrainerAdapter;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class ProfileFragment extends Fragment {

    // UI components
    private RecyclerView bookedClassesRecyclerView, bookedTrainerRecyclerView;
    private TextView usernameTextView;
    private Button logoutButton;

    // Realm instance
    private Realm realm;

    // Adapters for displaying booked classes and trainers
    private ClassAdapter classAdapter;
    private TrainerAdapter trainerAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI components
        usernameTextView = view.findViewById(R.id.usernameTextView);
        bookedClassesRecyclerView = view.findViewById(R.id.bookedClassesRecyclerView);
        bookedTrainerRecyclerView = view.findViewById(R.id.bookedTrainerRecyclerView);
        logoutButton = view.findViewById(R.id.logoutButton);

        // Set layout managers for RecyclerViews
        bookedClassesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookedTrainerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Realm
        realm = Realm.getDefaultInstance();

        // Set OnClickListener for logout button
        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    // Method to handle user logout
    private void logoutUser() {
        // Clear SharedPreferences
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("userId");
        editor.apply();

        // Redirect to Login Screen
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    // Refresh UI when the fragment is resumed
    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    // Method to update the UI with user's profile information and booked classes/trainers
    private void updateUI() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        if (userId == null) {
            Log.d("ProfileFragment", "User ID not found in SharedPreferences");
            return;
        }

        User currentUser = realm.where(User.class).equalTo("id", userId).findFirst();
        if (currentUser == null) {
            Log.d("ProfileFragment", "No user found in Realm with ID: " + userId);
            return;
        }

        // Display username
        usernameTextView.setText(currentUser.getUsername());

        // Display booked classes, if any
        if (currentUser.getBookedClasses() != null) {
            classAdapter = new ClassAdapter(currentUser.getBookedClasses(), null);
            bookedClassesRecyclerView.setAdapter(classAdapter);
        } else {
            bookedClassesRecyclerView.setAdapter(null); // Clear adapter if no classes booked
        }

        // Display booked trainers, if any
        if (currentUser.getBookedTrainer() != null) {
            trainerAdapter = new TrainerAdapter(currentUser.getBookedTrainer(), null);
            bookedTrainerRecyclerView.setAdapter(trainerAdapter);
        } else {
            bookedTrainerRecyclerView.setAdapter(null); // Clear adapter if no trainers booked
        }
    }

    // Close the Realm instance when the fragment view is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
