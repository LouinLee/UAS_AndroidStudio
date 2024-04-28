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

import java.util.List;

public class ClassAdapter extends RecyclerView.Adapter<ClassAdapter.ViewHolder> {

    private List<GymClass> classList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GymClass gymClass);
    }

    public ClassAdapter(List<GymClass> classList, OnItemClickListener listener) {
        this.classList = classList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_class, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        GymClass gymClass = classList.get(position);
        Log.d("ClassAdapter", "Binding position: " + position + " with title: " + gymClass.getTitle());
        holder.bind(gymClass, listener);
    }

    @Override
    public int getItemCount() {
        return classList != null ? classList.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView descriptionTextView;
        TextView timeTextView;
        ImageView classImageView;

        ViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            descriptionTextView = itemView.findViewById(R.id.textViewDescription);
            timeTextView = itemView.findViewById(R.id.textViewDateTime);
            classImageView = itemView.findViewById(R.id.imageView);
        }

        void bind(final GymClass gymClass, final OnItemClickListener listener) {
            titleTextView.setText(gymClass.getTitle());
            descriptionTextView.setText(gymClass.getDescription());
            timeTextView.setText(gymClass.getTime());
            classImageView.setImageResource(gymClass.getImageResourceId() != 0 ? gymClass.getImageResourceId() : R.drawable.default_image);

            itemView.setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(gymClass);
            });
        }
    }
}
