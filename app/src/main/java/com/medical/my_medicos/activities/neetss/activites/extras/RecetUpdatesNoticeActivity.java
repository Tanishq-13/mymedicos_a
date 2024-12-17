package com.medical.my_medicos.activities.neetss.activites.extras;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.extras.adapter.RecentUpdatesAdapter;
import com.medical.my_medicos.activities.news.News;
import com.medical.my_medicos.databinding.ActivityRecetUpdatesNoticeBinding;

import java.util.ArrayList;

public class RecetUpdatesNoticeActivity extends AppCompatActivity {
    ActivityRecetUpdatesNoticeBinding binding;
    RecentUpdatesAdapter newsupdatespgAdapter;
    ArrayList<News> newspg;

    private SwipeRefreshLayout swipeRefreshLayoutUpdates;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecetUpdatesNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        swipeRefreshLayoutUpdates = findViewById(R.id.swipeRefreshLayoutNews);
        swipeRefreshLayoutUpdates.setOnRefreshListener(this::refreshContent);
        binding.newsstoolbar.setNavigationOnClickListener(v -> onBackPressed());

        initImportantUpdates();
        configureWindow();
    }

    private void configureWindow() {
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void refreshContent() {
        clearData();
        fetchData();
        swipeRefreshLayoutUpdates.setRefreshing(false);
    }
    private void clearData() {
        newspg.clear();
    }
    private void fetchData() {
        getRecentNewsUpdates();
    }

    void initImportantUpdates() {
        newspg = new ArrayList<News>();
        newsupdatespgAdapter = new RecentUpdatesAdapter(this, newspg);
        getRecentNewsUpdates();
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.newsListPg.setLayoutManager(layoutManager);

        binding.newsListPg.setAdapter(newsupdatespgAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    void getRecentNewsUpdates() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MedicalNews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        newspg.clear(); // Clear existing data to avoid duplicates when refreshing
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String newsType = document.getString("type");

                            if ("Notice".equals(newsType)) {
                                News newsItem = new News(
                                        document.getId(), // Include the document ID here
                                        document.getString("Title"),
                                        document.getString("thumbnail"),
                                        document.getString("Description"),
                                        document.getString("subject"),
                                        document.getString("Time"),
                                        document.getString("URL"),
                                        newsType
                                );
                                newspg.add(newsItem);
                            }
                        }
                        newsupdatespgAdapter.notifyDataSetChanged();
                    } else {
                        // Optionally, show an error message or handle the failure case
                    }
                });
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

}