package com.medical.my_medicos.activities.university.adapters.insiders;

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
import com.medical.my_medicos.activities.publications.activity.CategoryPublicationActivity;
import com.medical.my_medicos.activities.university.model.Universities;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class UniversitiesInsiderAdapter extends RecyclerView.Adapter<UniversitiesInsiderAdapter.UniversitiesInsiderViewHolder> {
    Context context;
    ArrayList<Universities> universities;
    public UniversitiesInsiderAdapter(Context context, ArrayList<Universities> universities) {
        this.context = context;
        this.universities = universities;
    }
    @Override
    public UniversitiesInsiderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_insider_universities, parent, false);
        return new UniversitiesInsiderViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull UniversitiesInsiderViewHolder holder, int position) {
        Universities university = universities.get(position);
        holder.label.setText(university.getName());
        Glide.with(context)
                .load(university.getIcon())
                .into(holder.image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CategoryPublicationActivity.class);
                intent.putExtra("universityId", university.getId());
                intent.putExtra("universityName", university.getName());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return universities.size();
    }
    public class UniversitiesInsiderViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView label;

        public UniversitiesInsiderViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            label = itemView.findViewById(R.id.label);
        }
    }

}