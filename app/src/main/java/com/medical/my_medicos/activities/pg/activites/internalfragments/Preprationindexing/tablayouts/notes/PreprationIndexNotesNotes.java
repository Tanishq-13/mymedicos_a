package com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.tablayouts.notes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.Model.Index.NotesData;
import com.medical.my_medicos.activities.pg.activites.internalfragments.Preprationindexing.adapter.NotesAdapter;

import java.util.ArrayList;
import java.util.List;

public class PreprationIndexNotesNotes extends Fragment implements NotesAdapter.OnNoteClickListener {

    private RecyclerView recyclerView;
    private NotesAdapter adapter;
    LottieAnimationView emptyAnimation;
    private List<NotesData> notesList;
    private FirebaseFirestore db;
    private String speciality;

    public PreprationIndexNotesNotes() {
        // Required empty public constructor
    }

    public static PreprationIndexNotesNotes newInstance(String speciality) {
        PreprationIndexNotesNotes fragment = new PreprationIndexNotesNotes();
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
        emptyAnimation = view.findViewById(R.id.empty_animation);
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NotesAdapter(notesList, this,getContext());
        recyclerView.setAdapter(adapter);

        fetchNotesData();

        return view;
    }

    private void fetchNotesData() {
        db.collection("PGupload").document("Notes").collection("Note")
                .whereEqualTo("speciality", speciality)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        notesList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String descriptionField = document.getString("Description");
                            String titleField = document.getString("Title");
                            String fileField = document.getString("file");
                            String typeField = document.getString("type");
                            if (typeField.equals("Paid")){
                                typeField=document.getString("hyOption");
                                // Fetch the type field
                            }
                            else{
                                typeField="BASIC";
                            }

                            NotesData notesData = new NotesData();
                            notesData.setDescription(descriptionField);
                            notesData.setTitle(titleField);
                            notesData.setFile(fileField);
                            notesData.setType(typeField);  // Set the type field

                            notesList.add(notesData);
                        }
                        if(notesList.isEmpty()) {
                            emptyAnimation.setVisibility(View.VISIBLE);  // Show animation
                            recyclerView.setVisibility(View.GONE);       // Hide RecyclerView
                        } else {
                            emptyAnimation.setVisibility(View.GONE);     // Hide animation
                            recyclerView.setVisibility(View.VISIBLE);    // Show RecyclerView
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Log.e("FirestoreError", "Error getting documents: ", task.getException());
                    }
                });

    }


    @Override
    public void onNoteClick(NotesData note) {
        NotePreviewBottomSheet bottomSheet = new NotePreviewBottomSheet(note, this::openPreviewActivity);
        bottomSheet.show(getParentFragmentManager(), "NotePreviewBottomSheet");
    }

    private void openPreviewActivity(NotesData note) {
        Intent intent = new Intent(getContext(), PreviewActivity.class);
        intent.putExtra("fileUrl", note.getFile());
        startActivity(intent);
    }
}