package com.medical.my_medicos.activities.slideshow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.slideshow.insider.SpecialitySlideshowInsiderActivity;
import com.medical.my_medicos.activities.slideshow.model.SlideshareCategory;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class SlideshareCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_NORMAL = 1;
    private static final int VIEW_TYPE_MORE = 2;
    Context context;
    ArrayList<SlideshareCategory> slideshows;

    public SlideshareCategoryAdapter(Context context, ArrayList<SlideshareCategory> slideshows) {
        this.context = context;
        this.slideshows = slideshows;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_more_category_slideshow, parent, false);

        return new SlideshowViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_NORMAL) {
            SlideshowViewHolder slideshowViewHolder = (SlideshowViewHolder) holder;
            SlideshareCategory slideshowcategory = slideshows.get(position);
            slideshowViewHolder.label.setText(slideshowcategory.getName());
            Glide.with(context)
                    .load(slideshowcategory.getName())
                    .into(slideshowViewHolder.image);

            slideshowViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, SpecialitySlideshowInsiderActivity.class);
                    intent.putExtra("pgId", slideshowcategory.getPriority());
                    intent.putExtra("specialityPgName", slideshowcategory.getName());
                    context.startActivity(intent);
                }
            });
        } else {

        }
    }

    @Override
    public int getItemCount() {
        return slideshows.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == getItemCount() - 1 && getItemCount() > 5) ? VIEW_TYPE_MORE : VIEW_TYPE_NORMAL;
    }

    public class SlideshowViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView image;
        TextView label;

        public SlideshowViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            label = itemView.findViewById(R.id.label);
        }
    }
    public class MoreSlideshowViewHolder extends RecyclerView.ViewHolder {
        ImageView imagemore;
        TextView labelmore;

        public MoreSlideshowViewHolder(@NonNull View itemView) {
            super(itemView);
            imagemore = itemView.findViewById(R.id.arrowformore);
            labelmore = itemView.findViewById(R.id.moretext);
        }
    }

    // You can add the MoreCategoryViewHolder class here as well if needed
}