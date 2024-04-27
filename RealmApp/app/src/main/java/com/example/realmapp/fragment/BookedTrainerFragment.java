package com.example.realmapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.realmapp.R;
import com.example.realmapp.model.Trainer;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class BookedTrainerFragment extends Fragment {

    private Realm realm;
    private TextView name, rating, experience, deleteTrainerButton;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booked_trainer, container, false);

        name = view.findViewById(R.id.textViewTitle);
        experience = view.findViewById(R.id.textViewExperience);
        rating = view.findViewById(R.id.textViewRating);
        imageView = view.findViewById(R.id.imageViewTrainer);
        deleteTrainerButton = view.findViewById(R.id.deleteTrainerButton);

        deleteTrainerButton.setOnClickListener(v -> {
            String trainerId = getArguments() != null ? getArguments().getString("trainerId") : null;
            if (trainerId != null) {
                removeBookedTrainer(trainerId);
            } else {
                Toast.makeText(getContext(), "Trainer ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        realm = Realm.getDefaultInstance();

        loadTrainerDetails();

        return view;
    }

    private void loadTrainerDetails() {
        String trainerId = getArguments() != null ? getArguments().getString("trainerId") : null;
        if (trainerId == null) {
            Toast.makeText(getContext(), "Trainer ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        Trainer trainer = realm.where(Trainer.class).equalTo("id", trainerId).findFirst();

        if (trainer != null) {
            name.setText(trainer.getName());
            experience.setText(trainer.getExperience());
            rating.setText(trainer.getRating());
            imageView.setImageResource(trainer.getImageResourceId());
        } else {
            Toast.makeText(getContext(), "Trainer details not available.", Toast.LENGTH_LONG).show();
        }
    }

    private void removeBookedTrainer(String trainerId) {
        realm.executeTransactionAsync(realm -> {
            User user = realm.where(User.class).findFirst();
            if (user != null) {
                user.removeBookedTrainer(trainerId);
            }
        }, () -> {
            Toast.makeText(getContext(), "Trainer removed successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }, error -> {
            Toast.makeText(getContext(), "Failed to remove trainer: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (realm != null) {
            realm.close();
        }
    }
}
