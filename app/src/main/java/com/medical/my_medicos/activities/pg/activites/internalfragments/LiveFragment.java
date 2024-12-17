package com.medical.my_medicos.activities.pg.activites.internalfragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.home.model.Doubt;
import com.medical.my_medicos.activities.pg.activites.ChatActivity;
import com.medical.my_medicos.activities.pg.adapters.ChatAdapter;
import com.medical.my_medicos.activities.pg.adapters.DoubtAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiveFragment extends Fragment implements DoubtAdapter.DoubtClickListener {

    private FirebaseFirestore firestore;
    String message = "";
    private
    RecyclerView chatRecyclerView;
    ChatAdapter chatAdapter;
    String hh;
    private DoubtAdapter adapter;
    private List<Doubt> doubts;

    public LiveFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_live, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewLive);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firestore = FirebaseFirestore.getInstance();
        doubts = new ArrayList<>();
        adapter = new DoubtAdapter(doubts, this);
        recyclerView.setAdapter(adapter);

        fetchDoubts();

        return view;
    }

    private void fetchDoubts() {
        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();

        firestore.collection("MentorChats").document(userPhoneNumber)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<Map<String, Object>> allChats = (List<Map<String, Object>>) documentSnapshot.get("all");

                        if (allChats != null) {
                            for (Map<String, Object> chat : allChats) {
                                Boolean completed = (Boolean) chat.get("completed");
                                String chatId = (String) chat.get("id");
                                String name = (String) chat.get("name");
                                Log.d("namehe", name);
                                if (completed != null && !completed) {
                                    name=heading(name);
                                    fetchMessages(chatId, name);

                                }

                            }
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("LiveFragment", "Error fetching chats", e));
    }

    private void fetchMessages(String chatId, String name) {
        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
//        hh = heading(name);
        firestore.collection("MentorChats")
                .document(userPhoneNumber)
                .collection(chatId)
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    for (QueryDocumentSnapshot messageDoc : querySnapshot) {
                        message = messageDoc.getString("message");
                        String sender = messageDoc.getString("sender");
                    }
                    doubts.add(new Doubt(name, message, false, chatId));
                    adapter.notifyDataSetChanged();
                })

                .addOnFailureListener(e -> Log.e("LiveFragment", "Error fetching messages", e));

    }

    private String heading(String nm) {
        String s = "";
        for (int i = 0; i < nm.length(); i++) {
            if (nm.charAt(i) == '@') {
                continue;
            }
            if (nm.charAt(i) == '/') {
                return s;
            } else {
                s += "" + nm.charAt(i);
            }
        }
        return s;
    }

    @Override
    public void onDoubtClick(String chatId) {
        loadChatMessages(chatId);
    }

    void loadChatMessages(String chatId) {
        Log.d("chatId", chatId);

        // Create an Intent to start ChatActivity
        Intent intent = new Intent(getContext(), ChatActivity.class);

        // Add extras: chatId and a confirmation flag
        intent.putExtra("chatId", chatId);
        intent.putExtra("isnew", "exists");

        // Start the ChatActivity
        startActivity(intent);
    }

}
