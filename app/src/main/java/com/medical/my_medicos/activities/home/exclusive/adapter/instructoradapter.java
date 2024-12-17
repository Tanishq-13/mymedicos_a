package com.medical.my_medicos.activities.home.exclusive.adapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.faculty.facultyoverview;
import com.medical.my_medicos.activities.home.exclusive.model.Instructor;
import com.willy.ratingbar.ScaleRatingBar;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class instructoradapter extends RecyclerView.Adapter<instructoradapter.ViewHolder>{
    private Context context;
    private List<Instructor> courseList;

    public instructoradapter(Context context, List<Instructor> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.instructorcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Instructor course = courseList.get(position);

        // Set heading and subheading text
        holder.courseHeading.setText(course.getName());
        holder.courseSubHeading.setText(course.getInterest1());
        Log.d("ghjk",course.getName()+" "+course.getInterest1());

        // Load image using Glide
        Glide.with(context)
                .load(course.getProfile())
                .into(holder.courseImage);

        // Click listener (optional)
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, facultyoverview.class);
            intent.putExtra("instructor_id", course.getDocId()); // Pass instructor ID
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView courseImage;
        TextView courseHeading, courseSubHeading;
        ScaleRatingBar ratingBar;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            courseImage = itemView.findViewById(R.id.courseimage);
            courseHeading = itemView.findViewById(R.id.courseheading);
            courseSubHeading = itemView.findViewById(R.id.coursesubheading);
        }
    }
}
