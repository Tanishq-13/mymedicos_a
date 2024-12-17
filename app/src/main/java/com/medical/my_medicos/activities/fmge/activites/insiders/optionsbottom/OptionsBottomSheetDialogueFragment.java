package com.medical.my_medicos.activities.fmge.activites.insiders.optionsbottom;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.text.Html;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medical.my_medicos.R;

public class OptionsBottomSheetDialogueFragment extends BottomSheetDialogFragment {

    private String correctOption;
    private String selectedOption;
    private String description;

    public static OptionsBottomSheetDialogueFragment newInstance(String correctOption, String selectedOption, String description) {
        OptionsBottomSheetDialogueFragment fragment = new OptionsBottomSheetDialogueFragment();
        Bundle args = new Bundle();
        args.putString("correctOption", correctOption);
        args.putString("selectedOption", selectedOption);
        args.putString("description", description);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_option, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        correctOption = getArguments().getString("correctOption");
        selectedOption = getArguments().getString("selectedOption");
        description = getArguments().getString("description");

        TextView tvCorrectOption = view.findViewById(R.id.tvCorrectOption);
        TextView tvSelectedOption = view.findViewById(R.id.tvSelectedOption);
        TextView tvDescription = view.findViewById(R.id.tvDescription);

        tvCorrectOption.setText("Correct Option: " + correctOption);
        tvSelectedOption.setText("Your Selected Option: " + selectedOption);

        // Handle HTML content in the description
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            tvDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvDescription.setText(Html.fromHtml(description));
        }
    }
}
