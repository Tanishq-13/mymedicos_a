package com.medical.my_medicos.activities.neetss.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.model.QuestionSS;
import com.medical.my_medicos.activities.pg.model.QuestionPG;
import com.medical.my_medicos.databinding.ItemQuestionBinding;

import java.util.ArrayList;

public class QuestionBankSSAdapter extends RecyclerView.Adapter<QuestionBankSSAdapter.QuestionBankSSViewHolder> {

    Context context;
    ArrayList<QuestionSS> questions;

    public QuestionBankSSAdapter(Context context, ArrayList<QuestionSS> questions) {
        this.context = context;
        this.questions = questions;
    }

    @NonNull
    @Override
    public QuestionBankSSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuestionBankSSViewHolder(LayoutInflater.from(context).inflate(R.layout.item_category_qb, parent, false));
    }


    @Override
    public void onBindViewHolder(@NonNull QuestionBankSSViewHolder holder, int position) {
        QuestionSS news = questions.get(position);
        Glide.with(context);
        holder.binding.questionslabel.setText(news.getLabel());
        holder.binding.timeofquestions.setText(news.getUrl());

//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, NewsDetailedActivity.class);
//                intent.putExtra("Title", news.getLabel());
//                intent.putExtra("image", news.getThumbnail());
//                intent.putExtra("url", news.getUrl());
//                intent.putExtra("date", news.getDate());
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
    public class QuestionBankSSViewHolder extends RecyclerView.ViewHolder {
        ItemQuestionBinding binding;

        public QuestionBankSSViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemQuestionBinding.bind(itemView);
            binding.DownloadbtnQuestion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        QuestionSS clickedNews = questions.get(position);
                        openUrlInBrowser(clickedNews.getDate());
                    }
                }
            });
        }
    }
    private void openUrlInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }
}
