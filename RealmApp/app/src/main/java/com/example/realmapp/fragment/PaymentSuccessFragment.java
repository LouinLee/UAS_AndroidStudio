package com.example.realmapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.realmapp.R;
import com.example.realmapp.activity.HomeActivity;
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.Trainer;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class PaymentSuccessFragment extends Fragment {
    private Realm realm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment_success, container, false);

        // Load either class details or trainer details based on the source fragment
        String sourceFragment = getArguments().getString("sourceFragment", "");
        if (sourceFragment.equals("TrainerDetailFragment")) {
            attemptToBookTrainer();
        } else if (sourceFragment.equals("ClassDetailFragment")) {
            attemptToBookClass();
        } else {
            // Handle case where source fragment is not provided or unrecognized
            Toast.makeText(getContext(), "Invalid source fragment", Toast.LENGTH_SHORT).show();
        }
        return view;
    }

    private void attemptToBookClass() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("ClassDetailFragment", "User ID from SharedPreferences: " + userId);

        if (userId == null) {
            Toast.makeText(getContext(), "User ID not found, please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        String classId = getArguments() != null ? getArguments().getString("classId", "") : "";
        if (classId.isEmpty()) {
            Toast.makeText(getContext(), "Class ID not provided.", Toast.LENGTH_LONG).show();
            return;
        }

        realm.executeTransactionAsync(r -> {
            User user = r.where(User.class).equalTo("id", userId).findFirst();
            GymClass managedGymClass = r.where(GymClass.class).equalTo("id", classId).findFirst();

            if (user != null && managedGymClass != null) {
                // Check if the class is already booked
                if (!user.getBookedClasses().contains(managedGymClass)) {
                    user.getBookedClasses().add(managedGymClass);
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Class booked successfully", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "This class is already booked.", Toast.LENGTH_SHORT).show()
                        );
                    }
                    r.cancelTransaction(); // Cancel the transaction correctly using Realm's API
                }
            } else {
                if (user == null) {
                    Log.e("Realm", "User not found");
                    throw new IllegalStateException("User not found.");
                }
                if (managedGymClass == null) {
                    Log.e("Realm", "Class not found");
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Class not found.", Toast.LENGTH_LONG).show()
                    );
                }
            }
        }, () -> {
            // Success handler will not be called here; moved inside transaction logic
            goToHomeActivity();
        }, this::onError);
    }

    private void attemptToBookTrainer() {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("TrainerDetailFragment", "User ID from SharedPreferences: " + userId);

        if (userId == null) {
            Toast.makeText(getContext(), "User ID not found, please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        String trainerId = getArguments() != null ? getArguments().getString("trainerId", "") : "";
        if (trainerId.isEmpty()) {
            Toast.makeText(getContext(), "Trainer ID not provided.", Toast.LENGTH_LONG).show();
            return;
        }

        realm.executeTransactionAsync(r -> {
            User user = r.where(User.class).equalTo("id", userId).findFirst();
            Trainer managedTrainer = r.where(Trainer.class).equalTo("id", trainerId).findFirst();

            if (user != null && managedTrainer != null) {
                // Check if the trainer is already booked
                if (!user.getBookedTrainer().contains(managedTrainer)) {
                    user.getBookedTrainer().add(managedTrainer);
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Trainer booked successfully", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "This trainer is already booked.", Toast.LENGTH_SHORT).show()
                        );
                    }
                    r.cancelTransaction(); // Cancel the transaction correctly using Realm's API
                }
            } else {
                if (user == null) {
                    Log.e("Realm", "User not found");
                    throw new IllegalStateException("User not found.");
                }
                if (managedTrainer == null) {
                    Log.e("Realm", "Trainer not found");
                    getActivity().runOnUiThread(() ->
                            Toast.makeText(getContext(), "Trainer not found.", Toast.LENGTH_LONG).show()
                    );
                }
            }
        }, () -> {
            // Success handler will not be called here; moved inside transaction logic
            goToHomeActivity();
        }, this::onError);
    }

    private void onError(Throwable error) {
        if (getContext() == null) return;
        Toast.makeText(getContext(), "Failed to book trainer: " + error.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void goToHomeActivity() {
        if (getActivity() == null) return;

        // Delayed task to navigate to HomeActivity after 5 seconds
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getActivity(), HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            getActivity().finish();
        }, 2000); // 2000 milliseconds = 2 seconds
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
