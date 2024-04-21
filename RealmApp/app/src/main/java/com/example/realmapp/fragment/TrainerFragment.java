/*
 * TrainerFragment.java
 * This fragment displays a list of trainers and handles navigation to the TrainerDetailFragment.
 */

package com.example.realmapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realmapp.R;
import com.example.realmapp.adapter.TrainerAdapter;
import com.example.realmapp.model.Trainer;

import io.realm.Realm;
import io.realm.RealmResults;

public class TrainerFragment extends Fragment {

    // RecyclerView for displaying trainers
    private RecyclerView recyclerView;

    // Adapter for the RecyclerView
    private TrainerAdapter adapter;

    // Realm instance
    private Realm realm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trainer, container, false);

        // Initialize RecyclerView
        recyclerView = view.findViewById(R.id.trainerRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Initialize Realm
        realm = Realm.getDefaultInstance();

        // Query all trainers asynchronously
        RealmResults<Trainer> trainers = realm.where(Trainer.class).findAllAsync();

        // Set up the adapter
        adapter = new TrainerAdapter(trainers, trainer -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            TrainerDetailFragment detailFragment = new TrainerDetailFragment();
            Bundle args = new Bundle();
            args.putString("trainerId", trainer.getId());
            detailFragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        // Add a change listener to update the RecyclerView when data changes
        trainers.addChangeListener(trainers1 -> adapter.notifyDataSetChanged());

        return view;
    }

    // Close the Realm instance when the fragment is destroyed
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}
