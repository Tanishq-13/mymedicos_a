package com.medical.my_medicos.adapter.job;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.job.items.jobitem;

import java.util.List;

public class AdapterforApplicants extends RecyclerView.Adapter<AdapterforApplicants.MyViewHolder> {

    Context context;
    List<jobitem> joblistforapplicant;
    String data;

    public AdapterforApplicants(Context context, List<jobitem> joblist) {
        this.context = context;
        this.joblistforapplicant = joblist;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.job_design_for_applicants, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.applicantname.setText(joblistforapplicant.get(position).getTitle());
        holder.phoneapplicant.setText(joblistforapplicant.get(position).getHospital());
        holder.cover_letter.setText(joblistforapplicant.get(position).getLocation());
        holder.ageofapplicant.setText(joblistforapplicant.get(position).getDate());
        holder.phonepart.setText(joblistforapplicant.get(position).getPosition());
        data=joblistforapplicant.get(position).getCategory();
        holder.updateData(joblistforapplicant.get(position));


    }

    @Override
    public int getItemCount() {
        return joblistforapplicant.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView applicantname, phoneapplicant, cover_letter,ageofapplicant,downloadresume,phonepart;
        String data;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            applicantname = itemView.findViewById(R.id.applicantname);
            phonepart = itemView.findViewById(R.id.phone_of_applicant);
            cover_letter = itemView.findViewById(R.id.cover_letter_content);
            ageofapplicant = itemView.findViewById(R.id.age_of_applicant);
            downloadresume = itemView.findViewById(R.id.DownloadResume);
            phoneapplicant = itemView.findViewById(R.id.modeiscoming);
            downloadresume.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Call the method to download PDF
                    downloadPdf(data, itemView.getContext());
                }
            });


        }
        public void updateData(jobitem jobItem) {
            // Update the TextViews with data from the jobItem
            applicantname.setText(jobItem.getTitle());
            phonepart.setText(jobItem.getPosition());
            cover_letter.setText(jobItem.getLocation());
            ageofapplicant.setText(jobItem.getDate());
            phoneapplicant.setText(jobItem.getHospital());
            data = jobItem.getCategory();
        }
        public void downloadPdf(String pdfUrl, Context context) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));

            // Title and description of the download notification
            request.setTitle("Downloading PDF");
            request.setDescription("Please wait...");

            // Set destination in the external public directory
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "your_pdf_filename.pdf");

            // Set notification visibility to show the download progress in the notification bar
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            // Get download service and enqueue file
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
            }

            // Optionally, you can show a toast or notification to indicate the download has started
            Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
        }

    }
}
