package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.FmgePrepPayement;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.Model.twgt.Quiz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList;
    private Context context;

    public QuizAdapter(Context context, List<Quiz> quizList) {
        this.quizList = quizList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        String title = quiz.getTitle();
        if (title.length() > 23) {
            title = title.substring(0, 20) + "...";
        }

        // Update lock/unlock visibility based on quiz type
        if (quiz.getType()) {
            holder.unlock.setVisibility(View.GONE);
            holder.lock.setVisibility(View.VISIBLE);
        } else {
            holder.lock.setVisibility(View.GONE);
            holder.unlock.setVisibility(View.VISIBLE);
        }

        holder.titleTextView.setText(title);
        holder.categorytextview.setText(quiz.getIndex());
        com.google.firebase.Timestamp t = quiz.getDueDate();
        Date date = t.toDate();

        // Format the Date
        String formattedDate = formatTimestamp(date);


        holder.dueDateTextView.setText(formattedDate);

        Log.d("QuizAdapter", "Title1: " + quiz.getTitle1());
        Log.d("QuizAdapter", "Title: " + quiz.getTitle());
        Log.d("QuizAdapter", "Id: " + quiz.getId());
        Log.d("QuizAdapter", "Due: " + formattedDate);

        holder.pay.setOnClickListener(v -> {
            if (quiz.getType()) {
                holder.showBottomSheet();
            } else {
                Intent intent = new Intent(context, FmgePrepPayement.class);
                intent.putExtra("Title1", quiz.getTitle1());
                intent.putExtra("Title", quiz.getTitle());
                intent.putExtra("Id", quiz.getId());
                intent.putExtra("Due", formattedDate);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    // Method to update the quiz list
    public void updateQuizList(List<Quiz> newQuizList) {
        quizList.clear();
        quizList.addAll(newQuizList);
        notifyDataSetChanged();
    }

    class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dueDateTextView;
        TextView categorytextview;
        LinearLayout pay, lock, unlock;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            pay = itemView.findViewById(R.id.payfortheexam);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
            categorytextview = itemView.findViewById(R.id.categoryTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dueDateTextView = itemView.findViewById(R.id.dateTextView);
        }

        private void showBottomSheet() {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_paid, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
            btnBuyPlan.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(context, "Purchase action triggered", Toast.LENGTH_SHORT).show();
            });

            bottomSheetDialog.show();
        }
    }

    private String formatTimestamp(Date date) {
        try {
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return an empty string or handle the error as needed
        }
    }
}
