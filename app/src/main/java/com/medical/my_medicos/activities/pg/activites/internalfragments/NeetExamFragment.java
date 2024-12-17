package com.medical.my_medicos.activities.pg.activites.internalfragments;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.LiveNeetFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.PastNeetFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.UpcomingNeetFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.extras.CouponBottomSheetFragment;
import com.medical.my_medicos.activities.pg.activites.internalfragments.intwernaladapters.ExamQuizAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NeetExamFragment extends Fragment {

    private ViewFlipper viewFlipperPg;
    private Handler handlerpg;
    private ArrayList<QuizPG> quizpgneet;
    private ExamQuizAdapter quizAdapterneet;
    private LinearLayout dotsLayoutpg;
    String title2;
    FirebaseUser currentUser;

    private final int AUTO_SCROLL_DELAY = 3000;

    public static NeetExamFragment newInstance() {
        NeetExamFragment fragment = new NeetExamFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_neet_exam, container, false);

        viewFlipperPg = rootView.findViewById(R.id.viewFlipperpg);
        dotsLayoutpg = rootView.findViewById(R.id.dotsLayoutpg);
        handlerpg = new Handler();

        addDotspg();

        TabLayout tabLayout = rootView.findViewById(R.id.tablayoutpg);
        tabLayout.addTab(tabLayout.newTab().setText("LIVE"));
        tabLayout.addTab(tabLayout.newTab().setText("UPCOMING"));
        tabLayout.addTab(tabLayout.newTab().setText("PAST"));

        ViewPager2 viewPager = rootView.findViewById(R.id.view_pager_pg);

        FragmentStateAdapter adapter = new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 1:
                        return new UpcomingNeetFragment();
                    case 2:
                        return new PastNeetFragment();
                    default:
                        return new LiveNeetFragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Adjust based on your tabs count
            }
        };

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("LIVE");
                    break;
                case 1:
                    tab.setText("UPCOMING");
                    break;
                case 2:
                    tab.setText("PAST");
                    break;
            }
        }).attach();

        handlerpg.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);

        ImageView couponPopup = rootView.findViewById(R.id.CouponPopup);
        couponPopup.setOnClickListener(v -> {
            CouponBottomSheetFragment bottomSheetFragment = new CouponBottomSheetFragment();
            bottomSheetFragment.show(getChildFragmentManager(), bottomSheetFragment.getTag());
        });

        return rootView;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
        }

//        RecyclerView perDayQuestionsRecyclerView = view.findViewById(R.id.perdayquestions);
//
//        if (perDayQuestionsRecyclerView == null) {
//            Log.e("Fragment", "Empty");
//            return;
//        }

        showShimmer(true);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = currentUser.getPhoneNumber();
        db.collection("users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Log.d(TAG, document.getId() + " => " + document.getData());
                }
            } else {
                Log.w(TAG, "Error getting documents.", task.getException());
            }
        });

    }

    private final Runnable autoScrollRunnable = new Runnable() {
        @Override
        public void run() {
            int currentChildIndex = viewFlipperPg.getDisplayedChild();
            int nextChildIndex = (currentChildIndex + 1) % viewFlipperPg.getChildCount();
            viewFlipperPg.setDisplayedChild(nextChildIndex);
            updateDotspg(nextChildIndex);
            handlerpg.postDelayed(this, AUTO_SCROLL_DELAY);
        }
    };

    private void addDotspg() {
        for (int i = 0; i < viewFlipperPg.getChildCount(); i++) {
            ImageView dot = new ImageView(requireContext());
            dot.setImageDrawable(getResources().getDrawable(R.drawable.inactive_thumb));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dotsLayoutpg.addView(dot, params);
        }
        updateDotspg(0);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        handlerpg.removeCallbacksAndMessages(null);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void updateDotspg(int currentDotIndex) {
        if (!isAdded()) {
            return;
        }
        for (int i = 0; i < dotsLayoutpg.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayoutpg.getChildAt(i);
            dot.setImageDrawable(getResources().getDrawable(
                    i == currentDotIndex ? R.drawable.custom_thumb : R.drawable.inactive_thumb
            ));
        }
    }

    private void showShimmer(boolean show) {
        View shimmer = getView().findViewById(R.id.shimmercomeup);
        if (shimmer != null) {
            shimmer.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) {
                // Assuming shimmercomeup is a LottieAnimationView
                ((LottieAnimationView) shimmer).playAnimation();
            } else {
                ((LottieAnimationView) shimmer).cancelAnimation();
            }
        }
    }
}
