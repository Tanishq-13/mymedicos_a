package com.medical.my_medicos.activities.news.adapter;

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
import com.medical.my_medicos.databinding.ItemAnnouncementBinding;
import com.medical.my_medicos.databinding.ItemNewsBinding;
import com.medical.my_medicos.databinding.ItemPreparationBinding;

import java.util.ArrayList;

public class ImportantAnnouncementAdapter extends RecyclerView.Adapter<ImportantAnnouncementAdapter.ImportantAnnouncementViewHolder> {

    private Context context;
    private ArrayList<News> newses;

    public ImportantAnnouncementAdapter(Context context, ArrayList<News> news) {
        this.context = context;
        this.newses = news;
    }

    @NonNull
    @Override
    public ImportantAnnouncementAdapter.ImportantAnnouncementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ImportantAnnouncementAdapter.ImportantAnnouncementViewHolder(LayoutInflater.from(context).inflate(R.layout.item_announcement, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ImportantAnnouncementAdapter.ImportantAnnouncementViewHolder holder, int position) {
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

    public class ImportantAnnouncementViewHolder extends RecyclerView.ViewHolder {
        ItemAnnouncementBinding binding;

        public ImportantAnnouncementViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemAnnouncementBinding.bind(itemView);
        }
    }

    private void openNewsDetailedActivity(News news) {
        Intent intent = new Intent(context, NewsDetailedActivity.class);
        intent.putExtra("id", news.getDocumentId());
        intent.putExtra("Title", news.getLabel());
        intent.putExtra("thumbnail", news.getThumbnail());
        intent.putExtra("Description", news.getDescription());
        intent.putExtra("Time", news.getFormattedDate());
        intent.putExtra("URL", news.getUrl());
        intent.putExtra("type", news.getType());

        context.startActivity(intent);
    }
}