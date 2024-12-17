package com.medical.my_medicos.activities.pg.activites.insiders;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.FmgeprepActivity;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.pg.activites.PgprepActivity;
import com.medical.my_medicos.activities.pg.activites.ResultActivity;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapterinsider;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class WeeklyQuizInsiderActivity extends AppCompatActivity implements WeeklyQuizAdapterinsider.OnOptionInteractionListener {
    private RecyclerView recyclerView;
    private String str;
    private String text;
    private WeeklyQuizAdapterinsider adapter;
    private ArrayList<QuizPGinsider> quizList;
    private String sec;
    private ArrayList<QuizPGinsider> copy;
    private int currentQuestionIndex = 0;
    private CountDownTimer countDownTimer;
    private TextView currentquestion;
    private TextView timerTextView;
    private TextView description;
    private TextView descr_heading;
    private String quizid;
    private TextView nextbtn;
    private ImageView pause;
    int skippedQuestions = 0;
    private long timeLeftInMillis = 25 * 60 * 1000;
    private long remainingTimeInMillis;
    public ArrayList<String> selectedOptionsList = new ArrayList<>();
    public Map<String,String> selectedopt=new HashMap<>();
    private String id;
    private CheckBox markForReviewCheckBox;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekly_quiz_insider);
        TextView title1 = findViewById(R.id.setnamewillbehere);
//        String text= (String) title1.getText();

        currentquestion = findViewById(R.id.currentquestion1);
//        timerTextView = findViewById(R.id.timerTextView1);
        markForReviewCheckBox = findViewById(R.id.markForReviewCheckBox);

//        pause=findViewById(R.id.pauselyout);
        pause = findViewById(R.id.pauselyout);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog
                new AlertDialog.Builder(WeeklyQuizInsiderActivity.this)
                        .setTitle("Pause Test")
                        .setMessage("Are you sure you want to pause the test?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // If user confirms, save the quiz progress
                                saveQuizProgress();

                                // After saving, navigate to PreprationindexingActivity

                                Intent in =getIntent();
                                Intent intent;
                                if(in.getStringExtra("section").equals("FMGE")){
                                    intent=new Intent(WeeklyQuizInsiderActivity.this, FmgeprepActivity.class);
                                }
                                else{
                                    intent=new Intent(WeeklyQuizInsiderActivity.this, PgprepActivity.class);
                                }
// Clear the activity stack so the user can't go back to WeeklyQuizInsiderActivity
                                startActivity(intent);
                                finish(); // Finish current activity
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });


//        timerTextView = findViewById(R.id.timerTextView1);
        markForReviewCheckBox = findViewById(R.id.markForReviewCheckBox);
        nextbtn=findViewById(R.id.nextButton2);
        recyclerView = findViewById(R.id.recycler_view);
        Intent intent = getIntent();
        str = intent.getStringExtra("Title1"); // Specialty
        String str1 = intent.getStringExtra("Title"); // Title
        quizid=intent.getStringExtra("quizid");
        sec=intent.getStringExtra("section");
//        title1.setText(str1);
        text=str1;

        if (text.length() > 16) {
            title1.setText(text.substring(0, 16) + "..");
        } else {
            title1.setText(text);
        }
        quizList = new ArrayList<>();
        copy = new ArrayList<>();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        String userId=currentUser.getPhoneNumber();
        //startTimer();
        //Toast.makeText(this,str+" "+str1,Toast.LENGTH_SHORT).show();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");
        if(Objects.equals(sec, "FMGE")){
            Log.d("ngkf",quizid);
            quizzCollection = db.collection("Fmge").document("Weekley").collection("Quiz");

        }
        DocumentReference quizProgressRef = db.collection("QuizProgress").document(userId).collection("pgneet").document(quizid);


        quizzCollection.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                quizList.clear();
                copy.clear();

                // Fetch the quiz progress if paused
                quizProgressRef.get().addOnCompleteListener(progressTask -> {
                    if (progressTask.isSuccessful()) {
                        DocumentSnapshot quizProgressDoc = progressTask.getResult();
                        Map<String, Object> progressData = null;
                        if (quizProgressDoc.exists()) {
                            progressData = quizProgressDoc.getData(); // Fetch the saved progress data
                            Log.d("savedprogress",String.valueOf(progressData));
                        }

                        // Loop through all the quiz questions
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String speciality = document.getString("hyOption");
                            String title = document.getString("title");
                            Log.d("fmgecheck"," "+title+" "+str1);
                            if (str1.equals(title)) {
                                ArrayList<Map<String, Object>> dataList = (ArrayList<Map<String, Object>>) document.get("Data");
                                for (Map<String, Object> entry : dataList) {
                                    String question = (String) entry.get("Question");
                                    String correctAnswer = (String) entry.get("Correct");
                                    String optionA = (String) entry.get("A");
                                    String optionB = (String) entry.get("B");
                                    String optionC = (String) entry.get("C");
                                    String optionD = (String) entry.get("D");
                                    String description = (String) entry.get("Description");
                                    description= String.valueOf((Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT)));
                                    Log.d("htmltest",description);
                                    String imageUrl = (String) entry.get("Image");
                                    long number = (long) entry.get("number");
                                    id = document.getId();

                                    // Create a QuizPGinsider object
                                    QuizPGinsider quizQuestion;
                                    if (imageUrl != null && !imageUrl.isEmpty()) {
                                        quizQuestion = new QuizPGinsider(question, optionA, optionB, optionC, optionD, correctAnswer, imageUrl, description, number);
                                    } else {
                                        quizQuestion = new QuizPGinsider(question, optionA, optionB, optionC, optionD, correctAnswer, null, description, number);
                                    }

                                    // Initialize selected option for this question as null initially
                                    selectedOptionsList.add(null);

                                    // Check if progress exists for this question and update selectedOptionsList
                                    if (progressData != null && progressData.containsKey("progress")) {
                                        Log.d("pausetest2", String.valueOf( progressData.get("progress")));

                                        Map<String, String> savedAnswers = (Map<String, String>) progressData.get("progress");
                                        String savedAnswer = savedAnswers.get(String.valueOf(number));

                                        if (savedAnswer != null && savedAnswers.containsKey(String.valueOf(number))) {
                                            quizQuestion.setSelectedOption(savedAnswer); // Set the saved answer
                                            selectedOptionsList.set((int) number, savedAnswer); // Update selectedOptionsList with the saved answer
                                            Log.d("pausetest", savedAnswer);
                                        }
                                    }

                                    copy.add(quizQuestion);
                                    quizList.add(quizQuestion);
                                }
                            }
                        }


                        // Load the first question
                        loadNextQuestion1(copy);
                        updateQuestionNumber();
                    } else {
                        Log.e("FirestoreError", "Error fetching quiz progress: ", progressTask.getException());
                    }
                });
            } else {
                Log.e("FirestoreError", "Error getting quiz documents: ", task.getException());
            }
        });
        markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            QuizPGinsider currentQuestion = copy.get(currentQuestionIndex);
            currentQuestion.setMarkedForReview(isChecked);
            refreshNavigationGrid();
        });

        ImageView gotoQuestionButton = findViewById(R.id.Navigate);
        gotoQuestionButton.setOnClickListener(v -> {
            WeeklyQuizInsiderActivity.QuestionBottomSheetDialogFragment bottomSheetDialogFragment = WeeklyQuizInsiderActivity.QuestionBottomSheetDialogFragment.newInstance(selectedOptionsList, copy);
            bottomSheetDialogFragment.show(getSupportFragmentManager(), "QuestionNavigator");
        });

        TextView prevButton = findViewById(R.id.BackButton);
        prevButton.setOnClickListener(v -> loadPreviousQuestion());

        TextView nextButton = findViewById(R.id.nextButton2);
        nextButton.setOnClickListener(v -> loadNextQuestion(copy));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new WeeklyQuizAdapterinsider(this, quizList, this);
        Log.d("quizzllstsize",String.valueOf(quizList.size()));
        Log.d("BottomSheetFragmnt 10", "Weeklyquizinsider activity" + currentQuestionIndex);
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
    public void onBackPressed() {
        Intent intent = new Intent(this, HomeActivity.class);
        // Clear the activity stack and make HomeActivity the root
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Finish the current activity
    }




    @Override
    public void onOptionSelected(int questionIndex, String selectedOption) {
        Log.d("dekho ",questionIndex+" "+selectedOption);

        if (selectedOption == null || selectedOption.isEmpty()) {
            selectedOptionsList.set(questionIndex, "Skip");
        } else {
            selectedOptionsList.set(questionIndex, selectedOption);
            if(!selectedOption.equals("Skip")) {
                selectedopt.put(String.valueOf(questionIndex), selectedOption);
                Log.d("dekho2 ", selectedopt.get(String.valueOf(questionIndex)));
            }
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

            // Retrieve the selected option from selectedOptionsList
            String selectedOption = selectedOptionsList.get(currentQuestionIndex);
            adapter.setSelectedOption(selectedOption);

            adapter.setcurrentquestionindex(currentQuestionIndex);

            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            updateQuestionNumber();
            markForReviewCheckBox.setOnCheckedChangeListener(null);
            markForReviewCheckBox.setChecked(copy.get(currentQuestionIndex).isMarkedForReview());
            markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                QuizPGinsider currentQuestion = copy.get(currentQuestionIndex);
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
        Log.d("end wala",String.valueOf(currentQuestionIndex)+" "+String.valueOf(copy.size()));
        if(currentQuestionIndex==copy.size()-1){
            nextbtn.setText("End");
        }
    }
    private void loadNextQuestion1(ArrayList<QuizPGinsider> quizList) {
        int q1 = copy.size();
        int currentQuestionIndex = -1;
        currentQuestionIndex++;

        if (currentQuestionIndex < q1) {

            Log.d("abasjdnajs", "Current Question Index: " + currentQuestionIndex);
            Log.d("abasjdnajs", "Quiz List Size: " + quizList.size());
            for (int i = 0; i < q1; i++) {
                QuizPGinsider quizQuestion = copy.get(i);
                Log.d("QuizPGinsider", "Question " + (i + 1) + ": " + quizQuestion.getQuestion());
            }

            // Check if the selected option at the current index is null
            if (selectedOptionsList.get(currentQuestionIndex) == null) {
                onOptionSelected(currentQuestionIndex, "Skip");
            }

            adapter.setcurrentquestionindex(currentQuestionIndex);
            final QuizPGinsider currentQuestion = copy.get(currentQuestionIndex);
            adapter.setQuizQuestions(Collections.singletonList(currentQuestion));
            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            updateQuestionNumber();

            markForReviewCheckBox.setOnCheckedChangeListener(null);
            markForReviewCheckBox.setChecked(copy.get(currentQuestionIndex).isMarkedForReview());
            markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                currentQuestion.setMarkedForReview(isChecked);
                refreshNavigationGrid();
            });
        } else {
            Log.d("abasjdnajs", "Current Question Index: " + currentQuestionIndex);
            Log.d("abasjdnajs", "Quiz List Size: " + quizList.size());
            for (int i = 0; i < quizList.size(); i++) {
                QuizPGinsider quizQuestion = quizList.get(i);
                Log.d("QuizPGinsider", "Question " + (i + 1) + ": " + quizQuestion.getQuestion());
            }
            currentQuestionIndex--;
            showEndQuizConfirmation();
        }
    }
    private void loadNextQuestion(ArrayList<QuizPGinsider> quizList) {
        int q1 = copy.size();
        currentQuestionIndex++;

        if (currentQuestionIndex < q1) {

            Log.d("abasjdnajs", "Current Question Index: " + currentQuestionIndex);
            Log.d("abasjdnajs", "Quiz List Size: " + quizList.size());
            for (int i = 0; i < q1; i++) {
                QuizPGinsider quizQuestion = copy.get(i);
                Log.d("QuizPGinsider", "Question " + (i + 1) + ": " + quizQuestion.getQuestion());
            }

            // Check if the selected option at the current index is null
            if (selectedOptionsList.get(currentQuestionIndex) == null) {
                onOptionSelected(currentQuestionIndex, "Skip");
            }

            adapter.setcurrentquestionindex(currentQuestionIndex);
            final QuizPGinsider currentQuestion = copy.get(currentQuestionIndex);
            adapter.setQuizQuestions(Collections.singletonList(currentQuestion));
            recyclerView.smoothScrollToPosition(currentQuestionIndex);
            updateQuestionNumber();

            markForReviewCheckBox.setOnCheckedChangeListener(null);
            markForReviewCheckBox.setChecked(copy.get(currentQuestionIndex).isMarkedForReview());
            markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                currentQuestion.setMarkedForReview(isChecked);
                refreshNavigationGrid();
            });
        } else {
            Log.d("abasjdnajs", "Current Question Index: " + currentQuestionIndex);
            Log.d("abasjdnajs", "Quiz List Size: " + quizList.size());
            for (int i = 0; i < quizList.size(); i++) {
                QuizPGinsider quizQuestion = quizList.get(i);
                Log.d("QuizPGinsider", "Question " + (i + 1) + ": " + quizQuestion.getQuestion());
            }
            currentQuestionIndex--;
            showEndQuizConfirmation();
        }
    }

    private void loadPreviousQuestion() {
        if (!selectedOptionsList.isEmpty()) {
            if (currentQuestionIndex >= 1) {
                Log.d("prev v",String.valueOf(currentQuestionIndex));

                currentQuestionIndex=currentQuestionIndex-1;
                if(currentQuestionIndex<0){
                    currentQuestionIndex=0;
                }
                Log.d("prev",String.valueOf(currentQuestionIndex));
                adapter.setQuizQuestions(Collections.singletonList(copy.get(currentQuestionIndex)));
                adapter.setSelectedOption(selectedOptionsList.get(currentQuestionIndex));
                recyclerView.smoothScrollToPosition(currentQuestionIndex);
                adapter.setcurrentquestionindex(currentQuestionIndex);
                updateQuestionNumber();
                markForReviewCheckBox.setOnCheckedChangeListener(null);
                markForReviewCheckBox.setChecked(copy.get(currentQuestionIndex).isMarkedForReview());
                markForReviewCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    QuizPGinsider currentQuestion = copy.get(currentQuestionIndex);
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
        Intent intent = new Intent(WeeklyQuizInsiderActivity.this, PgprepActivity.class);
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

        for (QuizPGinsider quizQuestion : quizList) {
            String selectedOption = quizQuestion.getSelectedOption() != null ? quizQuestion.getSelectedOption() : "Skip";
            if ("Skip".equals(selectedOption)) {
                skippedQuestions++;
            }
            userSelectedOptions.add(selectedOption);
        }
        remainingTimeInMillis = timeLeftInMillis;
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();

        String userId = auth.getCurrentUser().getPhoneNumber();
        // Reference to the specific quiz document under pgneet collection for the user
        DocumentReference quizProgressRef = db.collection("QuizProgress")
                .document(userId)  // phonenumber document
                .collection("pgneet")
                .document(id); // Quiz ID as document ID
        Log.d("truewala",id);

        // Check if the document exists
        quizProgressRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // If the document exists, update the 'submitted' field to true
                quizProgressRef.update("submitted", true)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("QuizUpdate", "Quiz submission status updated successfully.");
                        })
                        .addOnFailureListener(e -> {
                            Log.e("QuizUpdate", "Error updating quiz submission status", e);
                        });
            } else {
                Log.d("QuizUpdate", "No such quiz found in progress for the user.");
            }
        }).addOnFailureListener(e -> {
            Log.e("QuizUpdate", "Error checking quiz document", e);
        });
//        countDownTimer.cancel();
        navigateToResultActivity(skippedQuestions);
    }

    private void navigateToResultActivity(int skippedQuestions) {
        Intent intent = new Intent(WeeklyQuizInsiderActivity.this, ResultActivity.class);
        intent.putExtra("quizname",text);
        Log.d("titres",text);
        intent.putExtra("speciality",str);
        intent.putExtra("questions", copy);
        intent.putExtra("remainingTime", remainingTimeInMillis);
        intent.putExtra("id", id);
        intent.putExtra("quizid",quizid);
        intent.putExtra("skippedQuestions", skippedQuestions); // Passing the number of skipped questions
        startActivity(intent);
        finish();
    }
    private void saveQuizResultToFirestore(int skippedQuestions) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.d("QuizResult", "User not logged in");
            return;
        }

        String phoneNumber = currentUser.getPhoneNumber();
        String quizId = quizid;  // Use the actual quizId
        String docId = quizId;   // The document ID will be the quizId

        // Create a map to store quiz result
        Map<String, Object> quizResult = new HashMap<>();
        quizResult.put("Quiz ID", quizId);
        quizResult.put("Total Questions", copy.size());
        quizResult.put("Unanswered", skippedQuestions);
        quizResult.put("Correct Answers", 0);  // Update with actual value
        quizResult.put("Wrong Answers", 0);  // Update with actual value
        quizResult.put("Total Marks Obtained", 38);  // Update with actual value
        quizResult.put("Marked for Review", 0);  // Update with actual value
        quizResult.put("Success Rate (%)", 0);  // Update with actual value
        quizResult.put("score", 38);  // Update with actual value
        quizResult.put("Comment", "Needs improvement in both speed and accuracy.");  // Custom comment
        quizResult.put("Time of Submission", FieldValue.serverTimestamp());

        // Get Firestore instance and set the data in the path 'QuizResults/phoneNumber/Weekley/quizId'
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("QuizResults")
                .document(phoneNumber)
                .collection("Weekley")
                .document(quizId)
                .set(quizResult)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("QuizResult", "Quiz result successfully saved!");
                        // Navigate to the ResultActivity
                        navigateToResultActivity(skippedQuestions);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("QuizResult", "Error saving quiz result", e);
                    }
                });
    }

    private void saveQuizProgress() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Log.d("SaveProgress", "User not logged in");
            return;
        }

        String phoneNumber = currentUser.getPhoneNumber();
        String quizId = quizid;  // Use the actual quizId
        String docId = quizId;  // The document ID will be the quizId

        // Create a map to store quiz progress
        Map<String, Object> quizProgress = new HashMap<>();
        quizProgress.put("docID", docId);  // Quiz document ID
        quizProgress.put("progress", selectedopt);  // ArrayList of selected options
        quizProgress.put("submitted", false);  // Mark as not submitted

        // Get Firestore instance and update data
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("QuizProgress")
                .document(phoneNumber)
                .collection("pgneet")
                .document(quizId)
                .set(quizProgress, SetOptions.merge())  // Merge the new data into the existing document
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("SaveProgress", "Quiz progress successfully saved/updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("SaveProgress", "Error saving/updating quiz progress", e);
                    }
                });
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

        public static QuestionBottomSheetDialogFragment newInstance(ArrayList<String> selectedOptionsList, ArrayList<QuizPGinsider> quizQuestions) {
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
            Log.d("BottomSheetFragment23", "Selected options list size: " + selectedOptions.size());
            ArrayList<QuizPGinsider> quizQuestions = (ArrayList<QuizPGinsider>) getArguments().getSerializable("quizQuestions");
            for (int i = 0; i < selectedOptions.size(); i++) {
                Log.d("BottomSheetFragment66", "Index " + i + ": " + selectedOptions.get(i));
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
                String selectedOption = selectedOptions.get(i);
                Log.d("SelectedOption", "Question " + i + ": " + selectedOption);
                if (selectedOption == null || selectedOption.isEmpty()) {
                    notVisitedCount++;
                } else if ("Skip".equals(selectedOption)) {
                    unansweredCount++;
                } else {
                    // Consider the question as answered if it has a valid selected option (not "Skip")
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
                holder.layout.setBackgroundResource(R.drawable.bgofans2); // Replace with your drawable resource
            } else {
                holder.layout.setBackgroundResource(R.drawable.backgroundoftheunselected); // Replace with your drawable resource
            }
            convertView.setOnClickListener(v -> {
                listener.onItemClick(position);

                    // Dismiss the bottom sheet after item is selected

            });
            return convertView;
        }

        public void setSelectedOptions(List<String> selectedOptions) {
            this.selectedOptions = selectedOptions;
            notifyDataSetChanged();
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
