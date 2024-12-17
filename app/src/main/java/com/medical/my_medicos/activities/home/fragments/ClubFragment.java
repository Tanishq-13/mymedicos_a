package com.medical.my_medicos.activities.home.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.exclusive.exclusivehome;
import com.medical.my_medicos.activities.home.exclusive.faculty.CoursesFragment;
import com.medical.my_medicos.activities.home.sidedrawer.SettingsActivity;
import com.medical.my_medicos.activities.login.GetstartedActivity;
import com.medical.my_medicos.activities.profile.Contactinfo;
import com.medical.my_medicos.activities.profile.Personalinfo;
import com.medical.my_medicos.activities.pg.activites.extras.CreditsActivity;
import com.medical.my_medicos.activities.home.sidedrawer.extras.BottomSheetForChatWithUs;
import com.medical.my_medicos.activities.home.sidedrawer.extras.BottomSheetForCommunity;
import com.squareup.picasso.Picasso;

import java.util.Map;

public class ClubFragment extends Fragment {

    private static final String TAG = "HomeSideFragment";
    private static final String DATA_LOADED_KEY = "data_loaded";
    private boolean dataLoaded = false;

    private LottieAnimationView verified;
    private LinearLayout progressBar;

    private CoursesFragment.CourseCheckListener listener;
    private CardView  contactinfo, verified_contact_info;
    private TextView user_name_dr, user_email_dr, user_phone_dr, user_location_dr, user_interest_dr, user_prefix;
    private ImageView profileImageView, verifiedprofilebehere;
    private FrameLayout verifiedUser, circularImageView;
    private LinearLayout intenttoaboutme, totheguide;
    private Toolbar toolbar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_club, container, false);

        setupViews(rootView);
        setupToolbar(rootView);
        setupWindowColors();

        if (savedInstanceState != null) {
            dataLoaded = savedInstanceState.getBoolean(DATA_LOADED_KEY, false);
        }

        if (!dataLoaded) {
            fetchUserData();
        }

        return rootView;
    }

    private void setupViews(View rootView) {
        progressBar = rootView.findViewById(R.id.progressBar);
        verified_contact_info = rootView.findViewById(R.id.verified_contact_info);

//        personalinfo = rootView.findViewById(R.id.contact_info);
//        personalinfo.setOnClickListener(view -> openActivity(Personalinfo.class));

//        intenttocredit = rootView.findViewById(R.id.intenttocredit);
//        intenttocredit.setOnClickListener(view -> openActivity(CreditsActivity.class));

        contactinfo = rootView.findViewById(R.id.personal_info);
        contactinfo.setOnClickListener(view -> openActivity(Contactinfo.class));

        intenttoaboutme = rootView.findViewById(R.id.intenttoaboutme);
        intenttoaboutme.setOnClickListener(view -> openActivity(Contactinfo.class));

        CardView logout = rootView.findViewById(R.id.logout);
        logout.setOnClickListener(view -> showLogoutConfirmationDialog());

        LinearLayout settingsbtn = rootView.findViewById(R.id.settings);
        settingsbtn.setOnClickListener(view -> openActivity(SettingsActivity.class));

        LinearLayout sharebtn = rootView.findViewById(R.id.refer);
        sharebtn.setOnClickListener(view -> shareApp());

        LinearLayout whatsappLayout = rootView.findViewById(R.id.whatsapp);
        whatsappLayout.setOnClickListener(view -> openBottomSheet(new BottomSheetForChatWithUs()));

        LinearLayout communityjoinLayout = rootView.findViewById(R.id.communityjoin);
        communityjoinLayout.setOnClickListener(view -> openBottomSheet(new BottomSheetForCommunity()));

        user_name_dr = rootView.findViewById(R.id.user_name_dr);
        user_email_dr = rootView.findViewById(R.id.user_email_dr);
        user_phone_dr = rootView.findViewById(R.id.user_phone_dr);
        user_location_dr = rootView.findViewById(R.id.user_location_dr);
        user_interest_dr = rootView.findViewById(R.id.user_interest_dr);
        profileImageView = rootView.findViewById(R.id.circularImageView);
        user_prefix = rootView.findViewById(R.id.prefixselecterfromuser);
        //user_credit = rootView.findViewById(R.id.currentcoinsprofile);
        verifiedprofilebehere = rootView.findViewById(R.id.verifiedprofilebehere);
        verifiedUser = rootView.findViewById(R.id.verifieduser);
        verified = rootView.findViewById(R.id.verifiedanime);
    }

    private void setupToolbar(View rootView) {
        toolbar = rootView.findViewById(R.id.profiletoolbar);
        // Toolbar configuration if needed
    }

    private void setupWindowColors() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = requireActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = requireActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.backgroundcolor));
            window.setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.backgroundcolor));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            View decorView = requireActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private void openActivity(Class<?> activityClass) {
        Intent intent = new Intent(requireContext(), activityClass);
        startActivity(intent);
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout Confirmation");
        builder.setMessage("Are you sure you want to logout?");
        builder.setPositiveButton("Confirm", (dialogInterface, i) -> logoutUser());
        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void openBottomSheet(BottomSheetDialogFragment bottomSheetFragment) {
        bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
    }

    private void shareApp() {
        String appLink = "https://play.google.com/store/apps/details?id=com.medical.my_medicos&pcampaignid=web_share";
        String message = "Check out our medical app!\nDownload now: " + appLink;

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        if (shareIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    private void logoutUser() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(requireContext(), GetstartedActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private void fetchUserData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String currentUserPhoneNumber = currentUser.getPhoneNumber();
            SharedPreferences preferences = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

            // Check if user data is already cached in SharedPreferences
            if (preferences.contains("username")) {
                // Data is cached, use it to populate the UI
                populateUIWithCachedData(preferences);
            } else {
                // Data is not cached, fetch from Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                db.collection("users")
                        .whereEqualTo("Phone Number", currentUserPhoneNumber)
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Map<String, Object> dataMap = document.getData();

                                    // Extract user details from the document
                                    String userName = (String) dataMap.get("Name");
                                    String userEmail = (String) dataMap.get("Email ID");
                                    String userLocation = (String) dataMap.get("Location");
                                    String userInterest = (String) dataMap.get("Interest");
                                    String userPhone = (String) dataMap.get("Phone Number");
                                    String userPrefix = (String) dataMap.get("Prefix");
                                    Boolean mcnVerified = (Boolean) dataMap.get("MCN verified");

                                    // Save the details in SharedPreferences
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("username", userName);
                                    editor.putString("email", userEmail);
                                    editor.putString("location", userLocation);
                                    editor.putString("interest", userInterest);
                                    editor.putString("userphone", userPhone);
                                    editor.putString("prefix", userPrefix);
                                    editor.putBoolean("mcn_verified", mcnVerified != null && mcnVerified);
                                    editor.apply();

                                    // Update UI with the fetched data
                                    populateUI(userName, userEmail, userLocation, userInterest, userPhone, userPrefix, mcnVerified);

                                    // Data is loaded, hide progress bar
                                    dataLoaded = true;
                                    hideProgressBar();
                                }
                            } else {
                                // Handle the case where no matching document was found or task failed
                                hideProgressBar();  // Hide progress bar even if no data is found
                            }
                        })
                        .addOnFailureListener(e -> {
                            // Handle any errors that occurred during the query
                            hideProgressBar();  // Hide progress bar if there's an error
                        });
            }
        } else {
            // Handle the case where currentUser is null (user is not logged in)
            hideProgressBar();  // Hide progress bar if no user is logged in
        }
    }

    private void populateUIWithCachedData(SharedPreferences preferences) {
        String userName = preferences.getString("username", "Loading...");
        String userEmail = preferences.getString("email", "Loading...");
        String userLocation = preferences.getString("location", "Loading...");
        String userInterest = preferences.getString("interest", "Loading...");
        String userPhone = preferences.getString("userphone", "Loading...");
        String userPrefix = preferences.getString("prefix", "Dr.");
        boolean mcnVerified = preferences.getBoolean("mcn_verified", false);

        populateUI(userName, userEmail, userLocation, userInterest, userPhone, userPrefix, mcnVerified);

        // Data is loaded, hide progress bar
        dataLoaded = true;
        hideProgressBar();
    }

    private void populateUI(String userName, String userEmail, String userLocation, String userInterest,
                            String userPhone, String userPrefix, Boolean mcnVerified) {
        user_name_dr.setText(userName);
        user_email_dr.setText(userEmail);
        user_location_dr.setText(userLocation);
        user_interest_dr.setText(userInterest);
        user_phone_dr.setText(userPhone);
        user_prefix.setText(userPrefix);

        if (mcnVerified != null && mcnVerified) {
            verifiedUser.setVisibility(View.VISIBLE);
            profileImageView.setVisibility(View.GONE);
            verified_contact_info.setVisibility(View.VISIBLE);
//            personalinfo.setVisibility(View.GONE);
            fetchUserProfileImageVerified(userPhone);
        } else {
            verifiedUser.setVisibility(View.GONE);
            profileImageView.setVisibility(View.VISIBLE);
            verified_contact_info.setVisibility(View.GONE);
//            personalinfo.setVisibility(View.VISIBLE);
            fetchUserProfileImage(userPhone);
        }
    }

    private void fetchUserProfileImage(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users").child(userId).child("profile_image.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(profileImageView);
        }).addOnFailureListener(exception -> {
            // Handle error
        });
    }

    private void fetchUserProfileImageVerified(String userId) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference().child("users").child(userId).child("profile_image.jpg");
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Picasso.get().load(uri).into(verifiedprofilebehere);
        }).addOnFailureListener(exception -> {
            // Handle error
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DATA_LOADED_KEY, dataLoaded);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle back button press
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to HomeFragment
                navigateToHomeFragment();
            }
        });
    }

    private void navigateToHomeFragment() {
        // Replace CubFragment with HomeFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new exclusivehome());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}
