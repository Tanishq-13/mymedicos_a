package com.medical.my_medicos.activities.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.FmgeprepActivity;
import com.medical.my_medicos.activities.home.model.Item; // Import the PG prep activity
import com.medical.my_medicos.activities.neetss.activites.SsprepActivity;
import com.medical.my_medicos.activities.pg.activites.PgprepActivity;
import com.medical.my_medicos.activities.ug.UgExamActivity;

import java.util.List;

public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.ViewHolder> {

    private Context context;
    private List<Item> itemList;

    public HorizontalAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_edu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.itemTextView.setText(item.getTitle());
        holder.itemImageView.setImageResource(item.getImageResId());

        // Set default colors before conditions
        holder.itemTextView.setTextColor(context.getResources().getColor(R.color.black));
        holder.itemImageView.clearColorFilter();

        // Set colors based on item title before setting the onClickListener
        if (item.getTitle().equals("PG NEET")) {
            holder.itemTextView.setTextColor(context.getResources().getColor(R.color.pgneet));
            holder.itemImageView.setColorFilter(context.getResources().getColor(R.color.pgneet), PorterDuff.Mode.SRC_IN);
        } else if (item.getTitle().equals("FMGE")) {
            holder.itemTextView.setTextColor(context.getResources().getColor(R.color.teal_700));
            holder.itemImageView.setColorFilter(context.getResources().getColor(R.color.teal_700), PorterDuff.Mode.SRC_IN);
        } else if (item.getTitle().equals("NEET SS")) {
            holder.itemTextView.setTextColor(context.getResources().getColor(R.color.golden));
            holder.itemImageView.setColorFilter(context.getResources().getColor(R.color.golden), PorterDuff.Mode.SRC_IN);
        }

        // Setting OnClickListener for each item
        holder.itemView.setOnClickListener(v -> {
            switch (item.getTitle()) {
                case "UG exams":
                    Intent ugIntent = new Intent(context, UgExamActivity.class);
                    context.startActivity(ugIntent);
                    break;

                case "PG NEET":
                    Intent pgIntent = new Intent(context, PgprepActivity.class);
                    context.startActivity(pgIntent);
                    break;

                case "FMGE":
                    Intent fmgeIntent = new Intent(context, FmgeprepActivity.class);
                    context.startActivity(fmgeIntent);
                    break;

                case "NEET SS":
                    showUnderConstructionDialog();
                    break;

                default:
                    break;
            }
        });
    }


    // Method to show "Under Construction" dialog
    private void showUnderConstructionDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(context)
                .setTitle("Under Construction")
                .setMessage("This section is currently under construction. Please check back later.")
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        ImageView itemImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            itemImageView = itemView.findViewById(R.id.itemImageView);
        }
    }
}
