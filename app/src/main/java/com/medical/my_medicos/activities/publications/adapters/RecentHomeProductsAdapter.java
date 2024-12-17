package com.medical.my_medicos.activities.publications.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.activity.ProductDetailedActivity;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.databinding.ItemProductBinding;
import com.medical.my_medicos.databinding.ItemRecentHomeProductsBinding;

import java.util.ArrayList;

public class RecentHomeProductsAdapter extends RecyclerView.Adapter<RecentHomeProductsAdapter.RecentHomeProductViewHolder> {

    Context context;
    ArrayList<Product> products;

    public RecentHomeProductsAdapter(Context context, ArrayList<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public RecentHomeProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecentHomeProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recent_home_products, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecentHomeProductViewHolder holder, int position) {
        Product recenthomeproducts = products.get(position);
        Glide.with(context)
                .load(recenthomeproducts.getThumbnail())
                .into(holder.binding.image);
        holder.binding.label.setText(recenthomeproducts.getTitle());
        holder.binding.author.setText(recenthomeproducts.getAuthor()); // Set the author

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailedActivity.class);
                intent.putExtra("Title", recenthomeproducts.getTitle());
                intent.putExtra("thumbnail", recenthomeproducts.getThumbnail());
                intent.putExtra("id", recenthomeproducts.getId());
                intent.putExtra("Subject", recenthomeproducts.getSubject());
                intent.putExtra("Price", recenthomeproducts.getPrice());
                intent.putExtra("Author", recenthomeproducts.getAuthor()); // Add the author to the intent
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return products.size();
    }

    public class RecentHomeProductViewHolder extends RecyclerView.ViewHolder {

        ItemRecentHomeProductsBinding binding;

        public RecentHomeProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemRecentHomeProductsBinding.bind(itemView);
        }
    }
}
