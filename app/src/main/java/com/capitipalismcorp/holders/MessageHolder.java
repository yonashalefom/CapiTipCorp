package com.capitipalismcorp.holders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.format.DateUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.ui.imageviewer.FullScreenActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageHolder extends RecyclerView.ViewHolder {
    private final String TAG = "CA/MessageHolder";

    private View view;
    private Context context;

    // Will handle User, Chat and Chat Typing data

    private DatabaseReference userDatabase, chatSeenDatabase, chatTypingDatabase;
    private ValueEventListener userListener, chatSeenListener, chatTypingListener;

    public MessageHolder(View view, Context context) {
        super(view);

        this.view = view;
        this.context = context;
    }

    public void hideBottom() {
        final ConstraintLayout messageBottom = view.findViewById(R.id.message_relative_bottom);

        messageBottom.setVisibility(View.GONE);
    }

    public void setLastMessage(final String currentUserId, final String from, final String to) {
        // If the message is the last message in the list
        final TextView messageSeen = view.findViewById(R.id.message_seen);

        final ConstraintLayout messagesTypingContainer = view.findViewById(R.id.messages_typing_container);
        final SpinKitView messageTyping = view.findViewById(R.id.message_typing);
        final CircleImageView messageTypingIndicatorProfilePic = view.findViewById(R.id.message_typing_indicator_profile_pic);

        final ConstraintLayout messageBottom = view.findViewById(R.id.message_relative_bottom);

        messageBottom.setVisibility(View.VISIBLE);

        String otherUserId = from;

        if (from.equals(currentUserId)) {
            otherUserId = to;

            if (chatSeenDatabase != null && chatSeenListener != null) {
                chatSeenDatabase.removeEventListener(chatSeenListener);
            }

            // Initialize/Update seen message on the bottom of the message

            chatSeenDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(to).child(currentUserId);
            chatSeenListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        if (from.equals(currentUserId) && dataSnapshot.hasChild("seen")) {
                            messageSeen.setVisibility(View.VISIBLE);

                            long seen = (long) dataSnapshot.child("seen").getValue();

                            if (seen == 0) {
                                messageSeen.setText("Sent");
                            } else {
                                messageSeen.setText("Seen at " + new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(seen));
                            }
                        } else {
                            messageSeen.setVisibility(View.INVISIBLE);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "chatSeenListerner exception: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.d(TAG, "chatSeenListerner failed: " + databaseError.getMessage());
                }
            };
            chatSeenDatabase.addValueEventListener(chatSeenListener);
        } else {
            messageSeen.setVisibility(View.INVISIBLE);
        }

        if (chatTypingDatabase != null && chatTypingListener != null) {
            chatTypingDatabase.removeEventListener(chatTypingListener);
        }

        // Initialize/Update typing status on the bottom
        String userID = otherUserId;

        chatTypingDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(otherUserId).child(currentUserId);
        chatTypingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChild("typing")) {
                        int typing = Integer.parseInt(dataSnapshot.child("typing").getValue().toString());

                        messageTyping.setVisibility(View.VISIBLE);
                        messagesTypingContainer.setVisibility(View.VISIBLE);


                        // region Initilize/Update user image
                        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                        userListener = new ValueEventListener() {
                            @Override
                            public void onDataChange(final DataSnapshot dataSnapshot) {
                                try {
                                    final String image = dataSnapshot.child("image").getValue().toString();

                                    if (!image.equals("default")) {
                                        Picasso.get()
                                                .load(image)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .networkPolicy(NetworkPolicy.OFFLINE)
                                                .placeholder(R.drawable.user)
                                                .into(messageTypingIndicatorProfilePic, new Callback() {
                                                    @Override
                                                    public void onSuccess() {

                                                    }

                                                    @Override
                                                    public void onError(Exception e) {
                                                        Picasso.get()
                                                                .load(image)
                                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                                .centerCrop()
                                                                .placeholder(R.drawable.user)
                                                                .error(R.drawable.user)
                                                                .into(messageTypingIndicatorProfilePic);
                                                    }
                                                });
                                    } else {
                                        messageTypingIndicatorProfilePic.setImageResource(R.drawable.hyphen_profile_picture_placeholder);
                                    }
                                } catch (Exception e) {
                                    Log.d(TAG, "userDatabase exception: " + e.getMessage());
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "userDatabase failed: " + databaseError.getMessage());
                            }
                        };
                        userDatabase.addValueEventListener(userListener);
                        // endregion

                        if (typing == 1) {
//                            messageTyping.setText("Typing...");
                        } else if (typing == 2) {
//                            messageTyping.setText("Deleting...");
                        } else if (typing == 3) {
//                            messageTyping.setText("Thinking...");
                        } else {
                            messageTyping.setVisibility(View.INVISIBLE);
                            messagesTypingContainer.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        messageTyping.setVisibility(View.INVISIBLE);
                        messagesTypingContainer.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "chatTypingListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "chatTypingListener failed: " + databaseError.getMessage());
            }
        };
        chatTypingDatabase.addValueEventListener(chatTypingListener);
    }

    // region Set Right Message
    public void setRightMessage(String userID, final String message, long time, String type) {
        // If this an upcoming message
        System.out.println("From Right ImageURL: " + message);

        final ConstraintLayout messageLayoutLeft = view.findViewById(R.id.message_relative_left);
        final ConstraintLayout message_main_container_right = view.findViewById(R.id.message_main_container_right);

        final ConstraintLayout messageLayoutRight = view.findViewById(R.id.message_relative_right);
        final TextView messageTextRight = view.findViewById(R.id.message_text_right);
        final TextView messageTimeRight = view.findViewById(R.id.message_time_right);
        final CircleImageView messageImageRight = view.findViewById(R.id.message_image_right);
        final ImageView messageTextPictureRight = view.findViewById(R.id.message_imagetext_right);
        final TextView messageLoadingRight = view.findViewById(R.id.message_loading_right);

        messageLayoutLeft.setVisibility(View.GONE);

        messageLayoutRight.setVisibility(View.VISIBLE);

        if (type.equals("text")) {
            messageTextPictureRight.setVisibility(View.GONE);
            messageLoadingRight.setVisibility(View.GONE);

            messageTextRight.setVisibility(View.VISIBLE);
            messageTextRight.setText(message);

            messageTimeRight.setBackgroundColor(Color.parseColor("#00000000"));
            messageTimeRight.setPadding(0,0,0,0);
            messageTimeRight.setTextColor(Color.parseColor("#60FFFFFF"));

            message_main_container_right.setBackgroundResource(R.drawable.hyphen_message_background_primary);
        } else {
//            Toast.makeText(context, "Loading Picture!", Toast.LENGTH_LONG).show();
            messageTextRight.setVisibility(View.GONE);

            messageTextPictureRight.setVisibility(View.VISIBLE);

            messageLoadingRight.setVisibility(View.VISIBLE);
            messageLoadingRight.setText("Loading image...");

            messageTimeRight.setBackgroundResource(R.drawable.time_background_transparent);
            // region Padding in DP
            float scale = context.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (5*scale + 0.5f);
            // endregion
            messageTimeRight.setPadding(dpAsPixels,0,dpAsPixels,0);
            messageTimeRight.setTextColor(Color.parseColor("#FFFFFF"));

//            message_main_container_right.setBackgroundResource(R.drawable.hyphen_message_background_primary_not_round);
//            message_main_container_right.setBackgroundColor(Color.parseColor("#00000000"));
//            hyphen_message_background_primary_not_round.xml
            System.out.println("Trying to load this image: " + message);

            System.out.println("Picasso Start-----------------");
            Glide.with(context)
                    .load(message)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            messageLoadingRight.setText("Unable to load image.");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            messageLoadingRight.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(messageTextPictureRight);

//            Picasso.get()
//                    .load(message)
//                    .fit()
//                    .networkPolicy(NetworkPolicy.OFFLINE)
//                    .into(messageTextPictureRight, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            System.out.println("((((((((((((((Image Load Successful");
//                            messageLoadingRight.setText("Image Loaded!");
//                            messageLoadingRight.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onError(Exception e) {
//                            System.out.println("((((((((((((((Image Load Error");
////                            Picasso.with(context)
////                                    .load(message)
////                                    .fit()
////                                    .into(messageTextPictureRight, new Callback() {
////                                        @Override
////                                        public void onSuccess() {
////                                            Toast.makeText(context, "Image Not Loded!", Toast.LENGTH_LONG).show();
////                                            messageLoadingRight.setVisibility(View.GONE);
////                                        }
////
////                                        @Override
////                                        public void onError() {
////                                            messageLoadingRight.setText("Error: could not load picture.");
////                                        }
////                                    });
//                        }
//                    });
            System.out.println("Picasso End-----------------");

            messageTextPictureRight.setOnClickListener(view -> {
                Intent intent = new Intent(context, FullScreenActivity.class);
                intent.putExtra("imageUrl", message);
                context.startActivity(intent);
            });
        }

        messageTimeRight.setText(DateUtils.isToday(time) ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time) : new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(time));

        if (userDatabase != null && userListener != null) {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update user image
        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();

        userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(userID);
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                try {
                    final String image = dataSnapshot.child("image").getValue().toString();

                    //                    if (!image.equals("default")) {
//                        Picasso.with(context)
//                                .load(image)
//                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
//                                .centerCrop()
//                                .networkPolicy(NetworkPolicy.OFFLINE)
//                                .placeholder(R.drawable.user)
//                                .into(messageImageRight, new Callback() {
//                                    @Override
//                                    public void onSuccess() {
//
//                                    }
//
//                                    @Override
//                                    public void onError() {
//                                        Picasso.with(context)
//                                                .load(image)
//                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
//                                                .centerCrop()
//                                                .placeholder(R.drawable.user)
//                                                .error(R.drawable.user)
//                                                .into(messageImageRight);
//                                    }
//                                });
//                    } else {
//                        messageImageRight.setImageResource(R.drawable.user);
//                    }
                } catch (Exception e) {
                    Log.d(TAG, "userDatabase exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "userDatabase failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
    // endregion

    // region Set Left Message
    public void setLeftMessage(String userID, final String message, long time, String type) {
        // If this is a sent message
        System.out.println("ImageURL: " + message);
        final ConstraintLayout messageLayoutRight = view.findViewById(R.id.message_relative_right);
        final ConstraintLayout message_main_container_left = view.findViewById(R.id.message_main_container_left);

        final ConstraintLayout messageLayoutLeft = view.findViewById(R.id.message_relative_left);
        final TextView messageTextLeft = view.findViewById(R.id.message_text_left);
        final TextView messageTimeLeft = view.findViewById(R.id.message_time_left);
        final CircleImageView messageImageLeft = view.findViewById(R.id.message_image_left);
        final ImageView messageTextPictureLeft = view.findViewById(R.id.message_imagetext_left);
        final TextView messageLoadingLeft = view.findViewById(R.id.message_loading_left);

        messageLayoutRight.setVisibility(View.GONE);

        messageLayoutLeft.setVisibility(View.VISIBLE);

        if (type.equals("text")) {
            messageTextPictureLeft.setVisibility(View.GONE);
            messageLoadingLeft.setVisibility(View.GONE);

            messageTextLeft.setVisibility(View.VISIBLE);
            messageTextLeft.setText(message);

            messageTimeLeft.setBackgroundColor(Color.parseColor("#00000000"));
            messageTimeLeft.setPadding(0,0,0,0);
            messageTimeLeft.setTextColor(Color.parseColor("#60FFFFFF"));

            message_main_container_left.setBackgroundResource(R.drawable.hyphen_message_background_white);
        } else {
            messageTextLeft.setVisibility(View.GONE);

            messageTextPictureLeft.setVisibility(View.VISIBLE);
            messageLoadingLeft.setVisibility(View.VISIBLE);
            messageLoadingLeft.setText("Loading Image...");

            messageTimeLeft.setBackgroundResource(R.drawable.time_background_transparent);
            // region Padding in DP
            float scale = context.getResources().getDisplayMetrics().density;
            int dpAsPixels = (int) (5*scale + 0.5f);
            // endregion
            messageTimeLeft.setPadding(dpAsPixels,0,dpAsPixels,0);
            messageTimeLeft.setTextColor(Color.parseColor("#FFFFFF"));

//            message_main_container_left.setBackgroundColor(Color.parseColor("#00000000"));

            Glide.with(context)
                    .load(message)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            messageLoadingLeft.setText("Unable to load image.");
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            messageLoadingLeft.setVisibility(View.GONE);
                            return false;
                        }
                    })
                    .into(messageTextPictureLeft);

            messageTextPictureLeft.setOnClickListener(view -> {
                Intent intent = new Intent(context, FullScreenActivity.class);
                intent.putExtra("imageUrl", message);
                context.startActivity(intent);
            });
        }
        messageTimeLeft.setText(DateUtils.isToday(time) ? new SimpleDateFormat("HH:mm", Locale.getDefault()).format(time) : new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(time));

        if (userDatabase != null && userListener != null) {
            userDatabase.removeEventListener(userListener);
        }

        // Initilize/Update user image

        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();

        userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(userID);
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                try {
                    final String image = dataSnapshot.child("image").getValue().toString();

                    if (!image.equals("default")) {
                        Picasso.get()
                                .load(image)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.hyphen_profile_picture_placeholder)
                                .into(messageImageLeft, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        Picasso.get()
                                                .load(image)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.hyphen_profile_picture_placeholder)
                                                .error(R.drawable.hyphen_profile_picture_placeholder)
                                                .into(messageImageLeft);
                                    }
                                });
                    } else {
                        messageImageLeft.setImageResource(R.drawable.hyphen_profile_picture_placeholder);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "userDatabase exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "userDatabase failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
    // endregion
}
