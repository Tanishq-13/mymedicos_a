package com.medical.my_medicos.adapter.ug;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.medical.my_medicos.R;
import com.medical.my_medicos.adapter.ug.items.ugitem1;

import java.util.List;

public class UgAdapter1 extends RecyclerView.Adapter<UgAdapter1.UgViewHolder1> {

    Context context;
    List<ugitem1> item;

    public UgAdapter1(Context context, List<ugitem1> item) {
        this.context = context;
        this.item = item;
    }

    @NonNull
    @Override
    public UgViewHolder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.ug_design1, parent, false);
        return new UgViewHolder1(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UgViewHolder1 holder, @SuppressLint("RecyclerView") int position) {
        holder.name.setText(item.get(position).getDocname());
        holder.description.setText(item.get(position).getDocdescripiton());
        holder.title.setText(item.get(position).getDoctitle());
        holder.date.setText(item.get(position).getdate());
        holder.pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Increase the downloads count by 1
                int currentDownloads = Integer.parseInt(item.get(position).getDocdownloads());
                int newDownloads = currentDownloads + 1;
                item.get(position).setDocdownloads(String.valueOf(newDownloads));

                // Notify the adapter that the data has changed
                notifyDataSetChanged();

                // Start the download
                holder.downloadPdf(item.get(position).getPdf(), context);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public static class UgViewHolder1 extends RecyclerView.ViewHolder {
        TextView name, title, description, date, downloads;
        LinearLayout pdf;

        public UgViewHolder1(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.dr_ug_name);
            description = itemView.findViewById(R.id.dr_ug_desciption);
            title = itemView.findViewById(R.id.dr_ug_title);
            date = itemView.findViewById(R.id.dr_ug_date);
            pdf = itemView.findViewById(R.id.dr_pdf);
        }

        public void downloadPdf(String pdfUrl, Context context) {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(pdfUrl));

            request.setTitle("Downloading PDF");
            request.setDescription("Please wait...");

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "your_pdf_filename.pdf");

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            if (manager != null) {
                manager.enqueue(request);
            }
            Toast.makeText(context, "Download started", Toast.LENGTH_SHORT).show();
        }
    }
}
