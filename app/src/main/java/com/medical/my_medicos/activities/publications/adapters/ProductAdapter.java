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
import com.medical.my_medicos.activities.publications.activity.ProductDetailedActivity;
import com.medical.my_medicos.activities.publications.model.Product;
import com.medical.my_medicos.databinding.ItemProductBinding;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private final Context context;
    private final List<Product> products;
    private OnItemClickListener onItemClickListener;

    public ProductAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ProductViewHolder(LayoutInflater.from(context).inflate(com.medical.my_medicos.R.layout.item_product, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Log.d("ProductAdapter", "Title: " + product.getTitle() +
                ", Author: " + product.getAuthor() +
                ", Thumbnail: " + product.getThumbnail());

        Glide.with(context).load(product.getThumbnail()).into(holder.binding.image);
        holder.binding.label.setText(product.getTitle());
        holder.binding.author.setText(product.getAuthor());


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailedActivity.class);
            intent.putExtra("Title", product.getTitle())
                    .putExtra("thumbnail", product.getThumbnail())
                    .putExtra("id", product.getId())
                    .putExtra("Subject", product.getSubject())
                    .putExtra("Price", product.getPrice())
                    .putExtra("Author", product.getAuthor());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ItemProductBinding binding;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemProductBinding.bind(itemView);

            itemView.setOnClickListener(view -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(products.get(position));
                    }
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Product product);
    }
}
