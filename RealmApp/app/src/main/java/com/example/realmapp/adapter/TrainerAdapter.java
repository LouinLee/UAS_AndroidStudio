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
import com.example.realmapp.model.GymClass;
import com.example.realmapp.model.Trainer;

import java.util.List;

public class TrainerAdapter extends RecyclerView.Adapter<TrainerAdapter.ViewHolder> {

    private List<Trainer> trainerList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Trainer trainer);
    }

    public TrainerAdapter(List<Trainer> trainerList, OnItemClickListener listener) {
        this.trainerList = trainerList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trainer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trainer trainer  = trainerList.get(position);
        Log.d("TrainerAdapter", "Binding position: " + position + " with title: " + trainer.getName());
        holder.bind(trainer, listener);
    }

    @Override
    public int getItemCount() {
        return trainerList != null ? trainerList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, descriptionTextView;
        ImageView trainerImageView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            trainerImageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final Trainer trainer, final OnItemClickListener listener) {
            titleTextView.setText(trainer.getName());
            descriptionTextView.setText(trainer.getDescription());
            trainerImageView.setImageResource(trainer.getImageResourceId() != 0 ? trainer.getImageResourceId() : R.drawable.default_image);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(trainer);
            });
        }
    }
}
