package com.medical.my_medicos.activities.news;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.databinding.ItemNewsBinding;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {

    private Context context;
    private ArrayList<News> newses;

    public NewsAdapter(Context context, ArrayList<News> news) {
        this.context = context;
        this.newses = news;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NewsViewHolder(LayoutInflater.from(context).inflate(com.medical.my_medicos.R.layout.item_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newses.get(position);

        // Load thumbnail image using Glide
        Glide.with(context)
                .load(news.getThumbnail())
                .into(holder.binding.thumbnailnews);

        holder.binding.newslabel.setText(news.getLabel());
        holder.binding.timeofnews.setText(news.getFormattedDate());
        Log.d("Date of news in the format ",news.getFormattedDate());

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.binding.newssubject.setText(Html.fromHtml(news.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.binding.newssubject.setText(Html.fromHtml(news.getDescription()));
        }

        holder.binding.viewentirenews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle the "View Entire News" button click here
                openNewsDetailedActivity(news);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newses.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        ItemNewsBinding binding;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemNewsBinding.bind(itemView);
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
        intent.putExtra("subject",news.getSubject());

        context.startActivity(intent);
    }
}