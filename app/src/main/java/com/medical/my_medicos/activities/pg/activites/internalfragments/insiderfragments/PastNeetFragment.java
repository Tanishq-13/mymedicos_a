package com.medical.my_medicos.activities.pg.activites.internalfragments.insiderfragments;

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
import com.medical.my_medicos.databinding.FragmentPastNeetBinding;
import com.medical.my_medicos.activities.pg.activites.internalfragments.intwernaladapters.ExamPastAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PastNeetFragment extends Fragment {
    private FragmentPastNeetBinding binding;
    private ArrayList<QuizPGExam> Livepg;
    private ExamPastAdapter LiveAdapter;
    private FirebaseUser currentUser;
    private String title1 = "Exam";

    public static PastNeetFragment newInstance() {
        PastNeetFragment fragment = new PastNeetFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPastNeetBinding.inflate(inflater, container, false);

        Livepg = new ArrayList<>();
        LiveAdapter = new ExamPastAdapter(requireContext(), Livepg);

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

            CollectionReference quizCollection = db.collection("PGupload").document("Weekley").collection("Quiz");
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
                            List<Object> dataList = (List<Object>) document.get("data");
                            int dataSize = (dataList != null) ? dataList.size() : 0;  // Get the size of the 'Data' array (number of questions)

                            if (paid1 == true) {
                                    Object field = document.get("type");
                                    if (field instanceof String) {
                                        String type1 = document.getString("type");
                                        if (type1.equals("Paid")) {
                                            type1=document.getString("hyOption");
                                            if (title.isEmpty() || title.equals("Exam")) {
                                                if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                    QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "index1",String.valueOf(dataSize));
                                                    Livepg.add(quiz);
                                                }
                                            } else {
                                                if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                    QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "index1",String.valueOf(dataSize));
                                                    Livepg.add(quiz);
                                                }
                                            }
                                        }
                                        else{
                                            if (title.isEmpty() || title.equals("Exam")) {
                                                if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                    QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "index1",String.valueOf(dataSize));
                                                    Livepg.add(quiz);
                                                }
                                            } else {
                                                if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                    QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, type1, "index1",String.valueOf(dataSize));
                                                    Livepg.add(quiz);
                                                }
                                            }

                                        }

                                    } else if (field instanceof Boolean) {
                                        if (title.isEmpty() || title.equals("Exam")) {

                                            if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, "Basic", "index1",String.valueOf(dataSize));
                                                Livepg.add(quiz);
                                            }
                                        } else {
                                            if (now.compareTo(to) > 0 && (title.isEmpty() || speciality.equals(title))) {
                                                QuizPGExam quiz = new QuizPGExam(quizTitle, speciality, to, id, "Basic", "index1",String.valueOf(dataSize));
                                                Livepg.add(quiz);
                                            }
                                        }
                                    }



                            }
                        }
                        // Sort the Livepg list here by the 'from' date
                        Collections.sort(Livepg, Comparator.comparing(QuizPGExam::getTo));
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
