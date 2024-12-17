package com.medical.my_medicos.activities.fmge.activites.insiders;

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
import com.medical.my_medicos.activities.fmge.activites.FmgeprepActivity;
import com.medical.my_medicos.activities.fmge.activites.ResultFActivity;
import com.medical.my_medicos.activities.fmge.adapters.WeeklyFmgeQuizAdapterinsider;
import com.medical.my_medicos.activities.fmge.model.QuizFmgeinsider;
import com.medical.my_medicos.activities.pg.activites.ResultActivity;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapterinsider;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class WeeklyQuizFMGEInsiderActivity extends AppCompatActivity implements WeeklyFmgeQuizAdapterinsider.OnOptionInteractionListener {
    private RecyclerView recyclerView;
    private String title;
    private WeeklyFmgeQuizAdapterinsider adapter;
    private ArrayList<QuizFmgeinsider> quizList;
    private ArrayList<QuizFmgeinsider> copy;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private TextView currentquestion;
    private TextView timerTextView;
    int skippedQuestions = 0;
    private long timeLeftInMillis = 25 * 60 * 1000; // 25 minutes
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
        String text= (String) title1.getText();
        currentquestion = findViewById(R.id.currentquestion1);
//        timerTextView = findViewById(R.id.timerTextView1);
        markForReviewCheckBox = findViewById(R.id.markForReviewCheckBox);
        if (title1.length() > 8) {
            title1.setText(text.substring(0, 8) + "...");
        } else {
            title1.setText(text);
        }
        recyclerView = findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        String specialty = intent.getStringExtra("Title1");
        title = intent.getStringExtra("Title");
        title1.setText(title);
        quizList = new ArrayList<>();
        copy = new ArrayList<>();
//        startTimer();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        fetchQuizData(db, "Weekley", specialty, title, title1);

        markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (currentQuestionIndex < copy.size()) {
                QuizFmgeinsider currentQuestion = copy.get(currentQuestionIndex);
                currentQuestion.setMarkedForReview(isChecked);
                refreshNavigationGrid();
            }
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
        adapter = new WeeklyFmgeQuizAdapterinsider(this, quizList, this);
        recyclerView.setAdapter(adapter);

//        LinearLayout toTheBackLayout = findViewById(R.id.totheback);
//        toTheBackLayout.setOnClickListener(v -> showConfirmationDialog());

        configureWindow();
    }

    private void fetchQuizData(FirebaseFirestore db, String collectionPath, String specialty, String title, TextView titleView) {
        CollectionReference quizCollection = db.collection("Fmge").document(collectionPath).collection("Quiz");
        quizCollection.whereEqualTo("speciality", specialty).whereEqualTo("title", title).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (!task.getResult().isEmpty()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        processDocument(document, titleView);
                    }
                    loadNextQuestion(copy);
                    updateQuestionNumber();
                } else if ("Weekley".equals(collectionPath)) {
                    fetchQuizData(db, "CWT", specialty, title, titleView);  // Fallback to "CWT" collection
                } else {
                    Log.e("FirestoreError", "No documents found in both collections.");
                    Toast.makeText(WeeklyQuizFMGEInsiderActivity.this, "No quiz data available in both 'Weekley' and 'CWT' collections.", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.e("FirestoreError", "Error getting documents: ", task.getException());
            }
        });
    }

    private void processDocument(QueryDocumentSnapshot document, TextView titleView) {
        String title = document.getString("title");
        titleView.setText(title);
        ArrayList<Map<String, Object>> dataList = (ArrayList<Map<String, Object>>) document.get("Data");
        for (Map<String, Object> entry : dataList) {
            QuizFmgeinsider quizQuestion = new QuizFmgeinsider(
                    (String) entry.get("Question"),
                    (String) entry.get("A"),
                    (String) entry.get("B"),
                    (String) entry.get("C"),
                    (String) entry.get("D"),
                    (String) entry.get("Correct"),
                    (String) entry.get("Image"),
                    (String) entry.get("Description"),
                    (long)entry.get("number")


            );
            copy.add(quizQuestion);
            quizList.add(quizQuestion);
            selectedOptionsList.add(null); // Ensure selectedOptionsList is initialized
        }
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
                QuizFmgeinsider currentQuestion = copy.get(currentQuestionIndex);
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

    private void loadNextQuestion(ArrayList<QuizFmgeinsider> quizList) {
        int q1 = copy.size();
        if (currentQuestionIndex < q1) {
            Log.d("abasjdnajs", "Current Question Index: " + currentQuestionIndex);
            Log.d("abasjdnajs", "Quiz List Size: " + quizList.size());
            for (int i = 0; i < q1; i++) {
                QuizFmgeinsider quizQuestion = copy.get(i);
                Log.d("QuizPGinsider", "Question " + (i + 1) + ": " + quizQuestion.getQuestion());
            }

            // Check if the selected option at the current index is null
            if (selectedOptionsList.get(currentQuestionIndex) == null) {
                onOptionSelected(currentQuestionIndex, "Skip");
            }

            adapter.setcurrentquestionindex(currentQuestionIndex);
            final QuizFmgeinsider currentQuestion = copy.get(currentQuestionIndex);
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
                QuizFmgeinsider quizQuestion = quizList.get(i);
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
                    QuizFmgeinsider currentQuestion = copy.get(currentQuestionIndex);
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
        Intent intent = new Intent(WeeklyQuizFMGEInsiderActivity.this, FmgeprepActivity.class);
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
//        timerTextView.setText(timeFormatted);
    }

    private void handleEndButtonClick() {
        ArrayList<String> userSelectedOptions = new ArrayList<>();

        for (QuizFmgeinsider quizQuestion : quizList) {
            String selectedOption = quizQuestion.getSelectedOption() != null ? quizQuestion.getSelectedOption() : "Skip";
            if ("Skip".equals(selectedOption)) {
                skippedQuestions++;
            }
            userSelectedOptions.add(selectedOption);
        }
        remainingTimeInMillis = timeLeftInMillis;
//        countDownTimer.cancel();
        navigateToResultActivity(skippedQuestions);
    }

    private void navigateToResultActivity(int skippedQuestions) {
        Intent intent = new Intent(WeeklyQuizFMGEInsiderActivity.this, ResultFActivity.class);
        intent.putExtra("quizname",title);
        Log.d("titres",title);
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

        public static QuestionBottomSheetDialogFragment newInstance(ArrayList<String> selectedOptionsList, ArrayList<QuizFmgeinsider> quizQuestions) {
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
            ArrayList<QuizFmgeinsider> quizQuestions = (ArrayList<QuizFmgeinsider>) getArguments().getSerializable("quizQuestions");
            for (int i = 0; i < selectedOptions.size(); i++) {
                Log.d("BottomSheetFragment", "Index " + i + ": " + selectedOptions.get(i));
            }
            adapter = new QuestionNavigationAdapter(selectedOptions.size(), selectedOptions, quizQuestions, position -> ((WeeklyQuizFMGEInsiderActivity) requireActivity()).navigateToQuestion(position));
            gridView.setAdapter(adapter);
            updateCounts(selectedOptions, quizQuestions);
            return view;
        }

        private void updateCounts(ArrayList<String> selectedOptions, ArrayList<QuizFmgeinsider> quizQuestions) {
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
        private final List<QuizFmgeinsider> quizQuestions;

        public QuestionNavigationAdapter(int itemCount, List<String> selectedOptions, List<QuizFmgeinsider> quizQuestions, OnItemClickListener listener) {
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
            QuizFmgeinsider quizQuestion = quizQuestions.get(position);
            if (quizQuestion.isMarkedForReview()) {
                holder.layout.setBackgroundResource(R.drawable.notvisited_bk); // Replace with your drawable resource
            } else if (selectedOptions.get(position) != null && selectedOptions.get(position).compareTo("Skip") != 0) {
                holder.layout.setBackgroundResource(R.drawable.bgofans2); // Replace with your drawable resource
                // Replace with your drawable resource
            } else {
                holder.layout.setBackgroundResource(R.drawable.backgroundoftheunselected); // Replace with your drawable resource
            }
            convertView.setOnClickListener(v -> listener.onItemClick(position));
            return convertView;
        }

        public void setSelectedOptions(List<String> selectedOptions) {
            this.selectedOptions = selectedOptions;
        }

        public ArrayList<QuizFmgeinsider> getQuizQuestions() {
            return (ArrayList<QuizFmgeinsider>) quizQuestions;
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
