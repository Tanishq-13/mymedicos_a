package com.example.my_medicos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.List;

public class MyAdapter6 extends RecyclerView.Adapter<MyAdapter6.MyViewHolder6>{

    Context context;
    List<profileitem> profile;


    public MyAdapter6(Context context, List<profileitem> profile) {
        this.context = context;
        this.profile = profile;
    }

    @NonNull
    @Override
    public MyViewHolder6 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.profileinfo_design, parent, false);
        return new MyAdapter6.MyViewHolder6(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder6 holder, int position) {
        holder.proinfo.setText(profile.get(position).getInfo());
    }

    @Override
    public int getItemCount() {
        return profile.size();
    }

    public static class MyViewHolder6 extends RecyclerView.ViewHolder{


        TextView proinfo;
        public MyViewHolder6(@NonNull View itemView) {
            super(itemView);

            proinfo=itemView.findViewById(R.id.pro_info);
        }
    }
}
