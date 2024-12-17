package com.medical.my_medicos.activities.fmge.activites;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.adapters.ResultReportFAdapter;
import com.medical.my_medicos.activities.fmge.model.QuizFmgeinsider;
import com.medical.my_medicos.activities.pg.adapters.ResultReportAdapter;
import com.medical.my_medicos.activities.pg.model.Neetpg;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultFActivity extends AppCompatActivity {

    private RecyclerView resultRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ResultReportFAdapter resultAdapter;
    private TextView gotopghome;
    private TextView correctAnswersTextView,incorrectAnswersTextView,answeredtv;
    private TextView totalQuestionsTextView;
    private TextView result;
    private ImageView bckbtn;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        gotopghome = findViewById(R.id.gotopghome);
        gotopghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResultFActivity.this, FmgeprepActivity.class);
                startActivity(i);
            }
        });

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
        ArrayList<QuizFmgeinsider> questions = (ArrayList<QuizFmgeinsider>) intent.getSerializableExtra("questions");
        String id = intent.getStringExtra("id");
        String title=intent.getStringExtra("quizname");
        Log.d("titres",title);
        TextView qn=findViewById(R.id.quizname);
        qn.setText(title);
        String skipped = intent.getStringExtra("skippedQuestions");
        String remainingTime = intent.getStringExtra("remainingTime");

        resultAdapter = new ResultReportFAdapter(this, questions);
        resultRecyclerView.setAdapter(resultAdapter);

        int correctAnswers = calculateCorrectAnswers(questions);
        int incorrectAnswers = calculateincorrectAnswers(questions);
        int answered=correctAnswers+incorrectAnswers;
        answeredtv.setText(String.valueOf(answered));
        int totalQuestions = questions.size();
        Log.d("Correct Answer", String.valueOf(correctAnswers));
        Log.d("Correct Answer", String.valueOf(totalQuestions));

        int score = calculateScore(questions);
        Log.d("scoreeff" ,String.valueOf(questions.size()));
        result.setText(String.valueOf(score));
        ProgressBar progressBar = findViewById(R.id.circularProgressBar);
        Log.d("f;je",String.valueOf((double)(score/(questions.size()*4))));
        Log.d("f;je",String.valueOf(((double)(score/(questions.size()*4)))*100));
        TextView tvScore = findViewById(R.id.tv_score);
        tvScore.setText(String.valueOf(score));
        correctAnswersTextView.setText("" + correctAnswers);
        incorrectAnswersTextView.setText("" + incorrectAnswers);

        totalQuestionsTextView.setText("" + totalQuestions);
//        uploadResultsToFirestore(correctAnswers, totalQuestions, remainingTime,id);

        double percentage = ((double) score / (totalQuestions * 4)) * 100;
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

    private int calculateScore(ArrayList<QuizFmgeinsider> questions) {
        int score = 0;

        for (QuizFmgeinsider question : questions) {
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


    private int calculateCorrectAnswers(ArrayList<QuizFmgeinsider> questions) {
        int correctAnswers = 0;

        for (QuizFmgeinsider question : questions) {
            if (question.isCorrect()) {
                correctAnswers++;
            }
        }
        return correctAnswers;
    }
    private int calculateincorrectAnswers(ArrayList<QuizFmgeinsider> questions) {
        int incorrectAnswers = 0;

        for (QuizFmgeinsider question : questions) {

            if (question.getSelectedOption()!=null && !question.getSelectedOption().equals(question.getCorrectAnswer()) ) {
                Log.d("cinc",String.valueOf(question.getNumber()));
                incorrectAnswers++;
            }
        }
        return incorrectAnswers;
    }
    private void uploadResultsToFirestore(int correctAnswers, int totalQuestions, String remainingTime, String id) {
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
            resultData.put("remainingTime", remainingTime);
            idSubcollectionRef
                    .document(id)
                    .set(resultData)
                    .addOnSuccessListener(aVoid -> Log.d("Result Upload", "Results uploaded successfully"))
                    .addOnFailureListener(e -> Log.e("Result Upload", "Error uploading results", e));
        }
    }
}
