package com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.Preprationindexing.Model.Index.NotesData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotePreviewBottomSheet extends BottomSheetDialogFragment {

    private NotesData notesData;
    private OnPreviewClickListener previewClickListener;

    public NotePreviewBottomSheet(NotesData notesData, OnPreviewClickListener previewClickListener) {
        this.notesData = notesData;
        this.previewClickListener = previewClickListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_note_preview, container, false);

        TextView titleTextView = view.findViewById(R.id.previewTitleTextView);
        TextView descriptionTextView = view.findViewById(R.id.previewDescriptionTextView);
        ImageView pdfPreviewImageView = view.findViewById(R.id.pdfPreviewImageView);
        TextView previewButton = view.findViewById(R.id.previewPdfButton);
        TextView openPdfButton = view.findViewById(R.id.openPdfButton);

        titleTextView.setText(notesData.getTitle());

        // Convert HTML to Spanned and set it to the TextView
        Spanned spannedDescription = Html.fromHtml(notesData.getDescription(), Html.FROM_HTML_MODE_LEGACY);
        descriptionTextView.setText(spannedDescription);

        // Load the first page of the PDF as an image
        loadPdfPreview(notesData.getFile(), pdfPreviewImageView);

        previewButton.setOnClickListener(v -> {
            dismiss();
            previewClickListener.onPreviewClick(notesData);
        });
        Log.d("testdownload","fj");

        openPdfButton.setOnClickListener(v -> {

            Intent intent = new Intent(getContext(), CustomPdfViewerActivity.class);
            intent.putExtra("fileUrl", notesData.getFile());
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        View view = getView();
        if (view != null) {
            // Post to ensure the view has been laid out
            view.post(() -> {
                View parent = (View) view.getParent();
                BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(parent);
                behavior.setDraggable(false); // Disable dragging the bottom sheet
            });
        }
    }

    private void loadPdfPreview(String pdfUrl, ImageView imageView) {
        new Thread(() -> {
            try {
                // Download the PDF file
                File pdfFile = downloadPdfFile(pdfUrl);

                if (pdfFile != null) {
                    // Use PdfRenderer to open the file
                    ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
                    PdfRenderer pdfRenderer = new PdfRenderer(fileDescriptor);
                    PdfRenderer.Page page = pdfRenderer.openPage(0);

                    // Create a bitmap to hold the first page
                    Bitmap bitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                    page.close();
                    pdfRenderer.close();
                    fileDescriptor.close();

                    // Set the bitmap to the ImageView
                    imageView.post(() -> imageView.setImageBitmap(bitmap));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private File downloadPdfFile(String fileUrl) {
        try {
            URL url = new URL(fileUrl);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            File tempFile = File.createTempFile("tempPdf", ".pdf", getContext().getCacheDir());
            FileOutputStream fileOutput = new FileOutputStream(tempFile);
            InputStream inputStream = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int bufferLength;

            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
            }

            fileOutput.close();
            return tempFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public interface OnPreviewClickListener {
        void onPreviewClick(NotesData note);
    }
}
