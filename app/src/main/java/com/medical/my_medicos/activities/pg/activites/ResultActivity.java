package com.medical.my_medicos.activities.pg.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.FmgeprepActivity;
import com.medical.my_medicos.activities.pg.adapters.ResultReportAdapter;
import com.medical.my_medicos.activities.pg.model.Neetpg;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView resultRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String quizid;
    private String skippedques;
    private ResultReportAdapter resultAdapter;
    private TextView gotopghome;
    private TextView qn;
    private TextView correctAnswersTextView,incorrectAnswersTextView,answeredtv;
    private TextView totalQuestionsTextView;
    private TextView result;
    private ImageView bckbtn;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        qn=findViewById(R.id.quizname);

        gotopghome = findViewById(R.id.gotopghome);


        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        bckbtn=findViewById(R.id.backbtnfromresult);
        resultRecyclerView = findViewById(R.id.resultRecyclerView);
        correctAnswersTextView = findViewById(R.id.correctanswercounter);
        totalQuestionsTextView = findViewById(R.id.totalanswwercounter);
        incorrectAnswersTextView = findViewById(R.id.incorrectanswercounter);
        answeredtv=findViewById(R.id.answeredc);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        result=findViewById(R.id.result_score1);
        // Set back button click listener
        bckbtn.setOnClickListener(v -> {
            // Mimic the back button behavior
            onBackPressed();
        });
        Intent intent = getIntent();
        ArrayList<QuizPGinsider> questions = (ArrayList<QuizPGinsider>) intent.getSerializableExtra("questions");
        String id = intent.getStringExtra("id");
        String quizname=intent.getStringExtra("quizname");
        qn.setText(quizname);
        String speciality=intent.getStringExtra("speciality");
        String skipped = intent.getStringExtra("skippedQuestions");
        String remainingTime = intent.getStringExtra("remainingTime");
        skippedques=intent.getStringExtra("skippedQuestions");
        quizid=intent.getStringExtra("quizid");
        gotopghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(speciality.equals("FMGE")){
                    Intent i = new Intent(ResultActivity.this, FmgeprepActivity.class);
                    startActivity(i);
                }
                else {
                    Intent i = new Intent(ResultActivity.this, PgprepActivity.class);
                    startActivity(i);
                }
            }
        });
        resultAdapter = new ResultReportAdapter(this, questions);
        resultRecyclerView.setAdapter(resultAdapter);

        int correctAnswers = calculateCorrectAnswers(questions);
        int incorrectAnswers = calculateincorrectAnswers(questions);
        int answered=correctAnswers+incorrectAnswers;
        answeredtv.setText(String.valueOf(answered));
        int totalQuestions = questions.size();
        Log.d("Correct Answer", String.valueOf(correctAnswers));
        Log.d("Correct Answer", String.valueOf(totalQuestions));

        int score = calculateScore(questions);
        Log.d("scoree" ,String.valueOf(score));
        result.setText(String.valueOf(score));
        ProgressBar progressBar = findViewById(R.id.circularProgressBar);
//        Log.d("f;je",String.valueOf((double)(score/(questions.size()*4))));
//        Log.d("f;je",String.valueOf(((double)(score/(questions.size()*4)))*100));
        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText(String.valueOf(score));
        correctAnswersTextView.setText("" + correctAnswers);
        incorrectAnswersTextView.setText("" + incorrectAnswers);

        totalQuestionsTextView.setText("" + totalQuestions);
//        uploadResultsToFirestore(correctAnswers, totalQuestions, remainingTime,id,incorrectAnswers);

        double percentage = ((double) score / (totalQuestions * 4.0)) * 100.0;
        progressBar.setProgress((int) percentage);  // Set this to your score percentage
        String greetingText;
        if (percentage < 50) {
            greetingText = "Don't worry, Keep Going";
        } else if (percentage <= 60) {
            greetingText = "Keep Practicing";
        } else if (percentage <= 75) {
            greetingText = "Keep it up";
        } else if (percentage <= 85) {
            greetingText = "Good";
        } else if (percentage <= 90) {
            greetingText = "Very Good";
        } else {
            greetingText = "Excellent";
        }

        TextView greetingTextView = findViewById(R.id.greeting);
        greetingTextView.setText(greetingText);

        configureWindow();
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
        quizResult.put("Total Questions", questions.size());
        quizResult.put("Unanswered", questions.size()-answered);
        quizResult.put("Correct Answers", correctAnswers);  // Update with actual value
        quizResult.put("Wrong Answers", incorrectAnswers);  // Update with actual value
        quizResult.put("Total Marks Obtained", correctAnswers*4);  // Update with actual value
        quizResult.put("Marked for Review", 0);  // Update with actual value
        quizResult.put("Success Rate (%)", 0);  // Update with actual value
        quizResult.put("score", score);  // Update with actual value
        quizResult.put("Comment", greetingText);  // Custom comment
        quizResult.put("Time of Submission", FieldValue.serverTimestamp());
        quizResult.put("section","pgneet");
        quizResult.put("submitted",true);

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
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("QuizResult", "Error saving quiz result", e);
                    }
                });
    }

    private void configureWindow() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private int calculateScore(ArrayList<QuizPGinsider> questions) {
        int score = 0;

        for (QuizPGinsider question : questions) {
            if (question.isCorrect()) {
                score += 4;
            } else if(question.getSelectedOption()!=null) {
                score -= 1;
            }
        }

        return Math.max(0, score);
    }

//    @Override
//    public void onBackPressed(){
////        Toast.makeText(ResultssActivity.this, "", Toast.LENGTH_SHORT).show();
//    }


    private int calculateCorrectAnswers(ArrayList<QuizPGinsider> questions) {
        int correctAnswers = 0;

        for (QuizPGinsider question : questions) {
            if (question.isCorrect()) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }
    private int calculateincorrectAnswers(ArrayList<QuizPGinsider> questions) {
        int incorrectAnswers = 0;

        for (QuizPGinsider question : questions) {

            if (question.getSelectedOption()!=null && !question.getSelectedOption().equals(question.getCorrectAnswer()) ) {
                Log.d("cinc",String.valueOf(question.getNumber()));
                incorrectAnswers++;
            }
        }
        return incorrectAnswers;
    }
    private void uploadResultsToFirestore(int correctAnswers, int totalQuestions, String remainingTime, String id,int incans) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getPhoneNumber();
            DocumentReference userDocumentRef = db.collection("QuizResultsPGPrep").document(userId);
            CollectionReference idSubcollectionRef = userDocumentRef.collection("Weekley");
            if (id!=null) {
                Log.d("collection id ", id);
            }
            Map<String, Object> resultData = new HashMap<>();
            resultData.put("correctAnswers", correctAnswers);
            resultData.put("totalQuestions", totalQuestions);
            resultData.put("skippedanswers", skippedques);
            resultData.put("incorrectanswers",incans);
            resultData.put("quizid",quizid);
            idSubcollectionRef
                    .document(id)
                    .set(resultData)
                    .addOnSuccessListener(aVoid -> Log.d("Result Upload", "Results uploaded successfully"))
                    .addOnFailureListener(e -> Log.e("Result Upload", "Error uploading results", e));
        }
    }
}
