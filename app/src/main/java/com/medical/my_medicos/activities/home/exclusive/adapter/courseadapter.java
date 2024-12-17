package com.medical.my_medicos.activities.home.exclusive.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.faculty.courses;
import com.medical.my_medicos.activities.home.exclusive.model.CourseCard;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

public class courseadapter extends RecyclerView.Adapter<courseadapter.ViewHolder> {

    private Context context;
    private List<CourseCard> courseCardList;

    public courseadapter(Context context, List<CourseCard> courseCardList) {
        this.context = context;
        this.courseCardList = courseCardList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.featured_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CourseCard courseCard = courseCardList.get(position);

        // Truncate title if it exceeds 20 characters
        String title = courseCard.getTitle();
        if (title.length() > 50) {
            holder.titleTextView.setText(title.substring(0, 50) + "...");
        } else {
            holder.titleTextView.setText(title);
        }

        // Truncate subtitle if it exceeds 20 characters
        String subtitle = courseCard.getDescription();
        if (subtitle.length() > 62) {
            holder.subtitleTextView.setText(subtitle.substring(0, 62) + "...");
        } else {
            holder.subtitleTextView.setText(subtitle);
        }

        // Set rating and ratings count
//        holder.ratingTextView.setText(courseCard.getRatingAvg());
        Log.d("rtng",courseCard.getRatingAvg()+"j");
        holder.ratingsCountTextView.setText(String.valueOf(courseCard.getRatedBy()) + " ratings");
        holder.ratingBar.setClickable(false);
        holder.ratingBar.setIsIndicator(true);
//        ratingBar.setIsIndicator(true);  // Make the rating bar non-interactive
//        ratingBar.setClickable(false);  // Ensure the rating bar is not clickable
        holder.ratingBar.setScrollable(false);  // Ensure it’s not scrollable
        holder.ratingBar.setClearRatingEnabled(false);
        Float gg= Float.valueOf(String.valueOf(courseCard.getRatingAvg()));// Disable clearing the rating
        holder.ratingBar.setRating(gg);
        // Load image with Glide
        Glide.with(context).load(courseCard.getCover()).into(holder.imageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, courses.class);
            Log.d("looo",courseCard.getCourseId());
//            new courses(course);
            intent.putExtra("courseId",courseCard.getCourseId());
            intent.putExtra("docId", courseCard); // Pass the docId
            context.startActivity(intent);
        });

    }


    @Override
    public int getItemCount() {
        return courseCardList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        ScaleRatingBar ratingBar;

        TextView titleTextView, subtitleTextView, ratingTextView, ratingsCountTextView, overlayBadge, sponsoredTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ratingBar=itemView.findViewById(R.id.simpleRatingBar);

            imageView = itemView.findViewById(R.id.courseimage);
            titleTextView = itemView.findViewById(R.id.courseheading);
            subtitleTextView = itemView.findViewById(R.id.coursesubheading);
            ratingTextView = itemView.findViewById(R.id.star);
            ratingsCountTextView = itemView.findViewById(R.id.numr);
//            overlayBadge = itemView.findViewById(R.id.overlayBadge);
//            sponsoredTextView = itemView.findViewById(R.id.sponsoredTextView);
        }
    }
}
