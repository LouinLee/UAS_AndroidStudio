package com.example.realmapp.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import com.example.realmapp.model.Trainer;
import com.example.realmapp.model.User;

import java.text.DecimalFormat;

import io.realm.Realm;

public class TrainerDetailFragment extends Fragment {

    private Realm realm;
    private TextView name, description, rating, client, experience, price, bookButton;
    private ImageView imageView;
    private Button btnChat;
    // User object
    private User user;

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
        description = view.findViewById(R.id.textViewDescription);
        experience = view.findViewById(R.id.textViewExperience);
        client = view.findViewById(R.id.textViewClient);
        rating = view.findViewById(R.id.textViewRating);
        price = view.findViewById(R.id.textViewPrice);
        imageView = view.findViewById(R.id.imageViewTrainer);
        btnChat = view.findViewById(R.id.btnChat);
        bookButton = view.findViewById(R.id.bookButton);

        // Load trainer details
        loadTrainerDetails();

        // Retrieve the user object from Realm database
        SharedPreferences prefs = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        String userId = prefs.getString("userId", null);
        user = realm.where(User.class).equalTo("id", userId).findFirst();

        btnChat.setOnClickListener(v -> {
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

        bookButton.setOnClickListener(v -> {
            // Check if the user already has 2 trainers booked
            if (user != null && user.getBookedTrainer().size() >= 2) {
                // Show a toast message indicating that the maximum number of trainers has been reached
                Toast.makeText(requireContext(), "You already have 2 trainers booked.", Toast.LENGTH_SHORT).show();
            } else {
                // Check if the trainer is already booked by the user
                Trainer trainer = realm.where(Trainer.class).equalTo("id", getArguments().getString("trainerId")).findFirst();
                if (trainer != null && user != null && user.getBookedTrainer().contains(trainer)) {
                    // Show a toast message indicating that the trainer is already booked
                    Toast.makeText(requireContext(), "You have already booked this trainer.", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to payment fragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    PaymentFragment paymentFragment = new PaymentFragment();
                    Bundle args = new Bundle();
                    args.putString("trainerId", getArguments().getString("trainerId"));
                    args.putString("sourceFragment", "TrainerDetailFragment");
                    paymentFragment.setArguments(args);
                    fragmentManager.beginTransaction()
                            .replace(R.id.frame_container, paymentFragment)
                            .addToBackStack(null)
                            .commit();
                }
            }
            // Disable the book button to prevent further clicks
            bookButton.setEnabled(false);
        });

        return view;
    }

    private void loadTrainerDetails() {
        String trainerId = getArguments() != null ? getArguments().getString("trainerId", "") : "";
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
