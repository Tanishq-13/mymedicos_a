package com.medical.my_medicos.activities.news;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medical.my_medicos.R;
import com.medical.my_medicos.databinding.ActivitySearchNewsBinding;

import java.util.ArrayList;

public class SearchNewsActivity extends AppCompatActivity {

    ActivitySearchNewsBinding binding;
    NewsAdapter newsAdapter;
    ArrayList<News> news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS );
        binding = ActivitySearchNewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowInsetsController controller = getWindow().getInsetsController();
            if (controller != null) {
                controller.setSystemBarsBehavior(WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
                controller.hide(WindowInsets.Type.statusBars());
            }
        } else {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        news = new ArrayList<>();
        newsAdapter = new NewsAdapter(this, news);

        String query = getIntent().getStringExtra("query");
        if (!TextUtils.isEmpty(query)) {
            getNews(query);
        } else {
            Toast.makeText(this, "No query provided", Toast.LENGTH_SHORT).show();
        }

        TextView titleTextView = findViewById(R.id.titleofthesearch);
        titleTextView.setText(query);

        ImageView backToPublicationActivity = findViewById(R.id.backtothenewsactivity);
        backToPublicationActivity.setOnClickListener(v -> {
            finish();
        });

        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        binding.newsList.setLayoutManager(layoutManager);
        binding.newsList.setAdapter(newsAdapter);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    void getNews(String query) {
        if (TextUtils.isEmpty(query)) {
            Toast.makeText(this, "Query is empty", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MedicalNews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        news.clear(); // Clear the existing list to avoid duplicates
                        boolean foundMatch = false;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String title = document.getString("Title");
                            if (title.toLowerCase().contains(query.toLowerCase())) {
                                News newsItem = new News(
                                        document.getId(), // Include the document ID here
                                        title,
                                        document.getString("thumbnail"),
                                        document.getString("Description"),
                                        document.getString("subject"),
                                        document.getString("Time"),
                                        document.getString("URL"),
                                        document.getString("type")
                                );
                                news.add(newsItem);
                                foundMatch = true;
                            }
                        }
                        newsAdapter.notifyDataSetChanged();
                        if (!foundMatch) {
                            binding.noResults.setVisibility(View.VISIBLE);
                        } else {
                            binding.noResults.setVisibility(View.GONE);
                        }
                    } else {
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
