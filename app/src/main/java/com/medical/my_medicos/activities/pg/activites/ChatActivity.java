package com.medical.my_medicos.activities.pg.activites;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.medical.my_medicos.R;
import com.medical.my_medicos.activities.pg.adapters.ChatAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

public class ChatActivity extends AppCompatActivity {

    private ImageView sndbtn;
    private int fl=0;
    private RecyclerView chatRecyclerView;
    private EditText messageInput;
    private String neworexist;
    private String chatId;
    private ChatAdapter chatAdapter;
    private FirebaseUser currentUser;
    private FirebaseFirestore firestore;
    private String subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
//        chatRecyclerView=findViewById(R.id.chatlist);
//        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        chatAdapter = new ChatAdapter(this);
//        chatRecyclerView.setAdapter(chatAdapter);
        // Handle edge insets
//        messageInput.requestFocus();
//        String
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firestore
        firestore = FirebaseFirestore.getInstance();

        // Retrieve subject from Intent
        subject = getIntent().getStringExtra("Subjectname");
        neworexist=getIntent().getStringExtra("isnew");

//         Initialize UI elements
        sndbtn = findViewById(R.id.sendbtn);
        messageInput = findViewById(R.id.appCompatEditText);
        chatId = UUID.randomUUID().toString();

        // Generate a random chat ID
        // Set up send button click listener
        if(neworexist.equals("new")) {
            sndbtn.setOnClickListener(v -> {

                String messageText = messageInput.getText().toString().trim();
                if (!messageText.isEmpty()) {
                    if(fl==0)
                        initializeChatRequest();

                    sendMessage(messageText);
                    messageInput.setText("");
                    fl=1;// Clear input field after sending
                    updateui(chatId);
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            chatId=getIntent().getStringExtra("chatId");
            updateui(chatId);
            sndbtn.setOnClickListener(v -> {

                String messageText = messageInput.getText().toString().trim();
                if (!messageText.isEmpty()) {
//                    if(fl==0)
//                        initializeChatRequest();
                    sendMessage(messageText);
                    messageInput.setText("");
                    fl=1;// Clear input field after sending
                    updateui(chatId);
                } else {
                    Toast.makeText(ChatActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            });
        }

        // Initialize the chat request document
    }

    private void initializeChatRequest() {
        DocumentReference chatRef = firestore.collection("MentorChatRequests").document(chatId);
        Map<String, Object> chatData = new HashMap<>();
        chatData.put("acceptedBy", "none"); // Initialize, can be updated when mentor accepts
        chatData.put("accepted", false);
        chatData.put("chatId", chatId);
        chatData.put("section", "PGNEET");
        chatData.put("speciality", subject); // Replace with actual speciality
        chatData.put("time", Timestamp.now());
        chatData.put("user", "+919999999999"); // Replace with actual user ID
        Map<String, Object> mentor = new HashMap<>();
        mentor.put("id", ""); // Replace with actual mentor ID
        mentor.put("name", ""); // Replace with actual mentor name

// Add the nested map to chatData
        chatData.put("mentor", mentor);
        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DocumentReference mentorChatsRef = firestore.collection("MentorChats").document(userPhoneNumber);

        // Create the new map to add to the 'all' array
        Map<String, Object> newChatEntry = new HashMap<>();
        newChatEntry.put("completed", false);
        newChatEntry.put("id", chatId);

        // Map for mentor with empty fields
        Map<String, String> mentorMap = new HashMap<>();
        mentorMap.put("id", "");
        mentorMap.put("name", "");

        // Add mentor map and name to newChatEntry
        newChatEntry.put("mentor", mentorMap);
        newChatEntry.put("name", '@'+subject); // Replace with the desired subject

        // Add the new chat entry to the 'all' array
        mentorChatsRef.update("all", FieldValue.arrayUnion(newChatEntry))
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "New chat entry added to 'all' array"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "Error adding chat entry to 'all' array", e));

        // Set data in Firestore
        chatRef.set(chatData)
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "Chat request created with chatId: " + chatId))
                .addOnFailureListener(e -> Log.e("ChatActivity", "Error creating chat request", e));
//        addChatToAllArray();


    }

    private void addChatToAllArray() {
        // Reference to the MentorChats document for the current user
        String userPhoneNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        DocumentReference mentorChatsRef = firestore.collection("MentorChats").document(userPhoneNumber);

        // Create the new map to add to the 'all' array
        Map<String, Object> newChatEntry = new HashMap<>();
        newChatEntry.put("completed", false);
        newChatEntry.put("id", chatId);

        // Map for mentor with empty fields
        Map<String, String> mentorMap = new HashMap<>();
        mentorMap.put("id", "");
        mentorMap.put("name", "");

        // Add mentor map and name to newChatEntry
        newChatEntry.put("mentor", mentorMap);
        newChatEntry.put("name", '@'+subject); // Replace with the desired subject

        // Add the new chat entry to the 'all' array
        mentorChatsRef.update("all", FieldValue.arrayUnion(newChatEntry))
                .addOnSuccessListener(aVoid -> Log.d("ChatActivity", "New chat entry added to 'all' array"))
                .addOnFailureListener(e -> Log.e("ChatActivity", "Error adding chat entry to 'all' array", e));
    }

    private void sendMessage(String messageText) {
//        chatId = UUID.randomUUID().toString();
        String msgclctn=UUID.randomUUID().toString();
        // Reference to Messages subcollection in MentorChatRequests document
        CollectionReference messagesRef = firestore.collection("MentorChats")
                .document(currentUser.getPhoneNumber())
                .collection(chatId);

        // Message data
        Map<String, Object> messageData = new HashMap<>();
        messageData.put("message", messageText);
        messageData.put("sender", "user"); // Replace with actual sender ID
        messageData.put("time", Timestamp.now());
//        messageData.put("isSentByUser", true);

        // Add message to Firestore
        messagesRef.add(messageData)
                .addOnSuccessListener(documentReference -> Log.d("ChatActivity", "Message sent: " + messageText))
                .addOnFailureListener(e -> Log.e("ChatActivity", "Error sending message", e));
    }
    private void updateui(String cid){
        CollectionReference messagesRef;
        chatRecyclerView = findViewById(R.id.chatlist);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setAdapter(chatAdapter);

        // Define Firestore collection path
        String phoneNumber = currentUser.getPhoneNumber();
//        String cid = getIntent().getStringExtra("chatId"); // Get chatId from intent
        messagesRef = firestore.collection("MentorChats").document(phoneNumber).collection(cid);

        messagesRef.orderBy("time", Query.Direction.ASCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("ChatActivity", "Listen failed.", e);
                    return;
                }

                List<Map<String, Object>> messages = new ArrayList<>();
                if (snapshots != null) {
                    for (DocumentSnapshot doc : snapshots.getDocuments()) {
                        messages.add(doc.getData());
                    }
                }
                Log.d("pol","p");
                // Update adapter with new messages
                chatAdapter.setMessages(messages);
                chatRecyclerView.scrollToPosition(messages.size() - 1); // Scroll to the latest message
            }
        });

    }
}
