package com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index.NotesData;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.NotesAdapter;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes.NotePreviewBottomSheet;
import com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes.PreviewActivity;

import java.util.ArrayList;
import java.util.List;

public class PreprationIndexNotesNotes extends Fragment implements com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.NotesAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.adapter.NotesAdapter adapter;
    private List<com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index.NotesData> notesList;
    private FirebaseFirestore db;
    private String speciality;

    public PreprationIndexNotesNotes() {
        // Required empty public constructor
    }

    public static com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexNotesNotes newInstance(String speciality) {
        com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexNotesNotes fragment = new com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.tablayouts.notes.PreprationIndexNotesNotes();
        Bundle args = new Bundle();
        args.putString("speciality", speciality);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            speciality = getArguments().getString("speciality");
        }

        db = FirebaseFirestore.getInstance();
        notesList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_prepration_index_notes_notes, container, false);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotesAdapter(notesList, this,getContext());
        recyclerView.setAdapter(adapter);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getPhoneNumber();
            DatabaseReference neetssRef = FirebaseDatabase.getInstance()
                    .getReference("profiles")
                    .child(userId)
                    .child("Neetss");

            neetssRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String neetssValue = dataSnapshot.getValue(String.class);
                        if (neetssValue != null) {
                            // Adjust neetssValue to match the required format if necessary
//                            String formattedNeetssValue = formatNeetssValue(neetssValue);
                            fetchNotesData(neetssValue);
                        } else {
                            Log.e("RealtimeDB", "Neetss value is null");
                        }
                    } else {
                        Log.e("RealtimeDB", "Neetss node does not exist");
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.e("RealtimeDB", "Failed to read Neetss value", databaseError.toException());
                }
            });
        } else {
            Log.e("RealtimeDB", "User is not authenticated");
        }



        return view;
    }

    private void fetchNotesData(String neetssValue) {
        db.collection("Neetss")
                .document(neetssValue) // Document fetched from Realtime Database
                .collection("Indexs")
                .document("Index")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            notesList.clear();

                            // Retrieve fields from the document
                            String descriptionField = document.getString("Description");
                            String titleField = ((DocumentSnapshot) document).getString("Title");
                            String fileField = document.getString("file");
                            String typeField = document.getString("type");

                            if (typeField != null && typeField.equals("Paid")) {
                                typeField = document.getString("hyOption");
                            } else {
                                typeField = "Free";
                            }

                            // Create a NotesData object and set the values
                            com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index.NotesData notesData = new com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index.NotesData();
                            notesData.setDescription(descriptionField);
                            notesData.setTitle(titleField);
                            notesData.setFile(fileField);
                            notesData.setType(typeField);  // Set the type field

                            // Add the object to the notesList
                            notesList.add(notesData);

                            // Notify the adapter about the dataset change
                            adapter.notifyDataSetChanged();
                        } else {
                            Log.e("FirestoreError", "Document does not exist");
                        }
                    } else {
                        Log.e("FirestoreError", "Error getting document: ", task.getException());
                    }
                });
    }



    @Override
    public void onNoteClick(com.medical.my_medicos.activities.neetss.activites.internalfragments.Preprationindexing.Model.Index.NotesData note) {
        NotePreviewBottomSheet bottomSheet = new NotePreviewBottomSheet(note, this::openPreviewActivity);
        bottomSheet.show(getParentFragmentManager(), "NotePreviewBottomSheet");
    }

    private void openPreviewActivity(NotesData note) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        intent.putExtra("fileUrl", note.getFile());
        startActivity(intent);
    }
}
