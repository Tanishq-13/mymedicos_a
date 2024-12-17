package com.medical.my_medicos.activities.neetss.animations;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.PgprepActivity;

public class CorrectAnswerSSActivity extends AppCompatActivity {

    LottieAnimationView congratsanim, done, continuetopgbtn;
    TextView correctOptionTextView;
    TextView descriptionTextView;
    LinearLayout complete;

    @SuppressLint({"WrongViewCast", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_correct_answer);

        congratsanim = findViewById(R.id.correctanswer);
        done = findViewById(R.id.doneanime);
        continuetopgbtn = findViewById(R.id.continueBtn);

        correctOptionTextView = findViewById(R.id.correctforperdayquestion);
        descriptionTextView = findViewById(R.id.descriptionforperdayquestion);

        Intent intent = getIntent();
        String correctOption = intent.getStringExtra("correctOption");
        String description = intent.getStringExtra("description");

        correctOptionTextView.setText(correctOption + " is the Correct Option" );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            descriptionTextView.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_LEGACY));
        } else {
            descriptionTextView.setText(Html.fromHtml(description));
        }

        complete = findViewById(R.id.complete);
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CorrectAnswerSSActivity.this, PgprepActivity.class);
                // Add the FLAG_ACTIVITY_CLEAR_TOP flag
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                // Finish the current activity
                finish();
            }
        });

        configureWindow();
    }

    private void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }
}
