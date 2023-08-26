package com.example.my_medicos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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
        return new MyAdapter4.MyViewHolder4(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder4 holder, int position) {

        holder.name.setText(item.get(position).getDoc_name());
        holder.hosp.setText(item.get(position).getHosp_name());
        holder.pose.setText(item.get(position).getPos_name());
        holder.imageView.setImageResource(item.get(position).getImage());
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class MyViewHolder4 extends  RecyclerView.ViewHolder{

        TextView name,hosp,pose;
        ImageView imageView;
        public MyViewHolder4(@NonNull View itemView) {
            super(itemView);

            name=itemView.findViewById(R.id.drname2);
            hosp=itemView.findViewById(R.id.hospname2);
            pose=itemView.findViewById(R.id.drpos2);
            imageView=itemView.findViewById(R.id.cme_img2);
        }
    }
}
