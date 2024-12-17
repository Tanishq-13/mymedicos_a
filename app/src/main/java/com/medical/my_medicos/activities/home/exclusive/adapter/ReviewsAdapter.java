package com.medical.my_medicos.activities.home.exclusive.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.model.Review;
import com.willy.ratingbar.ScaleRatingBar;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private List<Review> reviews;

    public ReviewsAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.nameTextView.setText(review.getStudentName());
        holder.reviewDescTextView.setText(review.getStudentReview());
        holder.ratingBar.setClickable(false);
        holder.ratingBar.setIsIndicator(true);
//        ratingBar.setIsIndicator(true);  // Make the rating bar non-interactive
//        ratingBar.setClickable(false);  // Ensure the rating bar is not clickable
        holder.ratingBar.setScrollable(false);  // Ensure it’s not scrollable
        holder.ratingBar.setClearRatingEnabled(false);  // Disable clearing the rating
        holder.ratingBar.setRating(review.getRating());
        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault());
        holder.dateTextView.setText(dateFormat.format(review.getDate()));

        // Initially hide "Read More"
        holder.readMoreText.setVisibility(View.VISIBLE);
        holder.reviewDescTextView.setMaxLines(3); // Show only 3 lines initially
        holder.reviewDescTextView.setEllipsize(TextUtils.TruncateAt.END); // Add ellipsis

        holder.readMoreText.setText("Read More");

        // Set click listener for "Read More" toggle
        holder.readMoreText.setOnClickListener(v -> {
            boolean isExpanded = holder.reviewDescTextView.getMaxLines() == Integer.MAX_VALUE;
            if (isExpanded) {
                // Collapse the text
                holder.reviewDescTextView.setMaxLines(3);
                holder.readMoreText.setText("Read More");
            } else {
                // Expand the text
                holder.reviewDescTextView.setMaxLines(Integer.MAX_VALUE);
                holder.readMoreText.setText("Read Less");
            }
        });


    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView nameTextView, reviewDescTextView, dateTextView,readMoreText;
        ScaleRatingBar ratingBar;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
//            ScaleRatingBar ratingBar = new ScaleRatingBar(this);
            nameTextView = itemView.findViewById(R.id.name);
            reviewDescTextView = itemView.findViewById(R.id.reviewdesc);
            dateTextView = itemView.findViewById(R.id.date);
            ratingBar=itemView.findViewById(R.id.simpleRatingBar);
            readMoreText=itemView.findViewById(R.id.readMoreText);
        }
    }
}
