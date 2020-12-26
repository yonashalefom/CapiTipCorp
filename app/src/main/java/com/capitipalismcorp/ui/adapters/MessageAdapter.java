package com.capitipalismcorp.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.holders.MessageHolder;
import com.capitipalismcorp.models.Message;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageHolder> {
    private List<Message> messagesList;

    public MessageAdapter(List<Message> messagesList) {
        this.messagesList = messagesList;
    }

    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);

        return new MessageHolder(view, view.getContext());
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
//        CapiUserManager.loadUserData();
        final String currentUserId = CapiUserManager.getCurrentUserID();

        Message messageItem = messagesList.get(position);

        if (messagesList.size() - 1 == position) {
            holder.setLastMessage(currentUserId, messageItem.getFrom(), messageItem.getTo());
        } else {
            holder.hideBottom();
        }

        if (messageItem.getFrom().equals(currentUserId)) {
            holder.setRightMessage(messageItem.getFrom(), messageItem.getMessage(), messageItem.getTimestamp(), messageItem.getType());
        } else {
            holder.setLeftMessage(messageItem.getFrom(), messageItem.getMessage(), messageItem.getTimestamp(), messageItem.getType());
        }
    }

    @Override
    public int getItemCount() {
        return messagesList.size();
    }
}
