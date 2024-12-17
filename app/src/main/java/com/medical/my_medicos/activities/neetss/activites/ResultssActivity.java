package com.medical.my_medicos.activities.neetss.activites;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

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
import com.medical.my_medicos.activities.neetss.adapters.ResultReportSSAdapter;
import com.medical.my_medicos.activities.neetss.model.QuizSSinsider;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultssActivity extends AppCompatActivity {

    private RecyclerView resultRecyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private ResultReportSSAdapter resultAdapter;
    private TextView gotopghome;
    private TextView correctAnswersTextView;
    private TextView totalQuestionsTextView;
    private TextView result;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        gotopghome = findViewById(R.id.gotopghome);
        gotopghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(ResultssActivity.this, SsprepActivity.class);
                startActivity(i);
            }
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        resultRecyclerView = findViewById(R.id.resultRecyclerView);
        correctAnswersTextView = findViewById(R.id.correctanswercounter);
        totalQuestionsTextView = findViewById(R.id.totalanswwercounter);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        resultRecyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        result=findViewById(R.id.result_score1);

        Intent intent = getIntent();
        ArrayList<QuizSSinsider> questions = (ArrayList<QuizSSinsider>) intent.getSerializableExtra("questions");
        String id = intent.getStringExtra("id");
        String skipped = intent.getStringExtra("skippedQuestions");
        String remainingTime = intent.getStringExtra("remainingTime");

        resultAdapter = new ResultReportSSAdapter(this, questions);
        resultRecyclerView.setAdapter(resultAdapter);

        int correctAnswers = calculateCorrectAnswers(questions);
        int totalQuestions = questions.size();
        Log.d("Correct Answer", String.valueOf(correctAnswers));
        Log.d("Correct Answer", String.valueOf(totalQuestions));

        int score = calculateScore(questions);
        result.setText(String.valueOf(score+skipped));

        correctAnswersTextView.setText("" + correctAnswers);
        totalQuestionsTextView.setText("" + totalQuestions);
        uploadResultsToFirestore(correctAnswers, totalQuestions, remainingTime,id);

        double percentage = ((double) score / (totalQuestions * 4)) * 100;

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

    private int calculateScore(ArrayList<QuizSSinsider> questions) {
        int score = 0;

        for (QuizSSinsider question : questions) {
            if (question.isCorrect()) {
                score += 4;
            } else {
                score -= 1;
            }
        }

        return Math.max(0, score);
    }

//    @Override
//    public void onBackPressed(){
////        Toast.makeText(ResultssActivity.this, "", Toast.LENGTH_SHORT).show();
//    }


    private int calculateCorrectAnswers(ArrayList<QuizSSinsider> questions) {
        int correctAnswers = 0;

        for (QuizSSinsider question : questions) {
            if (question.isCorrect()) {
                correctAnswers++;
            }
        }
        return correctAnswers;
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
