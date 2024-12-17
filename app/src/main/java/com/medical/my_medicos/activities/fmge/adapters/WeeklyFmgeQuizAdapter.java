package com.medical.my_medicos.activities.fmge.adapters;

import static androidx.media3.common.MediaLibraryInfo.TAG;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import androidx.annotation.OptIn;
import androidx.media3.common.util.UnstableApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.collection.LLRBNode;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.fmge.model.QuizFmge;
import com.medical.my_medicos.activities.fmge.activites.FmgePrepPayement;
import com.medical.my_medicos.activities.pg.activites.PgPrepPayement;
import com.medical.my_medicos.activities.pg.activites.resultexists;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyFmgeQuizAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_HEADING = 0;
    private static final int TYPE_ITEM = 1;

    private Context context;
    private ArrayList<Object> items;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    public WeeklyFmgeQuizAdapter(Context context, ArrayList<QuizPG> quizList) {
        this.context = context;
        this.db = FirebaseFirestore.getInstance(); // Initialize Firestore
        this.auth = FirebaseAuth.getInstance(); // Initialize FirebaseAuth

        populateItems(quizList); // Populate the items list
        Log.d("WeeklyFmgeQuizAdapter", "Total items in adapter: " + items.size());
    }

    private void populateItems(ArrayList<QuizPG> quizList) {
        this.items = new ArrayList<>();
        HashMap<String, ArrayList<QuizPG>> headingMap = new HashMap<>();

        // Group quizzes by heading (index)
        for (QuizPG quiz : quizList) {
            String heading = quiz.getIndex();

            // If the heading already exists in the map, add the quiz to its list
            if (headingMap.containsKey(heading)) {
                headingMap.get(heading).add(quiz);
            } else {
                // Otherwise, create a new list for this heading and add the quiz
                ArrayList<QuizPG> quizzes = new ArrayList<>();
                quizzes.add(quiz);
                headingMap.put(heading, quizzes);
            }
        }

        // Populate the items list with headings and quizzes, ensuring unique headings
        for (Map.Entry<String, ArrayList<QuizPG>> entry : headingMap.entrySet()) {
            String heading = entry.getKey();
            ArrayList<QuizPG> quizzes = entry.getValue();

            // Add the heading and its corresponding quizzes
            items.add(heading);
            items.addAll(quizzes);
        }

        // Handle the case where the quizList might be empty
        if (items.isEmpty()) {
            Log.d("WeeklyFmgeQuizAdapter", "No quizzes found, consider showing an empty state.");
        }
    }

    public void updateQuizList(ArrayList<QuizPG> newQuizList) {
        Log.d("cf", String.valueOf(newQuizList.size()));
        populateItems(newQuizList);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof String) {
            Log.d("WeeklyFmgeQuizAdapter", "Item at position " + position + " is a heading");
            return TYPE_HEADING;
        } else {
            Log.d("WeeklyFmgeQuizAdapter", "Item at position " + position + " is a quiz item");
            return TYPE_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADING) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_heading, parent, false);
            return new HeadingViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.quiz_list_item2, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @OptIn(markerClass = UnstableApi.class)
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_HEADING) {
            String index = (String) items.get(position);
            Log.d("WeeklyFmgeQuizAdapter", "Binding heading: " + index + " at position " + position);
            HeadingViewHolder headingHolder = (HeadingViewHolder) holder;
            headingHolder.headingTextView.setText(index);
        } else {
            QuizPG quiz = (QuizPG) items.get(position);
            Log.d("WeeklyFmgeQuizAdapter", "Binding quiz: " + quiz.getTitle() + " at position " + position);
            ItemViewHolder itemHolder = (ItemViewHolder) holder;
            String title = quiz.getTitle();
            if (title.length() > 23) {
                title = title.substring(0, 20) + "...";
            }
            itemHolder.titleTextView.setText(title);
            Log.d("hyocheck",quiz.getTitle1());
            itemHolder.hyo.setText(quiz.getTitle1());
            itemHolder.noq.setText(quiz.getSize()+" MCQs");
            Log.d("isSolvedadapter", String.valueOf(quiz.isSolved())+" "+quiz.getId());

            if(quiz.isProgress()){
                itemHolder.btn.setBackgroundResource(R.drawable.quizlistitemsolve);
                itemHolder.btn.setText("Resume");
            }
            else if(quiz.isSolved()){
                Log.d("isSolvedadapter", String.valueOf(quiz.isSolved())+" "+quiz.getId());
                itemHolder.btn.setBackgroundResource(R.drawable.quizlistitemresume);
                itemHolder.btn.setText("Result");
            }
            else {
                itemHolder.btn.setText("Solve");
                itemHolder.btn.setBackgroundResource(R.drawable.quizlistitemsolve);

            }
            String thumbnailUrl = quiz.getThumbnail();
            Glide.with(context)
                    .load(thumbnailUrl) // Error image in case of failure
                    .into(itemHolder.thumbnailImageView);
//            fetchSubscriptionStatusAndAdjustUI(itemHolder, quiz);

            itemHolder.btn.setOnClickListener(v -> {
                String userId = auth.getCurrentUser().getPhoneNumber();

                DocumentReference quizResultRef = db.collection("QuizResults")
                        .document(userId)
                        .collection("Weekley")
                        .document(quiz.getId());

                quizResultRef.get().addOnSuccessListener(quizResultSnapshot -> {
                            if (quizResultSnapshot.exists()) {
                                itemHolder.btn.setText("Result");
                                // Extract quiz result data
                                String unans = String.valueOf(quizResultSnapshot.get("Unanswered"));
                                String crans = String.valueOf(quizResultSnapshot.get("Correct Answers"));
                                String wrans = String.valueOf(quizResultSnapshot.get("Wrong Answers"));
                                String cmnt = (String) quizResultSnapshot.get("Comment");
                                String qid = (String) quizResultSnapshot.get("Quiz ID");
                                String score = String.valueOf(quizResultSnapshot.get("score"));
                                String mrks = String.valueOf(quizResultSnapshot.get("Total Marks Obtained"));
                                String totq = String.valueOf(quizResultSnapshot.get("Total Questions"));

                                // Calculate skipped questions
                                int sk = Integer.parseInt(totq) - (Integer.parseInt(crans) + Integer.parseInt(wrans));
                                String skippedques = String.valueOf(sk);
                                Log.d("scrr",score);
                                // Open the resultexists activity directly
                                Intent intent = new Intent(holder.itemView.getContext(), resultexists.class);
                                intent.putExtra("skippedques", skippedques);
                                intent.putExtra("quizid", qid);
                                intent.putExtra("crans", crans);
                                intent.putExtra("wrans", wrans);
                                intent.putExtra("cmnt", cmnt);
                                intent.putExtra("score", score);
                                intent.putExtra("section","FMGE");
                                intent.putExtra("mrks", mrks);
                                intent.putExtra("unans", unans);
                                intent.putExtra("totq", totq);
                                holder.itemView.getContext().startActivity(intent);
                            } else {
                                // If quiz result does not exist, show the AlertDialog
                                Log.d("WeeklyQuizAdapter", "Pay button clicked for quiz: " + quiz.getTitle());

                                new AlertDialog.Builder(v.getContext())
                                        .setTitle("Take Quiz on Laptop for better Experience.\n")
                                        .setMessage("Still wish to proceed?")

                                        // No button (Yes to proceed with the quiz on the app)
                                        .setNegativeButton("Yes", (dialog, which) -> {
                                            Log.d("WeeklyQuizAdapter", "User chose to continue with quiz on app.");
                                            if (true) {
                                                Intent intent = new Intent(v.getContext(), FmgePrepPayement.class);
                                                intent.putExtra("Title1", quiz.getTitle1());
                                                intent.putExtra("Title", quiz.getTitle());
                                                intent.putExtra("toptext", quiz.getType());
                                                intent.putExtra("From", formatTimestamp(quiz.getTo()));
                                                intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                                                intent.putExtra("Id", quiz.getId());
                                                v.getContext().startActivity(intent);
                                            } else {
                                                fetchSubscriptionStatusAndProceed(itemHolder, quiz);
                                            }
                                            dialog.dismiss();
                                        })

                                        // Yes button (No to proceed)
                                        .setPositiveButton("No", (dialog, which) -> {
                                            Log.d("WeeklyQuizAdapter", "User chose to take the quiz on the laptop.");
                                            dialog.dismiss();
                                        })
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .show();
                            }
                        })
                        .addOnFailureListener(e -> {
                            Log.e(TAG, "Error fetching quiz result", e);
                        });
            });
            String userId = auth.getCurrentUser().getPhoneNumber();



        }
    }

    @Override
    public int getItemCount() {
        Log.d("WeeklyFmgeQuizAdapter", "Getting item count: " + items.size());
        return items.size();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void fetchSubscriptionStatusAndAdjustUI(ItemViewHolder holder, QuizPG quiz) {
        holder.btn.setText("SOLVE");
        holder.btn.setBackgroundColor(Color.BLACK);
//        String userId = auth.getCurrentUser().getPhoneNumber();
//        Log.d("WeeklyQuizAdapter", "Fetching subscription status for user: " + userId);
//        DocumentReference docRef = db.collection("Subscription").document(userId);
//        DocumentReference quizProgressRef = db.collection("QuizProgress").document(userId);
//
//        // Reference to the quiz result for this quiz
//        DocumentReference quizResultRef = db.collection("QuizResults")
//                .document(userId)
//                .collection("Weekley")
//                .document(quiz.getId());
//
//        // First, check if this quiz has a result in QuizResults -> phoneNumber -> Weekley -> quizId
//        quizResultRef.get().addOnSuccessListener(quizResultSnapshot -> {
//            if (quizResultSnapshot.exists()) {
//                // Quiz result exists, set button to "Results" and change color to blue
//                Log.d("WeeklyQuizAdapter", "Quiz result found, showing Results button.");
//                holder.btn.setText("Results");
//                holder.btn.setBackgroundColor(Color.BLUE);
//            } else {
//                // Quiz result doesn't exist, proceed with checking the subscription status
//                docRef.get().addOnSuccessListener(documentSnapshot -> {
//                    if (documentSnapshot.exists()) {
//                        Boolean isActive = documentSnapshot.getBoolean("Active");
//                        List<String> fieldArray = (List<String>) documentSnapshot.get("Field");
//
//                        if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET")) {
//                            Log.d("WeeklyQuizAdapter", "Subscription is active for PGNEET and type matches: " + quiz.getType());
//
//                            // Check if the quiz has been previously paused (not submitted)
//                            quizProgressRef.collection("pgneet").document(quiz.getId()).get().addOnSuccessListener(quizProgressDoc -> {
//                                if (quizProgressDoc.exists()) {
//                                    Boolean submitted = quizProgressDoc.getBoolean("submitted");
//
//                                    if (submitted != null && !submitted) {
//                                        // Quiz is paused (not submitted), show "Resume"
//                                        Log.d("WeeklyQuizAdapter", "Quiz has been paused, showing Resume button.");
//                                        holder.btn.setText("Resume");
//                                        holder.btn.setBackgroundColor(Color.BLACK);
//                                    } else {
//                                        // Quiz was never paused, show "Solve"
//                                        Log.d("WeeklyQuizAdapter", "Quiz has not been paused, showing Solve button.");
//                                        holder.btn.setText("Solve");
//                                        holder.btn.setBackgroundColor(Color.BLACK);
//                                    }
//                                } else {
//                                    // No quiz progress found, show "Solve"
//                                    Log.d("WeeklyQuizAdapter", "No progress found for quiz, showing Solve button.");
//                                    holder.btn.setText("Solve");
//                                    holder.btn.setBackgroundColor(Color.BLACK);
//                                }
//                            }).addOnFailureListener(e -> {
//                                Log.e(TAG, "Error fetching quiz progress", e);
//                            });
//
//                        } else {
//                            // Subscription is not active or the type doesn't match
//                            Log.d("WeeklyQuizAdapter", "Subscription is not active or type doesn't match: " + quiz.getType() + " " + documentSnapshot.getId());
//
//                            // Set the button to "Unlock" with gray background
//                            holder.btn.setText("Solve");
//                            holder.btn.setBackgroundColor(Color.BLACK);
//                        }
//                    } else {
//                        // No subscription document found
//                        Log.d("WeeklyQuizAdapter", "No subscription document found for user: " + userId);
//
//                        // Set the button to "Unlock" with gray background
//                        holder.btn.setText("Solve");
//                        holder.btn.setBackgroundColor(Color.BLACK);
//                    }
//                }).addOnFailureListener(e -> {
//                    Log.e(TAG, "Error fetching subscription data", e);
//                });
//            }
//        }).addOnFailureListener(e -> {
//            Log.e(TAG, "Error fetching quiz result", e);
//        });
    }

    @OptIn(markerClass = UnstableApi.class)
    private void fetchSubscriptionStatusAndProceed(ItemViewHolder holder, QuizPG quiz) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        Log.d("WeeklyFmgeQuizAdapter", "Fetching subscription status for proceeding with payment for user: " + userId);
        DocumentReference docRef = db.collection("Subscription").document(userId);//test222

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && quiz.getType().equals(documentSnapshot.getString("hyOption"))) {
                    Log.d("WeeklyFmgeQuizAdapter", "Subscription is active and type matches, proceeding to payment for quiz: " + quiz.getTitle());
                    Intent intent = new Intent(holder.itemView.getContext(), FmgePrepPayement.class);
                    intent.putExtra("Title1", quiz.getTitle1());
                    intent.putExtra("Title", quiz.getTitle());
                    intent.putExtra("From", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Due", formatTimestamp(quiz.getTo()));
                    intent.putExtra("Id", quiz.getId());
                    holder.itemView.getContext().startActivity(intent);
                } else {
                    Log.d("WeeklyFmgeQuizAdapter", "Subscription is not active or type doesn't match, showing bottom sheet.");
                    holder.showBottomSheet();
                }
            } else {
                Log.d("WeeklyFmgeQuizAdapter", "No subscription document found, showing bottom sheet.");
                holder.showBottomSheet();
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error fetching subscription data", e);
            holder.showBottomSheet();
        });
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        public ImageView thumbnailImageView;

        LinearLayout pay;
        TextView noq,hyo;
        Button btn;


        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            pay = itemView.findViewById(R.id.payfortheexam);
            noq=itemView.findViewById(R.id.no_of_ques);
            thumbnailImageView = itemView.findViewById(R.id.thumbnailImageView);

            hyo=itemView.findViewById(R.id.hyoption);
            btn=itemView.findViewById(R.id.kismat);

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
