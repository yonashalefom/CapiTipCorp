package com.capitipalismcorp.holders;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.google.firebase.auth.FirebaseAuth;
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
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatHolder extends RecyclerView.ViewHolder {
    private final String TAG = "CA/ChatHolder";

    private Activity activity;
    private View view;
    private Context context;

    // Will handle user data

    private DatabaseReference userDatabase;
    private ValueEventListener userListener;

    public ChatHolder(Activity activity, View view, Context context) {
        super(view);

        this.activity = activity;
        this.view = view;
        this.context = context;
    }

    public View getView() {
        return view;
    }


    public void setHolder(String userID, String message, long timestamp, long seen) {
        final TextView userName = view.findViewById(R.id.user_name);
        final TextView userStatus = view.findViewById(R.id.user_status);
        final TextView userTime = view.findViewById(R.id.user_timestamp);
        final CircleImageView userImage = view.findViewById(R.id.user_image);
        final ImageView userOnline = view.findViewById(R.id.user_online);
        final ImageView seenStatusIndicator = view.findViewById(R.id.userlist_img_seen_status_indicator);

        userStatus.setText(message);

        userTime.setVisibility(View.VISIBLE);
        userTime.setText(new SimpleDateFormat("MMM d, HH:mm", Locale.getDefault()).format(timestamp));

        CapiUserManager.loadUserData(context);

        if (CapiUserManager.getCurrentUserID() == userID) {
//            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            System.out.println("seen");
//            System.out.println("User ID of the current user: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
//            System.out.println("User ID of the other user: " + userID);
//            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
            seenStatusIndicator.setVisibility(View.VISIBLE);
        } else {
            seenStatusIndicator.setVisibility(View.INVISIBLE);
//            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
//            System.out.println("not seen");
//            System.out.println("User ID of the current user: " + FirebaseAuth.getInstance().getCurrentUser().getUid());
//            System.out.println("User ID of the other user: " + userID);
//            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
        }

        if (seen == 0L) {
            userStatus.setTypeface(null, Typeface.BOLD);
            userTime.setTypeface(null, Typeface.BOLD);
        } else {
            userStatus.setTypeface(null, Typeface.NORMAL);
            userTime.setTypeface(null, Typeface.NORMAL);
        }

        if (userDatabase != null && userListener != null) {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update user data

        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();

        userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(userID);
        userListener = new ValueEventListener() {
            // Will be used to avoid flickering online status when changing activity
            Timer timer;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    final String name = dataSnapshot.child("userFirstName").getValue().toString() + " " + dataSnapshot.child("userLastName").getValue().toString();
                    final String image = dataSnapshot.child("image").getValue().toString();

//                    if (dataSnapshot.hasChild("online")) {
//                        String online = dataSnapshot.child("online").getValue().toString();
//
//                        if (online.equals("true")) {
//                            if (timer != null) {
//                                timer.cancel();
//                                timer = null;
//                            }
//
//                            userOnline.setVisibility(View.VISIBLE);
//                        } else {
//                            if (userName.getText().toString().equals("")) {
//                                userOnline.setVisibility(View.INVISIBLE);
//                            } else {
//                                timer = new Timer();
//                                timer.schedule(new TimerTask() {
//                                    @Override
//                                    public void run() {
//                                        activity.runOnUiThread(() -> userOnline.setVisibility(View.INVISIBLE));
//                                    }
//                                }, 2000);
//                            }
//                        }
//                    }

                    userName.setText(name);

                    if (!image.equals("default")) {
                        Picasso.get()
                                .load(image)
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                .centerCrop()
                                .placeholder(R.drawable.hyphen_profile_picture_placeholder)
                                .into(userImage, new Callback() {
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
                                                .into(userImage);
                                    }
                                });
                    } else {
                        userImage.setImageResource(R.drawable.hyphen_profile_picture_placeholder);
                    }
                } catch (Exception e) {
                    Log.d(TAG, "userListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "userListener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
}