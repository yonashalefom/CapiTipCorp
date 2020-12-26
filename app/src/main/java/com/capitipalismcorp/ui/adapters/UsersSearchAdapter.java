package com.capitipalismcorp.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.ui.helpers.AlertCreator;
import com.capitipalismcorp.ui.profile.Profile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

//
public class UsersSearchAdapter extends RecyclerView.Adapter<UsersSearchAdapter.SearchViewHolder> {
    private Context context;
    private ArrayList<String> profileIDList;
    private ArrayList<String> userNameList;
    private ArrayList<String> userSearchTagList;
    private ArrayList<String> userValidationList;
    private ArrayList<Long> userBalanceList;
    private ArrayList<String> profileImageList;
    private ArrayList<String> userStatusList;
    private ArrayList<String> userNewList;
    private DatabaseReference userDatabase;

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView profileImage, user_sponsor_indicator;
        TextView userName, userStatus, userBalance, user_holder_editText_tag;
        ConstraintLayout userHolderMainContainer;
        FrameLayout user_holder_write_nfc_tag;
        FrameLayout user_holder_swipe_right_text_editor_container;
        FrameLayout user_holder_delete_user;
        FrameLayout user_holder_logout_user;
        FrameLayout user_holder_image_save;
        FrameLayout user_holder_swipe_left_text_editor_container;
        EditText user_holder_editText;

        SearchViewHolder(View itemView) {
            super(itemView);
            // region User Holder Left Items
            user_holder_image_save = itemView.findViewById(R.id.user_holder_image_save);
            user_holder_swipe_left_text_editor_container = itemView.findViewById(R.id.user_holder_swipe_left_text_editor_container);
            user_holder_editText = itemView.findViewById(R.id.user_holder_editText);
            // endregion

            // region User Holder Middle Items
            userHolderMainContainer = itemView.findViewById(R.id.user_holder_main_container);
            userName = itemView.findViewById(R.id.user_name);
            profileImage = itemView.findViewById(R.id.user_image);
            user_sponsor_indicator = itemView.findViewById(R.id.user_sponsor_indicator);
            userStatus = itemView.findViewById(R.id.user_status);
            userBalance = itemView.findViewById(R.id.text_balance_right);
            // endregion

            // region User Holder Right Items
            user_holder_swipe_right_text_editor_container = itemView.findViewById(R.id.user_holder_swipe_right_text_editor_container);
            user_holder_editText_tag = itemView.findViewById(R.id.user_holder_editText_tag);
            user_holder_write_nfc_tag = itemView.findViewById(R.id.user_holder_write_nfc_tag);
            user_holder_delete_user = itemView.findViewById(R.id.user_holder_delete_user);
            user_holder_logout_user = itemView.findViewById(R.id.user_holder_logout_user);
            // endregion
        }
    }

    // region Constructor
    public UsersSearchAdapter(Context context, ArrayList<String> profileIDList, ArrayList<String> profileImageList, ArrayList<String> userNewList, ArrayList<String> userNameList, ArrayList<Long> userBalanceList, ArrayList<String> userValidationList, ArrayList<String> userSearchTagList, ArrayList<String> userStatusList) {
        this.context = context;
        this.profileIDList = profileIDList;
        this.profileImageList = profileImageList;
        this.userNameList = userNameList;
        this.userNewList = userNewList;
        this.userSearchTagList = userSearchTagList;
        this.userValidationList = userValidationList;
        this.userBalanceList = userBalanceList;
        this.userStatusList = userStatusList;
    }
    // endregion

    // region Create View Holder
    @NonNull
    @Override
    public UsersSearchAdapter.SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_user_item, parent, false);
        return new SearchViewHolder(view);
    }
    // endregion

    // region Bind View Holder
    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(SearchViewHolder holder, int position) {
        CapiUserManager.loadUserData(context);
        System.out.println("##########################################################");
        System.out.println("USER DATA: " + CapiUserManager.getCurrentUserID());
        System.out.println("USER DATA: " + CapiUserManager.getUserType());
        System.out.println("##########################################################");
        System.out.println("*************************************");
        System.out.println("View Bind at position: " + position);
        // userDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(profileIDList.get(position));

        // region Set Color Indicator For New Users
        if (userNewList.get(position).equals("true")) {
            holder.userName.setTextColor(Color.RED);
            DatabaseReference adminDatabaseReference;
            String groupID = GroupManager.getGroupID();
            String ownerID = GroupManager.getOwnerID();
            adminDatabaseReference = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(profileIDList.get(position));
            adminDatabaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    adminDatabaseReference.child("new_user").setValue("false");
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: " + databaseError.getCode());
                }
            });
        }
        // endregion

        // region Set Color Indicator For Users That Don't Have Validation Text
        if (userValidationList.get(position).equals("null") && !(userNewList.get(position).equals("true"))) {
            holder.userName.setTextColor(Color.BLUE);
        }
        // endregion

        // region Set User Name and Status
        holder.userName.setText(userNameList.get(position));
        holder.userStatus.setText(userStatusList.get(position));
        // endregion

        // region Set User Sponsor Star Indicator
        CapiUserManager.loadUserData(context);
        if (CapiUserManager.userDataExists()) {
            // region Beta Feature
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String groupID = GroupManager.getGroupID();
            String ownerID = GroupManager.getOwnerID();
            DatabaseReference reference = database.getReference("GroupUsers").child(ownerID).child(groupID).child(profileIDList.get(position));
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    try {
                        // If the currently logged in user sponsored the loaded user, it shows indicator on the loaded user's profile
                        final String userSponsorID = Objects.requireNonNull(dataSnapshot.child("userSponsorID").getValue()).toString();
                        if (Objects.requireNonNull(userSponsorID).compareToIgnoreCase(CapiUserManager.getCurrentUserID()) == 0) {
                            holder.user_sponsor_indicator.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        Log.d("Exception", e.getMessage());
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            // endregion
        }
        // holder.userName.setOnClickListener(v -> Toast.makeText(context, "Full Name Clicked", Toast.LENGTH_SHORT).show());
        // endregion

        // region Set Profile Image
        final String image = profileImageList.get(position);

        int userImagePlaceholder;

        // Random r = new Random();
        // int rand = r.nextInt((5 - 1) + 1) + 1;

        // if(rand == 1){
        //     userImagePlaceholder = R.drawable.ic_user_placeholder_avatar_1;
        // } else if(rand == 2){
        //     userImagePlaceholder = R.drawable.ic_user_placeholder_avatar_2;
        // } else if(rand == 3){
        //     userImagePlaceholder = R.drawable.ic_user_placeholder_avatar_3;
        // } else if(rand == 4){
        //     userImagePlaceholder = R.drawable.ic_user_placeholder_avatar_4;
        // } else if(rand == 5){
        //     userImagePlaceholder = R.drawable.ic_user_placeholder_avatar_5;
        // } else {
        userImagePlaceholder = R.drawable.ic_user_placeholder_avatar_1;
        // }

        if (!image.equals("default")) {
            Picasso.get()
                    .load(image)
                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                    .centerCrop()
                    .networkPolicy(NetworkPolicy.OFFLINE)
                    .placeholder(userImagePlaceholder)
                    .into(holder.profileImage, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get()
                                    .load(image)
                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, context.getResources().getDisplayMetrics()))
                                    .centerCrop()
                                    .placeholder(userImagePlaceholder)
                                    .error(userImagePlaceholder)
                                    .into(holder.profileImage);
                        }
                    });
        } else {
            holder.profileImage.setImageResource(userImagePlaceholder);
        }
        // endregion

        // region Set UI State Based On User Type
        CapiUserManager.loadUserData(context);
        if (CapiUserManager.getUserType().equals("Super Admin")) {

            // region User Holder Left Items
            holder.user_holder_editText.setText(userValidationList.get(position) + "");

            // region Set Click Event Handler for Image Save Icon (user_holder_image_save)
            holder.user_holder_image_save.setOnClickListener(view -> {
                String groupID = GroupManager.getGroupID();
                String ownerID = GroupManager.getOwnerID();
                userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(profileIDList.get(position));

                System.out.println("save imag clicked");
//                Toast.makeText(context, "Saving  User Amount: " + profileIDList.get(position), Toast.LENGTH_LONG).show();
                Map<String, Object> balanceLinksUpdateMap = new HashMap<>();
                balanceLinksUpdateMap.put("validation", holder.user_holder_editText.getText().toString());
                userDatabase.updateChildren(balanceLinksUpdateMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Validation Text Updated", Toast.LENGTH_SHORT).show();
                        AlertCreator.showSuccessAlert((Activity) context, "Success!", "Validation Text Updated!");
                        Intent intent = ((Activity) context).getIntent();
                        intent.putExtra("userID", CapiUserManager.getCurrentUserID());
                        ((Activity) context).finish();
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        AlertCreator.showSuccessAlert((Activity) context, "Error!", task.getException().getMessage());
                    }
                });
            });
            // endregion
            // endregion

            // region User Holder Middle Items
            holder.userBalance.setText("$" + userBalanceList.get(position).toString());

            // region Set Click Event Handler for User List Item
            holder.userHolderMainContainer.setOnClickListener(view -> {
                // Toast.makeText(context, "Clicked " + profileIDList.get(position), Toast.LENGTH_LONG).show();
                Intent userProfileIntent = new Intent(context.getApplicationContext(), Profile.class);
                userProfileIntent.putExtra("userID", profileIDList.get(position));
                context.startActivity(userProfileIntent);
            });
            // endregion
            // endregion

            // region User Holder Right Items
            holder.user_holder_swipe_right_text_editor_container.setVisibility(View.GONE);
            holder.user_holder_write_nfc_tag.setVisibility(View.VISIBLE);
            holder.user_holder_delete_user.setVisibility(View.VISIBLE);
            holder.user_holder_logout_user.setVisibility(View.GONE);

            // region Write NFC Tag (user_holder_write_nfc_tag) - (For Super Admin Only)
            holder.user_holder_write_nfc_tag.setOnClickListener(view -> {
                Toast.makeText(context, "Writing NFC Tag For: " + profileIDList.get(position), Toast.LENGTH_LONG).show();
            });
            // endregion

            // region Delete User (user_holder_delete_user) - (For Super Admin Only)
            holder.user_holder_delete_user.setOnClickListener(view -> {
                userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers");
                String groupID = GroupManager.getGroupID();
                String ownerID = GroupManager.getOwnerID();
                Query deleteQuery = userDatabase.child(ownerID).child(groupID).orderByChild("userID").equalTo(profileIDList.get(position));

                deleteQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                            appleSnapshot.getRef().removeValue();
                        }
                        userNameList.remove(userNameList.get(position));
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, profileIDList.size());
//                        holder.userHolderMainContainer.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(context, Objects.requireNonNull(databaseError).getMessage(), Toast.LENGTH_LONG).show();

                    }
                });
//                Toast.makeText(context, "Deleting User: " + profileIDList.get(position), Toast.LENGTH_LONG).show();
            });
            // endregion
            // endregion
        } else if (CapiUserManager.getUserType().equals("GroupDefaultUser")) {
            // region User Holder Left Items
            holder.user_holder_image_save.setVisibility(View.GONE);
            holder.user_holder_swipe_left_text_editor_container.setVisibility(View.GONE);
            // endregion

            // region User Holder Middle Items
            holder.userBalance.setVisibility(View.GONE);

            // region Set Click Event Handler for User List Item
            holder.userHolderMainContainer.setOnClickListener(view -> {
                // Toast.makeText(context, "Clicked " + profileIDList.get(position), Toast.LENGTH_LONG).show();
                Intent userProfileIntent = new Intent(context.getApplicationContext(), Profile.class);
                userProfileIntent.putExtra("userID", profileIDList.get(position));
                context.startActivity(userProfileIntent);
            });
            // endregion
            // endregion

            // region User Holder Right Items
            holder.user_holder_swipe_right_text_editor_container.setVisibility(View.GONE);
            holder.user_holder_write_nfc_tag.setVisibility(View.GONE);
            holder.user_holder_delete_user.setVisibility(View.GONE);
            holder.user_holder_logout_user.setVisibility(View.GONE);
            // endregion
        } else if (CapiUserManager.getUserType().equals("SupportRep")) {
            // region User Holder Left Items
            holder.user_holder_image_save.setVisibility(View.GONE);
            holder.user_holder_swipe_left_text_editor_container.setVisibility(View.GONE);
            // endregion

            // region User Holder Middle Items
            holder.userBalance.setText("$" + userBalanceList.get(position).toString());

            // region Set Click Event Handler for User List Item
            holder.userHolderMainContainer.setOnClickListener(view -> {
                // Toast.makeText(context, "Clicked " + profileIDList.get(position), Toast.LENGTH_LONG).show();
                Intent userProfileIntent = new Intent(context.getApplicationContext(), Profile.class);
                userProfileIntent.putExtra("userID", profileIDList.get(position));
                context.startActivity(userProfileIntent);
            });
            // endregion
            // endregion

            // region User Holder Right Items
//            holder.user_holder_write_nfc_tag.setVisibility(View.GONE);
            holder.user_holder_swipe_right_text_editor_container.setVisibility(View.GONE);
            holder.user_holder_write_nfc_tag.setVisibility(View.GONE);
            holder.user_holder_delete_user.setVisibility(View.GONE);
            holder.user_holder_logout_user.setVisibility(View.GONE);
            // endregion
        } else {

//            holder.userHolderMainContainer.setOnClickListener(view -> {
//                // Toast.makeText(context, "Clicked " + profileIDList.get(position), Toast.LENGTH_LONG).show();
//                Intent userProfileIntent = new Intent(context.getApplicationContext(), Profile.class);
//                userProfileIntent.putExtra("userID", profileIDList.get(position));
//                context.startActivity(userProfileIntent);
//            });

            // region User Holder Left Items
            holder.user_holder_editText.setHint("Amount");
            String userBalanceAmount = userBalanceList.get(position).toString();
            holder.user_holder_editText.setText(userBalanceAmount);

            // region Set User Balance (user_holder_image_save) - (For Admin Only)
            holder.user_holder_image_save.setOnClickListener(view -> {
                String groupID = GroupManager.getGroupID();
                String ownerID = GroupManager.getOwnerID();
                userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(profileIDList.get(position));
//                userDatabase.keepSynced(true);
                System.out.println("save imag clicked");
//                Toast.makeText(context, "Saving  User Amount: " + profileIDList.get(position), Toast.LENGTH_LONG).show();
                Map<String, Object> balanceLinksUpdateMap = new HashMap<>();
                balanceLinksUpdateMap.put("balance", Long.parseLong(holder.user_holder_editText.getText().toString()));
                userDatabase.updateChildren(balanceLinksUpdateMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        notifyDataSetChanged();
//                        notifyItemChanged(position);
                        Toast.makeText(context, "Balance Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = ((Activity) context).getIntent();
                        intent.putExtra("userID", CapiUserManager.getCurrentUserID());
                        ((Activity) context).finish();
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
            // endregion
            // endregion

            // region User Holder Middle Items
            holder.userBalance.setText(userBalanceList.get(position) + "");

            // region Set Click Event Handler for User List Item
            holder.userHolderMainContainer.setOnClickListener(view -> {
                // Toast.makeText(context, "Clicked " + profileIDList.get(position), Toast.LENGTH_LONG).show();
                Intent userProfileIntent = new Intent(context.getApplicationContext(), Profile.class);
                userProfileIntent.putExtra("userID", profileIDList.get(position));
                context.startActivity(userProfileIntent);
            });
            // endregion
            // endregion

            // region User Holder Right Items
            holder.user_holder_editText_tag.setText(userSearchTagList.get(position));
            holder.user_holder_delete_user.setVisibility(View.GONE);
            holder.user_holder_logout_user.setVisibility(View.VISIBLE);

            // region Change User's Pay Due (user_holder_write_nfc_tag) - (For Admin Only)
            holder.user_holder_write_nfc_tag.setOnClickListener(view -> {
                Map<String, Object> tagLinksInsertMap = new HashMap<>();
                tagLinksInsertMap.put("payDue", holder.user_holder_editText_tag.getText().toString());
                String groupID = GroupManager.getGroupID();
                String ownerID = GroupManager.getOwnerID();
                userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(profileIDList.get(position));

                userDatabase.updateChildren(tagLinksInsertMap).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
//                        notifyDataSetChanged();8
//                        notifyItemChanged(position);
                        Toast.makeText(context, "Pay Due Date Updated", Toast.LENGTH_SHORT).show();
                        Intent intent = ((Activity) context).getIntent();
                        intent.putExtra("userID", CapiUserManager.getCurrentUserID());
                        ((Activity) context).finish();
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

//                Toast.makeText(context,"Tag Inserted"+holder.user_holder_editText_tag.getText().toString(),Toast.LENGTH_SHORT).show();
            });
            // endregion

            // region Logout User (user_holder_logout_user) - (For Admin Only)
            holder.user_holder_logout_user.setOnClickListener(v -> {
                String groupID = GroupManager.getGroupID();
                String ownerID = GroupManager.getOwnerID();
                userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(profileIDList.get(position));
                System.out.println("save imag clicked");
                Map<String, Object> logoutUpdateLink = new HashMap<>();
                logoutUpdateLink.put("logged_in", "false");
                userDatabase.updateChildren(logoutUpdateLink).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "User Logged Out", Toast.LENGTH_SHORT).show();
                        AlertCreator.showSuccessAlert((Activity) context, "User Logged Out", "You logged out a user with ID " + profileIDList.get(position));
                        Intent intent = ((Activity) context).getIntent();
                        intent.putExtra("userID", CapiUserManager.getCurrentUserID());
                        ((Activity) context).finish();
                        context.startActivity(intent);
                    } else {
                        Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            });
            // endregion
            // endregion
        }
        // endregion
    }
    // endregion

    // region Get Item Count
    @Override
    public int getItemCount() {
        return userNameList.size();
    }
    // endregion


    // // region Write NFC TAG
    // @Override
    // public void callbackWithReturnValue(Boolean result) {
    //     String message = result ? "Success" : "Failed!";
    //     Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    // }
    //
    // @Override
    // public void onProgressUpdate(Boolean... booleans) {
    //     Toast.makeText(context, booleans[0] ? "We started writing" : "We could not write!", Toast.LENGTH_SHORT).show();
    // }
    //
    // @Override
    // public void onError(Exception e) {
    //     Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
    // }
    //
    // private AsyncOperationCallback mAsyncOperationCallback = (writeUtility, userIDToRight) -> writeUtility.writeTextToTagFromIntent("some@email.tld", getIntent());
    //
    // // @Override
    // // protected void onNewIntent(Intent intent) {
    // //     super.onNewIntent(intent);
    // //     new WriteEmailNfcAsync(this,mAsyncOperationCallback).executeWriteOperation();
    // // }
    // // endregion


}
