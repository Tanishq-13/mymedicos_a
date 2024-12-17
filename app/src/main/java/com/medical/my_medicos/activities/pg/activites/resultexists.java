package com.medical.my_medicos.activities.pg.activites;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.FmgeprepActivity;
import com.medical.my_medicos.activities.pg.adapters.ResultReportAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPGinsider;

import java.util.ArrayList;
import java.util.Objects;

public class resultexists extends AppCompatActivity {
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private String quizid;
    private String skippedques;
    private ProgressBar pie;
    private ResultReportAdapter resultAdapter;
    private TextView gotopghome,unanswered,quizidd;
    private TextView correctAnswersTextView,incorrectAnswersTextView,answeredtv,cmntt;
    private TextView totalQuestionsTextView;
    private TextView result,scoree;
    private ImageView bckbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_resultexists);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        gotopghome = findViewById(R.id.gotopghome);
        pie=findViewById(R.id.circularProgressBar);
        Intent intent = getIntent();

        String section=intent.getStringExtra("section");

        gotopghome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i;
                if(Objects.equals(section, "FMGE")){
                    i = new Intent(resultexists.this, FmgeprepActivity.class);
                } else {
                    i = new Intent(resultexists.this, PgprepActivity.class);
                }

                // Add this flag to clear the back stack
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                finish();  // This will finish the current activity to prevent it from staying in the stack
            }
        });

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        decorView.setSystemUiVisibility(uiOptions);
        unanswered=findViewById(R.id.totqc);
        quizidd=findViewById(R.id.quizidc);
        bckbtn=findViewById(R.id.backbtnfromresult);
        scoree=findViewById(R.id.tv_score);
        correctAnswersTextView = findViewById(R.id.correctanswercounter);
        totalQuestionsTextView = findViewById(R.id.totalanswwercounter);
        incorrectAnswersTextView = findViewById(R.id.incorrectanswercounter);
        cmntt=findViewById(R.id.greeting);
        answeredtv=findViewById(R.id.answeredc);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        resultRecyclerView.setLayoutManager(layoutManager);
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        result=findViewById(R.id.result_score1);
        // Set back button click listener
        bckbtn.setOnClickListener(v -> {
            // Mimic the back button behavior
            onBackPressed();
        });


        String skipped = intent.getStringExtra("skippedques");
        String cmnt=intent.getStringExtra("cmnt");
        String crans= intent.getStringExtra("crans");
        String wrans= intent.getStringExtra("wrans");
        String score= intent.getStringExtra("score");
        String unans= intent.getStringExtra("unans");
        String mrks= intent.getStringExtra("mrks");

        quizid=intent.getStringExtra("quizid");
        correctAnswersTextView.setText(crans);
        quizidd.setText(quizid);
        totalQuestionsTextView.setText(intent.getStringExtra("totq"));
        incorrectAnswersTextView.setText(wrans);
        unanswered.setText(unans);
        scoree.setText(score);
        result.setText(mrks);
        cmntt.setText(cmnt);
        answeredtv.setText(String.valueOf(Integer.valueOf(wrans)+Integer.valueOf(crans)));
        int marks=Integer.parseInt(score);
        int tot=Integer.parseInt(intent.getStringExtra("totq"));
        double percentage=(marks/((double)tot*4))*100;
        Log.d("prcb",String.valueOf(percentage));
        pie.setProgress((int) percentage);  // Set this to your score percentage



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
}