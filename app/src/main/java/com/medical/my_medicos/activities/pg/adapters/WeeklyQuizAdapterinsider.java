    package com.medical.my_medicos.activities.pg.adapters;

    import android.annotation.SuppressLint;
    import android.app.Activity;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.content.Intent;
    import android.graphics.Color;
    import android.graphics.Typeface;
    import android.media.MediaPlayer;
    import android.os.CountDownTimer;
    import android.os.Handler;
    import android.speech.tts.TextToSpeech;
    import android.text.Html;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.fragment.app.Fragment;
    import androidx.recyclerview.widget.RecyclerView;

    import com.bumptech.glide.Glide;
    import com.github.chrisbanes.photoview.PhotoView;
    import com.medical.my_medicos.R;
    import com.medical.my_medicos.activities.pg.activites.Neetexaminsider;
    import com.medical.my_medicos.activities.pg.activites.ResultActivity;
    import com.medical.my_medicos.activities.pg.activites.extras.InsiderDataBottomSheet;
    import com.medical.my_medicos.activities.pg.activites.insiders.WeeklyQuizInsiderActivity;
    import com.medical.my_medicos.activities.pg.activites.insiders.optionsbottom.OptionsBottomSheetDialogueFragment;
    import com.medical.my_medicos.activities.pg.model.QuizPGinsider;
    import com.medical.my_medicos.databinding.QuestionQuizDesignWeeklyBinding;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;

    public class WeeklyQuizAdapterinsider extends RecyclerView.Adapter<WeeklyQuizAdapterinsider.WeeklyQuizQuestionViewHolder> {

        private ArrayList<QuizPGinsider> copy;
        private ArrayList<String> selectedOptions;
        private Context context;
        private ArrayList<QuizPGinsider> quizquestionsweekly;
        private TextView textViewTimer;
        private boolean isOptionSelectionEnabled = true;

        private String selectedOption;
        public int currentQuestionIndex = 0;
        private CountDownTimer countDownTimer;
        private static final long TIME_LIMIT_MILLIS = 30000;
        private MediaPlayer mediaPlayer;
        private boolean isBottomSheetVisible = false;

        private OnOptionInteractionListener interactionListener;


        //    private OnCountdownFinishedListener onCountdownFinishedListener;
        public WeeklyQuizAdapterinsider(Context context, ArrayList<QuizPGinsider> quiz, OnOptionInteractionListener interactionListener) {
            this.context = context;
            this.quizquestionsweekly = quiz;
            this.selectedOption = null;

            this.interactionListener = interactionListener;
        }


        @NonNull
        @Override
        public WeeklyQuizQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.question_quiz_design_weekly, parent, false);

            Log.d("list size", String.valueOf(quizquestionsweekly.size()));
            return new WeeklyQuizQuestionViewHolder(view);

        }

        @Override
        public void onBindViewHolder(@NonNull WeeklyQuizQuestionViewHolder holder, @SuppressLint("RecyclerView") int position) {
            Log.d("hhih",String.valueOf(position));
            QuizPGinsider quizquestion = quizquestionsweekly.get(position);
            Log.d("btadoo2",String.valueOf(quizquestion.getQuestion()));

            // Bind the question and options to the UI
            holder.binding.questionspan.setText(quizquestion.getQuestion());
            holder.binding.optionA.setText(quizquestion.getOptionA());
            holder.binding.optionB.setText(quizquestion.getOptionB());
            holder.binding.optionC.setText(quizquestion.getOptionC());
            holder.binding.optionD.setText(quizquestion.getOptionD());

            // Reset option styles
            resetOptionStyle(holder);
            // Reset description visibility and text initially
            holder.binding.description.setVisibility(View.GONE);
            holder.binding.headingDesc.setVisibility(View.GONE);
            holder.binding.description.setText(""); // Clear previous description
            // If an option is already selected, apply the selected style
            String selectedOption = quizquestion.getSelectedOption();
            if (selectedOption != null && !selectedOption.isEmpty()) {
                // Display the selected option and show correct/incorrect state
                holder.binding.headingDesc.setVisibility(View.VISIBLE);
                holder.binding.description.setVisibility(View.VISIBLE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    holder.binding.description.setText(quizquestion.getDescription());
                } else {
                    holder.binding.description.setText(quizquestion.getDescription());
                }
                highlightSelectedAndCorrectAnswer(holder, selectedOption, quizquestion.getCorrectAnswer());
            }
            if(selectedOption==null || selectedOption.isEmpty() || selectedOption.equals("Skip")) {
                Log.d("slctdopt",String.valueOf(selectedOption));
                // Set click listeners for options
                setOptionClickListeners(holder, position);
            }

            // Handle image loading for the question
            if (quizquestion.getImage() != null) {
                Log.d("jijj",quizquestion.getImage());

                if (quizquestion.getImage().equals("https://firebasestorage.googleapis.com/v0/b/mymedicosupdated.appspot.com/o/Quiz%2Fthumbnails%2Fab415bfc-5a52-4640-beda-669cdacad12aNo.png?alt=media&token=9f0b7576-1b66-4803-b94c-05b5e89be1ba")) {
                    Log.d("jij","no img");
                    holder.binding.ifthequestionhavethumbnail.setVisibility(View.GONE);
                } else {
                    holder.binding.ifthequestionhavethumbnail.setVisibility(View.VISIBLE);
                    Glide.with(context).load(quizquestion.getImage()).into(holder.binding.ifthequestionhavethumbnail);
                    holder.binding.ifthequestionhavethumbnail.setOnClickListener(view -> showImagePopup(quizquestion.getImage()));
                }
            } else {
                holder.binding.ifthequestionhavethumbnail.setVisibility(View.GONE);
            }

        }

        private void handleOptionClick(WeeklyQuizQuestionViewHolder holder, String option, int position) {
            // Get the current quiz question
            QuizPGinsider quizquestion = quizquestionsweekly.get(position);

            // Check if an option has already been selected for this question
            if (quizquestion.getSelectedOption() == null || quizquestion.getSelectedOption().isEmpty()) {
                // Option is not selected yet, proceed to handle the click
                quizquestion.setSelectedOption(option);

                // Reset the styles and highlight the selected option and correct answer
                resetOptionStyle(holder);
                highlightSelectedAndCorrectAnswer(holder, option, quizquestion.getCorrectAnswer());

                // Display the description
                holder.binding.headingDesc.setVisibility(View.VISIBLE);
                holder.binding.description.setVisibility(View.VISIBLE);
                holder.binding.description.setText(quizquestion.getDescription());

                // Notify the activity or fragment about the option selection
                if (interactionListener != null) {
                    interactionListener.onOptionSelected((int) quizquestion.getNumber(), quizquestion.getSelectedOption());
                }

                // Disable further option clicks for this question
                disableOptionClicks(holder);
            }
        }

        // Disable option buttons once an option is selected
        private void disableOptionClicks(WeeklyQuizQuestionViewHolder holder) {
            holder.binding.optionA.setClickable(false);
            holder.binding.optionB.setClickable(false);
            holder.binding.optionC.setClickable(false);
            holder.binding.optionD.setClickable(false);
        }

        private void setOptionClickListeners(WeeklyQuizQuestionViewHolder holder, int position) {

            Log.d("btadoo",String.valueOf(position));
            holder.binding.optionA.setOnClickListener(view -> handleOptionClick(holder, "A", position));
            holder.binding.optionB.setOnClickListener(view -> handleOptionClick(holder, "B", position));
            holder.binding.optionC.setOnClickListener(view -> handleOptionClick(holder, "C", position));
            holder.binding.optionD.setOnClickListener(view -> handleOptionClick(holder, "D", position));
        }

        // Reset option styles to default (e.g., no color highlight)
        private void resetOptionStyle(WeeklyQuizQuestionViewHolder holder) {
            holder.binding.optionA.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.optionB.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.optionC.setBackgroundColor(Color.TRANSPARENT);
            holder.binding.optionD.setBackgroundColor(Color.TRANSPARENT);
        }

        // Highlight selected answer and correct answer with colors
        private void highlightSelectedAndCorrectAnswer(WeeklyQuizQuestionViewHolder holder, String selectedOption, String correctAnswer) {
            if (selectedOption.equals(correctAnswer)) {
                // If the selected option is correct, highlight it in green
                setOptionStyle(holder, selectedOption, R.drawable.correct_option_background);
            } else {
                // If the selected option is wrong, highlight it in red and correct answer in green
                setOptionStyle(holder, selectedOption, R.drawable.wrong_option_background);
                setOptionStyle(holder, correctAnswer, R.drawable.correct_option_background);
            }
        }

        // Helper method to apply color style to the selected or correct option
        private void setOptionStyle(WeeklyQuizQuestionViewHolder holder, String option, int color) {

            switch (option) {
                case "A":
                    holder.binding.optionA.setBackgroundResource(color);
                    break;
                case "B":
                    holder.binding.optionB.setBackgroundResource(color);
                    break;
                case "C":
                    holder.binding.optionC.setBackgroundResource(color);
                    break;
                case "D":
                    holder.binding.optionD.setBackgroundResource(color);
                    break;
            }
        }

        public void setSelectedOption(String selectedOption) {
            this.selectedOption = selectedOption;

        }


    //    private void startTimer() {
    //        if (countDownTimer != null) {
    //            countDownTimer.cancel();
    //        }
    //
    //        countDownTimer = new CountDownTimer(TIME_LIMIT_MILLIS, 1000) {
    //            @Override
    //            public void onTick(long millisUntilFinished) {
    //                long secondsRemaining = millisUntilFinished / 1000;
    //                textViewTimer.setText(String.valueOf(secondsRemaining));
    //
    //                if (secondsRemaining <= 16 && secondsRemaining > 10) {
    //                    textViewTimer.setBackgroundResource(R.drawable.counterbkfor16);
    //                } else if (secondsRemaining <= 10) {
    //                    textViewTimer.setBackgroundResource(R.drawable.counterforbk10);
    //                }
    //            }
    //
    //            @Override
    //            public void onFinish() {
    //                disableOptionSelection();
    //                if (onCountdownFinishedListener != null) {
    //                    onCountdownFinishedListener.onCountdownFinished();
    //                }
    //            }
    //
    //        }.start();
    //    }
    //
    //    public interface OnCountdownFinishedListener {
    //        void onCountdownFinished();
    //    }

        private void showBottomSheetAgain() {
            QuizPGinsider currentQuestion = quizquestionsweekly.get(currentQuestionIndex);
            String correctOption = currentQuestion.getCorrectAnswer();
            String selectedOption = currentQuestion.getSelectedOption();
            String description = currentQuestion.getDescription();

            OptionsBottomSheetDialogueFragment bottomSheetDialogFragment = OptionsBottomSheetDialogueFragment.newInstance(correctOption, selectedOption, description);
            bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialogFragment");
        }
        public interface OnOptionInteractionListener {
            void onOptionSelected(int questionIndex, String selectedOption);
        }
        @SuppressLint("SuspiciousIndentation")
        public void setcurrentquestionindex(int currentQuestionIndex){
            this.currentQuestionIndex=currentQuestionIndex;

                Log.d("BottomSheetFragment 20", "Index " + this.currentQuestionIndex );

        }




    //    public void refreshNavigationGrid() {
    //        Fragment fragment = getSupportFragmentManager().findFragmentByTag("QuestionNavigator");
    //        if (fragment instanceof Neetssexaminsider.QuestionBottomSheetDialogFragment) {
    //            ((Neetssexaminsider.QuestionBottomSheetDialogFragment) fragment).updateGrid();
    //        }
    //    }




        public void disableOptionSelection() {
            isOptionSelectionEnabled = false;
        }

        public void setQuizQuestions(List<QuizPGinsider> quizQuestions) {
            this.quizquestionsweekly.clear();
            this.quizquestionsweekly.addAll(quizQuestions);
            notifyDataSetChanged(); // This line is crucial
        }



        private void showOptionsAndDescription(QuizPGinsider quizQuestion, WeeklyQuizQuestionViewHolder holder) {
            String correctOption = quizQuestion.getCorrectAnswer();
            String selectedOption = quizQuestion.getSelectedOption();
            String description = quizQuestion.getDescription();

            OptionsBottomSheetDialogueFragment bottomSheetDialogFragment = OptionsBottomSheetDialogueFragment.newInstance(correctOption, selectedOption, description);
            bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialogFragment");

    //        disableOptionSelection();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    resetButtonColors1(holder);
                }
            }, 5000);
        }
        public void resetButtonColors1(WeeklyQuizQuestionViewHolder holder) {
            // Reset option A
            holder.binding.optionA.setBackgroundResource(R.drawable.categorynewbk);
            holder.binding.optionA.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.optionA.setTypeface(null, Typeface.NORMAL);

            // Reset option B
            holder.binding.optionB.setBackgroundResource(R.drawable.categorynewbk);
            holder. binding.optionB.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.optionB.setTypeface(null, Typeface.NORMAL);

            // Reset option C
            holder.binding.optionC.setBackgroundResource(R.drawable.categorynewbk);
            holder.binding.optionC.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.optionC.setTypeface(null, Typeface.NORMAL);

            // Reset option D
            holder.binding. optionD.setBackgroundResource(R.drawable.categorynewbk);
            holder. binding.optionD.setTextColor(context.getResources().getColor(R.color.black));
            holder.binding.optionD.setTypeface(null, Typeface.NORMAL);
        }


        private void showNoOptionSelectedDialog(QuizPGinsider quizquestion,WeeklyQuizQuestionViewHolder holder) {
    //        if (context instanceof Activity && !((Activity) context).isFinishing()) {
    //            QuizPGinsider quizquestion = quizquestionsweekly.get(currentQuestionIndex);

                String correctOption = quizquestion.getCorrectAnswer();
                String description = quizquestion.getDescription();

                OptionsBottomSheetDialogueFragment bottomSheetDialogFragment = OptionsBottomSheetDialogueFragment.newInstance(correctOption, "N/A", description);
                bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialogFragment");
    //        }
        }

//        private void setOptionSelectedStyle(WeeklyQuizQuestionViewHolder holder, String selectedOption) {
//            TextView selectedTextView = null;
//
//            switch (selectedOption) {
//                case "A":
//                    selectedTextView = holder.binding.optionA;
//                    break;
//                case "B":
//                    selectedTextView = holder.binding.optionB;
//                    break;
//                case "C":
//                    selectedTextView = holder.binding.optionC;
//                    break;
//                case "D":
//                    selectedTextView = holder.binding.optionD;
//                    break;
//            }
//
//            if (selectedTextView != null) {
//                selectedTextView.setBackgroundResource(R.drawable.selectedoptionbk);
//                selectedTextView.setTextColor(Color.WHITE);
//                selectedTextView.setTypeface(null, Typeface.BOLD);
//            }
//        }
//
//
//        private void resetOptionStyle(WeeklyQuizQuestionViewHolder holder) {
//            if (holder != null && holder.binding != null) {
//                holder.binding.optionA.setBackgroundResource(R.drawable.optionunselectedcolor);
//                holder.binding.optionA.setTextColor(Color.BLACK);
//                holder.binding.optionA.setTypeface(null, Typeface.NORMAL);
//
//                holder.binding.optionB.setBackgroundResource(R.drawable.optionunselectedcolor);
//                holder.binding.optionB.setTextColor(Color.BLACK);
//                holder.binding.optionB.setTypeface(null, Typeface.NORMAL);
//
//                holder.binding.optionC.setBackgroundResource(R.drawable.optionunselectedcolor);
//                holder.binding.optionC.setTextColor(Color.BLACK);
//                holder.binding.optionC.setTypeface(null, Typeface.NORMAL);
//
//                holder.binding.optionD.setBackgroundResource(R.drawable.optionunselectedcolor);
//                holder.binding.optionD.setTextColor(Color.BLACK);
//                holder.binding.optionD.setTypeface(null, Typeface.NORMAL);
//            }
//        }
        @Override
        public int getItemCount() {
            return quizquestionsweekly != null ? quizquestionsweekly.size() : 0;
        }


        public class WeeklyQuizQuestionViewHolder extends RecyclerView.ViewHolder {

            public QuestionQuizDesignWeeklyBinding binding;

            public WeeklyQuizQuestionViewHolder(@NonNull View itemView) {
                super(itemView);
                binding = QuestionQuizDesignWeeklyBinding.bind(itemView);

            }
        }

        @SuppressLint("MissingInflatedId")
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

            }
        }

    }
