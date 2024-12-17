package com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments;

import static android.content.ContentValues.TAG;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.medical.my_medicos.activities.neetss.model.QuizSSExam;
import com.medical.my_medicos.databinding.FragmentLiveNeetBinding;
import com.medical.my_medicos.activities.pg.activites.internalfragments.intwernaladapters.ExamQuizAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

public class LiveNeetFragment extends Fragment {
    private FragmentLiveNeetBinding binding;
    private ArrayList<QuizPGExam> Livepg;
    private ExamQuizAdapter LiveAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FirebaseUser currentUser;

    public static LiveNeetFragment newInstance() {
        return new LiveNeetFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLiveNeetBinding.inflate(inflater, container, false);
        Livepg = new ArrayList<>();
        LiveAdapter = new ExamQuizAdapter(requireContext(), Livepg);
        RecyclerView recyclerViewVideos = binding.recyclerViewLiveExams;
        LinearLayoutManager layoutManagerVideos = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewVideos.setLayoutManager(layoutManagerVideos);
        recyclerViewVideos.setAdapter(LiveAdapter);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.nocardpg.setVisibility(View.GONE); // Initially set to GONE
        getPaidExam("Exam");
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void getPaidExam(String title) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getPhoneNumber();
            fetchAttemptedQuizzes(userId, attemptedQuizIds -> fetchLiveQuizzes(FirebaseFirestore.getInstance(), title, Timestamp.now(), attemptedQuizIds));
        } else {
            Log.e(TAG, "User is not logged in.");
        }
    }

    private void fetchAttemptedQuizzes(String phoneNumber, Consumer<Set<String>> onCompleted) {
        FirebaseFirestore.getInstance().collection("QuizResults").document(phoneNumber).collection("Exam")
                .get()
                .addOnCompleteListener(task -> {
                    Set<String> attemptedQuizIds = new HashSet<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            attemptedQuizIds.add(document.getId());
                        }
                    } else {
                        Log.e(TAG, "Error fetching attempted quizzes: ", task.getException());
                    }
                    onCompleted.accept(attemptedQuizIds);
                });
    }

    private void fetchLiveQuizzes(FirebaseFirestore db, String title, Timestamp now, Set<String> attemptedQuizIds) {
        CollectionReference quizzCollection = db.collection("PGupload").document("Weekley").collection("Quiz");
        Query query = quizzCollection.whereLessThanOrEqualTo("from", now)
                .whereGreaterThanOrEqualTo("to", now);
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Livepg.clear(); // Clearing the list to ensure it only contains current live quizzes
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String quizId = document.getId();
                    if (!attemptedQuizIds.contains(quizId)) {
                        handleEachQuiz(document, title, now);
                    }
                }
                Collections.sort(Livepg, Comparator.comparing(QuizPGExam::getTo));
                LiveAdapter.notifyDataSetChanged();
                if (Livepg.isEmpty()) {
                    Log.d(TAG, "No live quizzes found.");
                    binding.nocardpg.setVisibility(View.VISIBLE); // Show the 'nocardpg' layout
                } else {
                    binding.nocardpg.setVisibility(View.GONE); // Hide the 'nocardpg' layout
                }
            } else {
                Log.e(TAG, "Error getting live quizzes: ", task.getException());
                binding.nocardpg.setVisibility(View.VISIBLE); // Show the 'nocardpg' layout in case of error
            }
            binding.progressBar.setVisibility(View.GONE);
        });
    }


    private void handleEachQuiz(QueryDocumentSnapshot document, String title, Timestamp now) {
        String quizTitle = document.getString("title");
        String speciality = document.getString("speciality");
        Timestamp to = document.getTimestamp("to");
        Timestamp from = document.getTimestamp("from");
        List<Object> dataList = (List<Object>) document.get("Data");

        int dataSize = (dataList != null) ? dataList.size() : 0;  // Get the size of the 'Data' array (number of questions)

        String id = document.getString("qid");
        boolean index=document.contains("index");

            boolean paid1=document.contains("type");
            if (paid1==true) {
                Object field = document.get("type");
                if (field instanceof String) {
                    String type1=document.getString("type");
                    if (type1.equals("Paid")) {
                        type1=document.getString("hyOption");
                        if (title.isEmpty() || title.equals("Exam")) {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "Z",String.valueOf(dataSize));
                                Livepg.add(quiz);
                            }
                        } else {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "index1",String.valueOf(dataSize));
                                Livepg.add(quiz);
                            }
                        }
                    }
                    else{
                        if (title.isEmpty() || title.equals("Exam")) {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "Z",String.valueOf(dataSize));
                                Livepg.add(quiz);
                            }
                        } else {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "index1",String.valueOf(dataSize));
                                Livepg.add(quiz);
                            }
                        }

                    }

                } else if (field instanceof Boolean) {
                    if (title.isEmpty() || title.equals("Exam")) {

                        if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                            QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, "Basic", "index1",String.valueOf(dataSize));
                            Livepg.add(quiz);
                        }
                    } else {
                        if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                            QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, "Basic", "index1",String.valueOf(dataSize));
                            Livepg.add(quiz);
                        }
                    }






            }
        }

    }


}
