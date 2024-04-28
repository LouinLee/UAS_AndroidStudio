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

import java.text.DecimalFormat;

import io.realm.Realm;

public class BookedTrainerFragment extends Fragment {

    private Realm realm;
    private TextView name, description, rating, client, experience, price, deleteTrainerButton;
    private ImageView imageView;
    private Button btnChat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booked_trainer, container, false);

        name = view.findViewById(R.id.textViewTitle);
        description = view.findViewById(R.id.textViewDescription);
        experience = view.findViewById(R.id.textViewExperience);
        client = view.findViewById(R.id.textViewClient);
        rating = view.findViewById(R.id.textViewRating);
        price = view.findViewById(R.id.textViewPrice);
        imageView = view.findViewById(R.id.imageViewTrainer);
        btnChat = view.findViewById(R.id.btnChat);
        deleteTrainerButton = view.findViewById(R.id.deleteTrainerButton);

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
            description.setText(trainer.getDescription());
            experience.setText(trainer.getExperience());
            client.setText(trainer.getClient());
            rating.setText(trainer.getRating());
            // Get the trainer's price
            double trainerPrice = trainer.getPrice();
            // Create a DecimalFormat object to format the price
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            price.setText(decimalFormat.format(trainerPrice));
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
