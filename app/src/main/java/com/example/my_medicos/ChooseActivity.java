package com.example.my_medicos;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;



public class ChooseActivity extends AppCompatActivity {

    ImageView doc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        doc=findViewById(R.id.circularImageView);
        doc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ChooseActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }
}