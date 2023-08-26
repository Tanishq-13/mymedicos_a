package com.example.my_medicos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class PostCmeActivity extends AppCompatActivity {


    TextView cmetitle,cmeorg,cmepresenter,cmevenu,virtuallink,cme_place;

    Button postcme;

    public FirebaseDatabase db= FirebaseDatabase.getInstance();

    public DatabaseReference cmeref=db.getReference().child("CME's");

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_cme);

        Spinner  spinner2= (Spinner) findViewById(R.id.cmemode);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> myadapter = ArrayAdapter.createFromResource(this,
                R.array.mode, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        myadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner2.setAdapter(myadapter);

        cmetitle=findViewById(R.id.cme_title);
        cmeorg=findViewById(R.id.cme_organiser);
        cmepresenter=findViewById(R.id.cme_presenter);
        cmevenu=findViewById(R.id.cme_venu);
        virtuallink=findViewById(R.id.cme_virtuallink);
        cme_place=findViewById(R.id.cme_place);

        postcme=findViewById(R.id.post_btn);

        postcme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postCme();
            }
        });
    }
    public void postCme(){
        String title=cmetitle.getText().toString().trim();
        String organiser=cmeorg.getText().toString().trim();
        String presenter=cmepresenter.getText().toString().trim();
        String venu=cmevenu.getText().toString().trim();
        String link=virtuallink.getText().toString().trim();
        String place=cme_place.getText().toString().trim();


        if(TextUtils.isEmpty(title)) {
            cmetitle.setError("Title Required");
            return;
        }
        if(TextUtils.isEmpty(organiser)) {
            cmeorg.setError("Organizer Required");
            return;
        }
        if(TextUtils.isEmpty(presenter)) {
            cmepresenter.setError("Email Required");
            return;
        }
        if(TextUtils.isEmpty(venu)) {
            cmevenu.setError("Email Required");
        }

        HashMap<String, String> usermap = new HashMap<>();

        usermap.put("CME Title", title);
        usermap.put("CME Organiser", organiser);
        usermap.put("CME Presenter", venu);
        usermap.put("Virtual Link", link);
        usermap.put("CME Place", place);

        cmeref.push().setValue(usermap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(PostCmeActivity.this, "Posted Successfully", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(PostCmeActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}