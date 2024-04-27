package com.example.realmapp.fragment;

import android.os.Bundle;
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
import com.example.realmapp.adapter.ClassAdapter;
import com.example.realmapp.adapter.TrainerAdapter;
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.Trainer;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private Realm realm;
    private ClassAdapter classAdapter;
    private TrainerAdapter trainerAdapter;
    private RecyclerView classRecyclerView;
    private RecyclerView trainerRecyclerView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        realm = Realm.getDefaultInstance();

        // Setup featured classes
        classRecyclerView = view.findViewById(R.id.featuredClassesRecyclerView);
        classRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Setup featured trainers
        trainerRecyclerView = view.findViewById(R.id.featuredTrainersRecyclerView);
        trainerRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        setupFeaturedClasses();
        setupFeaturedTrainers();

        // See all classes button
        TextView seeAllClassesButton = view.findViewById(R.id.seeAllClassesButton);
        seeAllClassesButton.setOnClickListener(v -> navigateToClassFragment());

        // See all trainers button
        TextView seeAllTrainersButton = view.findViewById(R.id.seeAllTrainersButton);
        seeAllTrainersButton.setOnClickListener(v -> navigateToTrainerFragment());

        return view;
    }

    private void setupFeaturedClasses() {
        RealmResults<GymClass> classes = realm.where(GymClass.class).findAllAsync();
        classes.addChangeListener(new RealmChangeListener<RealmResults<GymClass>>() {
            @Override
            public void onChange(RealmResults<GymClass> results) {
                // Ensure the operation is safe
                if (!results.isEmpty()) {
                    List<GymClass> stableList = realm.copyFromRealm(results);
                    int size = Math.min(3, stableList.size());
                    List<GymClass> subList = stableList.subList(0, size);
                    classAdapter = new ClassAdapter(subList, HomeFragment.this::onClassClicked);
                    classRecyclerView.setAdapter(classAdapter);
                }
            }
        });
    }

    private void setupFeaturedTrainers() {
        RealmResults<Trainer> trainers = realm.where(Trainer.class).findAllAsync();
        trainers.addChangeListener(new RealmChangeListener<RealmResults<Trainer>>() {
            @Override
            public void onChange(RealmResults<Trainer> results) {
                if (!results.isEmpty()) {
                    List<Trainer> stableList = realm.copyFromRealm(results);
                    int size = Math.min(3, stableList.size());
                    List<Trainer> subList = stableList.subList(0, size);
                    trainerAdapter = new TrainerAdapter(subList, HomeFragment.this::onTrainerClicked);
                    trainerRecyclerView.setAdapter(trainerAdapter);
                }
            }
        });
    }


    private void onClassClicked(GymClass gymClass) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        ClassDetailFragment detailFragment = new ClassDetailFragment();
        Bundle args = new Bundle();
        args.putString("classId", gymClass.getId());
        detailFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void onTrainerClicked(Trainer trainer) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        TrainerDetailFragment detailFragment = new TrainerDetailFragment();
        Bundle args = new Bundle();
        args.putString("trainerId", trainer.getId());
        detailFragment.setArguments(args);
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, detailFragment)
                .addToBackStack(null)
                .commit();
    }

    private void navigateToClassFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, new ClassFragment())
                .addToBackStack(null)
                .commit();

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_class);  // Set the ID corresponding to the ClassFragment
    }

    private void navigateToTrainerFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.frame_container, new TrainerFragment())
                .addToBackStack(null)
                .commit();

        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_trainer);  // Set the ID corresponding to the ClassFragment
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
