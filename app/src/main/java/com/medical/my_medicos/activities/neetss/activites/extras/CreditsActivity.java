package com.medical.my_medicos.activities.neetss.activites.extras;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.publications.activity.PaymentPublicationActivity;
import com.medical.my_medicos.activities.utils.ConstantsDashboard;
import com.medical.my_medicos.databinding.ActivityCreditsBinding;

import org.json.JSONObject;
public class CreditsActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "MyPrefs";
    private static final String LAST_CLICK_TIME_VIDEO_1 = "lastClickTimeVideo1";
    private static final String LAST_CLICK_TIME_VIDEO_2 = "lastClickTimeVideo2";
    private static final long COOLDOWN_PERIOD = 24 * 60 * 60 * 1000;
    ActivityCreditsBinding binding;
    private RewardedAd mRewardedAd;
    AlertDialog alertDialog;
    FirebaseDatabase database;
    String currentUid;
    String phoneNumber;
    private Context context;
    int coins = 300;
    private ImageView backtothehomesideactivity;
    private int previousCoinCount = 0;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Processing...");

        backtothehomesideactivity = findViewById(R.id.backtothehomesideactivity);
        backtothehomesideactivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CreditsActivity.this, HomeActivity.class);
                startActivity(i);
            }
        });

        context = CreditsActivity.this;
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        currentUid = FirebaseAuth.getInstance().getUid();

        if (currentUser != null) {
            phoneNumber = currentUser.getPhoneNumber();
        }
        loadAd();

        database.getReference().child("profiles")
                .child(phoneNumber)
                .child("coins")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        Integer coinsValue = snapshot.getValue(Integer.class);
                        if (coinsValue != null) {
                            coins = coinsValue;
                            binding.currentcoins.setText(String.valueOf(coins));
                        } else {
                            coins = 0;
                            binding.currentcoins.setText("0");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });

        binding.viewad1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to check if the user can click the video
                checkVideoClickEligibility("video1");
            }
        });
        binding.viewad2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call the method to check if the user can click the video
                checkVideoClickEligibility("video2");
            }
        });

        binding.puchase100credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet99();
            }
        });
        binding.puchase250credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet129();
            }
        });
        binding.puchase500credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheet199();
            }
        });
        TextView currentCoinsTextView = findViewById(R.id.currentcoins);
        int currentCoins = Integer.parseInt(currentCoinsTextView.getText().toString());

        int increase = currentCoins - previousCoinCount;
        if (increase == 150) {
            // Display the custom popup
            showCustomPopup();
        }

        previousCoinCount = currentCoins;

        configureWindow();

    }

    private void configureWindow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = getWindow();
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(this, R.color.backgroundcolor));
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void checkVideoClickEligibility(String videoName) {
        DatabaseReference videoRef = FirebaseDatabase.getInstance().getReference()
                .child("profiles")
                .child(phoneNumber);

        videoRef.child(videoName + "_last_watched_time").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    long lastClickTimestamp = dataSnapshot.getValue(Long.class);
                    long currentTime = System.currentTimeMillis();
                    long twentyFourHoursInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

                    if ((currentTime - lastClickTimestamp) >= twentyFourHoursInMillis) {
                        // 24 hours have passed since the last click, user can click the ad
                        enableButtonAndShowAd(videoName);
                    } else {
                        // Less than 24 hours have passed, disable the button and show a dialog
                        disableButtonAndShowDialog(videoName);
                    }
                } else {
                    // No last watched time found, assuming it's the first click
                    enableButtonAndShowAd(videoName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors if needed
            }
        });
    }

    private void enableButtonAndShowAd(String videoName) {
        // Enable the button
        if (videoName.equals("video1")) {
            binding.viewad1.setEnabled(true);
        } else if (videoName.equals("video2")) {
            binding.viewad2.setEnabled(true);
        }

        // Show the rewarded ad
        showRewardedAd(videoName);
    }

    private void disableButtonAndShowDialog(String videoName) {
        // Disable the button
        if (videoName.equals("video1")) {
            binding.viewad1.setEnabled(false);
        } else if (videoName.equals("video2")) {
            binding.viewad2.setEnabled(false);
        }

        // Show a dialog
        showDialog("Free Redeem Coins will be available after 24 hrs");
    }

    private void showRewardedAd(String videoName) {
        // Show the progress dialog
        progressDialog.setMessage("Loading ad...");
        progressDialog.show();

        // Set a timeout to dismiss the dialog and show a toast after 10 seconds
        new Handler().postDelayed(() -> {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
                Toast.makeText(CreditsActivity.this, "This issue might have arised due to some device issue. Check your internet connection or for any active ad blocker.", Toast.LENGTH_LONG).show();
            }
        }, 10000);

        if (mRewardedAd != null) {
            Activity activityContext = CreditsActivity.this;

            mRewardedAd.show(activityContext, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    // Dismiss the progress dialog when the ad is loaded
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    loadAd();
                    coins += (videoName.equals("video1") ? 10 : 20);
                    updateCoinsInDatabase(coins);
                    updateVideoWatchedStatus(videoName); // Update Realtime Database
                    handleRewardedAdCompletion();
                    showDialog("MedCoins Credited: " + (videoName.equals("video1") ? 10 : 20));
                }
            });

        } else {
            Log.e("Something went wrong", "Error");
        }
    }

    // Add this method to update the Realtime Database with video watched status
    private void updateVideoWatchedStatus(String videoName) {
        if (phoneNumber != null) {
            database.getReference().child("profiles")
                    .child(phoneNumber)
                    .child(videoName + "_last_watched_time")
                    .setValue(System.currentTimeMillis());
        }
    }

    private void handleRewardedAdCompletion() {
        loadAd();
        int updatedCoins = Integer.parseInt(binding.currentcoins.getText().toString());
        Log.d(TAG, "Updated Coins Value: " + updatedCoins);
    }

    private void updateVideoStatus(String currentUid, String videoName) {
        if (phoneNumber != null) {
            DatabaseReference videoRef = database.getReference().child("profiles").child(phoneNumber).child(videoName);

            videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Boolean isWatched = snapshot.getValue(Boolean.class);
                    if (isWatched != null && isWatched && isWatched != false) {
                        // Check the last watched time
                        DatabaseReference lastWatchedTimeRef = database.getReference().child("profiles").child(phoneNumber).child(videoName + "_last_watched_time");

                        lastWatchedTimeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot timeSnapshot) {
                                Long lastWatchedTime = timeSnapshot.getValue(Long.class);

                                if (lastWatchedTime != null) {
                                    long currentTime = System.currentTimeMillis();
                                    long timeDifference = currentTime - lastWatchedTime;
                                    long twentyFourHoursInMillis = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

                                    if (timeDifference >= twentyFourHoursInMillis) {
                                        // Video watched time is older than 24 hours, update video status to false
                                        videoRef.setValue(false);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                // Handle database error for last watched time
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle database error for video status
                }
            });
        }
    }

    private boolean isVideoAlreadyWatched(String videoName) {
        final boolean[] isVideoWatched = {true};
        if (phoneNumber != null) {
            DatabaseReference videoRef = database.getReference().child("profiles")
                    .child(phoneNumber)
                    .child(videoName);

            videoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Object value = snapshot.getValue();
                    String stringValue = value.toString();
                    Boolean isWatched = snapshot.getValue(Boolean.class);
                    Log.d("credits5", stringValue);

                    if (stringValue.equals("true")) {
                        // Video has already been watched
                        Toast.makeText(CreditsActivity.this, "You have already watched this video.", Toast.LENGTH_SHORT).show();
                        isVideoWatched[0] = false;
                        Log.d("credits5", "new5");
                        Log.d("credits5", String.valueOf(isVideoWatched));
                        Log.d("credits5", stringValue);
                    } else {
                        // Video variable not present or not watched
                        // You can handle this case accordingly
                        isVideoWatched[0] = true;
                        Log.d("credits51", String.valueOf(isVideoWatched[0]));
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }

        return isVideoWatched[0];
    }

    private void enableButtonAfterCooldown() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                binding.viewad1.setEnabled(true);
            }
        }, COOLDOWN_PERIOD);
    }

    private boolean canClickVideo(String videoName) {
        if (!isVideoAlreadyWatched(videoName)) {
            Log.d("credits512", String.valueOf(isVideoAlreadyWatched(videoName)));
            return false;
        }

        Log.d("credits512", String.valueOf(isVideoAlreadyWatched(videoName)));
        return true;
    }

    void loadAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        RewardedAd.load(this, "ca-app-pub-1452770494559845/3094113721",
                adRequest, new RewardedAdLoadCallback() {
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        mRewardedAd = null;
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }

                    @Override
                    public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                        mRewardedAd = rewardedAd;
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                    }
                });
    }

    private void updateCoinsInDatabase(int updatedCoins) {
        database.getReference().child("profiles")
                .child(phoneNumber)
                .child("coins")
                .setValue(updatedCoins);
    }

    private void showBottomSheet99() {
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_for_payment, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheetView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView textClickMe = bottomSheetView.findViewById(R.id.paymentpartcredit);

        textClickMe.setOnClickListener(v -> {
            processCreditsOrderPackage1();
        });
        bottomSheetDialog.show();
    }

    private void showBottomSheet129() {
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_250, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheetView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView textClickMe = bottomSheetView.findViewById(R.id.paymentpartcredit129);

        textClickMe.setOnClickListener(v -> {
            processCreditsOrderPackage2();
        });
        bottomSheetDialog.show();
    }

    private void showBottomSheet199() {
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_500, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(bottomSheetView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"})
        TextView textClickMe = bottomSheetView.findViewById(R.id.paymentpartcredit199);

        textClickMe.setOnClickListener(v -> {
            processCreditsOrderPackage3();
        });
        bottomSheetDialog.show();
    }

    void processCreditsOrderPackage1() {
        progressDialog.show();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            usersRef.whereEqualTo("Phone Number", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                RequestQueue queue = Volley.newRequestQueue(this);

                                String url = ConstantsDashboard.GET_ORDER_ID_99_41 + userId + "/" + "package1";
                                Log.d("API Request URL", url);

                                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                                    try {
                                        Log.d("API Response", response);
                                        JSONObject requestBody = new JSONObject(response);
                                        if (requestBody.getString("status").equals("success")) {
                                            Toast.makeText(CreditsActivity.this, "Success order.", Toast.LENGTH_SHORT).show();
                                            String orderNumber = requestBody.getString("order_id");
                                            Log.e("Order ID check", orderNumber);
                                            new AlertDialog.Builder(CreditsActivity.this)
                                                    .setTitle("Order Successful")
                                                    .setCancelable(false)
                                                    .setMessage("Your order number is: " + orderNumber)
                                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                                        Intent intent = new Intent(CreditsActivity.this, PaymentPublicationActivity.class);
                                                        intent.putExtra("orderCode", orderNumber);
                                                        startActivity(intent);
                                                    }).show();
                                        } else {
                                            Toast.makeText(CreditsActivity.this, "Failed order.", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                        Log.e("res", response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {

                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(CreditsActivity.this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();

                                });

                                queue.add(request);
                            }
                        } else {
                            progressDialog.dismiss();
                            Toast.makeText(CreditsActivity.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(CreditsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    void processCreditsOrderPackage2() {
        progressDialog.show();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            usersRef.whereEqualTo("Phone Number", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                RequestQueue queue = Volley.newRequestQueue(this);

                                String url = ConstantsDashboard.GET_ORDER_ID_99_41 + userId + "/" + "package2";
                                Log.d("API Request URL", url);

                                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                                    try {
                                        Log.d("API Response", response);
                                        JSONObject requestBody = new JSONObject(response);

                                        if (requestBody.getString("status").equals("success")) {
                                            Toast.makeText(CreditsActivity.this, "Success order.", Toast.LENGTH_SHORT).show();
                                            String orderNumber = requestBody.getString("order_id");
                                            Log.e("Order ID check", orderNumber);
                                            new AlertDialog.Builder(CreditsActivity.this)
                                                    .setTitle("Order Successful")
                                                    .setCancelable(false)
                                                    .setMessage("Your order number is: " + orderNumber)
                                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                                        Intent intent = new Intent(CreditsActivity.this, PaymentPublicationActivity.class);
                                                        intent.putExtra("orderCode", orderNumber);
                                                        startActivity(intent);
                                                    }).show();
                                        } else {
                                            Toast.makeText(CreditsActivity.this, "Failed order.", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                        Log.e("res", response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {
                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(CreditsActivity.this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                                queue.add(request);
                            }
                        } else {
                            // Handle the error when the document is not found
                            progressDialog.dismiss();
                            Toast.makeText(CreditsActivity.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(CreditsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    void processCreditsOrderPackage3() {
        progressDialog.show();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            CollectionReference usersRef = db.collection("users");

            usersRef.whereEqualTo("Phone Number", userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                RequestQueue queue = Volley.newRequestQueue(this);

                                String url = ConstantsDashboard.GET_ORDER_ID_99_41 + userId + "/" + "package3";
                                Log.d("API Request URL", url);

                                StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
                                    try {
                                        Log.d("API Response", response); // Log the API response

                                        JSONObject requestBody = new JSONObject(response);
                                        if (requestBody.getString("status").equals("success")) {
                                            // Your existing logic for processing the order
                                            Toast.makeText(CreditsActivity.this, "Success order.", Toast.LENGTH_SHORT).show();
                                            String orderNumber = requestBody.getString("order_id");
                                            Log.e("Order ID check", orderNumber);
                                            new AlertDialog.Builder(CreditsActivity.this)
                                                    .setTitle("Order Successful")
                                                    .setCancelable(false)
                                                    .setMessage("Your order number is: " + orderNumber)
                                                    .setPositiveButton("Pay Now", (dialogInterface, i) -> {
                                                        Intent intent = new Intent(CreditsActivity.this, PaymentPublicationActivity.class);
                                                        intent.putExtra("orderCode", orderNumber);
                                                        startActivity(intent);
                                                    }).show();
                                        } else {
                                            // Your existing logic for handling a failed order
                                            Toast.makeText(CreditsActivity.this, "Failed order.", Toast.LENGTH_SHORT).show();
                                        }
                                        progressDialog.dismiss();
                                        Log.e("res", response);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }, error -> {
                                    // Handle Volley error
                                    error.printStackTrace();
                                    progressDialog.dismiss();
                                    Toast.makeText(CreditsActivity.this, "Volley Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                });

                                queue.add(request);
                            }
                        } else {
                            // Handle the error when the document is not found
                            progressDialog.dismiss();
                            Toast.makeText(CreditsActivity.this, "Failed to retrieve user information", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressDialog.dismiss();
            Toast.makeText(CreditsActivity.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void showCustomPopup() {
        final Dialog customDialog = new Dialog(this);
        customDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDialog.setContentView(R.layout.custom_popup);
        TextView closeButton = customDialog.findViewById(R.id.closebtnforthecreditmessage99);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDialog.dismiss();
            }
        });

        customDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                customDialog.dismiss();
            }
        }, 3000);
    }

    private void showDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreditsActivity.this, R.style.CustomAlertDialog);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.custom_out_ofmoves_dialogue, null);
        builder.setView(dialogView);

        TextView dialogMessage = dialogView.findViewById(R.id.messageforout);
        dialogMessage.setText(message);

        LottieAnimationView lottieAnimationView = dialogView.findViewById(R.id.correctanswer);
        lottieAnimationView.setAnimation(R.raw.asorryforcredits);
        lottieAnimationView.playAnimation();

        TextView okButton = dialogView.findViewById(R.id.okbtnreplacer);
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        CardView packageButton = dialogView.findViewById(R.id.tothepackage);
        packageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                showBottomSheet129();
            }
        });

        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        alertDialog = dialog;
    }
}
