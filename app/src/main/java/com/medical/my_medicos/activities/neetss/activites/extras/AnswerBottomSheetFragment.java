package com.medical.my_medicos.activities.neetss.activites.extras;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medical.my_medicos.R;

public class AnswerBottomSheetFragment extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_layout, container, false);

        // Retrieve data from the bundle
        Bundle bundle = getArguments();
        if (bundle != null) {
            String selectedOption = bundle.getString("selectedOption", "");
            String correctOption = bundle.getString("correctOption", "");
            String description = bundle.getString("description", "");

            // Update UI elements with the retrieved data
            TextView resultSelectedOption = view.findViewById(R.id.resultSelectedOption);
            resultSelectedOption.setText(selectedOption);

            TextView resultCorrectOption = view.findViewById(R.id.resultCorrectOption);
            resultCorrectOption.setText(correctOption);

            TextView resultDescription = view.findViewById(R.id.resultdescription);
            resultDescription.setText(description);
        }

        return view;
    }
}
