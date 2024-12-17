package com.medical.my_medicos.activities.news;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.databinding.ItemNewsBinding;
import com.medical.my_medicos.databinding.ItemTodayNewsBinding;

import java.util.ArrayList;

public class TodayNewsAdapter extends RecyclerView.Adapter<TodayNewsAdapter.TodayNewsViewHolder> {

    private Context context;
    private ArrayList<NewsToday> newsestoday;

    public TodayNewsAdapter(Context context, ArrayList<NewsToday> news) {
        this.context = context;
        this.newsestoday = news;
    }

    @NonNull
    @Override
    public TodayNewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TodayNewsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_today_news, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TodayNewsViewHolder holder, int position) {
        NewsToday newstoday = newsestoday.get(position);

        Glide.with(context)
                .load(newstoday.getThumbnail())
                .into(holder.binding.thumbnailnewstoday);

        holder.binding.label.setText(newstoday.getLabel());

        holder.binding.viewtodaysnews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTodayNewsDetailedActivity(newstoday);
            }
        });
    }

    @Override
    public int getItemCount() {
        return newsestoday.size();
    }

    public class TodayNewsViewHolder extends RecyclerView.ViewHolder {
        ItemTodayNewsBinding binding;

        public TodayNewsViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemTodayNewsBinding.bind(itemView);

        }
    }

    private void openTodayNewsDetailedActivity(NewsToday news) {
        Intent intent = new Intent(context, NewsDetailedActivity.class);
        intent.putExtra("id", news.getDocumentId());
        intent.putExtra("Title", news.getLabel());
        intent.putExtra("thumbnail", news.getThumbnail());
        intent.putExtra("Description", news.getDescription());
        intent.putExtra("Time", news.getFormattedDate());
        intent.putExtra("URL", news.getUrl());
        context.startActivity(intent);
    }
}