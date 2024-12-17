package com.medical.my_medicos.activities.neetss.activites.internalfragments.insiderfragments.extras.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.insiderfragments.extras.model.Coupon;

import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {

    private List<Coupon> coupons;
    private Context context; // Context to access system services

    public CouponAdapter(Context context, List<Coupon> coupons) {
        this.context = context;
        this.coupons = coupons;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.coupon_item_available, parent, false);
        return new CouponViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = coupons.get(position);
        holder.tvCouponCode.setText("Code: " + coupon.getCode());
        holder.tvCouponDetail.setText(coupon.getAbout());
        holder.copyButton.setOnClickListener(v -> {
            // Access the Clipboard Manager
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // Create a clip with the coupon code
            ClipData clip = ClipData.newPlainText("Coupon Code", coupon.getCode());
            // Set the clip as the primary clipboard data
            clipboard.setPrimaryClip(clip);
            // Notify the user that the code has been copied
            Toast.makeText(context, "Coupon code copied to clipboard", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return coupons != null ? coupons.size() : 0;
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvCouponCode, tvCouponDetail;
        ImageView copyButton; // Reference to the copy button

        public CouponViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            tvCouponCode = itemView.findViewById(R.id.tvCouponCode);
            tvCouponDetail = itemView.findViewById(R.id.tvCouponDetail);
            copyButton = itemView.findViewById(R.id.copybtn); // Initialize the copy button
        }
    }

    public void updateData(List<Coupon> newCoupons) {
        coupons.clear();
        coupons.addAll(newCoupons);
        notifyDataSetChanged();
    }
}
