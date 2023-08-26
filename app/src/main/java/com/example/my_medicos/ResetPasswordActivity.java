package com.example.my_medicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {


    Button backbtn,resetbtn;
    TextInputEditText resetemail;


    FirebaseAuth auth = FirebaseAuth.getInstance();


    private AlertDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password_avtivity);



        backbtn=findViewById(R.id.btn_back);
        backbtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {

                Intent i=new Intent(ResetPasswordActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });



        resetemail=findViewById(R.id.resetemail);
        resetbtn=findViewById(R.id.reset_button);

        resetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=resetemail.getText().toString();
                if(TextUtils.isEmpty(email)){
                        resetemail.setError("Email cant be Empty");
                }else{
                    showProgressDialog();
                    ResetPassword();
                }
            }
        });


        }


    public void ResetPassword(){
        auth.sendPasswordResetEmail(resetemail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "Password reset mail is sent to your Email", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }

    public void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.progress_dialog_layout, null);
        ProgressBar progressBar = dialogView.findViewById(R.id.progressBar);
        builder.setView(dialogView);
        builder.setCancelable(false); // Set this to true if you want to make the dialog cancelable
        progressDialog = builder.create();
        progressDialog.show();
    }
}