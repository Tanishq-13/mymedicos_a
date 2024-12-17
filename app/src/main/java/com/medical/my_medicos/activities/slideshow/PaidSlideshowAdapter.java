package com.medical.my_medicos.activities.slideshow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.medical.my_medicos.R;
import com.medical.my_medicos.databinding.ItemSlideshowPaidBinding;

import java.util.ArrayList;

public class PaidSlideshowAdapter extends RecyclerView.Adapter<PaidSlideshowAdapter.SlideshowPaidViewHolder> {

    private final Context context;
    private final ArrayList<Slideshow> slideshows;

    FirebaseDatabase database;
    String currentUid;
    int coins = 50;

    public PaidSlideshowAdapter(Context context, ArrayList<Slideshow> slideshows) {
        this.context = context;
        this.slideshows = slideshows;
    }

    @NonNull
    @Override
    public SlideshowPaidViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSlideshowPaidBinding binding = ItemSlideshowPaidBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SlideshowPaidViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideshowPaidViewHolder holder, int position) {
        Slideshow slideshow = slideshows.get(position);

        if (!slideshow.getImages().isEmpty()) {
            Glide.with(context)
                    .load(slideshow.getImages().get(0).getUrl())
                    .into(holder.binding.thumbnailslideshow);
        }

        holder.binding.slideshowlabel.setText(slideshow.getTitle());
        holder.binding.typeofslide.setText(slideshow.getType());

        // Set the click listener for the download button
        holder.binding.bottomsheetupforpay.setOnClickListener(view -> {
            int adapterPosition = holder.getAdapterPosition();
            if (adapterPosition != RecyclerView.NO_POSITION) {
                Slideshow clickedSlideshow = slideshows.get(adapterPosition);
                holder.showBottomSheet(clickedSlideshow);
            }
        });
    }

    @Override
    public int getItemCount() {
        return slideshows.size();
    }

    public class SlideshowPaidViewHolder extends RecyclerView.ViewHolder {
        ItemSlideshowPaidBinding binding;

        public SlideshowPaidViewHolder(@NonNull ItemSlideshowPaidBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void showBottomSheet(Slideshow slideshow) {
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_payment, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            database = FirebaseDatabase.getInstance();
            currentUid = FirebaseAuth.getInstance().getUid();
            bottomSheetDialog.setContentView(bottomSheetView);

            Button click = bottomSheetView.findViewById(R.id.paymentpart);

            String customButtonText = "Pay 50 MedCoins to Download";
            click.setText(customButtonText);

            click.setOnClickListener(v -> {
                // Handle payment and download logic here
                handlePaymentAndDownload(slideshow);

                // Dismiss the bottom sheet dialog
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.show();
        }

        private void handlePaymentAndDownload(Slideshow slideshow) {
            database.getReference().child("profiles")
                    .child(currentUid)
                    .child("coins")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                            Integer coinsValue = snapshot.getValue(Integer.class);
                            if (coinsValue != null) {
                                int newCoinsValue = coinsValue - 50; // Updated to deduct 50 MedCoins
                                if (newCoinsValue >= 0) {
                                    // Download the Document
                                    Toast.makeText(context, "Downloading..", Toast.LENGTH_SHORT).show();
                                    // Add logic to handle the download here
                                    int position = getAdapterPosition();
                                    if (position != RecyclerView.NO_POSITION) {
                                        Slideshow clickedSlideshow = slideshows.get(position);
                                        openUrlInBrowser(clickedSlideshow.getFile());
                                    }
                                    // Update the user's MedCoins
                                    database.getReference().child("profiles")
                                            .child(currentUid)
                                            .child("coins")
                                            .setValue(newCoinsValue);
                                    coins = newCoinsValue;
                                } else {
                                    Toast.makeText(context, "Insufficient Credits", Toast.LENGTH_SHORT).show();
                                    // Revert the MedCoins value to the original value
                                    database.getReference().child("profiles")
                                            .child(currentUid)
                                            .child("coins")
                                            .setValue(coinsValue);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error) {
                        }
                    });
        }
    }
    private void openUrlInBrowser(String url) {
        if (!url.isEmpty()) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            context.startActivity(intent);
        } else {

        }
    }
}
