package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter;

import static androidx.media3.common.MediaLibraryInfo.TAG;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.Index.NotesData;
import com.medical.my_medicos.activities.pg.adapters.WeeklyQuizAdapter;
import com.medical.my_medicos.activities.pg.model.QuizPG;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {

    private List<NotesData> notesList;
    private OnNoteClickListener listener;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private Context context;

    public NotesAdapter(List<NotesData> notesList, OnNoteClickListener listener, Context context) {
        this.notesList = notesList;
        this.listener = listener;
        this.context = context;
        this.db = FirebaseFirestore.getInstance();  // Initialize Firestore
        this.auth = FirebaseAuth.getInstance();  // Initialize FirebaseAuth
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotesData note = notesList.get(position);

        // Set the title
        holder.titleTextView.setText(note.getTitle());

        // Handle the description with HTML content and length restriction
        String description = Html.fromHtml(note.getDescription(), Html.FROM_HTML_MODE_LEGACY).toString();
        if (description.length() > 60) {
            description = description.substring(0, 57) + "...";
        }
        holder.descriptionTextView.setText(note.getDescription());

        // Check subscription status and adjust UI
        checkSubscriptionStatusAndAdjustUI(holder, note);

        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            Log.d("Basicsnew notes",note.getType());
            if ("BASIC".equals(note.getType())) {
                listener.onNoteClick(note);
                // Handle paid note access

            } else {
                handlePaidNoteClick(holder, note);
                // Open the note directly

            }
        });
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @OptIn(markerClass = UnstableApi.class)
    private void checkSubscriptionStatusAndAdjustUI(ViewHolder holder, NotesData note) {
        if (note.getType().equals("BASIC")){
//            holder.lockIcon.setVisibility(View.GONE);
//            holder.unlockIcon.setVisibility(View.VISIBLE);
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
//                        holder.lockIcon.setVisibility(View.GONE);
//                        holder.unlockIcon.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("WeeklyQuizAdapter", "Subscription is not active or type doesn't match: " + note.getType());
//                        holder.lockIcon.setVisibility(View.VISIBLE);
//                        holder.unlockIcon.setVisibility(View.GONE);
                    }
                } else {
                    Log.d("WeeklyQuizAdapter", "No subscription document found for user: " + userId);
//                    holder.lockIcon.setVisibility(View.VISIBLE);
//                    holder.unlockIcon.setVisibility(View.GONE);
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error fetching subscription data", e);
//                holder.lockIcon.setVisibility(View.VISIBLE);
//                holder.unlockIcon.setVisibility(View.GONE);
            });
        }
    }


    private void handlePaidNoteClick(ViewHolder holder, NotesData note) {
        String userId = auth.getCurrentUser().getPhoneNumber();
        DocumentReference docRef = db.collection("Subscription").document(userId);

        docRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                Boolean isActive = documentSnapshot.getBoolean("Active");
                List<String> fieldArray = (List<String>) documentSnapshot.get("Field");

                if (isActive != null && isActive && fieldArray != null && fieldArray.contains("PGNEET") && note.getType().equals(documentSnapshot.getString("hyOption"))) {
                    // User has an active subscription, open the note
                    listener.onNoteClick(note);
                } else {
                    // Show bottom sheet for subscription
                    holder.showBottomSheet();
                }
            } else {
                // Show bottom sheet for subscription
                holder.showBottomSheet();
            }
        }).addOnFailureListener(e -> {
            // Show bottom sheet for subscription
            holder.showBottomSheet();
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.titleTextView);
            descriptionTextView = view.findViewById(R.id.descriptionTextView);
        }

        private void showBottomSheet() {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(itemView.getContext());
            View bottomSheetView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.bottom_sheet_up_paid, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            Button btnBuyPlan = bottomSheetView.findViewById(R.id.btnBuyPlan);
            btnBuyPlan.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                Toast.makeText(itemView.getContext(), "Purchase action triggered", Toast.LENGTH_SHORT).show();
                // Implement the purchase logic here
            });

            bottomSheetDialog.show();
        }

    }

    public interface OnNoteClickListener {
        void onNoteClick(NotesData note);
    }
}
