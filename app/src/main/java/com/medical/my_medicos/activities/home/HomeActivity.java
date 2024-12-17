package com.medical.my_medicos.activities.home;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.firebase.Timestamp;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.Extras.NoInternetActivity;
import com.medical.my_medicos.activities.cme.CmeDetailsActivity;
import com.medical.my_medicos.activities.fmge.activites.FmgeExamPayment;
import com.medical.my_medicos.activities.fmge.activites.FmgePrepPayement;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.exploreplansfmge;
import com.medical.my_medicos.activities.home.adapter.Dailyquestion;
import com.medical.my_medicos.activities.home.exclusive.exclusivehome;
import com.medical.my_medicos.activities.home.fragments.ClubFragment;
import com.medical.my_medicos.activities.home.fragments.HomeFragment;
import com.medical.my_medicos.activities.home.fragments.SlideshowFragment;
import com.medical.my_medicos.activities.home.model.dailyquestion;
import com.medical.my_medicos.activities.home.sidedrawer.HomeSideActivity;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medical.my_medicos.activities.job.JobDetailsActivity;
import com.medical.my_medicos.activities.news.NewsActivity;
import com.medical.my_medicos.activities.news.NewsDetailedActivity;
import com.medical.my_medicos.activities.notifications.activity.NotificationActivity;
import com.medical.my_medicos.activities.pg.activites.NeetExamPayment;
import com.medical.my_medicos.activities.pg.activites.PgPrepPayement;
import com.medical.my_medicos.activities.pg.activites.internalfragments.exploreplansactivity;
import com.medical.my_medicos.activities.pg.activites.resultexists;
import com.medical.my_medicos.activities.pg.model.QuizPG;
import com.medical.my_medicos.activities.publications.activity.ProductDetailedActivity;
import com.medical.my_medicos.activities.utils.ConnectvityUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.prefs.Preferences;
import com.google.firebase.messaging.FirebaseMessaging;
// This is the Home Activity..

public class HomeActivity extends AppCompatActivity implements BottomSheet.BottomSheetListener {
    BottomNavigationView bottomNavigation;
    BottomAppBar bottomAppBar;
    ImageView notificationbtn;
LottieAnimationView lta;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    ImageView profileImageView, verifiedprofilebehere;
    private static final long DOUBLE_PRESS_EXIT_INTERVAL = 2000;
    private static final int MY_UPDATE_CODE = 100;
    private long lastPressTime;
    FrameLayout verifiedUser, circularImageView;
    //    ProgressBar loadingCircle;
//    LinearLayout opensidehomedrawer;
    RecyclerView daily;
    private static final int NOTIFICATION_PERMISSION_CODE = 3300;

//    private TextView greetingTextView;
//    private TextView personname;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isFirstLogin = preferences.getBoolean("isFirstLogin", true);

//        lta=findViewById(R.id.lottieAnimationView2);

        if (isFirstLogin) {
            // Display the loader screen
            setContentView(R.layout.activity_loader_screen);

            // Fetch data in the background
            new Handler().postDelayed(() -> {
//                fetchUserData();
                // After fetching data, mark the first login as false and load the main layout
                preferences.edit().putBoolean("isFirstLogin", false).apply();
                setContentView(R.layout.activity_home);
                initializeHomeActivity();
            }, 5000); // Show the loader screen for 3 seconds
        } else {
            // If not the first login, directly load the main layout
            setContentView(R.layout.activity_home);

            initializeHomeActivity();
        }
    }
    @Override
    public void onOptionClick(String option) {
        // Handle the option click event here
        if (option.equals("Education")) {
            // Do something for Education option
        } else if (option.equals("Community")) {
            // Do something for Community option
        }
    }

    private void initializeHomeActivity() {
        // Initialize your HomeActivity components and functionality here

        // Initialize views
//        greetingTextView = findViewById(R.id.greetingTextView);
//        personname = findViewById(R.id.personname);
        verifiedUser = findViewById(R.id.verifieduser);
        profileImageView = findViewById(R.id.circularImageView);
        verifiedprofilebehere = findViewById(R.id.verifiedprofilebehere);
//        LinearLayout topLayout = findViewById(R.id.top);
        lta=findViewById(R.id.lottieAnimationView);
//        LottieAnimationView lta = findViewById(R.id.lottieAnimationView2);
//        lta.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(v.getContext(), exploreplansactivity.class);
//                startActivity(intent);
//            }
//        });
//        ImageView notification=findViewById(R.id.notificationIcon);
//        notification.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Navigate to PersonalInfoActivity
//
//                Intent intent;
//                intent = new Intent(v.getContext(), NotificationActivity.class);
//                startActivity(intent);
//            }
//        });
//        topLayout.setOnClickListener(v -> {
//            BottomSheet bottomSheet = new BottomSheet();
//            bottomSheet.show(getSupportFragmentManager(), "OptionsBottomSheet");
//        });


        // Network Issue Condition...............

        if (!ConnectvityUtil.isConnectedToInternet(this)) {
            // Start the NoInternetActivity if there is no internet connection
            Intent intent = new Intent(this, NoInternetActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
            return; // Exit the method early
        }

        // Notification permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            checkNotificationPermission();
        }

        // Loader Implementation..............

//        loadingCircle = findViewById(R.id.loadingCircle);
//        opensidehomedrawer = findViewById(R.id.opensidehomedrawer);

//        loadingCircle.setVisibility(View.VISIBLE);
//        opensidehomedrawer.setVisibility(View.GONE);

        handleDeepLinkIntent();

        // Theme for Status and Navigation bar.......

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }

        //...Toolbar Implementation......

        toolbar = findViewById(R.id.toolbar);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            Log.d("Something went wrong...", Objects.requireNonNull(userId));
        }

//        fetchUserData();

        Log.d("Something went wrong..", Objects.requireNonNull(currentUser.getPhoneNumber()));

        Preferences preferences = Preferences.userRoot();
        if (preferences.get("username", null) != null) {
            String username = preferences.get("username", null);
            Log.d("username", username);
        }
//        LinearLayout openhomedrawerIcon = findViewById(R.id.opensidehomedrawer);
//        openhomedrawerIcon.setOnClickListener(v -> openHomeSideActivity());

        bottomAppBar = findViewById(R.id.bottomappabar);

        replaceFragment(new exclusivehome());
        bottomNavigation = findViewById(R.id.bottomNavigationView);
        bottomNavigation.setBackground(null);

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        Log.d("current frag",String.valueOf(currentFragment));

        //....Fragment Shifter...........

        bottomNavigation.setOnItemSelectedListener(item -> {
            int frgId = item.getItemId();
            Log.d("Something went wrong", "Report to the Developer");
            Fragment selectedFragment = null;
            if (frgId == R.id.home) {
                selectedFragment = new exclusivehome();
            } else if (frgId == R.id.work) {
                selectedFragment = new ClubFragment();
            } else {
                selectedFragment=new NewsActivity();
            }
            replaceFragment(selectedFragment);
            updateToolbarColor(selectedFragment); // Update toolbar color based on the selected fragment

            return true;
        });

//        updateGreetingAndName();
        checkforAppUpdate();
    }
    private void showBottomSheet() {
        BottomSheet bottomSheet = new BottomSheet();
        bottomSheet.show(getSupportFragmentManager(), "BottomSheet");
    }


    private void checkNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
        } else {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.POST_NOTIFICATIONS)) {
                Toast.makeText(this, "Notifications are needed to inform you of important updates.", Toast.LENGTH_LONG).show();
            }
            // Request the permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                openNotificationSettings();
                Toast.makeText(this, "Notification permission granted.", Toast.LENGTH_SHORT).show();
            } else {
                // Permission was denied
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openNotificationSettings() {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            intent.putExtra("app_package", getPackageName());
            intent.putExtra("app_uid", getApplicationInfo().uid);
        } else {
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.fromParts("package", getPackageName(), null));
        }
        startActivity(intent);
    }

    //....Shareable Link Handler.............

    private void handleDeepLinkIntent() {
        FirebaseDynamicLinks.getInstance().getDynamicLink(getIntent())
                .addOnSuccessListener(this, pendingDynamicLinkData -> {
                    if (pendingDynamicLinkData != null) {
                        Uri deepLink = pendingDynamicLinkData.getLink();
                        if (deepLink != null) {
                            String link = deepLink.toString();
                            if (link.contains("jobdetails?")) {
                                handleDeepLinkIntentJOB(deepLink);
                            } else if (link.contains("cmedetails?")) {
                                handleDeepLinkIntentCME(deepLink);
                            } else if (link.contains("newsdetails?")) {
                                handleDeepLinkIntentNews(deepLink);
                            } else if (link.contains("bookdetails?")) {
                                handleDeepLinkIntentPublication(deepLink);
                            } else if (link.contains("examdetails?")) {
                                handleDeepLinkIntentPGUpload(deepLink);
                            } else if (link.contains("fmgedetails?")) {
                                handleDeepLinkIntentFMGEUpload(deepLink);
                            }
                        }
                    }
                })
                .addOnFailureListener(this, e -> {
                    Log.e("DeepLink", "Error handling deep link: " + e.getMessage());
                });
    }

    //...Handler Extension..........

    private void handleDeepLinkIntentJOB(Uri deepLink) {
        String jobId = deepLink.getQueryParameter("jobId");
        Log.d("DeepLink", "Received deep link with jobId: " + jobId);
        openJobDetailsActivity(jobId);
    }

    private void handleDeepLinkIntentPGUpload(Uri deepLink) {
        String examId = deepLink.getQueryParameter("examId");
        Log.d("DeepLink", "Received deep link with examId: " + examId);
        openExamDetailsActivity(examId);
    }

    private void handleDeepLinkIntentFMGEUpload(Uri deepLink) {
        String fmgeId = deepLink.getQueryParameter("fmgeId");
        Log.d("DeepLink", "Received deep link with fmgeId: " + fmgeId);
        openFmgeDetailsActivity(fmgeId);
    }

    private void handleDeepLinkIntentNews(Uri deepLink) {
        String newsId = deepLink.getQueryParameter("newsId");
        Log.d("DeepLink", "Received deep link with newsId: " + newsId);
        openNewsDetailsActivity(newsId);
    }

    private void handleDeepLinkIntentCME(Uri deepLink) {
        String cmeId = deepLink.getQueryParameter("cmeId");
        String typefordeep = deepLink.getQueryParameter("typefordeep");
        Log.d("DeepLink", "Received deep link with cmeId: " + cmeId + " and typefordeep: " + typefordeep);
        openCmeDetailsActivity(cmeId, typefordeep); // Pass typefordeep to openCmeDetailsActivity method
    }

    private void handleDeepLinkIntentPublication(Uri deepLink) {
        String bookId = deepLink.getQueryParameter("bookId");
        Log.d("DeepLink", "Received deep link with bookId: " + bookId);
        openPublicationsDetailsActivity(bookId);
    }

    //...CME Handler.............

    private void openCmeDetailsActivity(String cmeId, String typefordeep) {
        Log.d("HomeActivity", "Opening CmeDetailsActivity with documentId: " + cmeId);
        Intent intent = new Intent(this, CmeDetailsActivity.class);
        intent.putExtra("cmeId", cmeId);
        intent.putExtra("typefordeep", typefordeep); // Pass typefordeep parameter to CmeDetailsActivity
        startActivity(intent);
        finish();
        Log.d("HomeActivity", "CmeDetailsActivity started");
    }

    //...Job Handler...............

    private void openJobDetailsActivity(String jobId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String userId = currentUser.getPhoneNumber();

        Log.d("HomeActivity", "Opening JobDetailsActivity with documentId: " + jobId);
        Intent intent = new Intent(this, JobDetailsActivity.class);
        intent.putExtra("jobId", jobId);
        startActivity(intent);
        finish();
        Log.d("HomeActivity", "JobDetailsActivity started");
    }

    //...News Handler..............

    private void openNewsDetailsActivity(String newsId) {
        Log.d("HomeActivity", "Opening NewsDetailsActivity with documentId: " + newsId);
        Intent intent = new Intent(this, NewsDetailedActivity.class);
        intent.putExtra("newsId", newsId);
        startActivity(intent);
        finish();
        Log.d("HomeActivity", "NewsDetailedActivity started");
    }

    //...Publication Handler..............

    private void openPublicationsDetailsActivity(String bookId) {
        Log.d("HomeActivity", "Opening PublicationsDetailsActivity with documentId: " + bookId);
        Intent intent = new Intent(this, ProductDetailedActivity.class);
        intent.putExtra("bookId", bookId);
        startActivity(intent);
        finish();
        Log.d("HomeActivity", "PublicationsDetailsActivity started");
    }

    //.... PG Upload Handler .......................

    private void openExamDetailsActivity(String examId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String phoneNumber = currentUser.getPhoneNumber();
        // Firestore reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();

// Replace with the user's phone number

// Get the exam ID passed from the previous activity
        Log.d("HomeActivity2", "Score: ");

// Fetch Quiz Data from PGupload -> Weekley -> Quiz -> {examId}
        db.collection("PGupload")
                .document("Weekley")
                .collection("Quiz")
                .document(examId)
                .get()
                .addOnSuccessListener(quizSnapshot -> {
                    if (quizSnapshot.exists()) {
                        Log.d("HomeActivity2", "Score: ");

                        // Extract quiz data from the snapshot
                        String title = quizSnapshot.getString("title");
                        String title1 = quizSnapshot.getString("speciality");  // Assuming this is 'speciality'
                        String thumbnail = quizSnapshot.getString("thumbnail");
                        Timestamp to = quizSnapshot.getTimestamp("to");
                        boolean typeExists = quizSnapshot.contains("type");
                        boolean indexExists = quizSnapshot.contains("index");

                        String type = typeExists ? quizSnapshot.getString("type") : "";
                        String index = indexExists ? quizSnapshot.getString("index") : "";

                        List<Object> dataList = (List<Object>) quizSnapshot.get("Data");
                        int dataSize = (dataList != null) ? dataList.size() : 0;

                        // Create a QuizPG object
                        QuizPG quizday = new QuizPG(
                                title, title1, to, examId, type, index, thumbnail, String.valueOf(dataSize)
                        );

                        // Check if the quiz result exists in QuizResults -> phoneNumber -> weekly -> {examId}
                        db.collection("QuizResults")
                                .document(phoneNumber)
                                .collection("Weekley")
                                .document(examId)
                                .get()
                                .addOnSuccessListener(resultSnapshot -> {
                                    if (resultSnapshot.exists()) {
                                        // Extract result data
                                        String unans = String.valueOf(resultSnapshot.get("Unanswered"));
                                        String crans = String.valueOf(resultSnapshot.get("Correct Answers"));
                                        String wrans = String.valueOf(resultSnapshot.get("Wrong Answers"));
                                        String cmnt = resultSnapshot.getString("Comment");
                                        String qid = resultSnapshot.getString("Quiz ID");
                                        String score = String.valueOf(resultSnapshot.get("score"));
                                        String mrks = String.valueOf(resultSnapshot.get("Total Marks Obtained"));
                                        String totq = String.valueOf(resultSnapshot.get("Total Questions"));

                                        // Calculate skipped questions
                                        int skipped = Integer.parseInt(totq) - (Integer.parseInt(crans) + Integer.parseInt(wrans));
                                        String skippedques = String.valueOf(skipped);

                                        Log.d("HomeActivity2", "Score: " + score);

                                        // Open the resultexists activity
                                        Intent intent = new Intent(this, resultexists.class);
                                        intent.putExtra("skippedques", skippedques);
                                        intent.putExtra("quizid", qid);
                                        intent.putExtra("crans", crans);
                                        intent.putExtra("wrans", wrans);
                                        intent.putExtra("cmnt", cmnt);
                                        intent.putExtra("score", score);
                                        intent.putExtra("mrks", mrks);
                                        intent.putExtra("unans", unans);
                                        intent.putExtra("totq", totq);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d("HomeActivity22", "Score: ");

                                        // Result does not exist, open the payment activity
                                        Intent intent = new Intent(this, PgPrepPayement.class);
                                        intent.putExtra("Title1", quizday.getTitle1());
                                        intent.putExtra("Id",examId);
                                        intent.putExtra("Title", quizday.getTitle());
                                        intent.putExtra("From", formatTimestamp(quizday.getTo()));
                                        intent.putExtra("Due", formatTimestamp(quizday.getTo()));
                                        intent.putExtra("qid", quizday.getId());
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("HomeActivity2", "Error fetching result", e));
                    } else {
                        Log.e("HomeActivity2", "Quiz not found for examId: " + examId);
                    }
                })
                .addOnFailureListener(e -> Log.e("HomeActivity", "Error fetching quiz data", e));
    }

    private void openFmgeDetailsActivity(String examId) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("sharefmge",examId);
        String phoneNumber = currentUser.getPhoneNumber();
        // Firestore reference
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Fmge")
                .document("Weekley")
                .collection("Quiz")
                .document(examId)
                .get()
                .addOnSuccessListener(quizSnapshot -> {
                    Log.d("HomeActivity244", "Score: ");

                    if (quizSnapshot.exists()) {
                        Log.d("HomeActivity24", "Score: ");

                        // Extract quiz data from the snapshot
                        String title = quizSnapshot.getString("title");
                        String title1 = quizSnapshot.getString("speciality");  // Assuming this is 'speciality'
                        String thumbnail = quizSnapshot.getString("thumbnail");
                        Timestamp to = quizSnapshot.getTimestamp("to");
                        boolean typeExists = quizSnapshot.contains("type");
                        boolean indexExists = quizSnapshot.contains("index");

                        String type = typeExists ? quizSnapshot.getString("type") : "";
                        String index = indexExists ? quizSnapshot.getString("index") : "";

                        List<Object> dataList = (List<Object>) quizSnapshot.get("Data");
                        int dataSize = (dataList != null) ? dataList.size() : 0;

                        // Create a QuizPG object
                        QuizPG quizday = new QuizPG(
                                title, title1, to, examId, type, index, thumbnail, String.valueOf(dataSize)
                        );

                        // Check if the quiz result exists in QuizResults -> phoneNumber -> weekly -> {examId}
                        db.collection("QuizResults")
                                .document(phoneNumber)
                                .collection("Weekley")
                                .document(examId)
                                .get()
                                .addOnSuccessListener(resultSnapshot -> {
                                    if (resultSnapshot.exists()) {
                                        // Extract result data
                                        String unans = String.valueOf(resultSnapshot.get("Unanswered"));
                                        String crans = String.valueOf(resultSnapshot.get("Correct Answers"));
                                        String wrans = String.valueOf(resultSnapshot.get("Wrong Answers"));
                                        String cmnt = resultSnapshot.getString("Comment");
                                        String qid = resultSnapshot.getString("Quiz ID");
                                        String score = String.valueOf(resultSnapshot.get("score"));
                                        String mrks = String.valueOf(resultSnapshot.get("Total Marks Obtained"));
                                        String totq = String.valueOf(resultSnapshot.get("Total Questions"));

                                        // Calculate skipped questions
                                        int skipped = Integer.parseInt(totq) - (Integer.parseInt(crans) + Integer.parseInt(wrans));
                                        String skippedques = String.valueOf(skipped);

                                        Log.d("HomeActivity2", "Score: " + score);

                                        // Open the resultexists activity
                                        Intent intent = new Intent(this, resultexists.class);
                                        intent.putExtra("skippedques", skippedques);
                                        intent.putExtra("quizid", qid);
                                        intent.putExtra("crans", crans);
                                        intent.putExtra("wrans", wrans);
                                        intent.putExtra("cmnt", cmnt);
                                        intent.putExtra("score", score);
                                        intent.putExtra("mrks", mrks);
                                        intent.putExtra("unans", unans);
                                        intent.putExtra("totq", totq);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Log.d("HomeActivity22", "Score: ");

                                        // Result does not exist, open the payment activity
                                        Intent intent = new Intent(this, FmgePrepPayement.class);
                                        intent.putExtra("Title1", quizday.getTitle1());
                                        intent.putExtra("Id",examId);
                                        intent.putExtra("Title", quizday.getTitle());
                                        intent.putExtra("From", formatTimestamp(quizday.getTo()));
                                        intent.putExtra("Due", formatTimestamp(quizday.getTo()));
                                        intent.putExtra("qid", quizday.getId());
                                        startActivity(intent);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(e -> Log.e("HomeActivity2", "Error fetching result", e));
                    } else {
                        Log.e("HomeActivity2", "Quiz not found for examId: " + examId);
                    }
                })
                .addOnFailureListener(e -> Log.e("HomeActivity", "Error fetching quiz data", e));

    }


    //... Back Functionality Handler................
    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("date", dateFormat.format(timestamp.toDate()));
        return dateFormat.format(timestamp.toDate());
    }
    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        updateToolbarColor(currentFragment);

        Log.d("Current frag", String.valueOf(currentFragment));
        if (currentFragment instanceof exclusivehome) {
            long currentTime = System.currentTimeMillis();
            Log.d("Current frag", String.valueOf(currentFragment));

            if (currentTime - lastPressTime > DOUBLE_PRESS_EXIT_INTERVAL) {
                showToast("Press again to exit");
                lastPressTime = currentTime;
            } else {
                finishAffinity();
            }
        } else {
            super.onBackPressed();
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    //....Update the Toolbar................

    private void updateToolbarColor(Fragment fragment) {
        Log.d("current frag2",String.valueOf(fragment));
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (fragment instanceof NewsActivity || fragment instanceof ClubFragment) {
            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.backgroundcolor));
        } else {
            Log.d("current frag222",String.valueOf(fragment));

            toolbar.setBackgroundColor(ContextCompat.getColor(this, R.color.hmbg));
        }
    }

    public void openHomeSideActivity() {
        Intent settingsIntent = new Intent(HomeActivity.this, HomeSideActivity.class);
        startActivity(settingsIntent);
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }

//    @SuppressLint("RestrictedApi")
//    public void fetchUserData() {
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        if (currentUser != null) {
//            String userId = currentUser.getPhoneNumber();
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//            putUserData(userId);
//
//            db.collection("users")
//                    .whereEqualTo("Phone Number", userId)
//                    .get()
//                    .addOnCompleteListener(task -> {
//                        if (task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String, Object> dataMap = document.getData();
//                                String field1 = (String) dataMap.get("Phone Number");
//                                int a = (field1 != null) ? field1.compareTo(currentUser.getPhoneNumber()) : -1;
//
//                                if (a == 0) {
//                                    String userName = (String) dataMap.get("Name");
//                                    String userEmail = (String) dataMap.get("Email ID");
//                                    Boolean mcnVerified = (Boolean) dataMap.get("MCN verified");
//
//                                    // Store user data in SharedPreferences
//                                    SharedPreferences.Editor editor = getSharedPreferences("user_prefs", MODE_PRIVATE).edit();
//                                    editor.putString("userName", userName);
//                                    editor.putString("userEmail", userEmail);
//                                    editor.putBoolean("mcnVerified", mcnVerified != null && mcnVerified);
//                                    editor.apply();
//
//                                    // Obtain FCM token
//                                    FirebaseMessaging.getInstance().getToken()
//                                            .addOnCompleteListener(tokenTask -> {
//                                                if (tokenTask.isSuccessful()) {
//                                                    String fcmToken = tokenTask.getResult();
//                                                    // Upload FCM token to Firestore
//                                                    uploadFcmTokenToFirestore(userId, fcmToken);
//                                                } else {
//                                                    Log.e(TAG, "Failed to get FCM token: ", tokenTask.getException());
//                                                }
//                                            });
//
//                                    // Initialize views
//                                    verifiedUser = findViewById(R.id.verifieduser);
//                                    profileImageView = findViewById(R.id.circularImageView);
//
//                                    if (mcnVerified != null && mcnVerified) {
//                                        Preferences.userRoot().putBoolean("mcn_verified", true);
//                                        verifiedUser.setVisibility(View.VISIBLE);
//                                        profileImageView.setVisibility(View.GONE);
//                                        fetchUserProfileImageVerified(userId);
//                                    } else {
//                                        Preferences.userRoot().putBoolean("mcn_verified", false);
////                                        verifiedUser.setVisibility(View.GONE);
////                                        profileImageView.setVisibility(View.VISIBLE);
//                                        fetchUserProfileImage(userId);
//                                    }
//
//                                    // Fetch images (you may need to decide which one to fetch)
//                                    fetchUserProfileImage(userId);
//                                    fetchUserProfileImageVerified(userId);
//
//                                    // Update the UI based on user authentication status
//                                    runOnUiThread(() -> {
//                                        updateProfileUI(userName, userId, userEmail, mcnVerified != null && mcnVerified);
//                                    });
//                                }
//                            }
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                    });
//
////            new Handler(Looper.getMainLooper()).postDelayed(() -> {
////                if (!isFinishing()) {
////                    loadingCircle.setVisibility(View.GONE);
////                    opensidehomedrawer.setVisibility(View.VISIBLE);
////                }
////            }, 2000);
//        }
//    }

    @SuppressLint("RestrictedApi")
    private void uploadFcmTokenToFirestore(String userId, String fcmToken) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query Firestore to find the document for the current user
        db.collection("users")
                .whereEqualTo("Phone Number", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            // Update or create document with FCM token
                            Map<String, Object> data = new HashMap<>();
                            data.put("FCM Token", fcmToken);

                            // If document exists, update; otherwise, create new document
                            if (document.exists()) {
                                db.collection("users").document(document.getId())
                                        .update(data)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "FCM token updated"))
                                        .addOnFailureListener(e -> Log.e(TAG, "Error updating FCM token", e));
                            } else {
                                db.collection("users")
                                        .document()
                                        .set(data)
                                        .addOnSuccessListener(aVoid -> Log.d(TAG, "New user added with FCM token"))
                                        .addOnFailureListener(e -> Log.e(TAG, "Error adding new user", e));
                            }
                        }
                    } else {
                        Log.e(TAG, "Error querying Firestore for user document: ", task.getException());
                    }
                });
    }

    private void updateProfileUI(String userName, String userId, String userEmail, boolean mcnVerified) {
        if (mcnVerified) {
            verifiedUser.setVisibility(View.VISIBLE);
            profileImageView.setVisibility(View.GONE);
            fetchUserProfileImageVerified(userId);
        } else {
            verifiedUser.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            fetchUserProfileImage(userId);
        }

        // Set the user's name
//        personname.setText(userName);
    }

    private void putUserData(String phoneNumber) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            Query query = db.collection("users").whereEqualTo("Phone Number", phoneNumber);

            query.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        // Update the found document with the new user data
                        db.collection("users").document(document.getId())
                                .update("realtimeid", currentUser.getUid())
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "DocumentSnapshot updated successfully!"))
                                .addOnFailureListener(e -> Log.w("Firestore", "Error updating document", e));
                    }
                } else {
                    Log.w("Firestore", "Error getting documents.", task.getException());
                }
            });
        }
    }

    @SuppressLint("RestrictedApi")
    private void fetchUserProfileImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users").child(userId).child("profile_image.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error fetching profile image: " + exception.getMessage());
        });
    }

    @SuppressLint("RestrictedApi")
    private void fetchUserProfileImageVerified(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users").child(userId).child("profile_image.jpg");

        verifiedprofilebehere = findViewById(R.id.verifiedprofilebehere);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            if (verifiedprofilebehere != null) {
                Picasso.get().load(uri).into(verifiedprofilebehere);
            }
        }).addOnFailureListener(exception -> {
            Log.e(TAG, "Error fetching profile image: " + exception.getMessage());
        });
    }

    private void checkforAppUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                try {
                    appUpdateManager.startUpdateFlowForResult(
                            // Pass the intent that is returned by 'getAppUpdateInfo()'.
                            appUpdateInfo,
                            // an activity result launcher registered via registerForActivityResult
                            AppUpdateType.IMMEDIATE,
                            // Or pass 'AppUpdateType.FLEXIBLE' to newBuilder() for
                            // flexible updates.
                            this, MY_UPDATE_CODE);
                } catch (IntentSender.SendIntentException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_UPDATE_CODE) {
            if (resultCode != RESULT_OK) {
                Log.w("FirstActivity", "Update flow failed! Result code : " + resultCode);
            }
        }
    }


}


//    @SuppressLint("RestrictedApi")
//    private void updateGreetingAndName() {
//        // Get the current time to determine the greeting
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//
//        if (hour >= 0 && hour < 12) {
//            greetingTextView.setText("Good Morning!");
//        } else if (hour >= 12 && hour < 16) {
//            greetingTextView.setText("Good Afternoon!");
//        } else {
//            greetingTextView.setText("Good Evening!");
//        }
//
//        // Retrieve user information from SharedPreferences
//        SharedPreferences preferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
//        String userName = preferences.getString("userName", null);
//        String userPrefix = preferences.getString("userPrefix", "");
//        boolean mcnVerified = preferences.getBoolean("mcnVerified", false);
//
//        if (userName != null) {
//            // Use stored user data
//            personname.setText(userPrefix + " " + userName);
//            updateProfileUI(userName, userPrefix, null, mcnVerified);
//        } else {
//            // Retrieve user information from Firebase Firestore
//            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//            if (currentUser != null) {
//                String userId = currentUser.getPhoneNumber();
//                FirebaseFirestore db = FirebaseFirestore.getInstance();
//                db.collection("users")
//                        .whereEqualTo("Phone Number", userId)
//                        .get()
//                        .addOnCompleteListener(task -> {
//                            if (task.isSuccessful() && task.getResult() != null) {
//                                for (QueryDocumentSnapshot document : task.getResult()) {
//                                    Map<String, Object> dataMap = document.getData();
//                                    String fetchedUserName = (String) dataMap.get("Name");
//                                    String fetchedUserPrefix = (String) dataMap.get("Prefix");
//                                    Boolean fetchedMcnVerified = (Boolean) dataMap.get("MCN verified");
//
//                                    // Store fetched user data in SharedPreferences
//                                    SharedPreferences.Editor editor = preferences.edit();
//                                    editor.putString("userName", fetchedUserName);
//                                    editor.putString("userPrefix", fetchedUserPrefix);
//                                    editor.putBoolean("mcnVerified", fetchedMcnVerified != null && fetchedMcnVerified);
//                                    editor.apply();
//
//                                    personname.setText(fetchedUserPrefix + " " + fetchedUserName);
//                                    updateProfileUI(fetchedUserName, fetchedUserPrefix, null, fetchedMcnVerified != null && fetchedMcnVerified);
//                                }
//                            } else {
//                                Log.d(TAG, "Error getting documents: ", task.getException());
//                            }
//                        });
//            }
//        }
//    }


