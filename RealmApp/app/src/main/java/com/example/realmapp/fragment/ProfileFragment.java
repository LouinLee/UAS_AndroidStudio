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

    private RecyclerView bookedClassesRecyclerView, bookedTrainerRecyclerView;
    private TextView usernameTextView;
    private Realm realm;
    private ClassAdapter classAdapter;
    private TrainerAdapter trainerAdapter;
    private Button logoutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        usernameTextView = view.findViewById(R.id.usernameTextView);
        bookedClassesRecyclerView = view.findViewById(R.id.bookedClassesRecyclerView);
        bookedTrainerRecyclerView = view.findViewById(R.id.bookedTrainerRecyclerView);
        logoutButton = view.findViewById(R.id.logoutButton);

        bookedClassesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        bookedTrainerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        realm = Realm.getDefaultInstance();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        return view;
    }

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

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

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

        // Assuming the username is also stored in the User model in Realm
        usernameTextView.setText(currentUser.getUsername());

        Log.d("ProfileFragment", "User found: " + currentUser.getUsername());
        if (currentUser.getBookedClasses() != null) {
            Log.d("ProfileFragment", "Booked Classes: " + currentUser.getBookedClasses().size());
            classAdapter = new ClassAdapter(currentUser.getBookedClasses(), null);
            bookedClassesRecyclerView.setAdapter(classAdapter);
        } else {
            Log.d("ProfileFragment", "No classes booked");
            bookedClassesRecyclerView.setAdapter(null); // Clear adapter if no classes
        }

        if (currentUser.getBookedTrainer() != null) {
            Log.d("ProfileFragment", "Booked Trainer: " + currentUser.getBookedTrainer().size());
            trainerAdapter = new TrainerAdapter(currentUser.getBookedTrainer(), null);
            bookedTrainerRecyclerView.setAdapter(trainerAdapter);
        } else {
            Log.d("ProfileFragment", "No trainer booked");
            bookedTrainerRecyclerView.setAdapter(null); // Clear adapter if no classes
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
