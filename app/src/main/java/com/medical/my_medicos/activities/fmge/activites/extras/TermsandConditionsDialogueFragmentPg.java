package com.medical.my_medicos.activities.fmge.activites.extras;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.medical.my_medicos.R;

public class TermsandConditionsDialogueFragmentPg extends DialogFragment {

    private TermsandConditionsDialogueFragmentPg.OnTermsAcceptedListener listener;

    public interface OnTermsAcceptedListener {
        void onTermsAccepted();
    }

    public TermsandConditionsDialogueFragmentPg(TermsandConditionsDialogueFragmentPg.OnTermsAcceptedListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.custompopupforpg, null);

        Button agreeButton = view.findViewById(R.id.agreepg);
        Button disagreeButton = view.findViewById(R.id.visit);

        agreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onTermsAccepted();
                dismiss();
            }
        });

        disagreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        builder.setView(view);
        return builder.create();
    }
}
