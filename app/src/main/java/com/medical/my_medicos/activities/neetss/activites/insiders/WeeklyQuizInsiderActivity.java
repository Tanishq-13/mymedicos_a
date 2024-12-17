package com.medical.my_medicos.activities.neetss.activites.insiders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.SsprepActivity;
import com.medical.my_medicos.activities.neetss.activites.ResultssActivity;
import com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapterinsider;
import com.medical.my_medicos.activities.neetss.model.QuizSSinsider;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class WeeklyQuizInsiderActivity extends AppCompatActivity implements WeeklyQuizSSAdapterinsider.OnOptionInteractionListener {
    private RecyclerView recyclerView;
    private WeeklyQuizSSAdapterinsider adapter;
    private ArrayList<QuizSSinsider> quizList;
    private ArrayList<QuizSSinsider> copy;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private TextView currentquestion;
    private TextView timerTextView;
    int skippedQuestions = 0;
    private long timeLeftInMillis = 25 * 60 * 1000;
    private long remainingTimeInMillis;
    public ArrayList<String> selectedOptionsList = new ArrayList<>();
    private String id;
    private CheckBox markForReviewCheckBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_quiz_insider);
        TextView title1 = findViewById(R.id.setnamewillbehere);
        currentquestion = findViewById(R.id.currentquestion1);
//        timerTextView = findViewById(R.id.timerTextView1);
        markForReviewCheckBox = findViewById(R.id.markForReviewCheckBox);

        recyclerView = findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        String str = intent.getStringExtra("Title1"); // Specialty
        String str1 = intent.getStringExtra("Title"); // Title
        title1.setText(str1);
        quizList = new ArrayList<>();
        copy = new ArrayList<>();
        startTimer();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");
        quizzCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                quizList.clear();
                copy.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String speciality = document.getString("speciality");
                    String title = document.getString("title");

                    if (str.equals(speciality) && str1.equals(title)) {
                        title1.setText(title);
                        ArrayList<Map<String, Object>> dataList = (ArrayList<Map<String, Object>>) document.get("Data");
                        for (Map<String, Object> entry : dataList) {
                            String question = (String) entry.get("Question");
                            String correctAnswer = (String) entry.get("Correct");
                            String optionA = (String) entry.get("A");
                            String optionB = (String) entry.get("B");
                            String optionC = (String) entry.get("C");
                            String optionD = (String) entry.get("D");
                            String description = (String) entry.get("Description");
                            String imageUrl = (String) entry.get("Image");
                            id = document.getId();
                            QuizSSinsider quizQuestion;
                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                quizQuestion = new QuizSSinsider(question, optionA, optionB, optionC, optionD, correctAnswer, imageUrl, description);
                            } else {
                                quizQuestion = new QuizSSinsider(question, optionA, optionB, optionC, optionD, correctAnswer, null, description);
                            }
                            copy.add(quizQuestion);
                            quizList.add(quizQuestion);
                            selectedOptionsList.add(null); // Ensure selectedOptionsList is initialized
                        }
                    }
                }
                loadNextQuestion(copy);
                updateQuestionNumber();
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
            }
        });

        markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            QuizSSinsider currentQuestion = copy.get(currentQuestionIndex);
            currentQuestion.setMarkedForReview(isChecked);
            refreshNavigationGrid();
        });

        ImageView gotoQuestionButton = findViewById(R.id.Navigate);
        gotoQuestionButton.setOnClickListener(v -> {
            QuestionBottomSheetDialogFragment bottomSheetDialogFragment = QuestionBottomSheetDialogFragment.newInstance(selectedOptionsList, copy);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "QuestionNavigator");
        });

        TextView prevButton = findViewById(R.id.BackButton);
        prevButton.setOnClickListener(v -> loadPreviousQuestion());

        TextView nextButton = findViewById(R.id.nextButton2);
        nextButton.setOnClickListener(v -> loadNextQuestion(copy));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeeklyQuizSSAdapterinsider(this, quizList, this);
        Log.d("BottomSheetFragment 10", "Weeklyquizinsider activity" + currentQuestionIndex);
        recyclerView.setAdapter(adapter);

//        LinearLayout toTheBackLayout = findViewById(R.id.totheback);
//        toTheBackLayout.setOnClickListener(v -> showConfirmationDialog());

        configureWindow();
    }

    private void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }



    @Override
    public void onOptionSelected(int questionIndex, String selectedOption) {
        if (selectedOption == null || selectedOption.isEmpty()) {
            selectedOptionsList.set(questionIndex, "Skip");
        } else {
            selectedOptionsList.set(questionIndex, selectedOption);
        }
        refreshNavigationGrid();
    }


    public void refreshNavigationGrid() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("QuestionNavigator");
        if (fragment instanceof QuestionBottomSheetDialogFragment) {
            Log.d("BottomSheetFragment", "Fragment found with tag 'QuestionNavigator', updating grid");
            ((QuestionBottomSheetDialogFragment) fragment).updateGrid(selectedOptionsList);
            for (int i = 0; i < selectedOptionsList.size(); i++) {
                Log.d("BottomSheetFragment 3", "Index " + i + ": " + selectedOptionsList.get(i));
            }
            Log.d("BottomSheetFragment", "Updated grid with selected options list size: " + selectedOptionsList.size());
        } else {
            Log.d("BottomSheetFragment", "No fragment found with tag 'QuestionNavigator'");
        }
    }

    private void navigateToQuestion(int questionNumber) {
        if (questionNumber >= 0 && questionNumber < copy.size()) {
            currentQuestionIndex = questionNumber;
            adapter.setQuizQuestions(Collections.singletonList(copy.get(currentQuestionIndex)));
            adapter.setSelectedOption(selectedOptionsList.get(currentQuestionIndex));
            adapter.setcurrentquestionindex(currentQuestionIndex);
            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            updateQuestionNumber();
            markForReviewCheckBox.setOnCheckedChangeListener(null);
            markForReviewCheckBox.setChecked(copy.get(currentQuestionIndex).isMarkedForReview());
            markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                QuizSSinsider currentQuestion = copy.get(currentQuestionIndex);
                currentQuestion.setMarkedForReview(isChecked);
                refreshNavigationGrid();
            });
        } else {
            Toast.makeText(this, "Invalid question number", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuestionNumber() {
        int totalQuestions = copy.size();
        String questionNumberText = (currentQuestionIndex + 1) + " / " + totalQuestions;
        currentquestion.setText(questionNumberText);
    }

    private void loadNextQuestion(ArrayList<QuizSSinsider> quizList) {
        int q1 = copy.size();
        if (currentQuestionIndex < q1) {
            Log.d("abasjdnajs", "Current Question Index: " + currentQuestionIndex);
            Log.d("abasjdnajs", "Quiz List Size: " + quizList.size());
            for (int i = 0; i < q1; i++) {
                QuizSSinsider quizQuestion = copy.get(i);
                Log.d("QuizPGinsider", "Question " + (i + 1) + ": " + quizQuestion.getQuestion());
            }

            // Check if the selected option at the current index is null
            if (selectedOptionsList.get(currentQuestionIndex) == null) {
                onOptionSelected(currentQuestionIndex, "Skip");
            }

            adapter.setcurrentquestionindex(currentQuestionIndex);
            final QuizSSinsider currentQuestion = copy.get(currentQuestionIndex);
            adapter.setQuizQuestions(Collections.singletonList(currentQuestion));
            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            updateQuestionNumber();

            markForReviewCheckBox.setOnCheckedChangeListener(null);
            markForReviewCheckBox.setChecked(copy.get(currentQuestionIndex).isMarkedForReview());
            markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                currentQuestion.setMarkedForReview(isChecked);
                refreshNavigationGrid();
            });
            currentQuestionIndex++;
        } else {
            Log.d("abasjdnajs", "Current Question Index: " + currentQuestionIndex);
            Log.d("abasjdnajs", "Quiz List Size: " + quizList.size());
            for (int i = 0; i < quizList.size(); i++) {
                QuizSSinsider quizQuestion = quizList.get(i);
                Log.d("QuizPGinsider", "Question " + (i + 1) + ": " + quizQuestion.getQuestion());
            }
            showEndQuizConfirmation();
        }
    }

    private void loadPreviousQuestion() {
        if (!selectedOptionsList.isEmpty()) {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                adapter.setQuizQuestions(Collections.singletonList(copy.get(currentQuestionIndex)));
                adapter.setSelectedOption(selectedOptionsList.get(currentQuestionIndex));
                recyclerView.smoothScrollToPosition(currentQuestionIndex);
                adapter.setcurrentquestionindex(currentQuestionIndex);
                updateQuestionNumber();
                markForReviewCheckBox.setOnCheckedChangeListener(null);
                markForReviewCheckBox.setChecked(copy.get(currentQuestionIndex).isMarkedForReview());
                markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    QuizSSinsider currentQuestion = copy.get(currentQuestionIndex);
                    currentQuestion.setMarkedForReview(isChecked);
                    refreshNavigationGrid();
                });
            } else {
                Toast.makeText(this, "This is the first question", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No selected options available", Toast.LENGTH_SHORT).show();
        }
    }

    private void showEndQuizConfirmation() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("End Quiz");
        builder.setMessage("Are you sure you want to end the quiz?");
        builder.setPositiveButton("Confirm", (dialog, which) -> handleEndButtonClick());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("End Quiz");
        builder.setMessage("Are you sure you want to end the quiz?");
        builder.setPositiveButton("Confirm", (dialog, which) -> handleEndButtonClick());
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateToPrepareActivity() {
        Intent intent = new Intent(WeeklyQuizInsiderActivity.this, SsprepActivity.class);
        startActivity(intent);
        finish();
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                boolean timerRunning = false;
                handleQuizEnd();
            }
        }.start();
    }

    private void handleQuizEnd() {
        int skippedQuestions = (int) copy.stream().map(quizQuestion -> quizQuestion.getSelectedOption() != null ? quizQuestion.getSelectedOption() : "Skip").filter("Skip"::equals).count();
        remainingTimeInMillis = timeLeftInMillis;

        // Show alert dialog
        new AlertDialog.Builder(this)
                .setTitle("Time's Up!")
                .setMessage("Sorry, you've run out of time. The quiz will be ended.")
                .setPositiveButton("OK", (dialog, which) -> navigateToResultActivity(skippedQuestions))
                .setCancelable(false)
                .create()
                .show();
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        timerTextView.setText(timeFormatted);
    }

    private void handleEndButtonClick() {
        ArrayList<String> userSelectedOptions = new ArrayList<>();

        for (QuizSSinsider quizQuestion : quizList) {
            String selectedOption = quizQuestion.getSelectedOption() != null ? quizQuestion.getSelectedOption() : "Skip";
            if ("Skip".equals(selectedOption)) {
                skippedQuestions++;
            }
            userSelectedOptions.add(selectedOption);
        }
        remainingTimeInMillis = timeLeftInMillis;
        countDownTimer.cancel();
        navigateToResultActivity(skippedQuestions);
    }

    private void navigateToResultActivity(int skippedQuestions) {
        Intent intent = new Intent(WeeklyQuizInsiderActivity.this, ResultssActivity.class);
        intent.putExtra("questions", copy);
        intent.putExtra("remainingTime", remainingTimeInMillis);
        intent.putExtra("id", id);
        intent.putExtra("skippedQuestions", skippedQuestions); // Passing the number of skipped questions
        startActivity(intent);
        finish();
    }

    public void onCountdownFinished() {
        loadNextQuestion(copy);
    }

    public static class QuestionBottomSheetDialogFragment extends BottomSheetDialogFragment {
        private GridView gridView;
        private QuestionNavigationAdapter adapter;
        private TextView answeredCountTextView;
        private TextView unansweredCountTextView;
        private TextView notVisitedCountTextView;

        public static QuestionBottomSheetDialogFragment newInstance(ArrayList<String> selectedOptionsList, ArrayList<QuizSSinsider> quizQuestions) {
            QuestionBottomSheetDialogFragment fragment = new QuestionBottomSheetDialogFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("selectedOptionsList", selectedOptionsList);
            args.putSerializable("quizQuestions", quizQuestions);
            for (int i = 0; i < selectedOptionsList.size(); i++) {
                Log.d("BottomSheetFragment 2", "Index " + i + ": " + selectedOptionsList.get(i));
            }
            Log.d("BottomSheetFragment", "Selected options list size: " + selectedOptionsList.size());
            fragment.setArguments(args);
            return fragment;
        }

        @SuppressLint("MissingInflatedId")
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.bottom_sheet_layout_neet, container, false);
            gridView = view.findViewById(R.id.grid_view);
            answeredCountTextView = view.findViewById(R.id.answered_count);
            unansweredCountTextView = view.findViewById(R.id.unanswered_count);
            notVisitedCountTextView = view.findViewById(R.id.not_visited_count);
            ArrayList<String> selectedOptions = getArguments() != null ? getArguments().getStringArrayList("selectedOptionsList") : new ArrayList<>();
            Log.d("BottomSheetFragment", "Selected options list size: " + selectedOptions.size());
            ArrayList<QuizPGinsider> quizQuestions = (ArrayList<QuizPGinsider>) getArguments().getSerializable("quizQuestions");
            for (int i = 0; i < selectedOptions.size(); i++) {
                Log.d("BottomSheetFragment", "Index " + i + ": " + selectedOptions.get(i));
            }
            adapter = new QuestionNavigationAdapter(selectedOptions.size(), selectedOptions, quizQuestions, position -> ((WeeklyQuizInsiderActivity) requireActivity()).navigateToQuestion(position));
            gridView.setAdapter(adapter);
            updateCounts(selectedOptions, quizQuestions);
            return view;
        }

        private void updateCounts(ArrayList<String> selectedOptions, ArrayList<QuizPGinsider> quizQuestions) {
            int answeredCount = 0;
            int unansweredCount = 0;
            int notVisitedCount = 0;

            for (int i = 0; i < quizQuestions.size(); i++) {
                if (selectedOptions.get(i) == null) {
                    notVisitedCount++;
                } else if ("Skip".equals(selectedOptions.get(i))) {
                    unansweredCount++;
                } else {
                    answeredCount++;
                }
            }

            answeredCountTextView.setText(String.valueOf(answeredCount));
            unansweredCountTextView.setText(String.valueOf(unansweredCount));
            notVisitedCountTextView.setText(String.valueOf(notVisitedCount));
        }

        public void updateGrid(ArrayList<String> selectedOptionsList) {
            Log.d("BottomSheetFragment", "Updating grid with options list size 2: " + selectedOptionsList.size());
            if (adapter != null) {
                adapter.setSelectedOptions(selectedOptionsList);
                adapter.notifyDataSetChanged();
                updateCounts(selectedOptionsList, adapter.getQuizQuestions());
            }
        }
    }


    public static class QuestionNavigationAdapter extends BaseAdapter {
        private final int itemCount;
        private List<String> selectedOptions;
        private final OnItemClickListener listener;
        private final List<QuizPGinsider> quizQuestions;

        public QuestionNavigationAdapter(int itemCount, List<String> selectedOptions, List<QuizPGinsider> quizQuestions, OnItemClickListener listener) {
            this.itemCount = itemCount;
            this.selectedOptions = selectedOptions;
            this.listener = listener;
            this.quizQuestions = quizQuestions;
        }

        @Override
        public int getCount() {
            return itemCount;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item_layout, parent, false);
                holder = new ViewHolder();
                holder.textView = convertView.findViewById(R.id.grid_item_text);
                holder.layout = convertView.findViewById(R.id.grid_item_layout);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.textView.setText(String.valueOf(position + 1));
            QuizPGinsider quizQuestion = quizQuestions.get(position);
            if (quizQuestion.isMarkedForReview()) {
                holder.layout.setBackgroundResource(R.drawable.notvisited_bk); // Replace with your drawable resource
            } else if (selectedOptions.get(position) != null && selectedOptions.get(position).compareTo("Skip") != 0) {
                holder.layout.setBackgroundResource(R.drawable.backgroundofanswered); // Replace with your drawable resource
            } else {
                holder.layout.setBackgroundResource(R.drawable.backgroundoftheunselected); // Replace with your drawable resource
            }
            convertView.setOnClickListener(v -> listener.onItemClick(position));
            return convertView;
        }

        public void setSelectedOptions(List<String> selectedOptions) {
            this.selectedOptions = selectedOptions;
        }

        public ArrayList<QuizPGinsider> getQuizQuestions() {
            return (ArrayList<QuizPGinsider>) quizQuestions;
        }

        static class ViewHolder {
            TextView textView;
            LinearLayout layout;
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }
    }

}
