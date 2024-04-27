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
import androidx.fragment.app.FragmentManager;

import com.example.realmapp.R;
import com.example.realmapp.activity.HomeActivity;
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.Trainer;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class TrainerDetailFragment extends Fragment {

    private Realm realm;
    private TextView name, rating, experience, price, bookButton;
    private ImageView imageView;
    private Button btnChat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer_detail, container, false);

        // Initialize UI components
        name = view.findViewById(R.id.textViewTitle);
        rating = view.findViewById(R.id.textViewRating);
        experience = view.findViewById(R.id.textViewExperience);
        price = view.findViewById(R.id.textViewPrice);
        imageView = view.findViewById(R.id.imageViewTrainer);
        btnChat = view.findViewById(R.id.btnChat);
        bookButton = view.findViewById(R.id.bookButton);
        // Load trainer details
        loadTrainerDetails();


        btnChat.setOnClickListener(v -> {
            // Replace the current fragment with TrainerChatFragment
            FragmentManager fragmentManager = getParentFragmentManager();
            TrainerChatFragment trainerChatFragment = new TrainerChatFragment();
            Bundle args = new Bundle();
            args.putString("trainerId", getArguments().getString("trainerId"));
            trainerChatFragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, trainerChatFragment)
                    .addToBackStack(null)
                    .commit();
        });

        bookButton.setOnClickListener(this::attemptToBookTrainer);
        return view;
    }

    private void loadTrainerDetails() {
        String trainerId = getArguments() != null ? getArguments().getString("trainerId", "") : "";
        Trainer trainer = realm.where(Trainer.class).equalTo("id", trainerId).findFirst();

        if (trainer != null) {
            name.setText(trainer.getName());
            rating.setText(trainer.getRating());
            experience.setText(trainer.getExperience());
            price.setText(trainer.getPrice());
            imageView.setImageResource(trainer.getImageResourceId());
        } else {
            Toast.makeText(getContext(), "Trainer details not available.", Toast.LENGTH_LONG).show();
        }
    }

    private void attemptToBookTrainer(View view) {
        if (getContext() == null) return;

        SharedPreferences prefs = getContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        Log.d("TrainerDetailFragment", "User ID from SharedPreferences: " + userId);

        if (userId == null) {
            Toast.makeText(getContext(), "User ID not found, please log in again.", Toast.LENGTH_LONG).show();
            return;
        }

        realm.executeTransactionAsync(r -> {
            User user = r.where(User.class).equalTo("id", userId).findFirst();
            String trainerId = getArguments() != null ? getArguments().getString("trainerId", "") : "";
            Trainer managedTrainer = r.where(Trainer.class).equalTo("id", trainerId).findFirst();

            if (user != null && managedTrainer != null) {
                // Check if the class is already booked
                if (!user.getBookedTrainer().contains(managedTrainer)) {
                    user.getBookedTrainer().add(managedTrainer);
                    if (getContext() != null) {
                        getActivity().runOnUiThread(() ->
                                Toast.makeText(getContext(), "Trainer booked successfully", Toast.LENGTH_SHORT).show()
                        );
                        goToHomeActivity();
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
                    throw new IllegalStateException("Trainer not found.");
                }
            }
        }, () -> {
            // Success handler will not be called here; moved inside transaction logic
        }, this::onError);
    }

    private void onError(Throwable error) {
        if (getContext() == null) return;
        Toast.makeText(getContext(), "Failed to book trainer: " + error.getMessage(), Toast.LENGTH_LONG).show();
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
