package com.example.my_medicos;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class GetstartedActivity extends AppCompatActivity {

    Button gsbtn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);

        gsbtn1=findViewById(R.id.gsbtn2);

        gsbtn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i=new Intent(GetstartedActivity.this, ChooseActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}