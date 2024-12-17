package com.medical.my_medicos.activities.fmge.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.model.VideoFMGE;

import com.medical.my_medicos.activities.pg.model.VideoPG;
import com.medical.my_medicos.databinding.ItemVideosBinding;

import java.util.ArrayList;

public class VideoFMGEAdapter extends RecyclerView.Adapter<VideoFMGEAdapter.VideoFMGEViewHolder> {

    Context context;
    ArrayList<VideoFMGE> videobankspg;

    public VideoFMGEAdapter(Context context, ArrayList<VideoFMGE> videobanksfmge) {

        this.context = context;
        this.videobankspg = videobankspg;
    }

    @NonNull
    @Override
    public VideoFMGEViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoFMGEViewHolder(LayoutInflater.from(context).inflate(R.layout.item_videos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFMGEViewHolder holder, int position) {
        VideoFMGE videobankpg = videobankspg.get(position);
        Glide.with(context)
                .load(videobankpg.getThumbnail())
                .into(holder.binding.thumbnailvideo);
        holder.binding.videoslabel.setText(videobankpg.getLabel());
        holder.binding.timeofvideo.setText(videobankpg.getUrl());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, VideoBankDetailedActivity.class);
//                intent.putExtra("name", videobankpg.getLabel());
//                intent.putExtra("image", videobankpg.getThumbnail());
//                intent.putExtra("id", videobankpg.getDate());
//                intent.putExtra("price", videobankpg.getUrl());
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return videobankspg.size();
    }

    public class VideoFMGEViewHolder extends RecyclerView.ViewHolder {
        ItemVideosBinding binding;

        public VideoFMGEViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVideosBinding.bind(itemView);
            binding.Videobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        VideoFMGE clickedNews = videobankspg.get(position);
                        openUrlInBrowser(clickedNews.getDate());
                    }
                }
            });

        }
    }
    private void openUrlInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

}
