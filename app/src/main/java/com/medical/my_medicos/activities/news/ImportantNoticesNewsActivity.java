package com.medical.my_medicos.activities.news;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.news.adapter.ImportantAnnouncementAdapter;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ImportantNoticesNewsActivity extends AppCompatActivity {

    private ImportantAnnouncementAdapter newsannouncementAdapter;

    ImageView backtothehomefromimportant;
    private ArrayList<News> newsprepration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_important_notices_news);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        backtothehomefromimportant = findViewById(R.id.backtothehomefromimportant);
        backtothehomefromimportant.setOnClickListener(view -> {
            Intent intent = new Intent(ImportantNoticesNewsActivity.this, NewsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });


        initImportantUpdatesInPreparation();
    }

    void initImportantUpdatesInPreparation() {
        newsprepration = new ArrayList<>();
        newsannouncementAdapter = new ImportantAnnouncementAdapter(this, newsprepration);
        getRecentNewsUpdatesPrepration();

        RecyclerView recyclerView = findViewById(R.id.noticeimportant);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsannouncementAdapter);
    }

    void getRecentNewsUpdatesPrepration() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MedicalNews")
                .whereEqualTo("type", "Notice")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<String> updateIds = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            updateIds.add(document.getId());
                            News newsItem = new News(
                                    document.getId(),
                                    document.getString("Title"),
                                    document.getString("thumbnail"),
                                    document.getString("Description"),
                                    document.getString("subject"),
                                    document.getString("Time"),
                                    document.getString("URL"),
                                    document.getString("type")
                            );
                            newsprepration.add(newsItem);
                        }

                        SharedPreferences sharedPreferences = getSharedPreferences("My_Medicos_Prefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        JSONArray jsonArray = new JSONArray(updateIds);
                        editor.putString("SeenUpdates", jsonArray.toString());
                        editor.apply();

                        newsannouncementAdapter.notifyDataSetChanged();

                        // Here is the added logic for toggling visibility based on updates availability
                        LinearLayout noResults = findViewById(R.id.nonewimpnotice);
                        if(newsprepration.isEmpty()){
                            noResults.setVisibility(View.VISIBLE);
                        }else{
                            noResults.setVisibility(View.GONE);
                        }
                    } else {
                        // In case the task is not successful, also show the noResults view
                        LinearLayout noResults = findViewById(R.id.noResults);
                        noResults.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Implement any additional logic here if needed before closing the activity
    }
}
