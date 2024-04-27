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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realmapp.R;
import com.example.realmapp.activity.MainActivity;
import com.example.realmapp.adapter.ClassAdapter;
import com.example.realmapp.adapter.TrainerAdapter;
import com.example.realmapp.model.Trainer;
import com.example.realmapp.model.User;

import io.realm.Realm;

public class ProfileFragment extends Fragment {

    private RecyclerView bookedClassesRecyclerView, bookedTrainerRecyclerView;
    private TextView usernameTextView;
    private Button logoutButton;

    private Realm realm;

    private ClassAdapter classAdapter;
    private TrainerAdapter trainerAdapter;

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

        usernameTextView.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ProfileDetailFragment detailFragment = new ProfileDetailFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });

        logoutButton.setOnClickListener(v -> logoutUser());

        return view;
    }

    private void logoutUser() {
        SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("userId");
        editor.apply();

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
        try {
            SharedPreferences prefs = getActivity().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
            String userId = prefs.getString("userId", null);
            if (userId == null) {
                Log.d("ProfileFragment", "User ID not found in SharedPreferences");
                return;
            }

            if (realm == null) {
                Log.e("ProfileFragment", "Realm instance is null");
                return;
            }

            User currentUser = realm.where(User.class).equalTo("id", userId).findFirst();
            if (currentUser == null) {
                Log.d("ProfileFragment", "No user found in Realm with ID: " + userId);
                return;
            }

            usernameTextView.setText(currentUser.getUsername());

            if (currentUser.getBookedClasses() != null) {
                classAdapter = new ClassAdapter(currentUser.getBookedClasses(), gymClass -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("classId", gymClass.getId());
                    BookedClassFragment bookedClassFragment = new BookedClassFragment();
                    bookedClassFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, bookedClassFragment)
                            .addToBackStack(null)
                            .commit();
                });
                bookedClassesRecyclerView.setAdapter(classAdapter);
            } else {
                bookedClassesRecyclerView.setAdapter(null);
            }

            if (currentUser.getBookedTrainer() != null) {
                trainerAdapter = new TrainerAdapter(currentUser.getBookedTrainer(), trainer -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("trainerId", trainer.getId());
                    BookedTrainerFragment bookedTrainerFragment = new BookedTrainerFragment();
                    bookedTrainerFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frame_container, bookedTrainerFragment)
                            .addToBackStack(null)
                            .commit();
                });
                bookedTrainerRecyclerView.setAdapter(trainerAdapter);
            } else {
                bookedTrainerRecyclerView.setAdapter(null);
            }
        } catch (Exception e) {
            Log.e("ProfileFragment", "Error updating UI: " + e.getMessage(), e);
        }
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null) {
            realm.close();
        }
    }
}
