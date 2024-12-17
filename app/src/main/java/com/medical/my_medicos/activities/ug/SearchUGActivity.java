package com.medical.my_medicos.activities.ug;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.ug.UgAdapter1;
import com.medical.my_medicos.adapter.ug.items.ugitem1;
import com.medical.my_medicos.databinding.ActivitySearchUgactivityBinding;

import java.util.ArrayList;
import java.util.Map;

public class SearchUGActivity extends AppCompatActivity {

    ActivitySearchUgactivityBinding binding;
    UgAdapter1 ugitemAdapter;
    ArrayList<ugitem1> ugitemslist;

    private String field1, field2, field3, field4,field6,field7;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchUgactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ugitemslist = new ArrayList<>();
        ugitemAdapter = new UgAdapter1(this, ugitemslist);

        String query = getIntent().getStringExtra("query");

        // Set the query as the title of the search
        TextView titleTextView = findViewById(R.id.titleofthesearch);
        titleTextView.setText(query);

        getSlideshow(query);

        ImageView backToPublicationActivity = findViewById(R.id.backtothesearchugactivity);
        backToPublicationActivity.setOnClickListener(v -> {
            finish();
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.ugContentList.setLayoutManager(layoutManager);
        binding.ugContentList.setAdapter(ugitemAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getSlideshow(String query) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Assuming "UGConfirm" is the collection in Firestore
        CollectionReference ugConfirmRef = db.collection("UGConfirm");

        ugConfirmRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ugitemslist.clear();

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Map<String, Object> dataMap = document.getData();

                        String title = (String) dataMap.get("UG Title");
                        if (title != null && title.toLowerCase().contains(query.toLowerCase())) {
                            field3 = ((String) dataMap.get("UG Title"));
                            field4 = ((String) dataMap.get("UG Description"));
                            field1 = (String) dataMap.get("UG Organiser");
                            field2 = ((String) dataMap.get("Speciality"));
                            field6 = ((String) dataMap.get("User"));
                            Long downloadsValue = (Long) dataMap.get("Downloads");
                            field7 = String.valueOf(downloadsValue);
                            String field5=((String) dataMap.get("Date"));
                            String pdf=((String) dataMap.get("pdf"));

                            ugitem1 u = new ugitem1(field1, field2, pdf, field3, field4, field5, field6,field7);
                            ugitemslist.add(u);
                        }
                    }

                    ugitemAdapter.notifyDataSetChanged();
                    if (ugitemslist.isEmpty()) {
                        binding.noResultsfoundUG.setVisibility(View.VISIBLE);
                    } else {
                        binding.noResultsfoundUG.setVisibility(View.GONE);
                    }
                } else {
                    // Handle errors
                }
            }
        });
    }

    void updateDownloadCount(String docId, int currentDownloads) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ugConfirmRef = db.collection("UGConfirm");

        // Increment the downloads count
        int newDownloads = currentDownloads + 1;

        // Update the downloads count in Firestore
        ugConfirmRef.document(docId)
                .update("Downloads", newDownloads)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Log success or handle as needed
                        } else {
                            // Log failure or handle as needed
                        }
                    }
                });
    }
}
