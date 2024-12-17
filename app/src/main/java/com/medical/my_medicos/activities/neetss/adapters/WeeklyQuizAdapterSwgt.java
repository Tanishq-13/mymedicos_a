package com.medical.my_medicos.activities.neetss.adapters;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.model.QuizFmge;
import com.medical.my_medicos.activities.neetss.activites.SsPrepPayement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.widget.Toast;

import com.medical.my_medicos.activities.neetss.model.QuizSSExam;
import com.medical.my_medicos.activities.neetss.model.Swgtmodel;
import com.medical.my_medicos.activities.pg.activites.PgPrepPayement;

//import java.security.Timestamp;


public class WeeklyQuizAdapterSwgt extends RecyclerView.Adapter<WeeklyQuizAdapterSwgt.ViewHolder> {
    private Context context;
    private ArrayList<QuizSSExam> quizList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public WeeklyQuizAdapterSwgt(Context context, ArrayList<QuizSSExam> quizList) {
        this.context = context;
        this.quizList = quizList;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
        this.auth = FirebaseAuth.getInstance(); // Initialize FirebafseAuth
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_list_item_swgt, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizSSExam quiz = quizList.get(position);
        String title = quiz.getTitle();
        if (title.length() > 23) {
            title = title.substring(0, 20) + "...";
        }
        holder.titleTextView.setText(title);
        holder.time.setText(formatTimestamp(quiz.getTo()));

        // Automatically fetch subscription status and set visibility when binding the view
        fetchSubscriptionStatusAndAdjustUI(holder, quiz);

        holder.pay.setOnClickListener(v -> {
            if (quiz.getType().equals("Free")) {
                Log.d(TAG, "Click event triggered for position: " + position);
                Intent intent = new Intent(v.getContext(), PgPrepPayement.class);
                intent.putExtra("Title1", quiz.getTitle1());
                intent.putExtra("Title", quiz.getTitle());
                intent.putExtra("From", formatTimestamp(quiz.getTo()));
                intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                intent.putExtra("qid", quiz.getId());
                v.getContext().startActivity(intent);
            } else {
                // Fetch subscription status before allowing payment
                fetchSubscriptionStatusAndProceed(holder, quiz);
            }
        });
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    private void fetchSubscriptionStatusAndAdjustUI(ViewHolder holder, QuizSSExam quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && quiz.getType().equals(documentSnapshot.getString("hyOption")) ) {
                    // The user has an active plan that includes "NEETPG"
                    holder.lock.setVisibility(View.GONE);
                    holder.unlock.setVisibility(View.VISIBLE);
                } else {
                    // The user does not have an active plan or the plan does not include "NEETPG"
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

    private void fetchSubscriptionStatusAndProceed(ViewHolder holder, QuizSSExam quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("NEETPG") && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                    Log.d(TAG, "Click event triggered for position: " + quiz.getTitle());
                    Intent intent = new Intent(holder.itemView.getContext(), PgPrepPayement.class);
                    intent.putExtra("Title1", quiz.getTitle1());
                    intent.putExtra("Title", quiz.getTitle());
                    intent.putExtra("From", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Id", quiz.getId());
                    holder.itemView.getContext().startActivity(intent);
                } else {
                    holder.showBottomSheet();
                }
            } else {
                holder.showBottomSheet();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching subscription data", e);
            holder.showBottomSheet();
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, time;
        Button payforsets;
        LinearLayout pay, lock, unlock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            time = itemView.findViewById(R.id.dateTextView);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
            payforsets = itemView.findViewById(R.id.paymentpart);
            pay = itemView.findViewById(R.id.payfortheexam);
        }

        private void showBottomSheet() {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_paid, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
            btnBuyPlan.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(context, "Purchase action triggered", Toast.LENGTH_SHORT).show();
            });

            bottomSheetDialog.show();
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Log.d("date", dateFormat.format(timestamp.toDate()));
        return dateFormat.format(timestamp.toDate());
    }
}
