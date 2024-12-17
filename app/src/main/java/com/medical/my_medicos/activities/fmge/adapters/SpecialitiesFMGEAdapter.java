package com.medical.my_medicos.activities.fmge.adapters;

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
import com.medical.my_medicos.activities.fmge.activites.insiders.SpecialityFMGEQuizActivity;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.PreprationindexingActivityFMGE;
import com.medical.my_medicos.activities.fmge.model.SpecialitiesFmge;
import com.medical.my_medicos.activities.pg.activites.insiders.SpecialityPGInsiderActivity;
import com.medical.my_medicos.activities.pg.activites.insiders.SpecialityPGQuizActivity;
import com.medical.my_medicos.activities.pg.model.SpecialitiesPG;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SpecialitiesFMGEAdapter extends RecyclerView.Adapter<SpecialitiesFMGEAdapter.SpecialitiesFMGEViewHolder> {
    Context context;
    ArrayList<SpecialitiesFmge> specialitiespostfmge;

    public SpecialitiesFMGEAdapter(Context context, ArrayList<SpecialitiesFmge> specialitiesfmges) {
        this.context = context;
        this.specialitiespostfmge = specialitiesfmges;
    }

    @NonNull
    @Override
    public SpecialitiesFMGEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_categories, parent, false);

        return new SpecialitiesFMGEViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialitiesFMGEViewHolder holder, int position) {
        SpecialitiesFmge specialitiesFmge = specialitiespostfmge.get(position);
        holder.label.setText(specialitiesFmge.getName());
//        Glide.with(context)
//                .load(specialitiesFmge.getName())
//                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PreprationindexingActivityFMGE.class);
                intent.putExtra("fmgeId", specialitiesFmge.getPriority());
                intent.putExtra("specialityFmgeName", specialitiesFmge.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return specialitiespostfmge.size();
    }

    public class SpecialitiesFMGEViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView label;

        public SpecialitiesFMGEViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            label = itemView.findViewById(R.id.label);
        }
    }
}
