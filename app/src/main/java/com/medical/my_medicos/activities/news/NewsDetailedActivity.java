package com.medical.my_medicos.activities.news;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.databinding.ActivityNewsDetailedBinding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class NewsDetailedActivity extends AppCompatActivity {

    ActivityNewsDetailedBinding binding;
    News currentNews;
    String documentid;
    String newstitle, newstype;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    String current = user.getPhoneNumber();
    ImageView sharenews;
    String newsId;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewsDetailedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressBar = findViewById(R.id.progressBar); // Add this line to find the ProgressBar

        String documentId = getIntent().getStringExtra("id");
        String documentId2 = getIntent().getStringExtra("id");

        if (documentId != null && !documentId.isEmpty()) {
            progressBar.setVisibility(View.VISIBLE); // Show loader
            getNewsDetails(documentId);
        } else {
            Log.e(TAG, "DocumentId is null or empty");
            documentId2 = getIntent().getStringExtra("newsId");
            Log.e(documentId2, "print");
            progressBar.setVisibility(View.VISIBLE); // Show loader
            getNewsDetails2(documentId2);
        }

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

        String name = getIntent().getStringExtra("Title");
        String image = getIntent().getStringExtra("thumbnail");
        String status = getIntent().getStringExtra("Description");
        String time = getIntent().getStringExtra("Time");
        String url = getIntent().getStringExtra("URL");

        Glide.with(this)
                .load(image)
                .into(binding.newsthumbnail);

        binding.newsDescription.setText(status);
        binding.newsTitle.setText(name);
        binding.newsTime.setText(time);

        // Enable the back button in the toolbar
        setSupportActionBar(binding.newsstoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharenews = findViewById(R.id.sharebtnfornews);

        sharenews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createlink(current, documentid, newstitle, newstype);
            }
        });

        handleDeepLink();
    }
    public void getNewsDetails(String documentId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MedicalNews")
                .document(documentId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE); // Hide loader
                    if (documentSnapshot.exists()) {
                        String description = documentSnapshot.getString("Description");

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                            binding.newsDescription.setText(Html.fromHtml(description, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            binding.newsDescription.setText(Html.fromHtml(description));
                        }

                        String time = documentSnapshot.getString("Time");
                        String url = documentSnapshot.getString("URL");
                        String subject = documentSnapshot.getString("subject");
                        String thumbnail = documentSnapshot.getString("thumbnail");
                        String type = documentSnapshot.getString("type");

                        documentid = documentSnapshot.getId();
                        newstitle = documentSnapshot.getString("Title");
                        newstype = type;
                        newsId = documentSnapshot.getId();

                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                        originalFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                        SimpleDateFormat desiredFormat = new SimpleDateFormat("MMMM, dd HH:mm", Locale.US);

                        try {
                            Date date = originalFormat.parse(time);
                            String formattedTime = desiredFormat.format(date);
                            binding.newsTime.setText(formattedTime);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing date: " + time, e);
                            binding.newsTime.setText(time); // Fallback to original string in case of parsing error
                        }

                        binding.newsDescription.setText(Html.fromHtml(description));
                        Glide.with(NewsDetailedActivity.this)
                                .load(thumbnail)
                                .into(binding.newsthumbnail);
                        binding.newsTitle.setText(newstitle);

                        currentNews = new News(
                                documentid,
                                documentSnapshot.getString("Title"),
                                documentSnapshot.getString("thumbnail"),
                                documentSnapshot.getString("Description"),
                                documentSnapshot.getString("subject"),
                                documentSnapshot.getString("Time"),
                                documentSnapshot.getString("URL"),
                                documentSnapshot.getString("type")
                        );

                    } else {
                        // Handle the case where the document does not exist
                        Log.e(TAG, "No such document with name: " + documentId);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE); // Hide loader in case of failure
                    // Handle the error
                    Log.e(TAG, "Error fetching news details for name: " + documentId, e);
                });
        }
    void getNewsDetails2(String documentId2) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("MedicalNews")
                .document(documentId2)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    progressBar.setVisibility(View.GONE); // Hide loader
                    if (documentSnapshot.exists()) {
                        String description = documentSnapshot.getString("Description");
                        String time = documentSnapshot.getString("Time"); // Example format: "2024-03-12T09:45:00.458Z"
                        String url = documentSnapshot.getString("URL");
                        String thumbnail = documentSnapshot.getString("thumbnail");
                        String subject = documentSnapshot.getString("subject");
                        String type = documentSnapshot.getString("type");
                        documentid = documentSnapshot.getId();
                        newstitle = documentSnapshot.getString("Title");
                        newstype = type;
                        newsId = documentSnapshot.getId();

                        // Adjust the SimpleDateFormat to include milliseconds
                        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US);
                        originalFormat.setTimeZone(TimeZone.getTimeZone("UTC")); // Ensure parsing is in UTC
                        SimpleDateFormat desiredFormat = new SimpleDateFormat("MMMM, dd HH:mm", Locale.US);

                        try {
                            Date date = originalFormat.parse(time);
                            String formattedTime = desiredFormat.format(date);
                            binding.newsTime.setText(formattedTime);
                        } catch (Exception e) {
                            Log.e(TAG, "Error parsing date: " + time, e);
                            binding.newsTime.setText(time); // Fallback to original string in case of parsing error
                        }

                        binding.newsDescription.setText(Html.fromHtml(description));
                        binding.newsTitle.setText(newstitle);
                        Glide.with(this)
                                .load(thumbnail)
                                .into(binding.newsthumbnail);

                        currentNews = new News(
                                documentid,
                                documentSnapshot.getString("Title"),
                                documentSnapshot.getString("thumbnail"),
                                documentSnapshot.getString("Description"),
                                documentSnapshot.getString("subject"),
                                documentSnapshot.getString("Time"),
                                documentSnapshot.getString("URL"),
                                documentSnapshot.getString("type")
                        );

                    } else {
                        Log.e(TAG, "No such document with name: " + documentId2);
                    }
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE); // Hide loader in case of failure
                    Log.e(TAG, "Error fetching news details for name: " + documentId2, e);
                });
    }
    private void openUrlInBrowser(String url) {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        }
    public void createlink(String custid, String newsId, String newstitle, String newsdescription) {
            Log.e("main", "create link");

            DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                    .setLink(Uri.parse("https://www.mymedicos.in/newsdetails?custid=" + custid + "&newsId=" + newsId))
                    .setDomainUriPrefix("https://app.mymedicos.in")
                    .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                    .setIosParameters(new DynamicLink.IosParameters.Builder("com.example.ios").build())
                    .buildDynamicLink();

            Uri dynamicLinkUri = dynamicLink.getUri();
            Log.e("main", " Long refer " + dynamicLink.getUri());

            createreferlink(custid, newsId);
        }
    public void createreferlink(String custid, String newsId) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            DocumentReference docRef = db.collection("MedicalNews").document(newsId);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String newsTitle = documentSnapshot.getString("Title");
                    String newsThumbnail = documentSnapshot.getString("thumbnail");

                    String encodedNewsTitle = encode(newsTitle);
                    String encodedNewsThumbnail = encode(newsThumbnail); // Assuming you want to include this in the URL for some reason

                    // The shared text format
                    String sharelinktext = newsTitle + "\n\n For entire detail visit: " +
                            "https://app.mymedicos.in/?" +
                            "link=http://www.mymedicos.in/newsdetails?newsId=" + newsId +
                            "&st=" + encodedNewsTitle +
                            "&apn=" + getPackageName() +
                            "&si=" + encodedNewsThumbnail; // This will not make the thumbnail appear in the share text directly

                    Log.e("NewsDetailedActivity", "Sharelink - " + sharelinktext);

                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, sharelinktext);
                    intent.setType("text/plain");
                    startActivity(intent);

                } else {
                    Log.e(TAG, "No such document with documentId: " + newsId);
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching news details for documentId: " + newsId, e);
            });
        }
    private String encode(String s) {
            try {
                return URLEncoder.encode(s, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                return URLEncoder.encode(s);
            }
        }
    private void handleDeepLink() {
            FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                    .addOnSuccessListener(this, pendingDynamicLinkData -> {
                        if (pendingDynamicLinkData != null) {
                            Uri deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink != null) {
                                String newsId = deepLink.getQueryParameter("newsId");
                                Intent intent = getIntent();
                                intent.putExtra("newsId", newsId);
                                setIntent(intent);
                            }
                        }
                    })
                    .addOnFailureListener(this, e -> Log.w("DeepLink", "getDynamicLink:onFailure", e));
        }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                onBackPressed();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    @Override
    public boolean onSupportNavigateUp() {
            finish();
            return super.onSupportNavigateUp();
        }

}
