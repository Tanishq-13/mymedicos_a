package com.medical.my_medicos.activities.pg.adapters;

import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.Timestamp;
import com.medical.my_medicos.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private static final int VIEW_TYPE_USER = 1;
    private static final int VIEW_TYPE_MENTOR = 2;
    private static final int VIEW_TYPE_d = 1;
    private static final int VIEW_TYPE_c = 2;

    private Context context;
    private List<Map<String, Object>> messages;

    public ChatAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    public void setMessages(List<Map<String, Object>> messages) {
        this.messages = messages;
        String lastDate = null;
        SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Map<String, Object> messageData = messages.get(position);
        String sender = (String) messageData.get("sender");
        return sender.equals("user") ? VIEW_TYPE_USER : VIEW_TYPE_MENTOR;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = (viewType == VIEW_TYPE_USER) ? R.layout.chatitem : R.layout.chatitem;

        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Map<String, Object> messageData = messages.get(position);
        holder.messageTextView.setText((String) messageData.get("message"));

        // Optional: Format timestamp
        Timestamp timestamp = (Timestamp) messageData.get("time");
        if (timestamp != null) {
            holder.timeTextView.setText(new SimpleDateFormat("hh:mm a").format(timestamp.toDate()));
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }
    public static class DateViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;

        public DateViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
        }
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView;
        TextView timeTextView;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageTextView = itemView.findViewById(R.id.messageTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
        }
    }
}
