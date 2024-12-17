package com.medical.my_medicos.activities.login.bottom_controls;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.medical.my_medicos.R;

public class PrivacyPolicyActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);

        setupUI();

        WebView webView = findViewById(R.id.privacy_policy_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        String htmlContent = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Privacy Policy</title>" +
                "<style>" +
                "body { font-family: inter, sans-serif; line-height: 1.6; color: #333; margin: 20px; padding: 0; background: #F5F5F5 }" +
                "h1 { text-align: center; color: #353535; }" +
                "h2 { color: #353535; border-bottom: 1px solid #ccc; padding-bottom: 5px; }" +
                "p { margin-bottom: 15px; }" +
                "ul { list-style-type: none; padding-left: 0; }" +
                "ul li { margin-bottom: 10px; }" +
                ".section-header { font-weight: bold; margin-top: 20px; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h1>Privacy Policy</h1>" +
                "<p>Last updated on April 17th 2024</p>" +
                "<p>Thank you for using mymedicos, a mobile application designed to provide educational updates and verification services. Your privacy and security are important to us. This Privacy Policy outlines the information we collect, how we use it, and the choices you have regarding your information.</p>" +
                "<h2>1. Information We Collect</h2>" +
                "<ul>" +
                "<li class=\"section-header\">1.1 Personal Information:</li>" +
                "<p>When you register for an account on mymedicos, we may collect personal information such as your name, email address, and mobile phone number. This information is collected for verification purposes and to enhance your user experience.</p>" +
                "<li class=\"section-header\">1.2 Verification Information:</li>" +
                "<p>In order to verify your identity, we may collect and process additional information such as your mobile phone number. This information is used solely for verification purposes and is securely stored.</p>" +
                "<li class=\"section-header\">1.3 Usage Information:</li>" +
                "<p>We may collect information about how you interact with the mymedicos app, including the pages you visit, the features you use, and the actions you take. This information helps us improve the app and provide a better user experience.</p>" +
                "</ul>" +
                "<h2>2. Use of Information</h2>" +
                "<ul>" +
                "<li class=\"section-header\">2.1 Personalization:</li>" +
                "<p>We may use the information collected to personalize your experience on the mymedicos app, such as providing educational updates tailored to your interests.</p>" +
                "<li class=\"section-header\">2.2 Communication:</li>" +
                "<p>We may use your email address and mobile phone number to communicate with you about account-related matters, updates to our services, and educational content.</p>" +
                "<li class=\"section-header\">2.3 Security:</li>" +
                "<p>Your information may be used to enhance the security of the mymedicos app and to prevent fraudulent activity.</p>" +
                "</ul>" +
                "<h2>3. Sharing of Information</h2>" +
                "<ul>" +
                "<li class=\"section-header\">3.1 Third-Party Service Providers:</li>" +
                "<p>We may share your information with third-party service providers who assist us in providing and improving the mymedicos app. These service providers are contractually obligated to protect your information and only use it for the purposes specified by us.</p>" +
                "<li class=\"section-header\">3.2 Legal Compliance:</li>" +
                "<p>We may disclose your information if required to do so by law or in response to a valid legal request, such as a court order or subpoena.</p>" +
                "</ul>" +
                "<h2>4. Data Security</h2>" +
                "<p>We take the security of your information seriously and implement appropriate technical and organizational measures to protect it against unauthorized access, alteration, disclosure, or destruction.</p>" +
                "<h2>5. Your Choices</h2>" +
                "<ul>" +
                "<li class=\"section-header\">5.1 Account Information:</li>" +
                "<p>You may review, update, or delete the personal information associated with your mymedicos account at any time by accessing your account settings.</p>" +
                "<li class=\"section-header\">5.2 Communication Preferences:</li>" +
                "<p>You can choose to opt out of receiving promotional emails or SMS messages from us by following the instructions provided in the messages or by contacting us directly.</p>" +
                "</ul>" +
                "<h2>6. Children’s Privacy</h2>" +
                "<p>The mymedicos app is not intended for use by children under the age of 13. We do not knowingly collect personal information from children under 13 years of age. If you believe that we have inadvertently collected information from a child under 13, please contact us immediately so that we can take appropriate action.</p>" +
                "<h2>7. Changes to this Privacy Policy</h2>" +
                "<p>We reserve the right to update or modify this Privacy Policy at any time. If we make material changes to this Privacy Policy, we will notify you by email or by posting a notice in the mymedicos app prior to the changes taking effect. Your continued use of the mymedicos app after the effective date of the revised Privacy Policy constitutes your acceptance of the changes.</p>" +
                "<h2>8. Contact Us</h2>" +
                "<p>If you have any questions or concerns about this Privacy Policy or our privacy practices, please contact us at <a href=\"mailto:contact@mymedicos.in\">contact@mymedicos.in</a>.</p>" +
                "</body>" +
                "</html>";

        webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        ImageView backButton = findViewById(R.id.backbtnotp);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the activity and go back to the previous activity
                onBackPressed();
            }
        });
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

}