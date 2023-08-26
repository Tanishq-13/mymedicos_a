package com.example.my_medicos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MyAdapter5 extends RecyclerView.Adapter<MyAdapter5.MyViewHolder5>{

    Context context;
    List<publicationitem> pubitem;

    public MyAdapter5(Context context, List<publicationitem> pubitem) {
        this.context = context;
        this.pubitem = pubitem;
    }

    @NonNull
    @Override
    public MyViewHolder5 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.publications_design,parent,false);
        return new MyAdapter5.MyViewHolder5(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder5 holder, int position) {

        holder.date.setText(pubitem.get(position).getPublicationdate());
        holder.time.setText(pubitem.get(position).getPublicationtimings());
        holder.doc.setText(pubitem.get(position).getPublicationdr());
    }

    @Override
    public int getItemCount() {
        return pubitem.size();
    }

    public static class MyViewHolder5 extends  RecyclerView.ViewHolder{

        TextView date,time,doc;
        public MyViewHolder5(@NonNull View itemView) {
            super(itemView);

            date=itemView.findViewById(R.id.date_publication);
            time=itemView.findViewById(R.id.time_publication);
            doc=itemView.findViewById(R.id.drname_publication);

        }
    }
}
