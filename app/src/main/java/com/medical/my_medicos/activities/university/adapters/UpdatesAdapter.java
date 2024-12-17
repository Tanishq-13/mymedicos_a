package com.medical.my_medicos.activities.university.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.university.activity.UniversitiesListActivity;
import com.medical.my_medicos.activities.university.model.States;
import com.makeramen.roundedimageview.RoundedImageView;
import com.medical.my_medicos.activities.university.model.Updates;

import java.util.ArrayList;

public class UpdatesAdapter extends RecyclerView.Adapter<UpdatesAdapter.UpdatesViewHolder> {
    private static final int VIEW_TYPE_NORMAL = 1;
    Context context;
    ArrayList<Updates> updates;

    public UpdatesAdapter(Context context, ArrayList<Updates> update) {
        this.context = context;
        this.updates = update;
    }

    @NonNull
    @Override
    public UpdatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_universities, parent, false);
        return new UpdatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UpdatesViewHolder holder, int position) {
        Updates update = updates.get(position);
        holder.label.setText(update.getUpdatesName());

        Glide.with(context)
                .load(update.getUpdatesName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UniversitiesListActivity.class);
                intent.putExtra("stateName", update.getUpdatesName());
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return updates.size();
    }
    public class UpdatesViewHolder extends RecyclerView.ViewHolder {
        TextView label;

        public UpdatesViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
        }
    }
}
