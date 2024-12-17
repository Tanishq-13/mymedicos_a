package com.medical.my_medicos.activities.publications.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.publications.activity.DetailsActivity;
import com.medical.my_medicos.activities.publications.activity.ProductDetailedActivity;
import com.medical.my_medicos.activities.publications.model.Product;

import java.util.ArrayList;
import java.util.List;

public class PurchasedAdapter extends RecyclerView.Adapter<PurchasedAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> products;
    private OnItemClickListener onItemClickListener;

    public PurchasedAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_purchased_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        Log.d("PurchasedAdapter", "Title: " + product.getTitle() +
                ", Author: " + product.getAuthor() +
                ", Price: " + product.getPrice() +
                ", Thumbnail: " + product.getThumbnail());

        Glide.with(context)
                .load(product.getThumbnail())
                .into(holder.imageView);
        holder.labelTextView.setText(product.getTitle());
        holder.authorTextView.setText(product.getAuthor());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ProductDetailedActivity.class);
                intent.putExtra("Title", product.getTitle());
                intent.putExtra("thumbnail", product.getThumbnail());
                intent.putExtra("id", product.getId());
                intent.putExtra("Subject", product.getSubject());
                intent.putExtra("Price", product.getPrice());
                intent.putExtra("Author", product.getAuthor());
                intent.putExtra("URL",product.getURL());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView labelTextView;
        TextView authorTextView;
        TextView readTextView; // Reference for the "Read" TextView

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            labelTextView = itemView.findViewById(R.id.label);
            authorTextView = itemView.findViewById(R.id.author);
            readTextView = itemView.findViewById(R.id.read);
            readTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product product = products.get(position);
                        Log.d("PurchasedAdapter", "Product ID: " + product.getId()); // Check if the ID is being logged correctly
                        if (product.getId() != null && !product.getId().isEmpty()) {
                            Intent intent = new Intent(context, DetailsActivity.class);
                            intent.putExtra("bookName", product.getTitle());
                            intent.putExtra("authorName", product.getAuthor());
                            intent.putExtra("id", product.getId());
                            context.startActivity(intent);
                        } else {
                            Log.e("PurchasedAdapter", "Product ID is null or empty for position: " + position);
                        }
                    }

                }
            });
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Notify the listener on item click
                    if (onItemClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            onItemClickListener.onItemClick(products.get(position));
                        }
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
