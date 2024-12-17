package com.medical.my_medicos.activities.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.model.dailyquestion;
import com.medical.my_medicos.activities.pg.activites.internalfragments.DailyQuestionActivity;

import java.util.List;

public class Dailyquestion extends RecyclerView.Adapter<Dailyquestion.QuizViewHolder> {
    private List<dailyquestion> quizList;
    private Context context;

    public Dailyquestion(Context context, List<dailyquestion> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_question, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        dailyquestion quizQuestion = quizList.get(position);
        holder.questionTextView.setText(quizQuestion.getQuestionTitle());

        holder.solveNowButton.setOnClickListener(v -> {
            Intent intent = new Intent(context, DailyQuestionActivity.class);
            intent.putExtra("QUESTION_ID", quizQuestion.getDocumentId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView questionTextView;
        Button solveNowButton;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            solveNowButton = itemView.findViewById(R.id.solveNowButton);
        }
    }
}
