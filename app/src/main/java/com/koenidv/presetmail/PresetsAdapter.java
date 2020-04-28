package com.koenidv.presetmail;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.recyclerview.widget.RecyclerView;

//  Created by koenidv on 28.04.2020.
public class PresetsAdapter extends RecyclerView.Adapter<PresetsAdapter.ViewHolder> {

    private List<Mail> mDataset;

    PresetsAdapter(List<Mail> dataset) {
        mDataset = dataset;
    }

    @NonNull
    @Override

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_preset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(mDataset.get(position).getName());
        holder.view.setTag(position);
        holder.editButton.setTag(position);
        holder.removeButton.setTag(position);
        holder.pinButton.setTag(position);
        holder.sendButton.setTag(position);

        if (!ShortcutManagerCompat.isRequestPinShortcutSupported(holder.view.getContext()))
            holder.pinButton.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView;
        ImageButton editButton, removeButton, pinButton, sendButton;
        View view;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.presetTextView);
            editButton = itemView.findViewById(R.id.editButton);
            removeButton = itemView.findViewById(R.id.removeButton);
            pinButton = itemView.findViewById(R.id.pinButton);
            sendButton = itemView.findViewById(R.id.sendButton);
            view = itemView;
        }
    }
}
