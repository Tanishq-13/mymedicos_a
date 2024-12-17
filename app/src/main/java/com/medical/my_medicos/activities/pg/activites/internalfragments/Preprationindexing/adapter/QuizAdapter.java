package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter;

import static androidx.media3.common.MediaLibraryInfo.TAG;

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
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.PgPrepPayement;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.Index.NotesData;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.twgt.Quiz;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private List<Quiz> quizList;
    private Context context;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public QuizAdapter(Context context, List<Quiz> quizList) {
        this.quizList = quizList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Quiz quiz = quizList.get(position);
        String title = quiz.getTitle();
        if (title.length() > 23) {
            title = title.substring(0, 20) + "...";
        }

        checkSubscriptionStatusAndAdjustUI(holder, quiz);

        holder.titleTextView.setText(title);
        holder.categorytextview.setText(quiz.getIndex());

        com.google.firebase.Timestamp t = quiz.getDueDate();
        Date date = t.toDate();

        // Format the Date
        String formattedDate = formatTimestamp(date);
        holder.dueDateTextView.setText(formattedDate);

        Log.d("QuizAdapter", "Title1: " + quiz.getTitle1());
        Log.d("QuizAdapter", "Title: " + quiz.getTitle());
        Log.d("QuizAdapter", "Id: " + quiz.getId());

        holder.pay.setOnClickListener(v -> {
            if (quiz.getType().equals("Free")) {
                Intent intent = new Intent(context, PgPrepPayement.class);
                intent.putExtra("Title1", quiz.getTitle1());
                intent.putExtra("Title", quiz.getTitle());
                intent.putExtra("Id", quiz.getId());
                intent.putExtra("Due", formattedDate);
                context.startActivity(intent);

            } else {
                holder.showBottomSheet();

            }
        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void checkSubscriptionStatusAndAdjustUI(QuizAdapter.QuizViewHolder holder, Quiz note) {
        if (note.getType().equals("Free")){
            holder.lock.setVisibility(View.GONE);
            holder.unlock.setVisibility(View.VISIBLE);
        }
        else {
            String userId = auth.getCurrentUser().getPhoneNumber();
            Log.d("WeeklyQuizAdapter", "Fetching subscription status for user: " + userId);
            DocumentReference docRef = db.collection("Subscription").document(userId);

            docRef.get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    Boolean isActive = documentSnapshot.getBoolean("Active");
                    List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                    if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && note.getType().equals(documentSnapshot.getString("hyOption"))) {
                        Log.d("WeeklyQuizAdapter", "Subscription is active for PGNEET and type matches: " + note.getType());
                        holder.lock.setVisibility(View.GONE);
                        holder.unlock.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("WeeklyQuizAdapter", "Subscription is not active or type doesn't match: " + note.getType());
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
    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    // Method to update the quiz list
    public void updateQuizList(List<Quiz> newQuizList) {
        quizList.clear();
        quizList.addAll(newQuizList);
        notifyDataSetChanged();
    }

    class QuizViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView dueDateTextView;
        TextView categorytextview;
        LinearLayout pay, lock, unlock;

        public QuizViewHolder(@NonNull View itemView) {
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

            @SuppressLint({"MissingInflatedId", "LocalSuppress"})
            Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
            btnBuyPlan.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(context, "Purchase action triggered", Toast.LENGTH_SHORT).show();
            });

            bottomSheetDialog.show();
        }
    }

    private String formatTimestamp(Date date) {
        try {
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return ""; // Return an empty string or handle the error as needed
        }
    }
}
