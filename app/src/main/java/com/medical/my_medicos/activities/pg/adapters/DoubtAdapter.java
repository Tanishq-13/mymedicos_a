package com.medical.my_medicos.activities.pg.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.model.Doubt;

import java.util.List;

public class DoubtAdapter extends RecyclerView.Adapter<DoubtAdapter.DoubtViewHolder> {

    private List<Doubt> doubts;
    private DoubtClickListener clickListener;
    public interface DoubtClickListener {
        void onDoubtClick(String chatId);
    }

    public DoubtAdapter(List<Doubt> doubts, DoubtClickListener clickListener) {
        this.doubts = doubts;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public DoubtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_doubtchat, parent, false);
        return new DoubtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DoubtViewHolder holder, int position) {
        Doubt doubt = doubts.get(position);

        // Set the first letter of the subject as the icon
        holder.icon.setText(doubt.getSubject().substring(0, 1));

        // Set the subject title and message
        holder.title.setText(doubt.getSubject());
        String descriptionText = doubt.getMessage();
        Log.d("desctxt",descriptionText);
        if (descriptionText.length() > 25) {
            descriptionText = descriptionText.substring(0, 25) + "...";
        }
        holder.description.setText(descriptionText);
//        holder.description.setText(doubt.getMessage());

        // Set the status as "Closed" in red if it's closed, otherwise "Open" in green
        holder.status.setText(doubt.isClosed() ? "Closed" : "Open");
        holder.status.setTextColor(doubt.isClosed() ? Color.RED : Color.GREEN);

        holder.itemView.setOnClickListener(v -> clickListener.onDoubtClick(doubt.getChatId()));
    }

    @Override
    public int getItemCount() {
        return doubts.size();
    }

    static class DoubtViewHolder extends RecyclerView.ViewHolder {
        TextView icon, title, description, status;

        public DoubtViewHolder(View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            status = itemView.findViewById(R.id.status);
        }
    }
}
