package com.medical.my_medicos.activities.home.sidedrawer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.login.bottom_controls.PrivacyPolicyActivity;
import com.medical.my_medicos.activities.login.bottom_controls.TermsandConditionsActivity;

public class SettingsActivity extends AppCompatActivity {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        toolbar = findViewById(R.id.settingsstoolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LinearLayout termsbtn = findViewById(R.id.termsandconditions);
        termsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, TermsandConditionsActivity.class);
                startActivity(i);
            }
        });
        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        LinearLayout privacypage = findViewById(R.id.privacypage);
        privacypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsActivity.this, PrivacyPolicyActivity.class);
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}