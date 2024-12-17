package com.medical.my_medicos.activities.fmge.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.model.Fmgepg;
import com.medical.my_medicos.activities.pg.activites.Neetexaminsider;
import com.medical.my_medicos.activities.pg.activites.ResultActivityNeet;
import com.medical.my_medicos.activities.pg.model.Neetpg;

import java.util.ArrayList;
import java.util.List;


public class fmgeexampadapter extends RecyclerView.Adapter<fmgeexampadapter.FmgeQuizQuestionViewHolder> {

    private Context context;
    private ArrayList<Fmgepg> quizquestionsweekly;
    private String selectedOption;
    private boolean isOptionSelectionEnabled = true;
    private int currentQuestionIndex = 0;
    private OnOptionSelectedListener onOptionSelectedListener;
    private OnOptionInteractionListener interactionListener;


    public fmgeexampadapter(Context context, ArrayList<Fmgepg> quizList1, OnOptionInteractionListener interactionListener) {
        this.context = context;
        this.quizquestionsweekly = quizList1;
        this.selectedOption = null;
        this.interactionListener = interactionListener;
    }

    @NonNull
    @Override
    public FmgeQuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_quiz_design_weekly1, parent, false);
        return new FmgeQuizQuestionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull FmgeQuizQuestionViewHolder holder, int position) {
        Fmgepg quizQuestion = quizquestionsweekly.get(holder.getAdapterPosition());

        holder.questionspan.setText(quizQuestion.getQuestion());
        holder.optionA.setText(quizQuestion.getOptionA());
        holder.optionB.setText(quizQuestion.getOptionB());
        holder.optionC.setText(quizQuestion.getOptionC());
        holder.optionD.setText(quizQuestion.getOptionD());

        resetOptionStyle(holder);
        isOptionSelectionEnabled = true;

        // Check if the image URL matches the specific URL to hide the thumbnail
        if (quizQuestion.getImage() != null && !quizQuestion.getImage().isEmpty()) {
            if (quizQuestion.getImage().equals("https://res.cloudinary.com/dmzp6notl/image/upload/v1711436528/noimage_qtiaxj.jpg")) {
                holder.ifthequestionhavethumbnail.setVisibility(View.GONE);
            } else {
                holder.ifthequestionhavethumbnail.setVisibility(View.VISIBLE);
                Glide.with(context).load(quizQuestion.getImage()).into((ImageView) holder.ifthequestionhavethumbnail);
                holder.ifthequestionhavethumbnail.setOnClickListener(view -> showImagePopup(quizQuestion.getImage()));
            }
        } else {
            holder.ifthequestionhavethumbnail.setVisibility(View.GONE);
        }

        // Check if an option was previously selected for this question
        String selectedOption = quizQuestion.getSelectedOption();
        if (selectedOption != null && !selectedOption.isEmpty()) {
            setOptionSelectedStyle(holder, selectedOption);
        }


        setOptionClickListeners(holder);
    }

    public interface OnOptionInteractionListener {
        void onOptionSelected(int questionIndex, String selectedOption);
    }


    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }
    public void setOnOptionSelectedListener(OnOptionSelectedListener listener) {
        this.onOptionSelectedListener = listener;
    }

    public interface OnOptionSelectedListener {
        void onOptionSelected(String selectedOption);
    }

    public void setcurrentquestionindex(int currentQuestionIndex){
        this.currentQuestionIndex=currentQuestionIndex;

        Log.d("BottomSheetFragment 20", "Index " + this.currentQuestionIndex );

    }

    private void setOptionClickListeners(FmgeQuizQuestionViewHolder holder) {
        holder.optionA.setOnClickListener(view -> handleOptionClick(holder, "A"));
        holder.optionB.setOnClickListener(view -> handleOptionClick(holder, "B"));
        holder.optionC.setOnClickListener(view -> handleOptionClick(holder, "C"));
        holder.optionD.setOnClickListener(view -> handleOptionClick(holder, "D"));
    }

    private void handleOptionClick(FmgeQuizQuestionViewHolder holder, String selectedOption) {
        if (isOptionSelectionEnabled) {
            Fmgepg quizquestion = quizquestionsweekly.get(holder.getAdapterPosition());
            String currentSelectedOption = quizquestion.getSelectedOption();
            if (interactionListener != null) {
                interactionListener.onOptionSelected(this.currentQuestionIndex, selectedOption);
            }
            if (currentSelectedOption != null && currentSelectedOption.equals(selectedOption)) {
                resetOptionStyle(holder);
                quizquestion.setSelectedOption(null);
            } else {
                resetOptionStyle(holder);
                setOptionSelectedStyle(holder, selectedOption);
                quizquestion.setSelectedOption(selectedOption);
            }
            if (interactionListener != null) {
                interactionListener.onOptionSelected(holder.getAdapterPosition(), quizquestion.getSelectedOption());
            }
        }
    }

    public void disableOptionSelection() {
        isOptionSelectionEnabled = false;
    }

    public void setQuizQuestions(List<Fmgepg> quizQuestions) {
        this.quizquestionsweekly = new ArrayList<>(quizQuestions);
        notifyDataSetChanged();
    }

    private void setOptionSelectedStyle(FmgeQuizQuestionViewHolder holder, String selectedOption) {
        TextView selectedTextView = null;

        switch (selectedOption) {
            case "A":
                selectedTextView = holder.optionA;
                break;
            case "B":
                selectedTextView = holder.optionB;
                break;
            case "C":
                selectedTextView = holder.optionC;
                break;
            case "D":
                selectedTextView = holder.optionD;
                break;
        }

        if (selectedTextView != null) {
            selectedTextView.setBackgroundResource(R.drawable.selectedoptionbk);
            selectedTextView.setTextColor(Color.WHITE);
            selectedTextView.setTypeface(null, Typeface.BOLD);
        }
    }

    private void resetOptionStyle(FmgeQuizQuestionViewHolder holder) {
        holder.optionA.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.optionA.setTextColor(Color.BLACK);
        holder.optionA.setTypeface(null, Typeface.NORMAL);

        holder.optionB.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.optionB.setTextColor(Color.BLACK);
        holder.optionB.setTypeface(null, Typeface.NORMAL);

        holder.optionC.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.optionC.setTextColor(Color.BLACK);
        holder.optionC.setTypeface(null, Typeface.NORMAL);

        holder.optionD.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.optionD.setTextColor(Color.BLACK);
        holder.optionD.setTypeface(null, Typeface.NORMAL);
    }

    @Override
    public int getItemCount() {
        return quizquestionsweekly.size();
    }

    public static class FmgeQuizQuestionViewHolder extends RecyclerView.ViewHolder {

        TextView questionspan;
        TextView optionA;
        TextView optionB;
        TextView optionC;
        TextView optionD;
        View ifthequestionhavethumbnail;

        public FmgeQuizQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            questionspan = itemView.findViewById(R.id.questionspan1);
            optionA = itemView.findViewById(R.id.optionA1);
            optionB = itemView.findViewById(R.id.optionB1);
            optionC = itemView.findViewById(R.id.optionC1);
            optionD = itemView.findViewById(R.id.optionD1);
            ifthequestionhavethumbnail = itemView.findViewById(R.id.ifthequestionhavethumbnail);
        }

    }

    private void showImagePopup(String imageUrl) {
        if (imageUrl != null && !imageUrl.isEmpty()) {
            View popupView = LayoutInflater.from(context).inflate(R.layout.popup_image, null);
            PhotoView photoView = popupView.findViewById(R.id.photoView);
            Glide.with(context).load(imageUrl).into(photoView);

            int dialogWidth = context.getResources().getDimensionPixelSize(R.dimen.popup_width);
            int dialogHeight = context.getResources().getDimensionPixelSize(R.dimen.popup_height);

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setView(popupView);
            AlertDialog dialog = builder.create();

            dialog.getWindow().setLayout(dialogWidth, dialogHeight);

            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            popupView.findViewById(R.id.btnClose).setOnClickListener(view -> dialog.dismiss());

            dialog.show();
        } else {
            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
        }
    }
}