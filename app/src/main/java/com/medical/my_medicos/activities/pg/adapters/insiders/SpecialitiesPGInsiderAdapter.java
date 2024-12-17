package com.medical.my_medicos.activities.pg.adapters.insiders;

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
import com.medical.my_medicos.activities.pg.model.SpecialitiesPG;
import com.makeramen.roundedimageview.RoundedImageView;
import java.util.ArrayList;

public class SpecialitiesPGInsiderAdapter extends RecyclerView.Adapter<SpecialitiesPGInsiderAdapter.SpecialitiesPGInsiderViewHolder> {

    Context context;
    ArrayList<SpecialitiesPG> specialitiespggo;

    public SpecialitiesPGInsiderAdapter(Context context, ArrayList<SpecialitiesPG> specialitiespg) {
        this.context = context;
        this.specialitiespggo = specialitiespg;
    }

    @NonNull
    @Override
    public SpecialitiesPGInsiderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_insider_speciality, parent, false);
        return new SpecialitiesPGInsiderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpecialitiesPGInsiderViewHolder holder, int position) {
        SpecialitiesPG specialitiesPGS = specialitiespggo.get(position);
        holder.labelpg.setText(specialitiesPGS.getName());
        Glide.with(context)
                .load(specialitiesPGS.getName())
                .into(holder.thumbnailpg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SpecialitiesPG.class);
                intent.putExtra("pgId", specialitiesPGS.getPriority());
                intent.putExtra("pgName", specialitiesPGS.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return specialitiespggo.size();
    }

    public class SpecialitiesPGInsiderViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView thumbnailpg;
        TextView labelpg;

        public SpecialitiesPGInsiderViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailpg = itemView.findViewById(R.id.imagespeciality);
            labelpg = itemView.findViewById(R.id.labelspeciality);
        }
    }

}