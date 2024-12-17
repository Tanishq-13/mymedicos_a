package com.medical.my_medicos.activities.neetss.activites.internalfragments.insiderfragments;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.intwernaladapters.ExamQuizAdapter;
import com.medical.my_medicos.activities.neetss.model.QuizSSExam;

import com.medical.my_medicos.databinding.FragmentLiveNeetBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

public class LiveSSFragment extends Fragment {
    private FragmentLiveNeetBinding binding;
    private ArrayList<QuizSSExam> liveQuizzes;
    private ExamQuizAdapter adapter;
    private FirebaseUser currentUser;

    public static LiveSSFragment newInstance() {
        return new LiveSSFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentLiveNeetBinding.inflate(inflater, container, false);
        setupRecyclerView();
        authenticateAndInitialize();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        liveQuizzes = new ArrayList<>();
        adapter = new ExamQuizAdapter(requireContext(), liveQuizzes);
        RecyclerView recyclerView = binding.recyclerViewLiveExams;
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
        binding.progressBar.setVisibility(View.VISIBLE);
        Log.d(TAG, "RecyclerView setup complete, adapter attached.");
    }

    private void authenticateAndInitialize() {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "User authenticated: " + currentUser.getPhoneNumber());
            getPaidExam("exam");
        } else {
            Log.e(TAG, "User is not logged in.");
            showToast("Please log in to continue");
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    private void getPaidExam(String title) {
        Log.d(TAG, "Fetching attempted quizzes for user: " + currentUser.getPhoneNumber());
        fetchAttemptedQuizzes(currentUser.getPhoneNumber(), attemptedQuizIds ->
                fetchLiveQuizzes(FirebaseFirestore.getInstance(), title, Timestamp.now(), attemptedQuizIds));
    }

    private void fetchAttemptedQuizzes(String phoneNumber, Consumer<Set<String>> onCompleted) {
        FirebaseFirestore.getInstance().collection("QuizResults").document(phoneNumber).collection("Exam")
                .get()
                .addOnCompleteListener(task -> {
                    Set<String> attemptedQuizIds = new HashSet<>();
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            attemptedQuizIds.add(document.getId());
                            Log.d(TAG, "Attempted quiz ID: " + document.getId());
                        }
                        onCompleted.accept(attemptedQuizIds);
                        Log.d(TAG, "Total attempted quizzes fetched: " + attemptedQuizIds.size());
                    } else {
                        Log.e(TAG, "Error fetching attempted quizzes: ", task.getException());
                        showToast("Failed to fetch quiz data.");
                        binding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void fetchLiveQuizzes(FirebaseFirestore db, String title, Timestamp now, Set<String> attemptedQuizIds) {

        String currentUid = currentUser.getPhoneNumber();
        Log.d(TAG, "Fetching live quizzes for user specialization: " + currentUid);
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        DatabaseReference userRef = database.getReference().child("profiles").child(currentUid);

        userRef.child("Neetss").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String specialization = snapshot.getValue(String.class);
                Log.d(TAG, "User specialization fetched: " + specialization);
                queryLiveQuizzes(db, specialization, title, now, attemptedQuizIds);

            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(FragmentManager.TAG, "Error loading specialization from database: " + error.getMessage());
            }
        });
    }

    private void queryLiveQuizzes(FirebaseFirestore db, String specialization, String title, Timestamp now, Set<String> attemptedQuizIds) {
        CollectionReference quizCollection = db.collection("Neetss").document(specialization).collection("Weekley");
        Log.d(TAG, "Querying live quizzes in Firestore for specialization: " + specialization);

        Query query = quizCollection.whereLessThanOrEqualTo("from", now)
                .whereGreaterThanOrEqualTo("to", now);

        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                liveQuizzes.clear();
                Log.d(TAG, "Live quizzes query successful, processing results...");
                for (QueryDocumentSnapshot document : task.getResult()) {
                    handleEachQuiz(document, title, now, attemptedQuizIds);
                }
                Collections.sort(liveQuizzes, Comparator.comparing(QuizSSExam::getTo));
                adapter.notifyDataSetChanged();
                updateUIForQuizzes();
            } else {
                Log.e(TAG, "Error getting live quizzes: ", task.getException());
                showToast("Failed to fetch live quizzes.");
                updateUIForNoQuizzes();
            }
        });
    }


    private void handleEachQuiz(QueryDocumentSnapshot document, String title, Timestamp now, Set<String> attemptedQuizIds) {
        String quizId = document.getId();
        Log.d(TAG, "Processing quiz with ID: " + quizId);

        if (!attemptedQuizIds.contains(quizId)) {
            String quizTitle = document.getString("title");
            String speciality = document.getString("speciality");
            Timestamp to = document.getTimestamp("to");
            Timestamp from = document.getTimestamp("from");

            Log.d(TAG, "Quiz details: Title=" + quizTitle + ", Speciality=" + speciality + ", From=" + from + ", To=" + to);

            String id = document.getString("qid");
            boolean index = document.contains("index");

            boolean paid1 = document.contains("type");
            if (paid1) {
                Object field = document.get("type");
                if (field instanceof String) {
                    String type1 = document.getString("type");
                    if (type1.equals("Paid")) {
                        type1 = document.getString("hyOption");
                        Log.d(TAG, "Quiz is paid with option: " + type1);
                        if (title.isEmpty() || title.equals("exam")) {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, type1, "Z");
                                liveQuizzes.add(quiz);
                                Log.d(TAG, "Quiz added: " + quizTitle);
                            }
                        } else {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, type1, "index1");
                                liveQuizzes.add(quiz);
                                Log.d(TAG, "Quiz added: " + quizTitle);
                            }
                        }
                    } else {
                        Log.d(TAG, "Quiz is not paid.");
                        if (title.isEmpty() || title.equals("exam")) {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, "Free", "Z");
                                liveQuizzes.add(quiz);
                                Log.d(TAG, "Quiz added: " + quizTitle);
                            }
                        } else {
                            if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                                QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, "Free", "index1");
                                liveQuizzes.add(quiz);
                                Log.d(TAG, "Quiz added: " + quizTitle);
                            }
                        }
                    }

                } else if (field instanceof Boolean) {
                    Log.d(TAG, "Quiz has a boolean type field.");
                    if (title.isEmpty() || title.equals("exam")) {
                        if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                            QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, "Free", "index1");
                            liveQuizzes.add(quiz);
                            Log.d(TAG, "Quiz added: " + quizTitle);
                        }
                    } else {
                        if ((title.isEmpty() || speciality.equals(title)) && now.compareTo(from) >= 0 && now.compareTo(to) <= 0) {
                            QuizSSExam quiz = new QuizSSExam(quizTitle, speciality, to, id, "Free", "index1");
                            liveQuizzes.add(quiz);
                            Log.d(TAG, "Quiz added: " + quizTitle);
                        }
                    }
                }
            }
        }
    }

    private void updateUIForQuizzes() {
        if (liveQuizzes.isEmpty()) {
            Log.d(TAG, "No live quizzes found.");
            binding.nocardpg.setVisibility(View.VISIBLE); // Show the 'nocardpg' layout
        } else {
            Log.d(TAG, "Live quizzes found, updating UI.");
            binding.nocardpg.setVisibility(View.GONE); // Hide the 'nocardpg' layout
        }
        binding.progressBar.setVisibility(View.GONE);
    }

    private void updateUIForNoQuizzes() {
        Log.d(TAG, "No quizzes to display, updating UI accordingly.");
        binding.nocardpg.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Toast message displayed: " + message);
    }
}
