package com.medical.my_medicos.activities.pg.activites.internalfragments.intwernaladapters;

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

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.PGPastGTResult;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
public class ExamPastAdapter extends RecyclerView.Adapter<ExamPastAdapter.ExamPastViewHolder> {
    private static final String TAG = "ExamQuizAdapter";  // Added for consistent logging
    private Context context;
    private ArrayList<QuizPGExam> quizList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ExamPastAdapter(Context context, ArrayList<QuizPGExam> quizList) {
        this.context = context;
        this.quizList = quizList;
        db = FirebaseFirestore.getInstance(); // Initialize Firestore
        auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
    }

    @NonNull
    @Override
    public ExamPastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_past_item, parent, false);
        return new ExamPastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamPastViewHolder holder, int position) {
        QuizPGExam quiz = quizList.get(position);
        String title = quiz.getTitle();
        if (title.length() > 23) {
            title = title.substring(0, 20) + "...";
        }
        holder.titleTextView.setText(title);
        holder.timestart.setText(formatTimestamp(quiz.getTo()));
        holder.timeend.setText(formatTimestamp(quiz.getTo()));

        // Automatically fetch subscription status and set visibility when binding the view
        fetchSubscriptionStatusAndAdjustUI(holder, quiz);

        holder.pay.setOnClickListener(v -> {
            // Proceed with the quiz action based on the current UI state
            fetchSubscriptionStatusAndProceed(holder, quiz);
        });

        Log.d(TAG, "Binding view holder for position: " + position);
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy");
        return dateFormat.format(timestamp.toDate());
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    private void fetchSubscriptionStatusAndAdjustUI(ExamPastViewHolder holder, QuizPGExam quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field"); // Replace "Field" with your actual field name

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

    private void fetchSubscriptionStatusAndProceed(ExamPastViewHolder holder, QuizPGExam quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field"); // Replace "Field" with your actual field name

                // Check if the plan is active and includes the "PGNEET"
                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                    // Reference to the QuizResults collection for the current user
                    DocumentReference quizResultRef = db.collection("QuizResults")
                            .document(userId)
                            .collection("Exam")
                            .document(quiz.getId());

                    // Check if the document exists in the QuizResults collection
                    quizResultRef.get().addOnSuccessListener(documentSnapshot1 -> {
                        if (documentSnapshot1.exists()) {
                            // Quiz has been attempted, proceed to the intended activity
                            Intent intent = new Intent(holder.itemView.getContext(), PGPastGTResult.class);
                            intent.putExtra("Title1", quiz.getTitle1());
                            intent.putExtra("Title", quiz.getTitle());
                            intent.putExtra("From", formatTimestamp(quiz.getTo()));
                            intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                            intent.putExtra("qid", quiz.getId());
                            holder.itemView.getContext().startActivity(intent);
                        } else {
                            // Quiz has not been attempted, show a message or handle accordingly
                            Log.d(TAG, "Quiz has not been attempted.");
                            Toast.makeText(holder.itemView.getContext(), "This quiz has not been attempted.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(e -> {
                        Log.e(TAG, "Error checking quiz attempt status", e);
                        Toast.makeText(holder.itemView.getContext(), "Error checking quiz attempt status.", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    // Plan is not active or does not include "PGNEET", show bottom sheet to buy a plan
                    Log.d(TAG, "User does not have an active subscription.");
                    showBottomSheet(holder);
                }
            } else {
                // No subscription document found, show bottom sheet to buy a plan
                Log.d(TAG, "No subscription document found.");
                showBottomSheet(holder);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching subscription data", e);
            showBottomSheet(holder); // Handle failure by showing the bottom sheet as well
        });
    }

    private void showBottomSheet(ExamPastViewHolder holder) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_paid, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
        btnBuyPlan.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            Toast.makeText(context, "Purchase action triggered", Toast.LENGTH_SHORT).show();
            // You can add more logic here to handle the purchase process
        });

        bottomSheetDialog.show();
    }

    public static class ExamPastViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timestart, timeend;
        Button payforsets;
        LinearLayout lock, unlock;
        LinearLayout pay;

        public ExamPastViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize components
            titleTextView = itemView.findViewById(R.id.titleSets);
            timestart = itemView.findViewById(R.id.availablefromtime);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
            timeend = itemView.findViewById(R.id.availabletilltime);
            payforsets = itemView.findViewById(R.id.paymentpart);
            pay = itemView.findViewById(R.id.pastfortheexam);
        }
    }
}
