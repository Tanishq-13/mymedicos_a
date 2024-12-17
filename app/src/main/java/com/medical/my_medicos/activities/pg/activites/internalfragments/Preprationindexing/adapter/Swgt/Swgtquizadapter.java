package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter.Swgt;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.NeetssExamPayment;
import com.medical.my_medicos.activities.pg.activites.NeetExamPayment;
import com.medical.my_medicos.activities.pg.activites.PGSwgtPayement;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class Swgtquizadapter extends RecyclerView.Adapter<Swgtquizadapter.ExamViewHolder> {
    private static final String TAG = "ExamQuizAdapter";  // Added for consistent logging
    private Context context;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private ArrayList<QuizPGExam> quizList;

    public Swgtquizadapter(Context context, ArrayList<QuizPGExam> quizList) {
        this.context = context;
        this.quizList = quizList;
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_list_item_swgt, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        QuizPGExam quiz = quizList.get(position);
        String title = quiz.getTitle();
        if (title.length() > 23) {
            title = title.substring(0, 20) + "...";
        }

        fetchSubscriptionStatusAndAdjustUI(holder, quiz);
        holder.titleTextView.setText(title);
        holder.categorytextview.setText(quiz.getIndex());
        holder.dueDateTextView.setText(formatTimestamp(quiz.getTo()));


        Log.d(TAG, "Binding view holder for position: " + position);

        holder.pay.setOnClickListener(v -> {
            if (quiz.getType().equals("Basic")) {
                Log.d(TAG, "Click event triggered for position: " + position);
                Intent intent = new Intent(v.getContext(), PGSwgtPayement.class);
                intent.putExtra("Title1", quiz.getTitle1());
                intent.putExtra("Title", quiz.getTitle());
                intent.putExtra("From", formatTimestamp(quiz.getTo()));
                intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                intent.putExtra("qid", quiz.getId());
                v.getContext().startActivity(intent);

            } else {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                String phoneNumber = user.getPhoneNumber(); // Replace with the actual phone number

                DocumentReference docRef = db.collection("Subscription").document(phoneNumber);

                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Boolean isPlanActive = document.getBoolean("Active");

                                if (isPlanActive != null && isPlanActive) {
                                    // Get the array field and check if it contains "neetPg"
                                    List<String> fieldArray = (List<String>) document.get("Field"); // Replace "FIELD" with the actual field name

                                    if (fieldArray != null && fieldArray.contains("PGNEET")) {
                                        Log.d(TAG, "Click event triggered for position: " + position);
                                        Intent intent = new Intent(v.getContext(), PGSwgtPayement.class);
                                        intent.putExtra("Title1", quiz.getTitle1());
                                        intent.putExtra("Title", quiz.getTitle());
                                        intent.putExtra("From", formatTimestamp(quiz.getTo()));
                                        intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                                        intent.putExtra("qid", quiz.getId());
                                        v.getContext().startActivity(intent);

                                        // Show bottom sheet if plan is active and the array contains "neetPg"

                                    } else {
                                        holder.showBottomSheet();
                                        // Handle the case where the array doesn't contain "neetPg"
                                    }
                                } else {
                                    // Handle the case where the plan is not active
                                }
                            } else {
                                // Document does not exist, handle the case here
                            }
                        } else {
                            // Handle errors here
                            Log.d("Firestore", "Failed to retrieve document: ", task.getException());
                        }
                    }
                });

            }
        });
    }
    private void fetchSubscriptionStatusAndAdjustUI(Swgtquizadapter.ExamViewHolder holder, QuizPGExam quiz) {
        if (quiz.getType().equals("Free")){
            holder.lock.setVisibility(View.GONE);
            holder.unlock.setVisibility(View.VISIBLE);
        }
        else {
            String userId = auth.getCurrentUser().getPhoneNumber();
            DocumentReference docRef = db.collection("Subscription").document(userId);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Boolean isActive = documentSnapshot.getBoolean("Active");
                    List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                    if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                        // The user has an active plan that includes "PGNEET"
                        holder.lock.setVisibility(View.GONE);
                        holder.unlock.setVisibility(View.VISIBLE);
                    } else {
                        // The user does not have an active plan or the plan does not include "PGNEET"
                        holder.lock.setVisibility(View.VISIBLE);
                        holder.unlock.setVisibility(View.GONE);
                    }
                } else {
                    // No subscription document found
                    holder.lock.setVisibility(View.VISIBLE);
                    holder.unlock.setVisibility(View.GONE);
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching subscription data", e);
                holder.lock.setVisibility(View.VISIBLE);
                holder.unlock.setVisibility(View.GONE);
            });
        }
    }


    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy");
        return dateFormat.format(timestamp.toDate());
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timestart, timeend;
        Button payforsets;
        LinearLayout pay;
        FirebaseDatabase database;

        TextView dueDateTextView;
        TextView categorytextview;

        LinearLayout  lock, unlock;
        String currentUid;
        int coins = 50;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            pay = itemView.findViewById(R.id.payfortheexam);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
            categorytextview = itemView.findViewById(R.id.categoryTextView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dueDateTextView = itemView.findViewById(R.id.dateTextView);


        }
        private void showBottomSheet() {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_paid, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
            btnBuyPlan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bottomSheetDialog.dismiss();
                    Toast.makeText(context, "Purchase action triggered", Toast.LENGTH_SHORT).show();
                }
            });

            bottomSheetDialog.show();
        }
    }
    private String formatTimestamp(String timestamp) {
        try {
            // Adjust the format here to match the actual input format
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"); // Example format
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

            java.util.Date date = inputFormat.parse(timestamp);
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return an empty string or handle the error as needed
        }
    }
}
