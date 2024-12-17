package com.medical.my_medicos.activities.neetss.adapters;

import static androidx.media3.common.MediaLibraryInfo.TAG;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.model.PerDaySS;
import com.medical.my_medicos.activities.pg.animations.CorrectAnswerActivity;
import com.medical.my_medicos.activities.pg.animations.WrongAnswerActivity;
import com.medical.my_medicos.activities.pg.model.PerDayPG;
import com.medical.my_medicos.databinding.QuestionPerDayDesignBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class PerDaySSAdapter extends RecyclerView.Adapter<PerDaySSAdapter.DailyQuestionSSViewHolder> {

    private Context context;
    private ArrayList<PerDaySS> dailyquestions;
    PerDaySSAdapter PerDayPGAdapter;
    private String selectedOption;
    private long lastSelectionTimestamp;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    String user = currentUser.getPhoneNumber();


    public PerDaySSAdapter(Context context, ArrayList<PerDaySS> questions) {
        this.context = context;
        this.dailyquestions = questions;
        this.lastSelectionTimestamp = 0;
    }

    @NonNull
    @Override
    public DailyQuestionSSViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DailyQuestionSSViewHolder(LayoutInflater.from(context).inflate(R.layout.question_per_day_design, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DailyQuestionSSViewHolder holder, int position) {
        PerDaySS dailyquestion = dailyquestions.get(position);

        Glide.with(context);
        holder.binding.questionspan.setText(dailyquestion.getDailyQuestion());
        holder.binding.optionA.setText(dailyquestion.getDailyQuestionA());
        holder.binding.optionB.setText(dailyquestion.getDailyQuestionB());
        holder.binding.optionC.setText(dailyquestion.getDailyQuestionC());
        holder.binding.optionD.setText(dailyquestion.getDailyQuestionD());

        holder.binding.optionA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOptionClick(holder, "A");
                compareAndShowResult(holder, dailyquestion);
            }
        });

        holder.binding.optionB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOptionClick(holder, "B");
                compareAndShowResult(holder, dailyquestion);
            }
        });

        holder.binding.optionC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOptionClick(holder, "C");
                compareAndShowResult(holder, dailyquestion);
            }
        });

        holder.binding.optionD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleOptionClick(holder, "D");
                compareAndShowResult(holder, dailyquestion);
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user=currentUser.getPhoneNumber();

        if (System.currentTimeMillis() - lastSelectionTimestamp >= 24 * 60 * 60 * 1000) {
            holder.binding.questionsbox.setVisibility(View.VISIBLE);
        } else {
            holder.binding.questionsbox.setVisibility(View.GONE);
        }
    }

    private void handleOptionClick(DailyQuestionSSViewHolder holder, String selectedOption) {
        resetOptionStyle(holder);
        switch (selectedOption) {
            case "A":
                setOptionSelectedStyle(holder.binding.optionA);
                break;
            case "B":
                setOptionSelectedStyle(holder.binding.optionB);
                break;
            case "C":
                setOptionSelectedStyle(holder.binding.optionC);
                break;
            case "D":
                setOptionSelectedStyle(holder.binding.optionD);
                break;
        }
        this.selectedOption = selectedOption;
    }

    private void compareAndShowResult(DailyQuestionSSViewHolder holder, PerDaySS dailyquestion) {
        if (selectedOption != null) {
            lastSelectionTimestamp = System.currentTimeMillis();
            holder.binding.questionsbox.setVisibility(View.GONE);

            String correctAnswer = dailyquestion.getSubmitDailyQuestion();
            String docId = Preferences.userRoot().get("docId", "");

            if (selectedOption.equals(correctAnswer)) {
                showCorrectAnswerPopup(dailyquestion.getSubmitDailyQuestion(), dailyquestion.getDescription());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersCollection = db.collection("users");

                Query query = usersCollection.whereEqualTo("Phone Number", user);

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                updateDocument(document.getId(), String.valueOf(dailyquestion.getidQuestion()));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

            } else {
                showWrongAnswerPopup(dailyquestion.getSubmitDailyQuestion(), dailyquestion.getDescription());
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference usersCollection = db.collection("users");

                Query query = usersCollection.whereEqualTo("Phone Number", user);

                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @OptIn(markerClass = UnstableApi.class)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                updateDocument1(document.getId(), String.valueOf(dailyquestion.getidQuestion()));
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
            }
        } else {
            showToast("Please select an option");
        }
    }


    private void setOptionSelectedStyle(TextView option) {
        option.setBackgroundResource(R.drawable.selectedoptionbk);
        option.setTextColor(Color.WHITE);
        option.setTypeface(null, Typeface.BOLD);
    }
    private void updateDocument(String documentId, String QuizToday) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(documentId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("QuizToday", QuizToday);
        updates.put("CurrentTime", System.currentTimeMillis());

        updates.put("Streak", FieldValue.increment(1));

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @OptIn(markerClass = UnstableApi.class) @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(context.getApplicationContext(), "Successfully Ended", Toast.LENGTH_SHORT).show();

                        Log.d("abc", "bcd");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @OptIn(markerClass = UnstableApi.class) @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
    private void updateDocument1(String documentId, String QuizToday) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(documentId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("QuizToday", QuizToday);
        updates.put("CurrentTime", System.currentTimeMillis());

        docRef.update(updates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @OptIn(markerClass = UnstableApi.class) @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                        Toast.makeText(context.getApplicationContext(), "Successfully Ended", Toast.LENGTH_SHORT).show();

                        Log.d("abc", "bcd");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @OptIn(markerClass = UnstableApi.class) @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    private void resetOptionStyle(DailyQuestionSSViewHolder holder) {
        holder.binding.optionA.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.binding.optionA.setTextColor(Color.BLACK);
        holder.binding.optionA.setTypeface(null, Typeface.NORMAL);

        holder.binding.optionB.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.binding.optionB.setTextColor(Color.BLACK);
        holder.binding.optionB.setTypeface(null, Typeface.NORMAL);

        holder.binding.optionC.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.binding.optionC.setTextColor(Color.BLACK);
        holder.binding.optionC.setTypeface(null, Typeface.NORMAL);

        holder.binding.optionD.setBackgroundResource(R.drawable.questionsoptionbk);
        holder.binding.optionD.setTextColor(Color.BLACK);
        holder.binding.optionD.setTypeface(null, Typeface.NORMAL);
    }

    private void showCorrectAnswerPopup(String correctOption, String description) {
        Intent intent = new Intent(context, CorrectAnswerActivity.class);
        intent.putExtra("correctOption", correctOption);
        intent.putExtra("description", description);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    private void showWrongAnswerPopup(String correctOption, String description) {
        Intent intent = new Intent(context, WrongAnswerActivity.class);
        intent.putExtra("correctOption", correctOption);
        intent.putExtra("description", description);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
    @Override
    public int getItemCount() {
        return dailyquestions.size();
    }

    public class DailyQuestionSSViewHolder extends RecyclerView.ViewHolder {

        QuestionPerDayDesignBinding binding;

        public DailyQuestionSSViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = QuestionPerDayDesignBinding.bind(itemView);
        }
    }
}