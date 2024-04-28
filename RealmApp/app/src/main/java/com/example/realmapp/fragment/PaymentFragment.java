package com.example.realmapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.realmapp.R;
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.Trainer;

import java.text.DecimalFormat;

import io.realm.Realm;

public class PaymentFragment extends Fragment {
    private Realm realm;
    private TextView priceTextView, confirmPaymentButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        realm = Realm.getDefaultInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        priceTextView = view.findViewById(R.id.textViewPrice);
        confirmPaymentButton = view.findViewById(R.id.confirmPaymentButton);

        // Load either class details or trainer details based on the source fragment
        String sourceFragment = getArguments().getString("sourceFragment", "");
        if (sourceFragment.equals("TrainerDetailFragment")) {
            loadTrainerDetails("TrainerDetailFragment");
            confirmPaymentButton.setOnClickListener(v -> {
                FragmentManager fragmentManager = getParentFragmentManager();
                PaymentSuccessFragment paymentSuccessFragment = new PaymentSuccessFragment();
                Bundle args = new Bundle();
                args.putString("trainerId", getArguments().getString("trainerId"));
                args.putString("sourceFragment", "TrainerDetailFragment");
                paymentSuccessFragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, paymentSuccessFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else if (sourceFragment.equals("ClassDetailFragment")) {
            loadClassDetails("ClassDetailFragment");
            confirmPaymentButton.setOnClickListener(v -> {
                FragmentManager fragmentManager = getParentFragmentManager();
                PaymentSuccessFragment paymentSuccessFragment = new PaymentSuccessFragment();
                Bundle args = new Bundle();
                args.putString("classId", getArguments().getString("classId"));
                args.putString("sourceFragment", "ClassDetailFragment");
                paymentSuccessFragment.setArguments(args);
                fragmentManager.beginTransaction()
                        .replace(R.id.frame_container, paymentSuccessFragment)
                        .addToBackStack(null)
                        .commit();
            });
        } else {
            // Handle case where source fragment is not provided or unrecognized
            Toast.makeText(getContext(), "Invalid source fragment", Toast.LENGTH_SHORT).show();
        }
        return view;
    }
    private void loadClassDetails(String sourceFragment) {
        // Check if the source fragment matches the required fragment for loading class details
        if (sourceFragment.equals("ClassDetailFragment")) {
            String classId = getArguments() != null ? getArguments().getString("classId", "") : "";
            GymClass gymClass = realm.where(GymClass.class).equalTo("id", classId).findFirst();

            if (gymClass != null) {
                // Get the class's price
                double classPrice = gymClass.getPrice();
                // Create a DecimalFormat object to format the price
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                priceTextView.setText(decimalFormat.format(classPrice));
            } else {
                Toast.makeText(getContext(), "Class details not available.", Toast.LENGTH_LONG).show();
                // You may want to set a default value for the priceTextView or handle it according to your app's logic.
            }
        }
    }

    private void loadTrainerDetails(String sourceFragment) {
        // Check if the source fragment matches the required fragment for loading trainer details
        if (sourceFragment.equals("TrainerDetailFragment")) {
            String trainerId = getArguments() != null ? getArguments().getString("trainerId", "") : "";
            Trainer trainer = realm.where(Trainer.class).equalTo("id", trainerId).findFirst();

            if (trainer != null) {
                // Get the trainer's price
                double trainerPrice = trainer.getPrice();
                // Create a DecimalFormat object to format the price
                DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
                priceTextView.setText(decimalFormat.format(trainerPrice));
            } else {
                Toast.makeText(getContext(), "Trainer details not available.", Toast.LENGTH_LONG).show();
                // You may want to set a default value for the priceTextView or handle it according to your app's logic.
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
