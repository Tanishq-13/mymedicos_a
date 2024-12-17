package com.medical.my_medicos.activities.neetss.activites.internalfragments.intwernaladapters;

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
import com.medical.my_medicos.activities.neetss.model.QuizSSExam;
import com.medical.my_medicos.activities.neetss.activites.NeetssExamPayment;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
public class ExamQuizAdapter extends RecyclerView.Adapter<ExamQuizAdapter.ExamViewHolder> {
    private static final String TAG = "ExamQuizAdapter";  // Added for consistent logging
    private Context context;
    private ArrayList<QuizSSExam> quizList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public ExamQuizAdapter(Context context, ArrayList<QuizSSExam> quizList) {
        this.context = context;
        this.quizList = quizList;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
        this.auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
    }

    @NonNull
    @Override
    public ExamViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_list_item, parent, false);
        return new ExamViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExamViewHolder holder, int position) {
        QuizSSExam quiz = quizList.get(position);
        String title = quiz.getTitle();
        if (title.length() > 23) {
            title = title.substring(0, 20) + "...";
        }
        holder.titleTextView.setText(title);
        holder.categorytextview.setText(quiz.getIndex());
        holder.dueDateTextView.setText(formatTimestamp(quiz.getTo()));

        // Automatically fetch subscription status and set visibility when binding the view
        fetchSubscriptionStatusAndAdjustUI(holder, quiz);

        holder.pay.setOnClickListener(v -> {
            if (quiz.getType().equals("Free")) {
                Log.d(TAG, "Click event triggered for position: " + position);
                Intent intent = new Intent(v.getContext(), NeetssExamPayment.class);
                intent.putExtra("Title1", quiz.getTitle1());
                intent.putExtra("Title", quiz.getTitle());
                intent.putExtra("From", formatTimestamp(quiz.getTo()));
                intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                intent.putExtra("qid", quiz.getId());
                v.getContext().startActivity(intent);
            } else {
                // Reuse the same method to handle click events based on subscription status
                fetchSubscriptionStatusAndProceed(holder, quiz);
            }
        });
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy");
        return dateFormat.format(timestamp.toDate());
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    private void fetchSubscriptionStatusAndAdjustUI(ExamViewHolder holder, QuizSSExam quiz) {
        if("Basic".equals(quiz.getType())){
            holder.lock.setVisibility(View.GONE);
            holder.unlock.setVisibility(View.VISIBLE);
        }
        else {
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
    }

    private void fetchSubscriptionStatusAndProceed(ExamViewHolder holder, QuizSSExam quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET")  && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                    Log.d(TAG, "Click event triggered for position: " + quiz.getTitle());
                    Intent intent = new Intent(holder.itemView.getContext(), NeetssExamPayment.class);
                    intent.putExtra("Title1", quiz.getTitle1());
                    intent.putExtra("Title", quiz.getTitle());
                    intent.putExtra("From", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                    intent.putExtra("qid", quiz.getId());
                    holder.itemView.getContext().startActivity(intent);
                } else {
                    // Handle the case where the plan is not active or does not include the required field
                    holder.showBottomSheet();
                }
            } else {
                // Document does not exist
                holder.showBottomSheet();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching subscription data", e);
            holder.showBottomSheet(); // Handle failure by showing the bottom sheet as well
        });
    }

    public static class ExamViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, dueDateTextView, categorytextview;
        LinearLayout lock, unlock, pay;

        public ExamViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            dueDateTextView = itemView.findViewById(R.id.dateTextView);
            categorytextview = itemView.findViewById(R.id.categoryTextView);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
            pay = itemView.findViewById(R.id.payfortheexam);
        }

        private void showBottomSheet() {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(itemView.getContext());
            View bottomSheetView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.bottom_sheet_up_paid, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
            btnBuyPlan.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(itemView.getContext(), "Purchase action triggered", Toast.LENGTH_SHORT).show();
            });

            bottomSheetDialog.show();
        }
    }
}
