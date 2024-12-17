package com.medical.my_medicos.activities.fmge.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.model.QuizFmgeinsider;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;

public class ResultReportFAdapter extends RecyclerView.Adapter<ResultReportFAdapter.ResultFmgeViewHolder> {

    private Context context;
    private ArrayList<QuizFmgeinsider> quizList;

    public ResultReportFAdapter(Context context, ArrayList<QuizFmgeinsider> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public ResultFmgeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.result_report_item, parent, false);
        return new ResultFmgeViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ResultFmgeViewHolder holder, int position) {
        QuizFmgeinsider quizQuestion = quizList.get(position);

        holder.resultQuestion.setText(quizQuestion.getQuestion());
        holder.resultCorrectOption.setText("Correct Option: " + quizQuestion.getCorrectAnswer());

        // Check if the selected option is null and display "Skip" if it is
        String selectedOption = quizQuestion.getSelectedOption();
        if (selectedOption == null || selectedOption.isEmpty()) {
            holder.resultSelectedOption.setText("Selected Option: Skip");
        } else {
            holder.resultSelectedOption.setText("Selected Option: " + selectedOption);
        }

        // Handle HTML content in the description
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            holder.resultDescription.setText(Html.fromHtml(quizQuestion.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            holder.resultDescription.setText(Html.fromHtml(quizQuestion.getDescription()));
        }

        holder.showResultDescription.setOnClickListener(view -> {
            // Toggle visibility of resultDescription
            if (holder.resultDescription.getVisibility() == View.VISIBLE) {
                holder.resultDescription.setVisibility(View.GONE);
            } else {
                holder.resultDescription.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class ResultFmgeViewHolder extends RecyclerView.ViewHolder {
        TextView resultQuestion;
        TextView resultCorrectOption;
        TextView resultSelectedOption;
        TextView resultDescription;
        TextView showResultDescription;

        public ResultFmgeViewHolder(@NonNull View itemView) {
            super(itemView);
            resultQuestion = itemView.findViewById(R.id.resultQuestion);
            resultCorrectOption = itemView.findViewById(R.id.resultCorrectOption);
            resultSelectedOption = itemView.findViewById(R.id.resultSelectedOption);
            resultDescription = itemView.findViewById(R.id.resultdescription);
            showResultDescription = itemView.findViewById(R.id.showresultdescription);
        }
    }
}
