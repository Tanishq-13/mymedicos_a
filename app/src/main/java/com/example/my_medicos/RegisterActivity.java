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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.security.spec.ECFieldF2m;
import java.util.HashMap;


public class RegisterActivity extends AppCompatActivity {

    TextView loginreg;

    Button register;

    private FirebaseAuth mauth;

    private ProgressDialog mdialog;
    public FirebaseDatabase db= FirebaseDatabase.getInstance();

    public DatabaseReference jobref=db.getReference().child("Users");

    TextInputEditText email,fullname,phone,pass,conpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register=findViewById(R.id.register);
        email=findViewById(R.id.emailedit);
        fullname=findViewById(R.id.fullnameedit);
        phone=findViewById(R.id.phoneedit);
        pass=findViewById(R.id.passedit);
        conpass=findViewById(R.id.conpassedit);


        loginreg=findViewById(R.id.loginreg);

        loginreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        mauth=FirebaseAuth.getInstance();

        mdialog=new ProgressDialog(this);

        register();
    }

    private void register(){
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String mail=email.getText().toString().trim();
                String name=fullname.getText().toString().trim();
                String phoneno=phone.getText().toString().trim();
                String password=pass.getText().toString().trim();
                String conpassword=conpass.getText().toString().trim();

                if(TextUtils.isEmpty(mail)) {
                    email.setError("Email Required");
                    return;
                }
                if(TextUtils.isEmpty(name)){
                    fullname.setError("Full Name Required");
                    return;
                }
                if(TextUtils.isEmpty(phoneno)){
                    phone.setError("Phone Number Required");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    pass.setError("password Required");
                    return;
                }
                if(TextUtils.isEmpty(conpassword)){
                    conpass.setError("Confirm Password");
                }

                mdialog.setMessage("Registering");
                mdialog.show();



                mauth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {

                            mauth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        HashMap<String, String> usermap = new HashMap<>();

                                        usermap.put("Email ID", mail);
                                        usermap.put("Name", name);
                                        usermap.put("Phone Number", phoneno);
                                        usermap.put("Password", password);
                                        usermap.put("Confirm Password", conpassword);

                                        jobref.push().setValue(usermap);
                                        Intent i = new Intent(RegisterActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();

                                        Toast.makeText(getApplicationContext(), "Registration Successfull,please verify your Email ID", Toast.LENGTH_SHORT).show();
                                        mdialog.dismiss();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Registration Failed", Toast.LENGTH_SHORT).show();
                                        mdialog.dismiss();
                                    }
                                }
                            });

                        }
                    }
                });
            }
        });
    }

}