//package com.medical.my_medicos.adapter.job;
//
//public class Adapter8 {
//}
package com.medical.my_medicos.adapter.job;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.job.JobDetailsActivity;
import com.medical.my_medicos.activities.job.JobsApplyActivity;
import com.medical.my_medicos.adapter.job.items.jobitem1;

import java.util.List;

public class Adapter8 extends RecyclerView.Adapter<Adapter8.MyViewHolder6> {
    Context context;
    List<jobitem1> joblist;

    public Adapter8(Context context, List<jobitem1> joblist) {
        this.context = context;
        this.joblist = joblist;
    }

    @NonNull
    @Override
    public MyViewHolder6 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.job_design2, parent, false);
        return new MyViewHolder6(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder6 holder, @SuppressLint("RecyclerView") int position) {
        holder.title.setText(joblist.get(position).getTitle());
        holder.hosp.setText(joblist.get(position).getHospital());
        holder.loc.setText(joblist.get(position).getLocation());
        holder.Date.setText(joblist.get(position).getDate());
        holder.Category.setText(joblist.get(position).getCategory());

        Log.d("12345678", joblist.get(position).getPosition());
        Log.d("12345678", joblist.get(position).getHospital());
        Log.d("12345678", joblist.get(position).getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a) {
                Context context = a.getContext();

                // Create an Intent for the same activity
                Intent i = new Intent(context, JobDetailsActivity.class);

                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                // Add the data to the intent
                i.putExtra("user", joblist.get(position).getuser());
                i.putExtra("documentid", joblist.get(position).getDocumentid());

                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return joblist.size();
    }

    public class MyViewHolder6 extends RecyclerView.ViewHolder {
        TextView pos, hosp, loc,Date,title, Category;
        String user;
        TextView categoryshow;

        public MyViewHolder6(@NonNull View itemView) {
            super(itemView);
            String Jobid;

            hosp = itemView.findViewById(R.id.hosp_name1);
            loc = itemView.findViewById(R.id.job_loc1);
            Date=itemView.findViewById(R.id.date1);
            title=itemView.findViewById(R.id.job_pos1);
            Category=itemView.findViewById(R.id.categoryshow);


            Category.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent j=new Intent(context, JobsApplyActivity.class);
                    j.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    j.putExtra("user", joblist.get(getAdapterPosition()).getuser());
                    Log.d( "Something went wrong..",joblist.get(getAdapterPosition()).getuser());
                    context.startActivity(j);
                }
            });
        }
    }
}
