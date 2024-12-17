package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.Indexadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index.IndexData;

import java.util.List;

public class IndexAdapter extends RecyclerView.Adapter<IndexAdapter.IndexViewHolder> {

    private List<IndexData> indexList;

    public IndexAdapter(List<IndexData> indexList) {
        this.indexList = indexList;
    }

    @NonNull
    @Override
    public IndexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_index, parent, false);
        return new IndexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IndexViewHolder holder, int position) {
        holder.bind(indexList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return indexList.size();
    }

    static class IndexViewHolder extends RecyclerView.ViewHolder {
        private TextView indexTextView;
        private TextView snNoTextView;

        public IndexViewHolder(@NonNull View itemView) {
            super(itemView);
            indexTextView = itemView.findViewById(R.id.index_text_view);
            snNoTextView = itemView.findViewById(R.id.sn_no_text_view);
        }

        public void bind(IndexData indexData, int position) {
            indexTextView.setText(indexData.getData());
            // Adding a period after the formatted position number
            String formattedPosition = String.format("%02d.", position + 1);
            snNoTextView.setText(formattedPosition);
        }


    }
}
