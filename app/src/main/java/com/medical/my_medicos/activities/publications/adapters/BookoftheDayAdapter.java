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
import com.medical.my_medicos.databinding.ItemBookOfTheDayBinding;
import com.medical.my_medicos.databinding.ItemProductBinding;

import java.util.List;

public class BookoftheDayAdapter extends RecyclerView.Adapter<BookoftheDayAdapter.BookoftheDayViewHolder> {

    private final Context context;
    private final List<Product> products;
    private OnItemClickListener onItemClickListener;

    public BookoftheDayAdapter(Context context, List<Product> products) {
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public BookoftheDayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BookoftheDayViewHolder(LayoutInflater.from(context).inflate(R.layout.item_book_of_the_day, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BookoftheDayViewHolder holder, int position) {
        Product bookoftheday = products.get(position);
        Log.d("ProductAdapter", "Title: " + bookoftheday.getTitle() +
                ", Author: " + bookoftheday.getAuthor() +
                ", Thumbnail: " + bookoftheday.getThumbnail() +
                ", Price: " + bookoftheday.getPrice());

        Glide.with(context).load(bookoftheday.getThumbnail()).into(holder.binding.image);
        holder.binding.label.setText(bookoftheday.getTitle());
        holder.binding.author.setText(bookoftheday.getAuthor());
        holder.binding.price.setText(String.format("INR %.2f", bookoftheday.getPrice()));

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, ProductDetailedActivity.class);
            intent.putExtra("Title", bookoftheday.getTitle())
                    .putExtra("thumbnail", bookoftheday.getThumbnail())
                    .putExtra("id", bookoftheday.getId())
                    .putExtra("Subject", bookoftheday.getSubject())
                    .putExtra("Price", bookoftheday.getPrice())
                    .putExtra("Author", bookoftheday.getAuthor());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public class BookoftheDayViewHolder extends RecyclerView.ViewHolder {

        ItemBookOfTheDayBinding binding;

        public BookoftheDayViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemBookOfTheDayBinding.bind(itemView);

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
