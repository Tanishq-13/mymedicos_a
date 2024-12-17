package com.medical.my_medicos.activities.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.medical.my_medicos.activities.home.exclusive.exclusivehome;
import com.medical.my_medicos.activities.login.bottom_controls.PrivacyPolicyActivity;
import com.medical.my_medicos.activities.login.bottom_controls.TermsandConditionsActivity;

public class GetstartedActivity extends AppCompatActivity {

    TextView startButton;
    LinearLayout parttoshow;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_getstarted);

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

        if (isLoggedIn()) {
            Intent intent = new Intent(GetstartedActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        parttoshow = findViewById(R.id.parttoshow);
        parttoshow.setVisibility(View.GONE);

        TextView textView = findViewById(R.id.textforcoats);
        String htmlText = "<strong>Your all-in-one platform for medical students and professionals. \n" + "Let's get started!<strong>";
        textView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));

        TextView textView1 = findViewById(R.id.help_support);
        String htmlText1 = "Having trouble? <strong><u>Reach out</u></strong>";
        textView1.setText(Html.fromHtml(htmlText1, Html.FROM_HTML_MODE_LEGACY));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fadeInAnimation(parttoshow);
                parttoshow.setVisibility(View.VISIBLE);
                // Play sound effect when parttoshow LinearLayout becomes visible
            }
        }, 3000);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GetstartedActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        TextView help_support = findViewById(R.id.help_support);
        help_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
    }

    private void fadeInAnimation(View view) {
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        view.startAnimation(fadeIn);
    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(GetstartedActivity.this);
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

    private boolean isLoggedIn() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        return mAuth.getCurrentUser() != null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
