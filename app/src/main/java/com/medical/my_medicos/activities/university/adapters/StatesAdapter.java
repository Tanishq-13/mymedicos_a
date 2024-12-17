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

import java.util.ArrayList;

public class StatesAdapter extends RecyclerView.Adapter<StatesAdapter.StatesViewHolder> {
    private static final int VIEW_TYPE_NORMAL = 1;
    Context context;
    ArrayList<States> states;

    public StatesAdapter(Context context, ArrayList<States> states) {
        this.context = context;
        this.states = states;
    }

    @NonNull
    @Override
    public StatesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_universities, parent, false);
        return new StatesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatesViewHolder holder, int position) {
        States state = states.get(position);
        holder.label.setText(state.getName());

        Glide.with(context)
                .load(state.getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UniversitiesListActivity.class);
                intent.putExtra("stateName", state.getName());
                context.startActivity(intent);
            }
        });

    }
    @Override
    public int getItemCount() {
        return states.size();
    }
    public class StatesViewHolder extends RecyclerView.ViewHolder {
        TextView label;

        public StatesViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
        }
    }
}
