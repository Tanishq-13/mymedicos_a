package com.medical.my_medicos.activities.pg.adapters;

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
import com.medical.my_medicos.activities.pg.activites.insiders.SpecialityPGInsiderActivity;
import com.medical.my_medicos.activities.pg.activites.insiders.SpecialityPGQuizActivity;
import com.medical.my_medicos.activities.pg.model.SpecialitiesPG;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SpecialityPGForMaterialAdapter extends RecyclerView.Adapter<SpecialityPGForMaterialAdapter.SpecialityPGForMaterialViewHolder> {
    Context context;
    ArrayList<SpecialitiesPG> specialitiespost;

    public SpecialityPGForMaterialAdapter(Context context, ArrayList<SpecialitiesPG> specialitiespgs) {
        this.context = context;
        this.specialitiespost = specialitiespgs;
    }

    @NonNull
    @Override
    public SpecialityPGForMaterialViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_categories, parent, false);

        return new SpecialityPGForMaterialViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialityPGForMaterialViewHolder holder, int position) {
        SpecialitiesPG specialitiespg = specialitiespost.get(position);
        holder.label.setText(specialitiespg.getName());
        Glide.with(context)
                .load(specialitiespg.getName())
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SpecialityPGInsiderActivity.class);
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

    public class SpecialityPGForMaterialViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView label;

        public SpecialityPGForMaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            label = itemView.findViewById(R.id.label);
        }
    }
}
