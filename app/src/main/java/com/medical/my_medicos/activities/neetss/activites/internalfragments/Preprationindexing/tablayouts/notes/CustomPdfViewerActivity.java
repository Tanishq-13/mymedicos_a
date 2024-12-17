package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

//import com.github.barteksc.pdfviewer.PDFView;
import com.medical.my_medicos.R;

public class CustomPdfViewerActivity extends AppCompatActivity {

    //private PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_pdf_viewer);

        //pdfView = findViewById(R.id.pdfView);

        String fileUrl = getIntent().getStringExtra("fileUrl");

        // You can customize the loading or PDF display options here
//        pdfView.fromUri(Uri.parse(fileUrl))
//                .enableSwipe(true)
//                .swipeHorizontal(true) // Horizontal scrolling for reading mode
//                .enableDoubletap(true)
//                .spacing(10)
//                .load();
    }
}
