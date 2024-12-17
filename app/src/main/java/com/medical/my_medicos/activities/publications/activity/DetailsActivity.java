package com.medical.my_medicos.activities.publications.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
//
//import com.github.barteksc.pdfviewer.PDFView;
//import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
//import com.github.barteksc.pdfviewer.listener.OnRenderListener;
//import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;

public class DetailsActivity extends AppCompatActivity {

    //private PDFView pdfView;
    private ProgressBar progressBar;
    private SeekBar seekBar;
    private TextView bookNameTextView;
    private TextView authorNameTextView;
    private TextView leftSwipeTextView;
    private TextView rightSwipeTextView;
    private TextView currentPageTextView;
    private TextView totalPageTextView;
    private LinearLayout topLayout;
    private CardView seekbarCardView;
    private TextView focusModeTextView;
    private LinearLayout normalModeLayout;
    private ImageView backToPreviousImageView;
    private boolean isFocusMode = false; // Track focus mode state
    private SharedPreferences sharedPreferences;
    private int currentPage = 0;  // Track the current page
    private static final int STORAGE_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        // Initialize views
        //pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progress);
        seekBar = findViewById(R.id.seekBar);
        bookNameTextView = findViewById(R.id.bookname);
        authorNameTextView = findViewById(R.id.authorname);
        leftSwipeTextView = findViewById(R.id.leftswipe);
        rightSwipeTextView = findViewById(R.id.rightswipe);
        currentPageTextView = findViewById(R.id.currentpage);
        totalPageTextView = findViewById(R.id.totalpage);
        topLayout = findViewById(R.id.toplayout);
        seekbarCardView = findViewById(R.id.seekbarcardview);
        focusModeTextView = findViewById(R.id.focusmode);
        normalModeLayout = findViewById(R.id.normalmode_layout);
        backToPreviousImageView = findViewById(R.id.backtoprevious);
        sharedPreferences = getSharedPreferences("Permissions", MODE_PRIVATE);

        // Retrieve book and author names from Intent extras
        String bookName = getIntent().getStringExtra("bookName");
        String authorName = getIntent().getStringExtra("authorName");

        // Set the book and author names to the TextViews
        if (bookName != null) {
            bookNameTextView.setText(bookName);
        }
        if (authorName != null) {
            authorNameTextView.setText(authorName);
        }

        // Check and request storage permissions if not granted previously
        if (!hasStoragePermission() && !isStoragePermissionDeniedForever()) {
            requestStoragePermissions();
        } else {
            fetchPdf();
        }

        // Set SeekBar listener
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                if (fromUser && pdfView.getPageCount() > 0) {
//                    pdfView.jumpTo(progress, true);
//                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Set click listeners for left and right swipe TextViews
        leftSwipeTextView.setOnClickListener(view -> {
            if (currentPage > 0) {
                currentPage--;
                //pdfView.jumpTo(currentPage, true);
                seekBar.setProgress(currentPage);
                //updatePageTextViews(currentPage, pdfView.getPageCount());
            }
        });

        rightSwipeTextView.setOnClickListener(view -> {
//            if (currentPage < pdfView.getPageCount() - 1) {
//                currentPage++;
//                pdfView.jumpTo(currentPage, true);
//                seekBar.setProgress(currentPage);
//                updatePageTextViews(currentPage, pdfView.getPageCount());
//            }
        });

        // Set click listener for focus mode
        focusModeTextView.setOnClickListener(view -> toggleFocusMode());

        // Set click listener for normal mode
        findViewById(R.id.normalmode).setOnClickListener(view -> toggleFocusMode());

        // Set click listener for back functionality
        backToPreviousImageView.setOnClickListener(view -> showLeaveConfirmationDialog());

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

    private void toggleFocusMode() {
        if (isFocusMode) {
            // Disable focus mode
            topLayout.setVisibility(View.VISIBLE);
            seekbarCardView.setVisibility(View.VISIBLE);
            normalModeLayout.setVisibility(View.GONE);
            //pdfView.setBackgroundColor(getResources().getColor(R.color.backgroundcolor));
            focusModeTextView.setText("Focus");
        } else {
            // Enable focus mode
            topLayout.setVisibility(View.GONE);
            seekbarCardView.setVisibility(View.GONE);
            normalModeLayout.setVisibility(View.VISIBLE);
           // pdfView.setBackgroundColor(getResources().getColor(R.color.unselected));
            focusModeTextView.setText("Exit Focus");
        }
        isFocusMode = !isFocusMode;
    }

    private void showLeaveConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Leaving ?")
                .setMessage("Are you sure you want to leave?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setCancelable(false)
                .show();
    }
    private void updatePageTextViews(int currentPage, int totalPageCount) {
        currentPageTextView.setText(String.format("%02d", currentPage + 1));
        totalPageTextView.setText(String.format("%02d", totalPageCount));
    }

    private boolean hasStoragePermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isStoragePermissionDeniedForever() {
        return sharedPreferences.getBoolean("storage_permission_denied_forever", false);
    }

    private void requestStoragePermissions() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                STORAGE_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with fetching PDF
                fetchPdf();
            } else {
                // Permission denied, show a message or take necessary action
                boolean shouldShowRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE);
                if (!shouldShowRationale) {
                    // User has permanently denied the permission
                    sharedPreferences.edit().putBoolean("storage_permission_denied_forever", true).apply();
                }
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchPdf() {
        String productId = getIntent().getStringExtra("id");
        if (productId != null) {
            fetchProductDetailsAndShowPdf(productId);
        } else {
            Toast.makeText(this, "Product ID is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchProductDetailsAndShowPdf(String productId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Publications").document(productId).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    String url = document.getString("URL");
                    if (url != null) {
                        downloadAndDisplayPdf(url);
                    } else {
                        Toast.makeText(DetailsActivity.this, "PDF URL is missing", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(DetailsActivity.this, "No such document", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(DetailsActivity.this, "Failed to load product details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void downloadAndDisplayPdf(final String pdfUrl) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(pdfUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(ProgressBar.GONE);
                    Toast.makeText(DetailsActivity.this, "Failed to download file", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
//                if (!response.isSuccessful()) {
//                    runOnUiThread(() -> {
//                        progressBar.setVisibility(ProgressBar.GONE);
//                        Toast.makeText(DetailsActivity.this, "Failed to download file: " + response.message(), Toast.LENGTH_SHORT).show();
//                    });
//                    return;
//                }
//
//                File file = new File(getExternalFilesDir(null), "downloadedPdf.pdf");
//                BufferedSink sink = Okio.buffer(Okio.sink(file));
//                try {
//                    sink.writeAll(response.body().source());
//                    sink.close();
//
//                    runOnUiThread(() -> {
//                        pdfView.fromFile(file)
//                                .defaultPage(0)
//                                .enableSwipe(true) // enables swiping
//                                .swipeHorizontal(true) // enables horizontal swiping
//                                .onPageChange(new OnPageChangeListener() {
//                                    @Override
//                                    public void onPageChanged(int page, int pageCount) {
//                                        currentPage = page;  // Update current page
//                                        seekBar.setMax(pageCount - 1);
//                                        seekBar.setProgress(page);
//                                        updatePageTextViews(page, pageCount);
//                                    }
//                                })
//                                .onRender(new OnRenderListener() {
//                                    @Override
//                                    public void onInitiallyRendered(int nbPages) {
//                                        pdfView.fitToWidth(0); // Fit the PDF to the width of the view
//                                        progressBar.setVisibility(ProgressBar.GONE);
//                                        updatePageTextViews(currentPage, nbPages);
//                                    }
//                                })
//                                .scrollHandle(new DefaultScrollHandle(DetailsActivity.this))
//                                .load();
//                    });
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    runOnUiThread(() -> {
//                        progressBar.setVisibility(ProgressBar.GONE);
//                        Toast.makeText(DetailsActivity.this, "Failed to save file", Toast.LENGTH_SHORT).show();
//                    });
//                }
            }
        });
    }
}
