
package com.medical.my_medicos.adapter.cme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.cme.CmeDetailsActivity;
import com.medical.my_medicos.adapter.cme.items.cmeitem4;

import java.util.List;

public class MyAdapter1 extends RecyclerView.Adapter<MyAdapter1.MyViewHolder2>{

    Context context;
    List<cmeitem4> item;

    public MyAdapter1(Context context, List<cmeitem4> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public MyViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.cme_design1,parent,false);
        return new MyViewHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder2 holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(item.get(position).getDocname());
        holder.position.setText(item.get(position).getDocpos());
        holder.title.setText(item.get(position).getDoctitle());
        holder.mode.setText(item.get(position).getMode());
        holder.presenters.setText(item.get(position).getDocpresenter());
        holder.date.setText(item.get(position).getDate());
        holder.time.setText(item.get(position).getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View a) {
                Context context = a.getContext();
                Intent i = new Intent(context, CmeDetailsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);// Add this line
                i.putExtra("documentid",item.get(position).getDocumentid());
                i.putExtra("name",item.get(position).getEmail());
                i.putExtra("Mode",item.get(position).getMode());
                i.putExtra("time",item.get(position).getTime());
                i.putExtra("type",item.get(position).getType());
                context.startActivity(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return item.size();
    }
    public static class MyViewHolder2 extends  RecyclerView.ViewHolder {

        TextView name,position,title,presenters,date,time,mode;

        public MyViewHolder2(@NonNull View itemView) {
            super(itemView);

//            imageview = itemView.findViewById(R.id.cme_img);
            name=itemView.findViewById(R.id.dr_name);
            position=itemView.findViewById(R.id.dr_pos);
            title=itemView.findViewById(R.id.dr_title);
            mode=itemView.findViewById(R.id.dr_mode);
            presenters=itemView.findViewById(R.id.dr_presenters);
            date=itemView.findViewById(R.id.dr_date);
            time=itemView.findViewById(R.id.dr_time);

        }
    }
}
