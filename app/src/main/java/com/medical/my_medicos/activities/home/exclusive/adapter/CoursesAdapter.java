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

public class CoursesAdapter extends RecyclerView.Adapter<CoursesAdapter.CourseViewHolder> {

    private List<CourseCard> courses;
    private Context context;


    public CoursesAdapter(List<CourseCard> courses, Context context) {
        this.courses = courses;
        this.context = context;

    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        CourseCard course = courses.get(position);
        holder.title.setText(course.getTitle());
        holder.numr.setText(String.valueOf(course.getRatedBy()));
        holder.rating.setText(String.valueOf(course.getRatingAvg()));

        Glide.with(context).load(course.getCover()).into(holder.img);
        holder.ratingBar.setClickable(false);
        holder.ratingBar.setIsIndicator(true);
//        ratingBar.setIsIndicator(true);  // Make the rating bar non-interactive
//        ratingBar.setClickable(false);  // Ensure the rating bar is not clickable
        holder.ratingBar.setScrollable(false);  // Ensure it’s not scrollable
        holder.ratingBar.setClearRatingEnabled(false);
        Float gg= Float.valueOf(String.valueOf(course.getRatingAvg()));// Disable clearing the rating
        holder.ratingBar.setRating(gg);
        // Set OnClickListener to open CoursesActivity with course ID
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context,courses.class);
            Log.d("looo",course.getCourseId());
//            new courses(course);
            intent.putExtra("courseId",course.getCourseId());
            intent.putExtra("docId", course); // Pass the docId
            context.startActivity(intent);
        });
    }


    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, numr, rating, price;
        ImageView img;
        ScaleRatingBar ratingBar;


        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.courseTitle);
            ratingBar=itemView.findViewById(R.id.simpleRatingBar);

//            description = itemView.findViewById(R.id.courseDescription);
            numr = itemView.findViewById(R.id.numr);
            rating = itemView.findViewById(R.id.star);
            img = itemView.findViewById(R.id.courseimage);
            price = itemView.findViewById(R.id.price);
        }
    }
}

