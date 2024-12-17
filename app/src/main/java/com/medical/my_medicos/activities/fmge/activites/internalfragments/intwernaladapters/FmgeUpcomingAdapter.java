package com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
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
import com.medical.my_medicos.activities.fmge.model.QuizFmgeExam;
import com.medical.my_medicos.activities.pg.activites.NeetExamPayment;
import com.medical.my_medicos.activities.pg.model.QuizPGExam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
public class FmgeUpcomingAdapter extends RecyclerView.Adapter<com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters.FmgeUpcomingAdapter.ExamUpcomingViewHolder> {
    private static final String TAG = "ExamQuizAdapter";  // Added for consistent logging
    private Context context;
    private ArrayList<QuizFmgeExam> quizList;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public FmgeUpcomingAdapter(Context context, ArrayList<QuizFmgeExam> quizList) {
        this.context = context;
        this.quizList = quizList;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
        this.auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth
    }

    @NonNull
    @Override
    public com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters.FmgeUpcomingAdapter.ExamUpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.quiz_upcoming_list_item, parent, false);
        return new com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters.FmgeUpcomingAdapter.ExamUpcomingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters.FmgeUpcomingAdapter.ExamUpcomingViewHolder holder, int position) {
        QuizFmgeExam quiz = quizList.get(position);
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
            // Schedule the exam based on the current UI state
            holder.checkSubscriptionAndProceed(
                    () -> holder.openCalendarToAddEvent(), // Action if subscription is valid
                    () -> holder.showBottomSheet() ,quiz        // Action if subscription is invalid
            );
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

    private void fetchSubscriptionStatusAndAdjustUI(com.medical.my_medicos.activities.fmge.activites.internalfragments.intwernaladapters.FmgeUpcomingAdapter.ExamUpcomingViewHolder holder, QuizFmgeExam quiz) {
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

    public class ExamUpcomingViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timestart, timeend;
        Button payforsets;
        LinearLayout pay;
        LinearLayout lock, unlock;

        public ExamUpcomingViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize components
            titleTextView = itemView.findViewById(R.id.titleSets);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
            timeend = itemView.findViewById(R.id.availablefromtime);
            timestart = itemView.findViewById(R.id.availabletilltime);
            payforsets = itemView.findViewById(R.id.paymentpart);
            pay = itemView.findViewById(R.id.schedulefortheexam);
        }

        private void showBottomSheet() {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
            View bottomSheetView = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_up_paid, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
            btnBuyPlan.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(context, "Purchase action triggered", Toast.LENGTH_SHORT).show();
            });

            bottomSheetDialog.show();
        }

        private void checkSubscriptionAndProceed(Runnable onSuccess, Runnable onFailure,QuizFmgeExam quiz) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                String phoneNumber = user.getPhoneNumber();
                if (phoneNumber != null) {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    DocumentReference docRef = db.collection("Subscription").document(phoneNumber);

                    docRef.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Boolean isPlanActive = document.getBoolean("Active");

                                if (isPlanActive != null && isPlanActive) {
                                    List<String> fieldArray = (List<String>) document.get("Field");

                                    if (fieldArray != null && fieldArray.contains("NEETPG") && quiz.getType().equals(document.getString("hyOption"))) {
                                        // Call the success action if the conditions are met
                                        onSuccess.run();
                                    } else {
                                        onFailure.run();
                                    }
                                } else {
                                    onFailure.run();
                                }
                            } else {
                                onFailure.run();
                            }
                        } else {
                            onFailure.run();
                            Log.d("Firestore", "Failed to retrieve document: ", task.getException());
                        }
                    });
                }
            }
        }

        private void openCalendarToAddEvent() {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                QuizFmgeExam quiz = quizList.get(position);
                Timestamp startTimestamp = quiz.getTo();
                String title = quiz.getTitle();

                // Create and show AlertDialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Confirm Event");
                builder.setMessage("Do you want to schedule this exam on your calendar?");

                builder.setPositiveButton("Confirm", (dialog, which) -> createCalendarEvent(startTimestamp, title));
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }

        private void createCalendarEvent(Timestamp startTimestamp, String title) {
            Intent intent = new Intent(Intent.ACTION_INSERT);
            intent.setType("vnd.android.cursor.item/event");
            intent.putExtra(CalendarContract.Events.TITLE, title);
            intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startTimestamp.toDate().getTime());
            intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, startTimestamp.toDate().getTime() + 60 * 60 * 1000); // Assuming a 1-hour duration

            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            } else {
                Log.d(TAG, "No Calendar app found.");
            }
        }
    }
}

