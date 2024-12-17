package com.medical.my_medicos.activities.neetss.adapters.insiders;

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
import com.medical.my_medicos.activities.neetss.model.SpecialitiesSS;
import com.medical.my_medicos.activities.pg.model.SpecialitiesPG;

import java.util.ArrayList;

public class SpecialitiesSSInsiderAdapter extends RecyclerView.Adapter<SpecialitiesSSInsiderAdapter.SpecialitiesSSInsiderViewHolder> {

    Context context;
    ArrayList<SpecialitiesSS> specialitiespggo;

    public SpecialitiesSSInsiderAdapter(Context context, ArrayList<SpecialitiesSS> specialitiespg) {
        this.context = context;
        this.specialitiespggo = specialitiespg;
    }

    @NonNull
    @Override
    public SpecialitiesSSInsiderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_insider_speciality, parent, false);
        return new SpecialitiesSSInsiderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialitiesSSInsiderViewHolder holder, int position) {
        SpecialitiesSS specialitiesSSS = specialitiespggo.get(position);
        holder.labelpg.setText(specialitiesSSS.getName());
        Glide.with(context)
                .load(specialitiesSSS.getName())
                .into(holder.thumbnailpg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SpecialitiesPG.class);
                intent.putExtra("ssId", specialitiesSSS.getPriority());
                intent.putExtra("ssName", specialitiesSSS.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return specialitiespggo.size();
    }

    public class SpecialitiesSSInsiderViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView thumbnailpg;
        TextView labelpg;

        public SpecialitiesSSInsiderViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailpg = itemView.findViewById(R.id.imagespeciality);
            labelpg = itemView.findViewById(R.id.labelspeciality);
        }
    }

}