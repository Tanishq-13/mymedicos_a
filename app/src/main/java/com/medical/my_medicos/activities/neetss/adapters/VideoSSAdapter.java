package com.medical.my_medicos.activities.neetss.adapters;

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
import com.medical.my_medicos.activities.neetss.model.VideoSS;
import com.medical.my_medicos.activities.pg.model.VideoPG;
import com.medical.my_medicos.databinding.ItemVideosBinding;

import java.util.ArrayList;

public class VideoSSAdapter extends RecyclerView.Adapter<VideoSSAdapter.VideoSSViewHolder> {

    Context context;
    ArrayList<VideoSS> videobankspg;

    public VideoSSAdapter(Context context, ArrayList<VideoSS> videobankspg) {

        this.context = context;
        this.videobankspg = videobankspg;
    }

    @NonNull
    @Override
    public VideoSSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VideoSSViewHolder(LayoutInflater.from(context).inflate(R.layout.item_videos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VideoSSViewHolder holder, int position) {
        VideoSS videobankss = videobankspg.get(position);
        Glide.with(context)
                .load(videobankss.getThumbnail())
                .into(holder.binding.thumbnailvideo);
        holder.binding.videoslabel.setText(videobankss.getLabel());
        holder.binding.timeofvideo.setText(videobankss.getUrl());

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

    public class VideoSSViewHolder extends RecyclerView.ViewHolder {
        ItemVideosBinding binding;

        public VideoSSViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemVideosBinding.bind(itemView);
            binding.Videobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        VideoSS clickedNews = videobankspg.get(position);
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
