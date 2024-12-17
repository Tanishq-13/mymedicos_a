
package com.medical.my_medicos.adapter.ug;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.ug.items.ugitem1;

import java.util.List;

public class ActivityUgAdapter1 extends RecyclerView.Adapter<ActivityUgAdapter1.UgViewHolder2>{

    Context context;
    List<ugitem1> item;

    public ActivityUgAdapter1(Context context, List<ugitem1> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public UgViewHolder2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(context).inflate(R.layout.ug_design1,parent,false);
        return new UgViewHolder2(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UgViewHolder2 holder, int position) {
        holder.name.setText(item.get(position).getDocname());
        holder.description.setText(item.get(position).getDocdescripiton());
        holder.title.setText(item.get(position).getDoctitle());

    }

    @Override
    public int getItemCount() {
        return item.size();
    }
    public static class UgViewHolder2 extends  RecyclerView.ViewHolder {

        TextView name,title,description;

        public UgViewHolder2(@NonNull View itemView) {
            super(itemView);

//            imageview = itemView.findViewById(R.id.cme_img);
            name=itemView.findViewById(R.id.dr_name);
            description=itemView.findViewById(R.id.dr_ug_desciption);
            title=itemView.findViewById(R.id.dr_title);

        }
    }
}
