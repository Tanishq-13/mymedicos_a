package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
//import com.github.barteksc.pdfviewer.PDFView;
import com.medical.my_medicos.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PreviewActivity extends AppCompatActivity {

    //private PDFView pdfView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        //pdfView = findViewById(R.id.pdfView);

        // Get the PDF file URL from the intent
        String fileUrl = getIntent().getStringExtra("fileUrl");
//done
        // Load the PDF preview
        new LoadPdfPreviewTask().execute(fileUrl);
    }

    private class LoadPdfPreviewTask extends AsyncTask<String, Void, File> {

        @Override
        protected File doInBackground(String... params) {
            String fileUrl = params[0];
            File tempFile = null;
            try {
                URL url = new URL(fileUrl);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                tempFile = File.createTempFile("preview", ".pdf", getCacheDir());
                FileOutputStream fileOutput = new FileOutputStream(tempFile);
                InputStream inputStream = urlConnection.getInputStream();
                byte[] buffer = new byte[2048];
                int bufferLength;

                while ((bufferLength = inputStream.read(buffer)) > 0) {
                    fileOutput.write(buffer, 0, bufferLength);
                }
                fileOutput.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return tempFile;
        }

        @Override
        protected void onPostExecute(File file) {
//            if (file != null && pdfView != null) {
//                pdfView.fromFile(file)
//                        .enableSwipe(true)
//                        .swipeHorizontal(false) // Vertical scrolling for preview
//                        .enableDoubletap(true)
//                        .spacing(10)
//                        .load();
//            }
        }
    }
}
