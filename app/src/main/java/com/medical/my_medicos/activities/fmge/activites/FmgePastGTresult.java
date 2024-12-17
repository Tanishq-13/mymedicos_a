package com.medical.my_medicos.activities.fmge.activites;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.navigation.ui.AppBarConfiguration;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.PgprepActivity;
import com.medical.my_medicos.activities.pg.adapters.ResultReportNeetAdapter;
import com.medical.my_medicos.databinding.ActivityPgpastGtresultBinding;

import java.util.ArrayList;

public class FmgePastGTresult extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPgpastGtresultBinding binding;

    private ResultReportNeetAdapter resultAdapter;
    private TextView correctAnswersTextView;
    private TextView totalQuestionsTextView;
    private TextView remainingTimeTextView;
    private TextView gotopghome;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPgpastGtresultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        PieChart pieChart = findViewById(R.id.pieChart);

        NestedScrollView nestedScrollView = findViewById(R.id.nestedofresult);
        nestedScrollView.smoothScrollTo(0, nestedScrollView.getBottom());

        gotopghome = findViewById(R.id.gotopghome);
        gotopghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FmgePastGTresult.this, PgprepActivity.class);
                startActivity(i);
            }
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);

        correctAnswersTextView = findViewById(R.id.correctanswercounter);
        totalQuestionsTextView = findViewById(R.id.totalanswwercounter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();

        String id = intent.getStringExtra("qid");

        fetchResultsFromFirestore(id);

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

    private void fetchResultsFromFirestore(String id) {
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getPhoneNumber();
            DocumentReference userDocumentRef = db.collection("QuizResults").document(userId);
            CollectionReference idSubcollectionRef = userDocumentRef.collection("Exam");

            idSubcollectionRef.document(id).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        int correctAnswers = task.getResult().getLong("correctAnswers").intValue();
                        int totalQuestions = task.getResult().getLong("totalQuestions").intValue();
                        String remainingTime = task.getResult().getString("remainingTime");

                        correctAnswersTextView.setText("" + correctAnswers);
                        totalQuestionsTextView.setText("" + totalQuestions);

                        int score = calculateScore(correctAnswers, totalQuestions);
                        TextView resultScoreTextView = findViewById(R.id.result_score);
                        resultScoreTextView.setText(String.valueOf(score));

                        double percentage = ((double) score / (totalQuestions * 4)) * 100;
                        setGreetingMessage(percentage);

                        populatePieChart(correctAnswers, totalQuestions);

                    } else {
                        Toast.makeText(FmgePastGTresult.this, "No data found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("Firestore Fetch", "Error fetching results", task.getException());
                }
            });
        }
    }

    private int calculateScore(int correctAnswers, int totalQuestions) {
        int score = correctAnswers * 4;
        return Math.max(0, score);
    }

    private void setGreetingMessage(double percentage) {
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
    }

    private void populatePieChart(int correctAnswers, int totalQuestions) {
        PieChart pieChart = findViewById(R.id.pieChart);
        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(correctAnswers));
        entries.add(new PieEntry(totalQuestions - correctAnswers));

        PieDataSet dataSet = new PieDataSet(entries, "Quiz Results");

        int[] colors = new int[]{
                ContextCompat.getColor(this, R.color.teal_700),
                ContextCompat.getColor(this, R.color.red)
        };
        dataSet.setColors(colors);

        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.WHITE);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText("Score");
        pieChart.setCenterTextSize(16f);
        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }
}