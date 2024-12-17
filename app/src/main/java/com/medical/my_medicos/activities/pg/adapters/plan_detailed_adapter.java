package com.medical.my_medicos.activities.pg.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.plandetails;
import com.medical.my_medicos.activities.pg.model.Plan_detailed;

import java.util.List;

public class plan_detailed_adapter extends RecyclerView.Adapter<plan_detailed_adapter.PlanViewHolder> {
    private Context context;
    private List<Plan_detailed> planList;
    private int lastCheckedPosition = -1; // For radio button selection

    public plan_detailed_adapter(Context context, List<Plan_detailed> planList) {
        this.context = context;
        this.planList = planList;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inside_plan_card, parent, false);
        return new PlanViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan_detailed plan = planList.get(position);
        holder.numberOfMonths.setText(plan.getMonths());
        holder.originalPrice.setText(plan.getOriginalPrice());
        holder.discountedPrice.setText(plan.getDiscountedPrice());
        holder.radioPlan.setChecked(position == lastCheckedPosition);

        holder.mi.setSelected(position == lastCheckedPosition);

        holder.mi.setOnClickListener(v -> {
            updateSelection(holder.getAdapterPosition());
            String month = plan.getMonths();
            String discountedPrice = plan.getDiscountedPrice();
            ((plandetails) context).onPlanSelected(month, discountedPrice, plan.getMonths());
        });

        holder.radioPlan.setOnClickListener(v -> {
            updateSelection(holder.getAdapterPosition());
            String month = plan.getMonths();
            String discountedPrice = plan.getDiscountedPrice();
            ((plandetails) context).onPlanSelected(month, discountedPrice, plan.getMonths());
        });

    }
    private void updateSelection(int position) {
        lastCheckedPosition = position;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return planList.size();
    }

    public static class PlanViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioPlan;
        LinearLayout mi;
        TextView numberOfMonths, originalPrice, discountedPrice;

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            mi=itemView.findViewById(R.id.main_it);
            radioPlan = itemView.findViewById(R.id.radio_plan);
            numberOfMonths = itemView.findViewById(R.id.number_of_months);
            originalPrice = itemView.findViewById(R.id.original_price);
            discountedPrice = itemView.findViewById(R.id.discounted_price);
        }
    }

}

