package com.example.my_medicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    TextView createacc,forgotpassword;

    Button login;

    TextInputEditText email,password;

    private FirebaseAuth mauth;

    private ProgressDialog mdialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login=findViewById(R.id.lgn_btn);
        email=findViewById(R.id.emailedit);
        password=findViewById(R.id.passedit);
        createacc=findViewById(R.id.createacc);

        createacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
        mauth=FirebaseAuth.getInstance();

        mdialog=new ProgressDialog(this);

        login();
    }
    private void login(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail=email.getText().toString().trim();
                String pass=password.getText().toString().trim();

                if(TextUtils.isEmpty(mail)){
                    email.setError("Email Required");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    password.setError("Password Required");
                    return;
                }
                mdialog.setMessage("Logging in");
                mdialog.show();

                mauth.signInWithEmailAndPassword(mail,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            if(mauth.getCurrentUser().isEmailVerified()){
                                Toast.makeText(getApplicationContext(), "Login Successfull", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(i);
                                finish();

                                mdialog.dismiss();
                            }else{
                                Toast.makeText(getApplicationContext(), "Please Verify ur email", Toast.LENGTH_SHORT).show();
                                mdialog.dismiss();
                            }

                        } else {
                            Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            mdialog.dismiss();
                        }
                    }
                });

            }
        });

        forgotpassword=findViewById(R.id.forgot_pass);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(MainActivity.this, ResetPasswordActivity.class);
                startActivity(i);
            }
        });
    }
}