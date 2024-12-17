package com.medical.my_medicos.activities.neetss.activites;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.medical.my_medicos.activities.neetss.adapters.neetssexampadapter;
import com.medical.my_medicos.activities.neetss.model.Neetss;
import com.medical.my_medicos.activities.pg.model.Neetpg;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Neetssexaminsider extends AppCompatActivity implements neetssexampadapter.OnOptionInteractionListener {

    private RecyclerView recyclerView;
    private neetssexampadapter adapter;
    private TextView currentquestion;
    private LinearLayout questionNumberLayout;
    private String id;
    private ArrayList<Neetss> quizList1;
    private TextView timerTextView;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis = 210 * 60 * 1000; // 210 minutes in milliseconds
    private long remainingTimeInMillis;
    private ArrayList<String> selectedOptionsList = new ArrayList<>();
    private CheckBox markForReviewCheckBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.neetexaminsideractivity);

        currentquestion = findViewById(R.id.currentquestion);
        recyclerView = findViewById(R.id.recycler_view1);
        quizList1 = new ArrayList<>();
        timerTextView = findViewById(R.id.timerTextView);
        markForReviewCheckBox = findViewById(R.id.markForReviewCheckBox1);
        TextView title = findViewById(R.id.setnamewillbehere);
        ImageView gotoQuestionButton = findViewById(R.id.Navigate);

        startTimer();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");

        quizzCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                quizList1.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String speciality = document.getString("speciality");
                    String titleText = document.getString("title");
                    Intent intent = getIntent();
                    String str = intent.getStringExtra("Title");

                    if (titleText.equals(str)) {
                        title.setText(str) ;
                        ArrayList<Map<String, Object>> dataList = (ArrayList<Map<String, Object>>) document.get("Data");
                        if (dataList != null) {
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
                                Neetss quizQuestion = new Neetss(question, optionA, optionB, optionC, optionD, correctAnswer, imageUrl, description);
                                quizList1.add(quizQuestion);
                                selectedOptionsList.add(null);
                            }
                        }
                    }
                }
                loadNextQuestion();
            }
        });

        gotoQuestionButton.setOnClickListener(v -> {
            QuestionBottomSheetDialogFragment bottomSheetDialogFragment = QuestionBottomSheetDialogFragment.newInstance(selectedOptionsList, quizList1);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "QuestionNavigator");
        });

        markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Neetss currentQuestion = quizList1.get(currentQuestionIndex);
            currentQuestion.setMarkedForReview(isChecked);
            refreshNavigationGrid();
        });

        findViewById(R.id.BackButtom).setOnClickListener(v -> loadPreviousQuestion());
        findViewById(R.id.nextButton1).setOnClickListener(v -> loadNextQuestion());

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new neetssexampadapter(this, quizList1, this);
        recyclerView.setAdapter(adapter);

        findViewById(R.id.totheback1).setOnClickListener(v -> showConfirmationDialog());

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

    private void loadNextQuestion() {
        if (currentQuestionIndex < quizList1.size()) {
            // Check if the selected option at the current index is null
            if (selectedOptionsList.get(currentQuestionIndex) == null) {
                onOptionSelected(currentQuestionIndex, "Skip");
            }

            adapter.setQuizQuestions(Collections.singletonList(quizList1.get(currentQuestionIndex)));
            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            updateQuestionNumber();
            adapter.setcurrentquestionindex(currentQuestionIndex);
            adapter.setSelectedOption(selectedOptionsList.get(currentQuestionIndex));
            markForReviewCheckBox.setOnCheckedChangeListener(null);
            if (currentQuestionIndex < quizList1.size()) {
                markForReviewCheckBox.setChecked(quizList1.get(currentQuestionIndex).isMarkedForReview());
                markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    Neetss currentQuestion = quizList1.get(currentQuestionIndex);
                    currentQuestion.setMarkedForReview(isChecked);
                    refreshNavigationGrid();
                });
                currentQuestionIndex++;
            }
        } else {
            showEndQuizConfirmation();
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
            ((QuestionBottomSheetDialogFragment) fragment).updateGrid(selectedOptionsList);
        }
    }

    private void navigateToQuestion(int questionNumber) {
        if (questionNumber >= 0 && questionNumber < quizList1.size()) {
            currentQuestionIndex = questionNumber;
            adapter.setQuizQuestions(Collections.singletonList(quizList1.get(currentQuestionIndex)));
            adapter.setSelectedOption(selectedOptionsList.get(currentQuestionIndex));
            adapter.setcurrentquestionindex(currentQuestionIndex);
            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            updateQuestionNumber();
            markForReviewCheckBox.setOnCheckedChangeListener(null);
            markForReviewCheckBox.setChecked(quizList1.get(currentQuestionIndex).isMarkedForReview());
            markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Neetss currentQuestion = quizList1.get(currentQuestionIndex);
                currentQuestion.setMarkedForReview(isChecked);
                refreshNavigationGrid();
            });
        } else {
            Toast.makeText(this, "Invalid question number", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            adapter.setQuizQuestions(Collections.singletonList(quizList1.get(currentQuestionIndex)));
            adapter.setSelectedOption(selectedOptionsList.get(currentQuestionIndex));
            adapter.setcurrentquestionindex(currentQuestionIndex);
            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            markForReviewCheckBox.setOnCheckedChangeListener(null);
            markForReviewCheckBox.setChecked(quizList1.get(currentQuestionIndex).isMarkedForReview());
            markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                Neetss currentQuestion = quizList1.get(currentQuestionIndex);
                currentQuestion.setMarkedForReview(isChecked);
                refreshNavigationGrid();
            });
            updateQuestionNumber();
        } else {
            Toast.makeText(this, "This is the first question", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateQuestionNumber() {
        int totalQuestions = quizList1.size();
        String questionNumberText = (currentQuestionIndex + 1) + " / " + totalQuestions;
        currentquestion.setText(questionNumberText);
    }

    private void showEndQuizConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("End Quiz")
                .setMessage("Are you sure you want to end the quiz?")
                .setPositiveButton("Confirm", (dialog, which) -> handleEndButtonClick())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("End Quiz")
                .setMessage("Are you sure you want to end the quiz?")
                .setPositiveButton("Confirm", (dialog, which) -> handleEndButtonClick())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void handleEndButtonClick() {
        ArrayList<String> userSelectedOptions = new ArrayList<>();
        int skippedQuestions = 0;
        for (Neetss quizQuestion : quizList1) {
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
        Intent intent = new Intent(Neetssexaminsider.this, ResultActivityNeetss.class);
        intent.putExtra("questions", quizList1);
        intent.putExtra("remainingTime", remainingTimeInMillis);
        intent.putExtra("id", id);
        intent.putExtra("skippedQuestions", skippedQuestions); // Passing the number of skipped questions
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
                handleQuizEnd();
            }
        }.start();
    }

    private void handleQuizEnd() {
        int skippedQuestions = 0;
        for (Neetss quizQuestion : quizList1) {
            String selectedOption = quizQuestion.getSelectedOption() != null ? quizQuestion.getSelectedOption() : "Skip";
            if ("Skip".equals(selectedOption)) {
                skippedQuestions++;
            }
        }
        remainingTimeInMillis = timeLeftInMillis;

        // Show alert dialog
        int finalSkippedQuestions = skippedQuestions;
        new AlertDialog.Builder(this)
                .setTitle("Time's Up!")
                .setMessage("Sorry, you've run out of time. The quiz will be ended.")
                .setPositiveButton("OK", (dialog, which) -> navigateToResultActivity(finalSkippedQuestions))
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

    // Inside Neetssexaminsider activity
    public static class QuestionBottomSheetDialogFragment extends BottomSheetDialogFragment {
        private GridView gridView;
        private QuestionNavigationAdapter adapter;
        private TextView answeredCount;
        private TextView unansweredCount;
        private TextView notVisitedCount;

        public static QuestionBottomSheetDialogFragment newInstance(ArrayList<String> selectedOptionsList, ArrayList<Neetss> quizQuestions) {
            QuestionBottomSheetDialogFragment fragment = new QuestionBottomSheetDialogFragment();
            Bundle args = new Bundle();
            args.putStringArrayList("selectedOptionsList", selectedOptionsList);
            args.putSerializable("quizQuestions", quizQuestions);
            fragment.setArguments(args);
            return fragment;
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.bottom_sheet_layout_neet, container, false);
            gridView = view.findViewById(R.id.grid_view);
            answeredCount = view.findViewById(R.id.answered_count);
            unansweredCount = view.findViewById(R.id.unanswered_count);
            notVisitedCount = view.findViewById(R.id.not_visited_count);

            ArrayList<String> selectedOptions = getArguments() != null ? getArguments().getStringArrayList("selectedOptionsList") : new ArrayList<>();
            ArrayList<Neetpg> quizQuestions = (ArrayList<Neetpg>) getArguments().getSerializable("quizQuestions");

            adapter = new QuestionNavigationAdapter(selectedOptions.size(), selectedOptions, quizQuestions, position -> ((Neetssexaminsider) requireActivity()).navigateToQuestion(position));
            gridView.setAdapter(adapter);

            updateQuestionCounts(selectedOptions);

            return view;
        }

        public void updateGrid(ArrayList<String> selectedOptionsList) {
            if (adapter != null) {
                adapter.setSelectedOptions(selectedOptionsList);
                adapter.notifyDataSetChanged();
                updateQuestionCounts(selectedOptionsList);
            }
        }

        private void updateQuestionCounts(ArrayList<String> selectedOptionsList) {
            int answered = 0;
            int unanswered = 0;
            int notVisited = 0;

            for (String option : selectedOptionsList) {
                if (option == null) {
                    notVisited++;
                } else if ("Skip".equals(option)) {
                    unanswered++;
                } else {
                    answered++;
                }
            }

            answeredCount.setText(String.valueOf(answered));
            unansweredCount.setText(String.valueOf(unanswered));
            notVisitedCount.setText(String.valueOf(notVisited));
        }
    }


    public static class QuestionNavigationAdapter extends BaseAdapter {
        private final int itemCount;
        private List<String> selectedOptions;
        private final OnItemClickListener listener;
        private final List<Neetpg> quizQuestions;

        public QuestionNavigationAdapter(int itemCount, List<String> selectedOptions, List<Neetpg> quizQuestions, OnItemClickListener listener) {
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
            Neetpg quizQuestion = quizQuestions.get(position);
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

        static class ViewHolder {
            TextView textView;
            LinearLayout layout;
        }

        public interface OnItemClickListener {
            void onItemClick(int position);
        }
    }

}
