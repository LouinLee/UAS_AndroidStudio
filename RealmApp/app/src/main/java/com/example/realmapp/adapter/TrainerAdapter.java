/*
 * TrainerAdapter.java
 * This class is an adapter for the RecyclerView used to display trainers in the RealmApp application.
 * It binds Trainer objects to the item view, and provides a listener interface for item clicks.
 */

package com.example.realmapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.realmapp.R;
import com.example.realmapp.model.Trainer;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.ViewHolder> {

    // List of trainers
    private final List<Trainer> trainerList;

    // Listener for item clicks
    private final OnItemClickListener listener;

    // Interface for item click listener
    public interface OnItemClickListener {
        void onItemClick(Trainer trainer);
    }

    // Constructor
    public TrainerAdapter(List<Trainer> trainerList, OnItemClickListener listener) {
        this.trainerList = trainerList;
        this.listener = listener;
    }

    // Create view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trainer, parent, false);
        return new ViewHolder(view);
    }

    // Bind data to view holder
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trainer trainer = trainerList.get(position);
        Log.d("TrainerAdapter", "Binding position: " + position + " with title: " + trainer.getName());
        holder.bind(trainer, listener);
    }

    // Get item count
    @Override
    public int getItemCount() {
        return trainerList != null ? trainerList.size() : 0;
    }

    // View holder class
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView trainerImageView;

        // Constructor
        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            trainerImageView = itemView.findViewById(R.id.imageView);
        }

        // Bind data to views
        void bind(final Trainer trainer, final OnItemClickListener listener) {
            titleTextView.setText(trainer.getName());
            descriptionTextView.setText(trainer.getDescription());
            trainerImageView.setImageResource(trainer.getImageResourceId() != 0 ? trainer.getImageResourceId() : R.drawable.default_image);

            // Set click listener for item
            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(trainer);
            });
        }
    }
}
