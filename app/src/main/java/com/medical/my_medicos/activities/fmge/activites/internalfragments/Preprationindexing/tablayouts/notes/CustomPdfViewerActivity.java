package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.medical.my_medicos.R;

public class CustomPdfViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_pdf_viewer);

        // Get the file URL from Firestore (passed via Intent)
        String fileUrl = getIntent().getStringExtra("fileUrl");

        // Call the method to download and open the PDF
        downloadPdf(fileUrl);
    }

    private void downloadPdf(String fileUrl) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(fileUrl));
            request.setTitle("Downloading PDF");
            request.setDescription("Downloading PDF file from Firebase Storage...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "downloaded_file.pdf");

            // Get the DownloadManager service and enqueue the download
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            long downloadId = downloadManager.enqueue(request);

            // Open the PDF once downloaded
            registerReceiver(new android.content.BroadcastReceiver() {
                @Override
                public void onReceive(Context context, android.content.Intent intent) {
                    // Get the file path of the downloaded file
                    Uri uri = downloadManager.getUriForDownloadedFile(downloadId);

                    if (uri != null) {
                        openPdf(uri);
                    } else {
                        Toast.makeText(context, "Failed to download PDF", Toast.LENGTH_SHORT).show();
                    }
                }
            }, new android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Method to open the PDF file
    private void openPdf(Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NO_HISTORY);

        // Check if there's an app that can open PDFs
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
        }
    }
}
