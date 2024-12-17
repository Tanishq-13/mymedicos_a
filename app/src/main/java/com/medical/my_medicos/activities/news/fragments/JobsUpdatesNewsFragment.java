package com.medical.my_medicos.activities.news.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.Query;
import com.medical.my_medicos.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.activities.news.News;
import com.medical.my_medicos.activities.news.NewsAdapter;

import java.util.ArrayList;

public class JobsUpdatesNewsFragment extends Fragment {

    private ArrayList<News> news;
    private NewsAdapter newsAdapter;
    private LottieAnimationView loadingAnimation;
    private LottieAnimationView noDataAnimation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_jobs_updates_news, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadingAnimation = view.findViewById(R.id.loading_animation);
        noDataAnimation = view.findViewById(R.id.no_data_animation);
        initNewsSlider();
    }

    void initNewsSlider() {
        news = new ArrayList<>();
        newsAdapter = new NewsAdapter(getActivity(), news);
        getRecentNews();
        GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1);
        RecyclerView newsList = getView().findViewById(R.id.recyclerview_job_updates_news);
        newsList.setLayoutManager(layoutManager);
        newsList.setAdapter(newsAdapter);
    }

    void getRecentNews() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        loadingAnimation.setVisibility(View.VISIBLE);
        db.collection("MedicalNews")
                .whereEqualTo("type", "News")
                .whereEqualTo("tag", "Alerts")
                .orderBy("Time", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    loadingAnimation.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            News newsItem = new News(
                                    document.getId(),
                                    document.getString("Title"),
                                    document.getString("thumbnail"),
                                    document.getString("Description"),
                                    document.getString("subject"),
                                    document.getString("Time"),
                                    document.getString("URL"),
                                    document.getString("tag")
                            );
                            news.add(newsItem);
                        }
                        newsAdapter.notifyDataSetChanged();
                        if (news.isEmpty()) {
                            noDataAnimation.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show();
                        noDataAnimation.setVisibility(View.VISIBLE);
                    }
                });
    }
}
