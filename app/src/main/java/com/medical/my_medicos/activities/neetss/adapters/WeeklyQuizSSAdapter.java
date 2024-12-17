package com.medical.my_medicos.activities.neetss.adapters;

import static androidx.media3.common.MediaLibraryInfo.TAG;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.SsPrepPayement;
import com.medical.my_medicos.activities.neetss.model.QuizSS;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class WeeklyQuizSSAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADING = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private ArrayList<Object> items;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public WeeklyQuizSSAdapter(Context context, ArrayList<QuizSS> quizList) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
        this.auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        populateItems(quizList); // Populate the items list
        Log.d("WeeklyQuizAdapter", "Total items in adapter: " + items.size());
    }

    private void populateItems(ArrayList<QuizSS> quizList) {
        this.items = new ArrayList<>();
        String lastIndex = "";

        for (QuizSS quiz : quizList) {
            Log.d("WeeklyQuizAdapter", "Processing quiz: " + quiz.getTitle() + " with index: " + quiz.getIndex());

            if (!quiz.getIndex().equals(lastIndex)) {
                Log.d("WeeklyQuizAdapter", "Adding index heading: " + quiz.getIndex());
                items.add(quiz.getIndex());  // Add the index as a heading
                lastIndex = quiz.getIndex();
            }
            items.add(quiz);  // Add the quiz item
        }

        // Handle the case where the quizList might be empty
        if (items.isEmpty()) {
            Log.d("WeeklyQuizAdapter", "No quizzes found, consider showing an empty state.");
        }
    }

    public void updateQuizList(ArrayList<QuizSS> newQuizList) {
        populateItems(newQuizList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            Log.d("WeeklyQuizAdapter", "Item at position " + position + " is a heading");
            return TYPE_HEADING;
        } else {
            Log.d("WeeklyQuizAdapter", "Item at position " + position + " is a quiz item");
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_heading, parent, false);
            return new com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.HeadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.quiz_list_item, parent, false);
            return new com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.ItemViewHolder(view);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADING) {
            String index = (String) items.get(position);
            Log.d("WeeklyQuizAdapter", "Binding heading: " + index + " at position " + position);
            com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.HeadingViewHolder headingHolder = (com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.HeadingViewHolder) holder;
            headingHolder.headingTextView.setText(index);
        } else {
            QuizSS quiz = (QuizSS) items.get(position);
            Log.d("WeeklyQuizAdapter", "Binding quiz: " + quiz.getTitle() + " at position " + position);
            com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.ItemViewHolder itemHolder = (com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.ItemViewHolder) holder;
            String title = quiz.getTitle();
            if (title.length() > 23) {
                title = title.substring(0, 20) + "...";
            }
            itemHolder.titleTextView.setText(title);
            itemHolder.categoryTextView.setText(quiz.getIndex());
            itemHolder.time.setText(formatTimestamp(quiz.getTo()));

            fetchSubscriptionStatusAndAdjustUI(itemHolder, quiz);

            itemHolder.pay.setOnClickListener(v -> {
                Log.d("WeeklyQuizAdapter", "Pay button clicked for quiz: " + quiz.getTitle());
                if (quiz.getType().equals("Free")) {
                    Intent intent = new Intent(v.getContext(), SsPrepPayement.class);
                    intent.putExtra("Title1", quiz.getTitle1());
                    intent.putExtra("Title", quiz.getTitle());
                    intent.putExtra("From", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Id", quiz.getId());
                    v.getContext().startActivity(intent);
                } else {
                    fetchSubscriptionStatusAndProceed(itemHolder, quiz);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("WeeklyQuizAdapter", "Getting item count: " + items.size());
        return items.size();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void fetchSubscriptionStatusAndAdjustUI(com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.ItemViewHolder holder, QuizSS quiz) {
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

    @OptIn(markerClass = UnstableApi.class)
    private void fetchSubscriptionStatusAndProceed(com.medical.my_medicos.activities.neetss.adapters.WeeklyQuizSSAdapter.ItemViewHolder holder, QuizSS quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        Log.d("WeeklyQuizAdapter", "Fetching subscription status for proceeding with payment for user: " + userId);
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                    Log.d("WeeklyQuizAdapter", "Subscription is active and type matches, proceeding to payment for quiz: " + quiz.getTitle());
                    Intent intent = new Intent(holder.itemView.getContext(), SsPrepPayement.class);
                    intent.putExtra("Title1", quiz.getTitle1());
                    intent.putExtra("Title", quiz.getTitle());
                    intent.putExtra("From", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Id", quiz.getId());
                    holder.itemView.getContext().startActivity(intent);
                } else {
                    Log.d("WeeklyQuizAdapter", "Subscription is not active or type doesn't match, showing bottom sheet.");
                    holder.showBottomSheet();
                }
            } else {
                Log.d("WeeklyQuizAdapter", "No subscription document found, showing bottom sheet.");
                holder.showBottomSheet();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching subscription data", e);
            holder.showBottomSheet();
        });
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, time, categoryTextView;
        LinearLayout pay, lock, unlock;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            categoryTextView = itemView.findViewById(R.id.categoryTextView);
            time = itemView.findViewById(R.id.dateTextView);
            pay = itemView.findViewById(R.id.payfortheexam);
            lock = itemView.findViewById(R.id.lock);
            unlock = itemView.findViewById(R.id.unlock);
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
    }

    public class HeadingViewHolder extends RecyclerView.ViewHolder {
        TextView headingTextView;

        public HeadingViewHolder(@NonNull View itemView) {
            super(itemView);
            headingTextView = itemView.findViewById(R.id.headingTextView);
        }
    }

    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(timestamp.toDate());
    }
}
