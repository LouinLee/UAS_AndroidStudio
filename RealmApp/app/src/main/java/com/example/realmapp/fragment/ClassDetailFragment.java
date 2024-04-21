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

    private Realm realm;
    private TextView title, description, time;
    private ImageView imageView;
    private Button bookButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        bookButton.setOnClickListener(this::attemptToBookClass);
        return view;
    }

    private void loadClassDetails() {
        String classId = getArguments() != null ? getArguments().getString("classId", "") : "";
        GymClass gymClass = realm.where(GymClass.class).equalTo("id", classId).findFirst();

        if (gymClass != null) {
            title.setText(gymClass.getTitle());
            description.setText(gymClass.getDescription());
            time.setText(gymClass.getTime());
            imageView.setImageResource(gymClass.getImageResourceId());
        } else {
            Toast.makeText(getContext(), "Class details not available.", Toast.LENGTH_LONG).show();
        }
    }

    private void attemptToBookClass(View view) {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("ClassDetailFragment", "User ID from SharedPreferences: " + userId);

        if (userId == null) {
            Toast.makeText(getContext(), "User ID not found, please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        realm.executeTransactionAsync(r -> {
            User user = r.where(User.class).equalTo("id", userId).findFirst();
            String classId = getArguments() != null ? getArguments().getString("classId", "") : "";
            GymClass managedGymClass = r.where(GymClass.class).equalTo("id", classId).findFirst();

            if (user != null && managedGymClass != null) {
                // Check if the class is already booked
                if (!user.getBookedClasses().contains(managedGymClass)) {
                    user.getBookedClasses().add(managedGymClass);
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Class booked successfully", Toast.LENGTH_SHORT).show()
                        );
                        goToHomeActivity();
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
                    throw new IllegalStateException("Class not found.");
                }
            }
        }, () -> {
            // Success handler will not be called here; moved inside transaction logic
        }, this::onError);
    }

    private void onError(Throwable error) {
        if (getContext() == null) return;
        Toast.makeText(getContext(), "Failed to book class: " + error.getMessage(), Toast.LENGTH_LONG).show();
    }

    private void goToHomeActivity() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
