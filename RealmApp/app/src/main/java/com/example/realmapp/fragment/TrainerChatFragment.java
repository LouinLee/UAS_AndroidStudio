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
import com.example.realmapp.model.Trainer;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class TrainerChatFragment extends Fragment {
    // Realm instance
    private Realm realm;

    // UI components
    private TextView name;
    private ImageView imageViewTrainer;
    private Button bookButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer_chat, container, false);

        name = view.findViewById(R.id.textViewTitle);
        imageViewTrainer = view.findViewById(R.id.imageViewTrainer);
        // Load trainer details
        loadTrainerDetails();
        return view;
    }

    // Method to load trainer details
    private void loadTrainerDetails() {
        String trainerId = getArguments() != null ? getArguments().getString("trainerId", "") : "";
        Trainer trainer = realm.where(Trainer.class).equalTo("id", trainerId).findFirst();

        if (trainer != null) {
            name.setText(trainer.getName());
            imageViewTrainer.setImageResource(trainer.getImageResourceId());
        } else {
            Toast.makeText(getContext(), "Trainer details not available.", Toast.LENGTH_LONG).show();
        }
    }
}