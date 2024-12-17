package com.medical.my_medicos.activities.neetss.activites.extras.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.news.News;
import com.medical.my_medicos.activities.news.NewsDetailedActivity;
import com.medical.my_medicos.databinding.ItemPreparationBinding;

import java.util.ArrayList;

public class ImportantPreprationAdapter extends RecyclerView.Adapter<ImportantPreprationAdapter.ImportantPreparationViewHolder> {

    private Context context;
    private ArrayList<News> newses;

    public ImportantPreprationAdapter(Context context, ArrayList<News> news) {
        this.context = context;
        this.newses = news;
    }

    @NonNull
    @Override
    public ImportantPreparationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImportantPreparationViewHolder(LayoutInflater.from(context).inflate(R.layout.item_preparation, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImportantPreparationViewHolder holder, int position) {
        News news = newses.get(position);

        Glide.with(context)
                .load(news.getThumbnail())
                .into(holder.binding.thumbnailprep);

        holder.binding.preplabel.setText(news.getLabel());
        holder.binding.timeofprep.setText(news.getFormattedDate());

        holder.binding.viewentirenews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewsDetailedActivity(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newses.size();
    }

    public class ImportantPreparationViewHolder extends RecyclerView.ViewHolder {
        ItemPreparationBinding binding;

        public ImportantPreparationViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemPreparationBinding.bind(itemView);
        }
    }

    private void openNewsDetailedActivity(News news) {
        Intent intent = new Intent(context, NewsDetailedActivity.class);
        intent.putExtra("Title", news.getLabel());
        intent.putExtra("thumbnail", news.getThumbnail());
        intent.putExtra("Description", news.getDescription());
        intent.putExtra("Time", news.getFormattedDate());
        intent.putExtra("URL", news.getUrl());
        intent.putExtra("type", news.getType());

        context.startActivity(intent);
    }
}