package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.medical.my_medicos.R;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<String> {

    private final LayoutInflater inflater;
    private List<String> options;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<String> options) {
        super(context, resource, options);
        this.options = options;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.spinner_text);
        textView.setText(options.get(position));

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }

        TextView textView = convertView.findViewById(R.id.spinner_text);
        textView.setText(options.get(position));

        return convertView;
    }

    public void updateOptions(List<String> newOptions) {
        this.options = newOptions;
        notifyDataSetChanged();
    }
}
