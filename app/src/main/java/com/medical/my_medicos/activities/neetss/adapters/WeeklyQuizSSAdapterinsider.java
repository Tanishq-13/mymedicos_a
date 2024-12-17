    package com.medical.my_medicos.activities.neetss.adapters;

    import android.annotation.SuppressLint;
    import android.app.AlertDialog;
    import android.content.Context;
    import android.graphics.Color;
    import android.graphics.Typeface;
    import android.media.MediaPlayer;
    import android.os.CountDownTimer;
    import android.os.Handler;
    import android.util.Log;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.TextView;

    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.recyclerview.widget.RecyclerView;

    import com.bumptech.glide.Glide;
    import com.github.chrisbanes.photoview.PhotoView;
    import com.medical.my_medicos.R;
    import com.medical.my_medicos.activities.neetss.activites.insiders.optionsbottom.OptionsBottomSheetDialogueFragment;
    import com.medical.my_medicos.activities.neetss.model.QuizSSinsider;
    import com.medical.my_medicos.activities.pg.model.QuizPGinsider;
    import com.medical.my_medicos.databinding.QuestionQuizDesignWeeklyBinding;

    import java.util.ArrayList;
    import java.util.List;

    public class WeeklyQuizSSAdapterinsider extends RecyclerView.Adapter<WeeklyQuizSSAdapterinsider.WeeklyQuizQuestionSSViewHolder> {

        private Context context;
        private ArrayList<QuizSSinsider> quizquestionsweekly;
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
        public WeeklyQuizSSAdapterinsider(Context context, ArrayList<QuizSSinsider> quiz, OnOptionInteractionListener interactionListener) {
            this.context = context;
            this.quizquestionsweekly = quiz;
            this.selectedOption = null;

            this.interactionListener = interactionListener;
        }


        @NonNull
        @Override
        public WeeklyQuizQuestionSSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.question_quiz_design_weekly, parent, false);
            return new WeeklyQuizQuestionSSViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull WeeklyQuizQuestionSSViewHolder holder, @SuppressLint("RecyclerView") int position) {
            QuizSSinsider quizquestion = quizquestionsweekly.get(position);

            holder.binding.questionspan.setText(quizquestion.getQuestion());
            holder.binding.optionA.setText(quizquestion.getOptionA());
            holder.binding.optionB.setText(quizquestion.getOptionB());
            holder.binding.optionC.setText(quizquestion.getOptionC());
            holder.binding.optionD.setText(quizquestion.getOptionD());

            resetOptionStyle(holder);

            String selectedOption = quizquestion.getSelectedOption();
            if (selectedOption != null && !selectedOption.isEmpty()) {
                setOptionSelectedStyle(holder, selectedOption);
            }

            setOptionClickListeners(holder);

            if (quizquestion.getImage() != null && !quizquestion.getImage().isEmpty()) {
                if (quizquestion.getImage().equals("https://res.cloudinary.com/dmzp6notl/image/upload/v1711436528/noimage_qtiaxj.jpg")) {
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



        private void setOptionClickListeners(WeeklyQuizQuestionSSViewHolder holder) {
            holder.binding.optionA.setOnClickListener(view -> handleOptionClick(holder, "A"));
            holder.binding.optionB.setOnClickListener(view -> handleOptionClick(holder, "B"));
            holder.binding.optionC.setOnClickListener(view -> handleOptionClick(holder, "C"));
            holder.binding.optionD.setOnClickListener(view -> handleOptionClick(holder, "D"));
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
            QuizSSinsider currentQuestion = quizquestionsweekly.get(currentQuestionIndex);
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


        private void handleOptionClick(WeeklyQuizQuestionSSViewHolder holder, String option) {
            if (isOptionSelectionEnabled) {
                QuizSSinsider quizquestion = quizquestionsweekly.get(0);
                String selectedOption = quizquestion.getSelectedOption();

                // Check if the clicked option is the same as the currently selected one
                if (option.equals(selectedOption)) {
                    // Deselect the option
                    quizquestion.setSelectedOption(null);
                    resetOptionStyle(holder);
                } else {
                    // Select the new option
                    quizquestion.setSelectedOption(option);
                    resetOptionStyle(holder);
                    setOptionSelectedStyle(holder, option);
                }

                // Notify the activity that an option has been selected or deselected
                if (interactionListener != null) {
                    interactionListener.onOptionSelected(currentQuestionIndex, quizquestion.getSelectedOption());
                }
            }
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

        public void setQuizQuestions(List<QuizSSinsider> quizQuestions) {
            this.quizquestionsweekly.clear();
            this.quizquestionsweekly.addAll(quizQuestions);
            notifyDataSetChanged(); // This line is crucial
        }



        private void showOptionsAndDescription(QuizPGinsider quizQuestion, WeeklyQuizQuestionSSViewHolder holder) {
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
        public void resetButtonColors1(WeeklyQuizQuestionSSViewHolder holder) {
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


        private void showNoOptionSelectedDialog(QuizPGinsider quizquestion,WeeklyQuizQuestionSSViewHolder holder) {
    //        if (context instanceof Activity && !((Activity) context).isFinishing()) {
    //            QuizPGinsider quizquestion = quizquestionsweekly.get(currentQuestionIndex);

                String correctOption = quizquestion.getCorrectAnswer();
                String description = quizquestion.getDescription();

                OptionsBottomSheetDialogueFragment bottomSheetDialogFragment = OptionsBottomSheetDialogueFragment.newInstance(correctOption, "N/A", description);
                bottomSheetDialogFragment.show(((AppCompatActivity) context).getSupportFragmentManager(), "OptionsBottomSheetDialogFragment");
    //        }
        }

        private void setOptionSelectedStyle(WeeklyQuizQuestionSSViewHolder holder, String selectedOption) {
            TextView selectedTextView = null;

            switch (selectedOption) {
                case "A":
                    selectedTextView = holder.binding.optionA;
                    break;
                case "B":
                    selectedTextView = holder.binding.optionB;
                    break;
                case "C":
                    selectedTextView = holder.binding.optionC;
                    break;
                case "D":
                    selectedTextView = holder.binding.optionD;
                    break;
            }

            if (selectedTextView != null) {
                selectedTextView.setBackgroundResource(R.drawable.selectedoptionbk);
                selectedTextView.setTextColor(Color.WHITE);
                selectedTextView.setTypeface(null, Typeface.BOLD);
            }
        }


        private void resetOptionStyle(WeeklyQuizQuestionSSViewHolder holder) {
            if (holder != null && holder.binding != null) {
                holder.binding.optionA.setBackgroundResource(R.drawable.optionunselectedcolor);
                holder.binding.optionA.setTextColor(Color.BLACK);
                holder.binding.optionA.setTypeface(null, Typeface.NORMAL);

                holder.binding.optionB.setBackgroundResource(R.drawable.optionunselectedcolor);
                holder.binding.optionB.setTextColor(Color.BLACK);
                holder.binding.optionB.setTypeface(null, Typeface.NORMAL);

                holder.binding.optionC.setBackgroundResource(R.drawable.optionunselectedcolor);
                holder.binding.optionC.setTextColor(Color.BLACK);
                holder.binding.optionC.setTypeface(null, Typeface.NORMAL);

                holder.binding.optionD.setBackgroundResource(R.drawable.optionunselectedcolor);
                holder.binding.optionD.setTextColor(Color.BLACK);
                holder.binding.optionD.setTypeface(null, Typeface.NORMAL);
            }
        }
        @Override
        public int getItemCount() {
            return quizquestionsweekly != null ? quizquestionsweekly.size() : 0;
        }


        public class WeeklyQuizQuestionSSViewHolder extends RecyclerView.ViewHolder {

            QuestionQuizDesignWeeklyBinding binding;

            public WeeklyQuizQuestionSSViewHolder(@NonNull View itemView) {
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
