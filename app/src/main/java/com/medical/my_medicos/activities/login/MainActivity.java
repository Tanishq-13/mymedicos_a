package com.medical.my_medicos.activities.login;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.hbb20.CountryCodePicker;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.home.exclusive.exclusivehome;
import com.medical.my_medicos.activities.login.bottom_controls.PrivacyPolicyActivity;
import com.medical.my_medicos.activities.login.bottom_controls.TermsandConditionsActivity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    EditText phone;
    TextView login;
    String selectedCountryCode;
    private Dialog mdialog;
    Toolbar toolbar;
    FirebaseAuth mauth;
    CountryCodePicker countryCodePicker ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupUI();
        setupAdMob();
        setupPhoneNumberInput();
        setupLoginButton();

        // Add the setup for toolbar_help click
        ImageView toolbarHelp = findViewById(R.id.toolbar_help);
        ImageView backArrow= findViewById(R.id.toolbar_back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GetstartedActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            }
        });
        toolbarHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
        countryCodePicker = findViewById(R.id.countryCodePicker);
        countryCodePicker.setDefaultCountryUsingNameCode("IN");

        TextView textView2 = findViewById(R.id.textidforquote);
        String htmlText2 = "<strong>India’s</strong> first premier <strong>medical community</strong> app, connecting healthcare experts seamlessly.";
        textView2.setText(Html.fromHtml(htmlText2, Html.FROM_HTML_MODE_LEGACY));

    }

    private void setupUI() {
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
    }

    private void setupAdMob() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
    }

    private void setupPhoneNumberInput() {
        phone = findViewById(R.id.phonenumberedit);
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed for this implementation
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Not needed for this implementation
            }

            @Override
            public void afterTextChanged(Editable s) {
                String selectedCountryCode = countryCodePicker.getSelectedCountryCodeWithPlus();

                // Enable the login button only if the phone number is valid
                if (selectedCountryCode.equals("+91")) {
                    // For India, the phone number must be exactly 10 digits
                    if (s.length() == 10) {
                        login.setEnabled(true);
                        login.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));
                    } else {
                        login.setEnabled(false);
                        login.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.grey));
                    }
                } else {
                    // For other country codes, enable the button without length check
                    login.setEnabled(true);
                    login.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));
                }
//                // Check if the text length is exactly 10
////                if (s.length() == 10) {
//                    login.setEnabled(true);
//                    login.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.unselected));
//////                } else {
////                    login.setEnabled(false);
////                    login.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.grey));
//////                }
            }
        });
    }

    private void setupLoginButton() {
        login = findViewById(R.id.lgn_btn);
        mdialog = new Dialog(this);
        mauth = FirebaseAuth.getInstance();


        login.setBackgroundColor(ContextCompat.getColor(this, R.color.grey));

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = phone.getText().toString().trim();

                if (TextUtils.isEmpty(phoneNumber)) {
                    phone.setError("Phone Number Required");
                    return;
                }

                selectedCountryCode = countryCodePicker.getSelectedCountryCode();

                phoneNumber = "+"+selectedCountryCode + phoneNumber; // Add static "+91" prefix
                Log.d("PhoneNumberofmobile",phoneNumber);

                checkIfUserExists(phoneNumber);
            }
        });
    }

    private void checkIfUserExists(String phoneNumber) {
        if (TextUtils.isEmpty(phoneNumber)) {
            Toast.makeText(MainActivity.this, "Phone Number Required", Toast.LENGTH_SHORT).show();
            return;
        }
        db.collection("users")
                .whereEqualTo("Phone Number", phoneNumber)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult() != null && !task.getResult().isEmpty()) {
                                showCustomProgressDialog("Checking...");
                                mdialog.show();

                                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                        phoneNumber,
                                        0L,
                                        TimeUnit.SECONDS,
                                        MainActivity.this,
                                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                            @Override
                                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                                signInWithPhoneAuthCredential(phoneAuthCredential);
                                            }

                                            @Override
                                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                mdialog.dismiss();
                                            }

                                            @Override
                                            public void onCodeSent(@NonNull String verificationId,
                                                                   @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                mdialog.dismiss();
                                                Intent intent = new Intent(MainActivity.this, EnterOtp.class);
                                                intent.putExtra("phone", phoneNumber);
                                                intent.putExtra("verificationId", verificationId);
                                                Log.e("otp sent", verificationId);
                                                startActivity(intent);
                                            }
                                        }
                                );
                            } else {
                                Toast.makeText(MainActivity.this, "Not Registered", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                                // Pass the trimmed phone number (without the country code) to RegistrationActivity
                                String trimmedPhoneNumber = phone.getText().toString().trim();
                                intent.putExtra("phone", trimmedPhoneNumber);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Error checking user registration", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void showCustomProgressDialog(String message) {
        if (mdialog != null && mdialog.isShowing()) {
            mdialog.dismiss();
        }

        LayoutInflater inflater = getLayoutInflater();
        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.custom_dialogue, null);
        LottieAnimationView lottieAnimationView = view.findViewById(R.id.lottieAnimationView);
        TextView progressText = view.findViewById(R.id.progressText);
        progressText.setText(message);

        mdialog = new Dialog(this);
        mdialog.setContentView(view);
        mdialog.setCancelable(false);
        mdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mdialog.show();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mauth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (mauth.getCurrentUser().isEmailVerified()) {
                                setLoggedIn(true);
                                Toast.makeText(getApplicationContext(), "Login Successful", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(MainActivity.this, exclusivehome.class);
                                startActivity(i);
                                finish();
                                mdialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(), "Please Verify your Email Address, For safe Login", Toast.LENGTH_SHORT).show();
                                mdialog.dismiss();
                            }
                        } else {
                            mdialog.dismiss();
                        }
                    }
                });
    }

    private void setLoggedIn(boolean loggedIn) {
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_logged_in", loggedIn);
        editor.apply();
    }

    private boolean isLoggedIn() {
        SharedPreferences preferences = getSharedPreferences("user_data", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_logged_in", false);
    }

    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_login_layout, null);
        bottomSheetView.findViewById(R.id.check_mail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendEmail();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.check_privacy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PrivacyPolicyActivity.class);
                v.getContext().startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetView.findViewById(R.id.check_terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TermsandConditionsActivity.class);
                v.getContext().startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void sendEmail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:")); // only email apps should handle this
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@mymedicos.in"});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Facing issue in {Problem here}");

        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(emailIntent);
        }
    }
}
