package com.medical.my_medicos.activities.neetss.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.insiders.SpecialityPGQuizActivity;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.PreprationindexingActivitySS;
import com.medical.my_medicos.activities.neetss.model.SpecialitiesSS;


import java.util.ArrayList;

public class SpecialitiesSSAdapter extends RecyclerView.Adapter<SpecialitiesSSAdapter.SpecialitiesSSViewHolder> {
    Context context;
    ArrayList<SpecialitiesSS> specialitiespost;

    public SpecialitiesSSAdapter(Context context, ArrayList<SpecialitiesSS> specialitiespgs) {
        this.context = context;
        this.specialitiespost = specialitiespgs;
    }

    @NonNull
    @Override
    public SpecialitiesSSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_categories, parent, false);

        return new SpecialitiesSSViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialitiesSSViewHolder holder, int position) {
        SpecialitiesSS specialitiespg = specialitiespost.get(position);
        holder.label.setText(specialitiespg.getName());
//        Glide.with(context)
//                .load(specialitiespg.getName())
//                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PreprationindexingActivitySS.class);
                intent.putExtra("pgId", specialitiespg.getPriority());
                intent.putExtra("specialityPgName", specialitiespg.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return specialitiespost.size();
    }

    public class SpecialitiesSSViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView label;

        public SpecialitiesSSViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            label = itemView.findViewById(R.id.label);
        }
    }
}
