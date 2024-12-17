package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.filterdata;

import java.util.List;

public class CustomSpinnerAdapter extends ArrayAdapter<filterdata> {

    private final LayoutInflater inflater;
    private List<filterdata> options;

    public CustomSpinnerAdapter(@NonNull Context context, int resource, @NonNull List<filterdata> options) {
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
        TextView num=convertView.findViewById(R.id.topics);
        TextView textView = convertView.findViewById(R.id.spinner_text);
        filterdata data=options.get(position);
        textView.setText(data.getIndexHeading());
        num.setText(String.valueOf(data.getQuizCount()));


        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.spinner_item, parent, false);
        }
        TextView num=convertView.findViewById(R.id.topics);
        TextView textView = convertView.findViewById(R.id.spinner_text);
        filterdata data=options.get(position);
        textView.setText(data.getIndexHeading());
        num.setText(String.valueOf(data.getQuizCount())+" QBanks");

        return convertView;
    }

    public void updateOptions(List<filterdata> newOptions) {
        this.options = newOptions;
        notifyDataSetChanged();
    }
}
