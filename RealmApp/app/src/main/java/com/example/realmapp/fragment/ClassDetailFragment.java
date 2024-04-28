package com.example.realmapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.realmapp.R;
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.User;

import java.text.DecimalFormat;

import io.realm.Realm;

public class ClassDetailFragment extends Fragment {

    private Realm realm;
    private TextView title, description, time, price, bookButton, joined, maximum, difficulty;
    private ImageView imageView;
    // User object
    private User user;

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
        price = view.findViewById(R.id.textViewPrice);
        joined = view.findViewById(R.id.textViewJoined);
        maximum = view.findViewById(R.id.textViewMaximum);
        difficulty = view.findViewById(R.id.textViewDifficulty);
        imageView = view.findViewById(R.id.imageViewClass);
        bookButton = view.findViewById(R.id.bookClassButton);

        // Load class details
        loadClassDetails();

        bookButton.setOnClickListener(v -> {
            // Check if the user already has 3 classes booked
            if (user != null && user.getBookedClasses().size() >= 3) {
                // Show a toast message indicating that the maximum number of classes has been reached
                Toast.makeText(requireContext(), "You already have 3 classes booked.", Toast.LENGTH_SHORT).show();
            } else {
                // Check if the class is already booked by the user
                GymClass gymClass = realm.where(GymClass.class).equalTo("id", getArguments().getString("classId")).findFirst();
                if (gymClass != null && user != null && user.getBookedClasses().contains(gymClass)) {
                    // Show a toast message indicating that the class is already booked
                    Toast.makeText(requireContext(), "You have already booked this class.", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to payment fragment
                    FragmentManager fragmentManager = getParentFragmentManager();
                    PaymentFragment paymentFragment = new PaymentFragment();
                    Bundle args = new Bundle();
                    args.putString("classId", getArguments().getString("classId"));
                    args.putString("sourceFragment", "ClassDetailFragment");
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

    private void loadClassDetails() {
        String classId = getArguments() != null ? getArguments().getString("classId", "") : "";
        GymClass gymClass = realm.where(GymClass.class).equalTo("id", classId).findFirst();

        if (gymClass != null) {
            title.setText(gymClass.getTitle());
            description.setText(gymClass.getDescription());
            time.setText(gymClass.getTime());
            maximum.setText(String.valueOf((int) gymClass.getMaximum()));
            joined.setText(String.valueOf((int) gymClass.getJoined()));
            // Get the class's price
            double classPrice = gymClass.getPrice();
            // Create a DecimalFormat object to format the price
            DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
            price.setText(decimalFormat.format(classPrice));
            difficulty.setText(gymClass.getDifficulty());
            imageView.setImageResource(gymClass.getImageResourceId());
        } else {
            Toast.makeText(getContext(), "Class details not available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Load user details when the fragment resumes
        loadUserDetails();
    }

    private void loadUserDetails() {
        // Load user details from Realm
        user = realm.where(User.class).findFirst();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
