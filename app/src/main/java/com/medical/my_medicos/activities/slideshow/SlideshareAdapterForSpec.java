package com.medical.my_medicos.activities.slideshow;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.media3.common.util.Log;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.databinding.ItemSlideshareForSpecBinding;
import com.medical.my_medicos.databinding.ItemSlideshowBinding;

import java.util.ArrayList;

public class SlideshareAdapterForSpec extends RecyclerView.Adapter<SlideshareAdapterForSpec.SlideshowViewHolder> {

    private final Context context;
    private final ArrayList<Slideshow> slideshows;

    public SlideshareAdapterForSpec(Context context, ArrayList<Slideshow> slideshows) {
        this.context = context;
        this.slideshows = slideshows;
    }

    @NonNull
    @Override
    public SlideshowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSlideshareForSpecBinding binding = ItemSlideshareForSpecBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SlideshowViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideshowViewHolder holder, int position) {
        Slideshow slideshow = slideshows.get(position);

        if (!slideshow.getImages().isEmpty()) {
            Glide.with(context)
                    .load(slideshow.getImages().get(0).getUrl())
                    .into(holder.binding.thumbnailslideshow);
        }

        holder.binding.slideshowlabel.setText(slideshow.getTitle());
        holder.binding.typeofslide.setText(slideshow.getType());

    }

    @Override
    public int getItemCount() {
        return slideshows.size();
    }

    public class SlideshowViewHolder extends RecyclerView.ViewHolder {
        ItemSlideshareForSpecBinding binding;

        public SlideshowViewHolder(@NonNull ItemSlideshareForSpecBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.downloadpptbtn.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Slideshow clickedSlideshow = slideshows.get(position);
                    openUrlInBrowser(clickedSlideshow.getFile());
                }
            });
        }
    }

    private void openUrlInBrowser(String url) {
        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } else {

        }
    }
}