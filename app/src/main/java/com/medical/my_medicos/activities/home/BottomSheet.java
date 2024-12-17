package com.medical.my_medicos.activities.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.medical.my_medicos.R;

public class BottomSheet extends BottomSheetDialogFragment {

    private BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onOptionClick(String option);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mListener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement BottomSheetListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layouts, container, false);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RadioButton educationOption = view.findViewById(R.id.educationOption);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) RadioButton communityOption = view.findViewById(R.id.communityOption);

        if (educationOption == null || communityOption == null) {
            Log.e("BottomSheet", "One or both RadioButtons are null. Check the layout file.");
        } else {
            // Set Education as the default checked option
            educationOption.setChecked(true);

            educationOption.setOnClickListener(v -> {
                mListener.onOptionClick("Education");
                dismiss();
            });

            communityOption.setOnClickListener(v -> {
                // Show an alert dialog with a construction message
                new MaterialAlertDialogBuilder(getContext())
                        .setTitle("Feature Under Construction")
                        .setMessage("The Community feature is currently under construction. Please check back later.")
                        .setPositiveButton("OK", (dialog, which) -> {
                            // Dismiss the dialog
                            dialog.dismiss();
                        })
                        .show();

                // Prevent the RadioButton from being checked
                communityOption.setChecked(false);
            });
        }

        return view;
    }
}
