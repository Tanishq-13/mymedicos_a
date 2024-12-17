package com.medical.my_medicos.activities.publications.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.medical.my_medicos.databinding.ItemSponsoredProductBinding;

import java.util.ArrayList;

public class SponsoredProductAdapter extends RecyclerView.Adapter<SponsoredProductAdapter.SponsoredProductViewHolder> {

    Context context;
    ArrayList<Product> sponsored;

    public SponsoredProductAdapter(Context context, ArrayList<Product> sponsoredproducts) {
        this.context = context;
        this.sponsored = sponsoredproducts;
    }

    @NonNull
    @Override
    public SponsoredProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SponsoredProductViewHolder(LayoutInflater.from(context).inflate(R.layout.item_sponsored_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SponsoredProductViewHolder holder, int position) {
        Product sponsoredproduct = sponsored.get(position);
        Log.d("ProductAdapter", "Title: " + sponsoredproduct.getTitle() +
                ", Author: " + sponsoredproduct.getAuthor() +
                ", Thumbnail: " + sponsoredproduct.getThumbnail());

        Glide.with(context).load(sponsoredproduct.getThumbnail()).into(holder.binding.image);
        holder.binding.label.setText(sponsoredproduct.getTitle());
        holder.binding.author.setText(sponsoredproduct.getAuthor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailedActivity.class);
                intent.putExtra("Title", sponsoredproduct.getTitle());
                intent.putExtra("thumbnail", sponsoredproduct.getThumbnail());
                intent.putExtra("id", sponsoredproduct.getId());
                intent.putExtra("Subject",sponsoredproduct.getSubject());
                intent.putExtra("Price", sponsoredproduct.getPrice());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sponsored.size();
    }

    public class SponsoredProductViewHolder extends RecyclerView.ViewHolder {

        ItemSponsoredProductBinding binding;

        public SponsoredProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemSponsoredProductBinding.bind(itemView);
        }
    }
}
