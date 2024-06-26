package com.example.realmapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.realmapp.R;
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.User;

import java.text.DecimalFormat;

import io.realm.Realm;

public class BookedClassFragment extends Fragment {

    private Realm realm;
    private TextView title, description, time, price, deleteBookClassButton, joined, maximum, difficulty;
    private ImageView imageView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_booked_class, container, false);

        title = view.findViewById(R.id.textViewTitle);
        description = view.findViewById(R.id.textViewDescription);
        time = view.findViewById(R.id.textViewTime);
        price = view.findViewById(R.id.textViewPrice);
        joined = view.findViewById(R.id.textViewJoined);
        maximum = view.findViewById(R.id.textViewMaximum);
        difficulty = view.findViewById(R.id.textViewDifficulty);
        imageView = view.findViewById(R.id.imageViewClass);
        deleteBookClassButton = view.findViewById(R.id.deleteClassButton);

        deleteBookClassButton.setOnClickListener(v -> {
            String classId = getArguments() != null ? getArguments().getString("classId") : null;
            if (classId != null) {
                removeBookedClass(classId);
            } else {
                Toast.makeText(getContext(), "Class ID not found", Toast.LENGTH_SHORT).show();
            }
        });

        realm = Realm.getDefaultInstance();

        loadClassDetails();

        return view;
    }

    private void loadClassDetails() {
        String classId = getArguments() != null ? getArguments().getString("classId") : null;
        if (classId == null) {
            Toast.makeText(getContext(), "Class ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

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

    private void removeBookedClass(String classId) {
        realm.executeTransactionAsync(realm -> {
            User user = realm.where(User.class).findFirst();
            if (user != null) {
                user.removeBookedClass(classId);
            }
        }, () -> {
            Toast.makeText(getContext(), "Class removed successfully", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
        }, error -> {
            Toast.makeText(getContext(), "Failed to remove class: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
