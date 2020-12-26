package com.capitipalismcorp.ui.support;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.holders.ChatHolder;
import com.capitipalismcorp.models.Chat;
import com.capitipalismcorp.ui.supportchat.ChatActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class ChatFragment extends Fragment {
    private FirebaseRecyclerAdapter chatsListContainerAdapter;

    public ChatFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View chatListContainerRecyclerView = inflater.inflate(R.layout.fragment_chat, container, false);

        // region Initialize 'Chat' Database (Firebase Realtime Database Instance)
        CapiUserManager.loadUserData(this.requireContext());
        String currentUserID = CapiUserManager.getCurrentUserID();
        Toast.makeText(getContext(), "Current User ID: " + currentUserID, Toast.LENGTH_LONG).show();
        DatabaseReference chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserID);
        // Setup database for offline use
        chatDatabase.keepSynced(true);
        // endregion

        // region Setup Recycler View That Contains Chat List
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        RecyclerView recyclerView = chatListContainerRecyclerView.findViewById(R.id.chat_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), linearLayoutManager.getOrientation());
        recyclerView.addItemDecoration(mDividerItemDecoration);
        // endregion

        // region Initializing chatsListContainerAdapter (Adapter that contains users list that the current user is chatting with)
        FirebaseRecyclerOptions<Chat> firebaseRecyclerOptions = new FirebaseRecyclerOptions
                .Builder<Chat>()
                .setQuery(
                        chatDatabase.orderByChild("timestamp"),
                        Chat.class
                )
                .build();

        chatsListContainerAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(firebaseRecyclerOptions) {
            @Override
            public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user, parent, false);

                return new ChatHolder(getActivity(), view, getContext());
            }

            @Override
            protected void onBindViewHolder(final ChatHolder chatHolder, int position, final Chat chatModel) {
                final String chatUserId = getRef(position).getKey();

                // Set view for a single chat instance (single list)
                chatHolder.setHolder(chatUserId, chatModel.getMessage(), chatModel.getTimestamp(), chatModel.getSeen());
                // Set on click listener for a single list item
                chatHolder.getView().setOnClickListener(view1 -> {
                    Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                    chatIntent.putExtra("userID", chatUserId);
                    startActivity(chatIntent);
                });
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();


                TextView chatListContainerStatusIndicator = chatListContainerRecyclerView.findViewById(R.id.f_chat_text);

                // region If chat list is empty show 'there is no chat' text in the list
                if (chatsListContainerAdapter.getItemCount() == 0) {
                    chatListContainerStatusIndicator.setVisibility(View.VISIBLE);
                } else {
                    chatListContainerStatusIndicator.setVisibility(View.GONE);
                }
                // endregion
            }
        };

        recyclerView.setAdapter(chatsListContainerAdapter);
        // endregion
        return chatListContainerRecyclerView;
    }

    @Override
    public void onStart() {
        super.onStart();
        chatsListContainerAdapter.startListening();
        chatsListContainerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatsListContainerAdapter.stopListening();
    }
}
