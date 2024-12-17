//package com.medical.my_medicos.activities.fmge.activites.internalfragments.insiderfragments;
//
//import static android.content.ContentValues.TAG;
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;
//import com.google.firebase.Timestamp;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.auth.FirebaseUser;
//import com.google.firebase.firestore.CollectionReference;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.Query;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//import com.google.firebase.firestore.QuerySnapshot;
//import com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters.FmgePastAdapter;
//import com.medical.my_medicos.activities.fmge.model.QuizFmgeExam;
//import com.medical.my_medicos.databinding.FragmentPastFmgeBinding;
//import com.medical.my_medicos.databinding.FragmentPastNeetBinding;
//import com.medical.my_medicos.activities.pg.activites.internalfragments.intwernaladapters.ExamPastAdapter;
//import com.medical.my_medicos.activities.pg.model.QuizPGExam;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//
//public class PastFmgeFragment extends Fragment {
//    private FragmentPastFmgeBinding binding;
//    private ArrayList<QuizFmgeExam> Livepg;
//    private FmgePastAdapter LiveAdapter;
//    private FirebaseUser currentUser;
//    private String title1 = "Exam";
//
//    public static PastFmgeFragment newInstance() {
//        PastFmgeFragment fragment = new PastFmgeFragment();
//        Bundle args = new Bundle();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        binding = FragmentPastFmgeBinding.inflate(inflater, container, false);
//
//        Livepg = new ArrayList<>();
//        LiveAdapter = new FmgePastAdapter(requireContext(), Livepg);
//
//        RecyclerView recyclerViewVideos = binding.recyclerViewPastExams;
//        LinearLayoutManager layoutManagerVideos = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
//        recyclerViewVideos.setLayoutManager(layoutManagerVideos);
//        recyclerViewVideos.setAdapter(LiveAdapter);
//
//        fetchPastQuizzes(title1); // Fetch data regardless of args
//
//        return binding.getRoot();
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//    }
//
//    void fetchPastQuizzes(String title) {
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//        Timestamp now = Timestamp.now();
//
//        if (user != null) {
//            String userId = user.getPhoneNumber();
//
//            CollectionReference quizCollection = db.collection("Fmge").document("Weekley").collection("Quiz");
//            Query query = quizCollection;
//            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                    if (task.isSuccessful()) {
//                        Livepg.clear(); // Clear existing list to avoid duplicates
//                        for (QueryDocumentSnapshot document : task.getResult()) {
//                            String quizTitle = document.getString("title");
//                            String speciality = document.getString("speciality");
//                            String type = document.getString("type");
//                            Timestamp to = document.getTimestamp("to");
//                            Timestamp from = document.getTimestamp("from");
//
//                            if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
//                                QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, document.getId(),type, from);
//                                Livepg.add(quiz);
//                            }
//                        }
//                        // Sort the Livepg list here by the 'from' date
//                        Collections.sort(Livepg, Comparator.comparing(QuizFmgeExam::getFrom));
//                        LiveAdapter.notifyDataSetChanged();
//                        updateUI();
//                    } else {
//                        Log.e(TAG, "Error getting documents: ", task.getException());
//                    }
//                }
//            });
//        } else {
//            Log.e(TAG, "User is not logged in.");
//        }
//    }
//
//
//    private void updateUI() {
//        if (Livepg.isEmpty()) {
//            binding.recyclerViewPastExams.setVisibility(View.GONE);
//            binding.nocardpg.setVisibility(View.VISIBLE);
//            Log.d(TAG, "No past quizzes found, showing no card layout.");
//        } else {
//            binding.recyclerViewPastExams.setVisibility(View.VISIBLE);
//            binding.nocardpg.setVisibility(View.GONE);
//        }
//    }
//}
package com.medical.my_medicos.activities.fmge.activites.internalfragments.insiderfragments;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters.FmgePastAdapter;
import com.medical.my_medicos.activities.fmge.model.QuizFmgeExam;
import com.medical.my_medicos.databinding.FragmentPastFmgeBinding;
import com.medical.my_medicos.databinding.FragmentPastNeetBinding;
import com.medical.my_medicos.activities.pg.activites.internalfragments.intwernaladapters.ExamPastAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class PastFmgeFragment extends Fragment {
    private FragmentPastFmgeBinding binding;
    private ArrayList<QuizFmgeExam> Livepg;
    private FmgePastAdapter LiveAdapter;
    private FirebaseUser currentUser;
    private String title1 = "Exam";

    public static com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.PastNeetFragment newInstance() {
        com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.PastNeetFragment fragment = new com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments.PastNeetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPastFmgeBinding.inflate(inflater, container, false);

        Livepg = new ArrayList<>();
        LiveAdapter = new FmgePastAdapter(requireContext(), Livepg);

        RecyclerView recyclerViewVideos = binding.recyclerViewPastExams;
        LinearLayoutManager layoutManagerVideos = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewVideos.setLayoutManager(layoutManagerVideos);
        recyclerViewVideos.setAdapter(LiveAdapter);

        fetchPastQuizzes(title1); // Fetch data regardless of args

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    void fetchPastQuizzes(String title) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Timestamp now = Timestamp.now();

        if (user != null) {
            String userId = user.getPhoneNumber();

            CollectionReference quizCollection = db.collection("Fmge").document("Weekley").collection("Quiz");
            Query query = quizCollection;
            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Livepg.clear(); // Clear existing list to avoid duplicates
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String quizTitle = document.getString("title");
                            String speciality = document.getString("speciality");
                            Timestamp to = document.getTimestamp("to");
                            String id = document.getString("qid");
                            boolean index=document.contains("index");
                            boolean paid1=document.contains("type");

                            if (paid1 == true) {
                                Object field = document.get("type");
                                if (field instanceof String) {
                                    String type1 = document.getString("type");
                                    if (type1.equals("Paid")) {
                                        type1=document.getString("hyOption");
                                        if (title.isEmpty() || title.equals("exam")) {
                                            if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, type1, "index1");
                                                Livepg.add(quiz);
                                            }
                                        } else {
                                            if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, type1, "index1");
                                                Livepg.add(quiz);
                                            }
                                        }
                                    }
                                    else{
                                        if (title.isEmpty() || title.equals("exam")) {
                                            if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, type1, "index1");
                                                Livepg.add(quiz);
                                            }
                                        } else {
                                            if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, type1, "index1");
                                                Livepg.add(quiz);
                                            }
                                        }

                                    }

                                } else if (field instanceof Boolean) {
                                    if (title.isEmpty() || title.equals("exam")) {

                                        if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                            QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, "Basic", "index1");
                                            Livepg.add(quiz);
                                        }
                                    } else {
                                        if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                            QuizFmgeExam quiz = new QuizFmgeExam(quizTitle, speciality, to, id, "Basic", "index1");
                                            Livepg.add(quiz);
                                        }
                                    }
                                }



                            }
                        }
                        // Sort the Livepg list here by the 'from' date
                        Collections.sort(Livepg, Comparator.comparing(QuizFmgeExam::getTo));
                        LiveAdapter.notifyDataSetChanged();
                        updateUI();
                    } else {
                        Log.e(TAG, "Error getting documents: ", task.getException());
                    }
                }
            });
        } else {
            Log.e(TAG, "User is not logged in.");
        }
    }


    private void updateUI() {
        if (Livepg.isEmpty()) {
            binding.recyclerViewPastExams.setVisibility(View.GONE);
            binding.nocardpg.setVisibility(View.VISIBLE);
            Log.d(TAG, "No past quizzes found, showing no card layout.");
        } else {
            binding.recyclerViewPastExams.setVisibility(View.VISIBLE);
            binding.nocardpg.setVisibility(View.GONE);
        }
    }
}

