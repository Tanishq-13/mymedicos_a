package com.medical.my_medicos.activities.news;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.HomeActivity;
import com.medical.my_medicos.activities.home.exclusive.exclusivehome;
import com.medical.my_medicos.activities.news.ImportantNoticesNewsActivity;
import com.medical.my_medicos.activities.news.SearchNewsActivity;
import com.medical.my_medicos.activities.news.fragments.AllNewsFragment;
import com.medical.my_medicos.activities.news.fragments.DrugnDiseasesNewsFragment;
import com.medical.my_medicos.activities.news.fragments.EducationNewsFragment;
import com.medical.my_medicos.activities.news.fragments.JobsUpdatesNewsFragment;
import com.medical.my_medicos.activities.news.fragments.MedicalNewsFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class NewsActivity extends Fragment {
    private TodayNewsAdapter todayNewsAdapter;
    private ArrayList<NewsToday> newstoday;
    private RelativeLayout importantnoticego;
    private ImageView backtothehomefrompg;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_news, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MaterialSearchBar searchBarNews = view.findViewById(R.id.searchBarNews);
        searchBarNews.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for now
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not needed for now
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not needed for now
            }
        });

        searchBarNews.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                // Not needed for now
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                String query = text.toString();
                if (!TextUtils.isEmpty(query)) {
                    Intent intent = new Intent(requireContext(), SearchNewsActivity.class);
                    intent.putExtra("query", query);
                    startActivity(intent);
                }
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    // Handle back button click
                }
            }
        });

//        backtothehomefrompg = view.findViewById(R.id.backtothehomefrompg);
//        backtothehomefrompg.setOnClickListener(v -> {
//            Intent i = new Intent(requireContext(), HomeActivity.class);
//            startActivity(i);
//        });

//        importantnoticego = view.findViewById(R.id.importantnoticego);
//        importantnoticego.setOnClickListener(v -> {
//            Intent i = new Intent(requireContext(), ImportantNoticesNewsActivity.class);
//            startActivity(i);
//        });

        TabLayout tabLayout = view.findViewById(R.id.tablayoutnews);//juh
        ViewPager2 viewPager = view.findViewById(R.id.view_pager_news);

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: // All
                        return new AllNewsFragment();
                    case 1: // Medical
                        return new MedicalNewsFragment();
                    case 2: // Education
                        return new EducationNewsFragment();
                    case 3: // Drugs & Diseases
                        return new DrugnDiseasesNewsFragment();
                    case 4: // Job Updates
                        return new JobsUpdatesNewsFragment();
                    default:
                        return new AllNewsFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 5; // Number of tabs
            }
        };

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Discovery");
                    break;
                case 1:
                    tab.setText("Law in Medicine");
                    break;
                case 2:
                    tab.setText("Education");
                    break;
                case 3:
                    tab.setText("Drugs & Devices");
                    break;
                case 4:
                    tab.setText("Job Alerts");
                    break;
            }
        }).attach();

        initTodaysSlider(view);
        setupRealtimeUpdatesListener();
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Navigate back to HomeFragment
                navigateToHomeFragment();
            }
        });
    }

    private void navigateToHomeFragment() {
        // Replace CubFragment with HomeFragment
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, new exclusivehome());
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    private void initTodaysSlider(View view) {
        newstoday = new ArrayList<>();
        todayNewsAdapter = new TodayNewsAdapter(requireContext(), newstoday);
        getTodayNews();

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView newsListToday = view.findViewById(R.id.newsListToday);
        newsListToday.setLayoutManager(layoutManager);
        newsListToday.setAdapter(todayNewsAdapter);
    }

    private void getTodayNews() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        long twentyFourHoursAgo = calendar.getTimeInMillis();

        db.collection("MedicalNews")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            try {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.US);
                                Date newsDate = dateFormat.parse(document.getString("Time"));
                                long newsTime = newsDate.getTime();
                                if (newsTime >= twentyFourHoursAgo && "News".equals(document.getString("type"))) {
                                    NewsToday newsItemToday = new NewsToday(
                                            document.getId(),
                                            document.getString("Title"),
                                            document.getString("thumbnail"),
                                            document.getString("Description"),
                                            document.getString("Time"),
                                            document.getString("URL"),
                                            document.getString("subject")
                                    );
                                    newstoday.add(newsItemToday);
                                }
                            } catch (Exception e) {
                                Log.e("NewsFragment", "Error parsing timestamp", e);
                            }
                        }
                        todayNewsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void setupRealtimeUpdatesListener() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("MedicalNews")
                .whereEqualTo("type", "Notice")
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        return;
                    }
                    try {
                        processUpdates(snapshots.getDocuments());
                    } catch (JSONException jsonException) {
                        jsonException.printStackTrace();
                    }
                });
    }

    private void processUpdates(List<DocumentSnapshot> documents) throws JSONException {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("My_Medicos_Prefs", requireContext().MODE_PRIVATE);
        String seenUpdatesJson = sharedPreferences.getString("SeenUpdates", "[]");
        JSONArray seenUpdatesArray = new JSONArray(seenUpdatesJson);
        HashSet<String> seenUpdates = new HashSet<>();
        for (int i = 0; i < seenUpdatesArray.length(); i++) {
            seenUpdates.add(seenUpdatesArray.getString(i));
        }

        int unseenCount = 0;
        for (DocumentSnapshot document : documents) {
            if (!seenUpdates.contains(document.getId())) {
                unseenCount++;
            }
        }

        updateImportantNoticesCounter(unseenCount);
    }

    private void updateImportantNoticesCounter(int unseenCount) {
//        View view = getView();
//        if (view == null) return;
//        TextView counter = view.findViewById(R.id.counterforthenumberofimportantnotice);
//        if (unseenCount > 0) {
//            counter.setText(String.valueOf(unseenCount));
//            counter.setVisibility(View.VISIBLE);
//        } else {
//            counter.setVisibility(View.GONE);
//        }
    }
}
