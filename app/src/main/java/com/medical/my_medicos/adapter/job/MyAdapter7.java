package com.medical.my_medicos.adapter.job;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.job.JobsActivity2;
import com.medical.my_medicos.adapter.job.items.jobitem2;

import java.util.List;

public class MyAdapter7 extends RecyclerView.Adapter<MyAdapter7.MyViewHolder7> {

    Context context;
    List<jobitem2> joblist;

    public MyAdapter7(Context context, List<jobitem2> joblist) { // Change to Context
        this.context = context;
        this.joblist = joblist;
    }

    @NonNull
    @Override
    public MyViewHolder7 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.job_design3, parent, false);
        return new MyViewHolder7(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder7 holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(joblist.get(position).getTitle());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a) {
                Context context = a.getContext();
                Intent i = new Intent(context, JobsActivity2.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// Add this line
                i.putExtra("Title",joblist.get(position).getTitle());

                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return joblist.size();
    }

    public static class MyViewHolder7 extends RecyclerView.ViewHolder {
        TextView pos, hosp, loc,Date,title;
        Button apply;

        public MyViewHolder7(@NonNull View itemView) {
            super(itemView);
            title=itemView.findViewById(R.id.job_pos2);

        }
    }
}
