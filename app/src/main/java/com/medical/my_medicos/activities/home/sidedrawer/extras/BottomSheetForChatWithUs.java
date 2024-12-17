package com.medical.my_medicos.activities.home.sidedrawer.extras;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medical.my_medicos.R;

public class BottomSheetForChatWithUs extends BottomSheetDialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheetloaderforchatwithus, container, false);
        applySystemUiChanges();

        // Open link after 3 seconds
        openLinkAfterDelay();

        return view;
    }

    private void applySystemUiChanges() {
        if (getDialog() != null && getDialog().getWindow() != null && isAdded()) {
            Window window = getDialog().getWindow();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.white));
                window.setNavigationBarColor(ContextCompat.getColor(requireContext(), R.color.white));
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                View decorView = window.getDecorView();
                decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
            }
        }
    }

    private void openLinkAfterDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                openWhatsAppLink();
                dismiss(); // Dismiss the bottom sheet after opening the link
            }
        }, 1000); // 3 seconds delay
    }

    private void openWhatsAppLink() {
        if (isAdded()) {  // Check if the fragment is attached to the activity
            String url = "https://wa.me/message/AB2QUXAZYEV2E1";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }
}
