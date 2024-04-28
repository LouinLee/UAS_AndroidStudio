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
import com.example.realmapp.adapter.ClassAdapter;
import com.example.realmapp.model.GymClass;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;

public class ClassFragment extends Fragment {

    private RecyclerView recyclerView;
    private ClassAdapter adapter;
    private Realm realm;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_class, container, false);
        recyclerView = view.findViewById(R.id.classesRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        realm = Realm.getDefaultInstance();
        RealmResults<GymClass> classes = realm.where(GymClass.class).findAllAsync();

        adapter = new ClassAdapter(classes, gymClass -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            ClassDetailFragment detailFragment = new ClassDetailFragment();
            Bundle args = new Bundle();
            args.putString("classId", gymClass.getId());
            detailFragment.setArguments(args);
            fragmentManager.beginTransaction()
                    .replace(R.id.frame_container, detailFragment)
                    .addToBackStack(null)
                    .commit();
        });
        recyclerView.setAdapter(adapter);

        // Add a change listener to update the RecyclerView when data changes
        classes.addChangeListener(new RealmChangeListener<RealmResults<GymClass>>() {
            @Override
            public void onChange(RealmResults<GymClass> gymClasses) {
                adapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realm.close();
    }
}