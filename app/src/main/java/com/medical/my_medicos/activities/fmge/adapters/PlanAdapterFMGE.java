package com.medical.my_medicos.activities.fmge.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.model.Plan;

import java.util.ArrayList;
import java.util.List;

public class PlanAdapterFMGE extends RecyclerView.Adapter<PlanAdapterFMGE.PlanViewHolder> {
    private List<Plan> plans;
    private int maxHeight = 0; // Variable to store maximum height

    public PlanAdapterFMGE(List<Plan> plans) {
        this.plans = plans;
    }

    @NonNull
    @Override
    public PlanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_card, parent, false);
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanViewHolder holder, int position) {
        Plan plan = plans.get(position);
        holder.planTitle.setText(plan.getPlanName());
        holder.planDescription.setText(plan.getPlanTagline());
        holder.planPrice.setText(plan.getPlanPrice());

        holder.planFeaturesLayout.removeAllViews(); // Ensure previous views are cleared

        // Dynamically add each feature
        for (String feature : plan.getPlansFeature()) {
            addFeatureTextViews(holder, feature);
        }

        // Measure current card height and adjust max height if needed
        holder.itemView.post(() -> {
            int currentHeight = holder.itemView.getHeight();
            if (currentHeight > maxHeight) {
                maxHeight = currentHeight;
                updateCardHeights(); // Call method to update all heights
            }
        });

        // If max height is already set, apply it to the current card
        if (maxHeight > 0) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            layoutParams.height = maxHeight;
            holder.itemView.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemCount() {
        return plans.size();
    }

    // Method to add feature text views
    private void addFeatureTextViews(PlanViewHolder holder, String feature) {
        String[] words = feature.split(" ");
        int maxCharsPerLine = 28; // Max characters allowed per line

        // Initialize a StringBuilder to construct each line
        StringBuilder lineBuilder = new StringBuilder();
        List<String> featureParts = new ArrayList<>();

        // Build the feature text line by line, ensuring words are not split
        for (String word : words) {
            if (lineBuilder.length() + word.length() <= maxCharsPerLine) {
                if (lineBuilder.length() > 0) {
                    lineBuilder.append(" ");
                }
                lineBuilder.append(word);
            } else {
                featureParts.add(lineBuilder.toString());
                lineBuilder = new StringBuilder(word); // Start a new line with the current word
            }
        }

        if (lineBuilder.length() > 0) {
            featureParts.add(lineBuilder.toString());
        }

        // Add the first part with a tick mark
        TextView firstPartTextView = new TextView(holder.itemView.getContext());
        firstPartTextView.setText("✔ " + featureParts.get(0));
        firstPartTextView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.inter));
        firstPartTextView.setMaxLines(Integer.MAX_VALUE);
        firstPartTextView.setEllipsize(null);
        firstPartTextView.setSingleLine(false);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, 8, 0, 0);
        firstPartTextView.setLayoutParams(params);

        holder.planFeaturesLayout.addView(firstPartTextView);

        // Add remaining parts (without tick mark)
        for (int i = 1; i < featureParts.size(); i++) {
            TextView partTextView = new TextView(holder.itemView.getContext());
            partTextView.setText(featureParts.get(i));
            partTextView.setTypeface(ResourcesCompat.getFont(holder.itemView.getContext(), R.font.inter));
            partTextView.setMaxLines(Integer.MAX_VALUE);
            partTextView.setEllipsize(null);
            partTextView.setSingleLine(false);
            partTextView.setLayoutParams(params);
            holder.planFeaturesLayout.addView(partTextView);
        }
    }

    // Notify all cards to update their height to the maximum height
    private void updateCardHeights() {
        for (int i = 0; i < getItemCount(); i++) {
            notifyItemChanged(i); // This will update the height only for each card, instead of the whole list
        }
    }

    static class PlanViewHolder extends RecyclerView.ViewHolder {
        TextView planTitle, planDescription, planPrice;
        Button planButton;
        LinearLayout planFeaturesLayout; // This will contain the features dynamically

        public PlanViewHolder(@NonNull View itemView) {
            super(itemView);
            planTitle = itemView.findViewById(R.id.planTitle);
            planDescription = itemView.findViewById(R.id.planDescription);
            planPrice = itemView.findViewById(R.id.planPrice);
            planButton = itemView.findViewById(R.id.planButton);
            planFeaturesLayout = itemView.findViewById(R.id.planFeaturesLayout);
        }
    }
}
