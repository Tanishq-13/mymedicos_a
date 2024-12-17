package com.medical.my_medicos.activities.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.NeetExamPayment;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class LiveExamAdapter extends RecyclerView.Adapter<LiveExamAdapter.QuizViewHolder> {

    private Context context;
    private List<QuizPG> quizList; // Use List instead of ArrayList for flexibility
    private static final String TAG = "LiveExamAdapter";
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    // Constructor
    public LiveExamAdapter(Context context, List<QuizPG> quizList) {
        this.context = context;
        this.quizList = quizList != null ? quizList : new ArrayList<>(); // Initialize to avoid null issues
    }

    // Method to update the quiz list data
    public void updateData(List<QuizPG> newQuizList) {
        this.quizList = newQuizList; // Update the quizList
        notifyDataSetChanged(); // Notify the adapter about the dataset change
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_exam_home, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        QuizPG quiz = quizList.get(position);

        // Log quiz details for debugging
        Log.d(TAG, "Quiz at position " + position + ":");
        Log.d(TAG, "Title: " + quiz.getTitle());
        Log.d(TAG, "Speciality: " + quiz.getType());
        Log.d(TAG, "Type: " + quiz.getType());
        Log.d(TAG, "To Timestamp: " + quiz.getTo().toDate().toString());
        Log.d(TAG, "Quiz ID: " + quiz.getId());
        Log.d(TAG, "Index: " + quiz.getIndex());

        // Set title to the TextView
        holder.title.setText(quiz.getTitle());

        // Set UI visibility based on the quiz type
//        if ("Free".equals(quiz.getType())) {
//            holder.liveSection.setVisibility(View.GONE);
//            holder.neetPGSection.setVisibility(View.VISIBLE);
//            Log.d(TAG, "Quiz Type is 'Free', showing NEET PG section.");
//        } else {
//            holder.neetPGSection.setVisibility(View.GONE);
//            holder.liveSection.setVisibility(View.VISIBLE);
//            Log.d(TAG, "Quiz Type is not 'Free', showing Live section.");
//        }
        if (quiz.getType().equals("Free")){
            holder.lock.setVisibility(View.GONE);
            holder.unlock.setVisibility(View.VISIBLE);
        }
        else {
            fetchSubscriptionStatusAndAdjustUI(holder, quiz);
        }

        // Setting dummy values for MCC and Time (replace with real data if available)
        holder.mccText.setText("200 MCC's");
        holder.timeText.setText("210 mins");

        // Log dummy data for MCC and Time
        Log.d(TAG, "MCC Text: 200 MCC's");
        Log.d(TAG, "Time Text: 210 mins");

        // Set onClickListener for Solve Now button
        holder.solveNowButton.setOnClickListener(v -> {
            // Handle button click (you can navigate to another activity or perform an action here)
            if (quiz.getType().equals("Basic") ||  (quiz.getType().equals("Free"))) {
                Log.d(TAG, "Click event triggered for position: " + position);
                Intent intent = new Intent(v.getContext(), NeetExamPayment.class);
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
            Log.d(TAG, "Solve Now button clicked for Quiz ID: " + quiz.getId());
        });
    }
    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd, MMMM yyyy");
        return dateFormat.format(timestamp.toDate());
    }
    private void fetchSubscriptionStatusAndProceed(QuizViewHolder holder, QuizPG quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET")  && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                    Log.d(TAG, "Click event triggered for position: " + quiz.getTitle());
                    Intent intent = new Intent(holder.itemView.getContext(), NeetExamPayment.class);
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

    private void fetchSubscriptionStatusAndAdjustUI(LiveExamAdapter.QuizViewHolder holder, QuizPG quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        Log.d("WeeklyQuizAdapter", "Fetching subscription status for user: " + userId);
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                    Log.d("WeeklyQuizAdapter", "Subscription is active for PGNEET and type matches: " + quiz.getType());
                    holder.lock.setVisibility(View.GONE);
                    holder.unlock.setVisibility(View.VISIBLE);
                } else {
                    Log.d("WeeklyQuizAdapter", "Subscription is not active or type doesn't match: " + quiz.getType());
                    holder.lock.setVisibility(View.VISIBLE);
                    holder.unlock.setVisibility(View.GONE);
                }
            } else {
                Log.d("WeeklyQuizAdapter", "No subscription document found for user: " + userId);
                holder.lock.setVisibility(View.VISIBLE);
                holder.unlock.setVisibility(View.GONE);
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching subscription data", e);
            holder.lock.setVisibility(View.VISIBLE);
            holder.unlock.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return quizList != null ? quizList.size() : 0; // Return the size of the quiz list
    }

    // ViewHolder class to bind quiz data to UI elements
    public static class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView title, mccText, timeText;
        LinearLayout liveSection, neetPGSection;
        Button solveNowButton;
        ImageView lock,unlock;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize views
            title = itemView.findViewById(R.id.title);
            liveSection = itemView.findViewById(R.id.live_section);
            neetPGSection = itemView.findViewById(R.id.neet_pg_section);
            mccText = itemView.findViewById(R.id.mcc_text);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
            timeText = itemView.findViewById(R.id.time_text);
            solveNowButton = itemView.findViewById(R.id.solve_now_button);
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
