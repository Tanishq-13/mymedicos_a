package com.medical.my_medicos.adapter.cme;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.cme.CmeDetailsActivity;
import com.medical.my_medicos.adapter.cme.items.cmeitem2;

import java.util.List;

public class MyAdapter4 extends RecyclerView.Adapter<MyAdapter4.MyViewHolder4>{

    Context context;
    List<cmeitem2> item;

    public MyAdapter4(Context context, List<cmeitem2> myitem) {
        this.context = context;
        this.item = myitem;
    }


    @NonNull
    @Override
    public MyViewHolder4 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.cme_design2,parent,false);
        return new MyViewHolder4(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder4 holder, @SuppressLint("RecyclerView") int position) {

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
                i.putExtra("type",item.get(position).getType());
                i.putExtra("Mode",item.get(position).getMode());
                i.putExtra("time",item.get(position).getTime());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class MyViewHolder4 extends  RecyclerView.ViewHolder{


        TextView date,time,title,name,position,presenters,mode;
        ImageView imageView;
        public MyViewHolder4(@NonNull View itemView) {
            super(itemView);

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
