package com.capitipalismcorp.ui.profile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.nfc.FormatException;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.ui.adapters.UsersSearchAdapter;
import com.capitipalismcorp.ui.helpers.AlertCreator;
import com.capitipalismcorp.ui.imageviewer.FullScreenActivity;
import com.capitipalismcorp.ui.login.Login;
import com.capitipalismcorp.ui.search.SearchUser;
import com.capitipalismcorp.ui.settings.AdminSettings;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import be.appfoundry.nfclibrary.exceptions.InsufficientCapacityException;
import be.appfoundry.nfclibrary.exceptions.ReadOnlyTagException;
import be.appfoundry.nfclibrary.exceptions.TagNotPresentException;
import be.appfoundry.nfclibrary.tasks.interfaces.AsyncOperationCallback;
import be.appfoundry.nfclibrary.tasks.interfaces.AsyncUiCallback;
import be.appfoundry.nfclibrary.utilities.async.WriteEmailNfcAsync;
import be.appfoundry.nfclibrary.utilities.interfaces.NfcWriteUtility;
import be.appfoundry.nfclibrary.utilities.sync.NfcReadUtilityImpl;
import de.hdodenhof.circleimageview.CircleImageView;
import jahirfiquitiva.libs.fabsmenu.FABsMenu;
import jahirfiquitiva.libs.fabsmenu.TitleFAB;

public class Profile extends AppCompatActivity {
    private FirebaseRecyclerAdapter usersOnesReferenceFirebaseRecyclerAdapter;
    ArrayList<String> userIDList1stGen;
    ArrayList<String> userNameList1stGen;
    ArrayList<String> userSearchTagList1stGen;
    ArrayList<String> userNewUserList1stGen;
    ArrayList<String> userValidationList1stGen;
    ArrayList<Long> userBalanceList1stGen;
    ArrayList<String> profileImageList1stGen;
    ArrayList<String> userStatusList1stGen;

    ArrayList<String> userIDList2ndGen;
    ArrayList<String> userNameList2ndGen;
    ArrayList<String> userNewUserList2ndGen;
    ArrayList<String> userSearchTagList2ndGen;

    ArrayList<String> userValidationList2ndGen;
    ArrayList<Long> userBalanceList2ndGen;
    ArrayList<String> profileImageList2ndGen;
    ArrayList<String> userStatusList2ndGen;
    UsersSearchAdapter userOnesAdapter, userTwosAdapter;

    // region Firebase Variables
    private DatabaseReference userDatabase, usersOnesDatabaseReference, usersTwosDatabaseReference, userInfoChange;
    private ValueEventListener userListener, usersOnesReferenceListener, friendsListener;
    // endregion

    private LinearLayout personalInfo, experience, review;
    private TextView profileSponsoredBy, personalInfoBtn, experienceBtn, reviewBtn, name, profile_user_id, userFacebook, userTwitter, userInstagram, userLink4, userLink5, userLink6, profile_direct_signup_description, profile_indirect_signup_description, profile_edit, social_edit;

    private CircleImageView image;
    private FABsMenu menu;
    private TitleFAB button1, button2, button3, button4, button5, button6;
    private ImageView profile_logout_users, searchUsers, profile_back_button, profile_sponsored_by_icon, profile_admin_settings;

    private String userID;

    private RecyclerView userOnesSignupsRecyclerView;
    private RecyclerView userTwosSignupsRecyclerView;

    // region NFC Reader
    private PendingIntent pendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;
    private NfcAdapter mNfcAdapter;
    private Vibrator myVibrator;
    private boolean isReadingNFC = false;
    private boolean isWritingNFC = false;

    private TextView nfcDialogTextView;
    // endregion

    // region [Override] On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        userID = getIntent().getStringExtra("userID");
        initUI();
        initEventHandlers();
        initCurrentUsersOnes();
        initCurrentUserTwos();
        // initCurrentUserTwosAnother();
    }
    // endregion

    // region Init UI
    private void initUI() {
        bindUIElements();
        setUpUI();
    }

    private void setUpUI() {
        CapiUserManager.loadUserData(getApplicationContext());
        if (CapiUserManager.userDataExists()) {
            if (CapiUserManager.getUserType().equals("GroupAdmin")) {
                profile_admin_settings.setVisibility(View.VISIBLE);
            }
        }
    }
    // endregion

    // region Bind UI Elements
    private void bindUIElements() {
        // region Final Vars
        name = findViewById(R.id.profile_name);
        profile_user_id = findViewById(R.id.profile_user_id);
        // status = findViewById(R.id.profile_status);
        image = findViewById(R.id.profile_image);
        menu = findViewById(R.id.profile_fabs_menu);
        userFacebook = findViewById(R.id.profile_facebook);
        userTwitter = findViewById(R.id.profile_twitter);
        userInstagram = findViewById(R.id.profile_instagram);
        userLink4 = findViewById(R.id.profile_user4);
        userLink5 = findViewById(R.id.profile_user5);
        userLink6 = findViewById(R.id.profile_user6);


        searchUsers = findViewById(R.id.profile_register_new_users);
        // endregion
        personalInfo = findViewById(R.id.personalinfo);
        experience = findViewById(R.id.experience);
        review = findViewById(R.id.review);
        personalInfoBtn = findViewById(R.id.users_list_count);
        experienceBtn = findViewById(R.id.experiencebtn);
        reviewBtn = findViewById(R.id.reviewbtn);
        profile_back_button = findViewById(R.id.profile_back_button);
        profile_sponsored_by_icon = findViewById(R.id.profile_sponsored_by_icon);

        profileSponsoredBy = findViewById(R.id.profile_sponsored_by);
        userOnesSignupsRecyclerView = findViewById(R.id.user_ones_signups_list);
        userTwosSignupsRecyclerView = findViewById(R.id.user_twos_signups_list);
        profile_direct_signup_description = findViewById(R.id.profile_direct_signup_description);
        profile_indirect_signup_description = findViewById(R.id.profile_indirect_signup_description);
        profile_edit = findViewById(R.id.activity_profile_text_edit);
        social_edit = findViewById(R.id.activity_profile_text_social_edit);

        profile_logout_users = findViewById(R.id.profile_logout_users);
        profile_admin_settings = findViewById(R.id.profile_admin_settings);

        userIDList1stGen = new ArrayList<>();
        userNameList1stGen = new ArrayList<>();
        userSearchTagList1stGen = new ArrayList<>();
        userNewUserList1stGen = new ArrayList<>();
        userValidationList1stGen = new ArrayList<>();
        userBalanceList1stGen = new ArrayList<>();
        profileImageList1stGen = new ArrayList<>();
        userStatusList1stGen = new ArrayList<>();

        userIDList2ndGen = new ArrayList<>();
        userNameList2ndGen = new ArrayList<>();
        userSearchTagList2ndGen = new ArrayList<>();
        userBalanceList2ndGen = new ArrayList<>();
        userNewUserList2ndGen = new ArrayList<>();

        userValidationList2ndGen = new ArrayList<>();
        profileImageList2ndGen = new ArrayList<>();
        userStatusList2ndGen = new ArrayList<>();

        /*making personal info visible*/
        personalInfo.setVisibility(View.VISIBLE);
        experience.setVisibility(View.GONE);
        review.setVisibility(View.GONE);
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        CapiUserManager.loadUserData(getApplicationContext());
        personalInfoBtn.setOnClickListener(v -> {
            personalInfo.setVisibility(View.VISIBLE);
            experience.setVisibility(View.GONE);
            review.setVisibility(View.GONE);
            personalInfoBtn.setTextColor(getResources().getColor(R.color.capitipalism_primary));
            experienceBtn.setTextColor(getResources().getColor(R.color.main_gray));
            reviewBtn.setTextColor(getResources().getColor(R.color.main_gray));
        });

        experienceBtn.setOnClickListener(v -> {
            personalInfo.setVisibility(View.GONE);
            experience.setVisibility(View.VISIBLE);
            review.setVisibility(View.GONE);
            personalInfoBtn.setTextColor(getResources().getColor(R.color.main_gray));
            experienceBtn.setTextColor(getResources().getColor(R.color.capitipalism_primary));
            reviewBtn.setTextColor(getResources().getColor(R.color.main_gray));
        });

        reviewBtn.setOnClickListener(v -> {
            personalInfo.setVisibility(View.GONE);
            experience.setVisibility(View.GONE);
            review.setVisibility(View.VISIBLE);
            personalInfoBtn.setTextColor(getResources().getColor(R.color.main_gray));
            experienceBtn.setTextColor(getResources().getColor(R.color.main_gray));
            reviewBtn.setTextColor(getResources().getColor(R.color.capitipalism_primary));
        });

        profile_logout_users.setOnClickListener(view -> {
            CapiUserManager.loadUserData(this);
            String groupID = GroupManager.getGroupID();
            String ownerID = GroupManager.getOwnerID();
            DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(CapiUserManager.getCurrentUserID());
            Map<String, Object> logoutUpdateLink = new HashMap<>();
            logoutUpdateLink.put("logged_in", "false");
            userDatabase.updateChildren(logoutUpdateLink).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "You Are Logged Out", Toast.LENGTH_SHORT).show();
                    CapiUserManager.removeUserData(getApplicationContext());
                    startActivity(new Intent(Profile.this, Login.class));
                    // finish();
                } else {
                    Toast.makeText(this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });

        profile_admin_settings.setOnClickListener(view -> {
            startActivity(new Intent(Profile.this, AdminSettings.class));
        });

        searchUsers.setOnClickListener(view -> {
            // startActivity(new Intent(Profile.this, SearchUser.class));
            // ViewDialog alert = new ViewDialog();
            // alert.showDialog(Profile.this, "Bring your NFC Tag close to your phone so that we can write to it.");

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.write_nfc_tag_dialog);

            nfcDialogTextView = dialog.findViewById(R.id.text_dialog);
            nfcDialogTextView.setText("Bring your NFC Tag close to your phone so that we can write to it.");

            Button dialogButton = dialog.findViewById(R.id.btn_dialog);
            dialogButton.setOnClickListener(v -> {
                isWritingNFC = false;
                dialog.dismiss();
            });

            dialog.show();

            isWritingNFC = true;
        });

        profile_indirect_signup_description.setOnClickListener(view -> {
            userTwosAdapter = new UsersSearchAdapter(Profile.this, userIDList2ndGen, profileImageList2ndGen, userNewUserList2ndGen, userNameList2ndGen, userBalanceList2ndGen, userValidationList2ndGen, userSearchTagList2ndGen, userStatusList2ndGen);
            userTwosSignupsRecyclerView.setAdapter(userTwosAdapter);
        });

        profileSponsoredBy.setOnClickListener(v -> {
            Intent userProfileIntent = new Intent(Profile.this, Profile.class);
            userProfileIntent.putExtra("userID", profileSponsoredBy.getText().toString());
            startActivity(userProfileIntent);
        });
        profile_edit.setOnClickListener(v -> {
            Toast.makeText(this, "Edit Text Pressd", Toast.LENGTH_SHORT).show();
        });
        social_edit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setTitle("Enter your Social Links:");

            View mView = Profile.this.getLayoutInflater().inflate(R.layout.social_links_dialog, null);

            final EditText userFB = mView.findViewById(R.id.social_links_dialog_facebook);
            final EditText userTwi = mView.findViewById(R.id.social_links_dialog_twitter);
            final EditText userInsta = mView.findViewById(R.id.social_links_dialog_instagram);
            final EditText userLink4 = mView.findViewById(R.id.social_links_dialog_4);
            final EditText userLink5 = mView.findViewById(R.id.social_links_dialog_5);
            final EditText userLink6 = mView.findViewById(R.id.social_links_dialog_6);

            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                final String newUserFacebook = userFB.getText().toString();
                final String newUserTwitter = userTwi.getText().toString();
                final String newUserInstagram = userInsta.getText().toString();
                final String newUserLink4 = userLink4.getText().toString();
                final String newUserLink5 = userLink5.getText().toString();
                final String newUserLink6 = userLink6.getText().toString();


                // Toast.makeText(Profile.this, "Length can't exceed no more than 255 characters.", Toast.LENGTH_LONG).show();
                // dialogInterface.dismiss();

                // Updating on status on user data

                // userDatabase.child("userFirstName").setValue(newStatus).addOnCompleteListener(task -> {
                //     if (task.isSuccessful()) {
                //         Toast.makeText(Profile.this, "Status updated", Toast.LENGTH_SHORT).show();
                //     } else {
                //         Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                //     }
                // });

                Map<String, Object> socialLinksUpdateMap = new HashMap<>();
                boolean shouldWeUpdate = false;
                if (newUserFacebook.length() > 2 && newUserFacebook.length() < 256) {
                    socialLinksUpdateMap.put("facebook", newUserFacebook);
                    shouldWeUpdate = true;
                }

                if (newUserTwitter.length() > 2 && newUserTwitter.length() < 256) {
                    socialLinksUpdateMap.put("instagram", newUserTwitter);
                    shouldWeUpdate = true;
                }

                if (newUserInstagram.length() > 2 && newUserInstagram.length() < 256) {
                    socialLinksUpdateMap.put("twitter", newUserInstagram);
                    shouldWeUpdate = true;
                }

                if (newUserLink4.length() > 2 && newUserLink4.length() < 256) {
                    socialLinksUpdateMap.put("userLink4", newUserLink4);
                    shouldWeUpdate = true;
                }

                if (newUserLink5.length() > 2 && newUserLink5.length() < 256) {
                    socialLinksUpdateMap.put("userLink5", newUserLink5);
                    shouldWeUpdate = true;
                }

                if (newUserLink6.length() > 2 && newUserLink6.length() < 256) {
                    socialLinksUpdateMap.put("userLink6", newUserLink6);
                    shouldWeUpdate = true;
                }

                if (shouldWeUpdate) {
                    userDatabase.updateChildren(socialLinksUpdateMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Social Links updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            builder.setView(mView);
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        profile_back_button.setOnClickListener(view -> {
            onBackPressed();
        });
    }
    // endregion

    // region [Override] On Start
    protected void onStart() {
        super.onStart();

        if (userDatabase != null && userListener != null) {
            userDatabase.removeEventListener(userListener);
        }

        // Initialize/Update realtime user data such as name, status, image
        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();
        userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(userID);
        // userDatabase.keepSynced(true); // For offline use
        userListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String firstName = Objects.requireNonNull(dataSnapshot.child("userFirstName").getValue()).toString();
                    String lastName = Objects.requireNonNull(dataSnapshot.child("userLastName").getValue()).toString();
                    String userSponsoredBy = Objects.requireNonNull(dataSnapshot.child("userSponsorID").getValue()).toString();
                    String layoutName = firstName + " " + lastName;
                    String layoutUserID = Objects.requireNonNull(dataSnapshot.child("userID").getValue()).toString();
                    String layoutStatus = Objects.requireNonNull(dataSnapshot.child("status").getValue()).toString();
                    final String layoutImage = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();

                    String userFacebookLoaded = Objects.requireNonNull(dataSnapshot.child("facebook").getValue()).toString();
                    String userTwitterLoaded = Objects.requireNonNull(dataSnapshot.child("twitter").getValue()).toString();
                    String userInstagramLoaded = Objects.requireNonNull(dataSnapshot.child("instagram").getValue()).toString();
                    String userLink4Loaded = Objects.requireNonNull(dataSnapshot.child("userLink4").getValue()).toString();
                    String userLink5Loaded = Objects.requireNonNull(dataSnapshot.child("userLink5").getValue()).toString();
                    String userLink6Loaded = Objects.requireNonNull(dataSnapshot.child("userLink6").getValue()).toString();

                    System.out.println("****************************************");
                    System.out.println("Layout Name: " + layoutName);
                    System.out.println("****************************************");

                    name.setText(layoutName);
                    profile_user_id.setText("ID: " + layoutUserID);
                    profileSponsoredBy.setText(userSponsoredBy);
                    // status.setText(layoutStatus);

                    userFacebook.setText(userFacebookLoaded);
                    userTwitter.setText(userTwitterLoaded);
                    userInstagram.setText(userInstagramLoaded);
                    userLink4.setText(userLink4Loaded);
                    userLink5.setText(userLink5Loaded);
                    userLink6.setText(userLink6Loaded);

                    // region Fetch User Profile Picture
                    if (!layoutImage.equals("default")) {
                        Picasso.with(getApplicationContext())
                                .load(layoutImage)
                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                .centerCrop()
                                .networkPolicy(NetworkPolicy.OFFLINE)
                                .placeholder(R.drawable.ic_user_placeholder_avatar_1)
                                .error(R.drawable.ic_user_placeholder_avatar_1)
                                .into(image, new Callback() {
                                    @Override
                                    public void onSuccess() {

                                    }

                                    @Override
                                    public void onError() {
                                        Picasso.with(getApplicationContext())
                                                .load(layoutImage)
                                                .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                                .centerCrop()
                                                .placeholder(R.drawable.ic_user_placeholder_avatar_1)
                                                .error(R.drawable.ic_user_placeholder_avatar_1)
                                                .into(image);
                                    }
                                });

                        image.setOnClickListener(view -> {
                            Intent intent = new Intent(Profile.this, FullScreenActivity.class);
                            intent.putExtra("imageUrl", layoutImage);
                            startActivity(intent);
                        });
                    } else {
                        image.setImageResource(R.drawable.ic_user_placeholder_avatar_1);
                    }
                    // endregion

                    CapiUserManager.loadUserData(getApplicationContext());
                    if (userID.equals(CapiUserManager.getCurrentUserID()) || userSponsoredBy.equals(CapiUserManager.getCurrentUserID())) {
                        if (userSponsoredBy.equals(CapiUserManager.getCurrentUserID())) {
                            profile_sponsored_by_icon.setBackground(getResources().getDrawable(R.drawable.circular_golden_bordersolid));
                            profile_sponsored_by_icon.setImageResource(R.drawable.ic_capitipalism_golden_star);
                        }
                        initializeCurrentUserProfile();
                    } else {
                        menu.setVisibility(View.INVISIBLE);
                    }
                } catch (Exception e) {
                    Log.d("PROFILE ACTIVITY", "userDatabase listener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("PROFILE ACTIVITY", "userDatabase listener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
    }
    // endregion

    // region [Override] On Stop
    @Override
    protected void onStop() {
        super.onStop();
    }
    // endregion

    // region [Override] On Activity Result
    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            Uri url = data.getData();
            System.out.println("*******************************");
            System.out.println("Image URL: " + url);
            System.out.println("*******************************");

            // Uploading selected picture
            StorageReference file = FirebaseStorage.getInstance().getReference().child("profile_images").child(userID + ".jpg");
            file.putFile(Objects.requireNonNull(url)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String imageUrl = Objects.requireNonNull(task.getResult().getDownloadUrl()).toString();

                    // Updating image on user data
                    userDatabase.child("image").setValue(imageUrl).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(Profile.this, "Picture updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.d("Profile", "updateImage listener failed: " + Objects.requireNonNull(task1.getException()).getMessage());
                        }
                    });
                } else {
                    Log.d("Profile", "uploadImage listener failed: " + Objects.requireNonNull(task.getException()).getMessage());
                }
            });
        }
    }
    // endregion

    // region [Override] On Back Pressed
    @Override
    public void onBackPressed() {
        if (menu.isExpanded()) {
            menu.collapse();
        } else {
            super.onBackPressed();
        }
    }
    // endregion

    // region Init My Profile
    public void initializeCurrentUserProfile() {
        menu.setVisibility(View.VISIBLE);
        // region Fab Button Initializations
        if (button1 != null) {
            menu.removeButton(button1);
        }

        if (button2 != null) {
            menu.removeButton(button2);
        }

        if (button3 != null) {
            menu.removeButton(button3);
        }

        if (button4 != null) {
            menu.removeButton(button4);
        }
        // endregion

        // region Button To Edit User Name
        button1 = new TitleFAB(Profile.this);
        button1.setTitle("Change Name");
        button1.setBackgroundColor(getResources().getColor(R.color.capitipalism_primary));
        button1.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
        button1.setImageResource(R.drawable.ic_edit_white_24dp);
        button1.setOnClickListener(view -> {
            menu.collapse();

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setTitle("Enter your Name:");

            View mView = Profile.this.getLayoutInflater().inflate(R.layout.name_dialog, null);

            final EditText firstName = mView.findViewById(R.id.name_dialog_first_name);
            final EditText lastName = mView.findViewById(R.id.name_dialog_last_name);

            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                final String newFirstName = firstName.getText().toString();
                final String newLastName = lastName.getText().toString();

                if (newFirstName.length() < 3 || newFirstName.length() > 254 || newLastName.length() < 3 || newLastName.length() > 254) {
                    Toast.makeText(Profile.this, "Length can't exceed no more than 254 characters.", Toast.LENGTH_LONG).show();
                    dialogInterface.dismiss();
                } else {
                    // Updating on status on user data

                    // userDatabase.child("userFirstName").setValue(newStatus).addOnCompleteListener(task -> {
                    //     if (task.isSuccessful()) {
                    //         Toast.makeText(Profile.this, "Status updated", Toast.LENGTH_SHORT).show();
                    //     } else {
                    //         Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                    //     }
                    // });

                    Map<String, Object> userNameUpdateMap = new HashMap<>();
                    userNameUpdateMap.put("userFirstName", newFirstName);
                    userNameUpdateMap.put("userLastName", newLastName);

                    userDatabase.updateChildren(userNameUpdateMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "User Name updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            builder.setView(mView);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        menu.addButton(button1);
        // endregion

        // region Button To Change Profile Picture
        button2 = new TitleFAB(Profile.this);
        button2.setTitle("Change Image");
        button2.setBackgroundColor(getResources().getColor(R.color.capitipalism_primary));
        button2.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
        button2.setImageResource(R.drawable.ic_image_white_24dp);
        button2.setOnClickListener(view -> {
            Intent gallery = new Intent();
            gallery.setType("image/*");
            gallery.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(gallery, "Select Image"), 1);

            menu.collapse();
        });
        menu.addButton(button2);
        // endregion

        // region Button To Change User Status (About Info)
        button3 = new TitleFAB(Profile.this);
        button3.setTitle("Change Status");
        button3.setBackgroundColor(getResources().getColor(R.color.capitipalism_primary));
        button3.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
        button3.setImageResource(R.drawable.ic_edit_white_24dp);
        button3.setOnClickListener(view -> {
            menu.collapse();

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setTitle("Enter your new Status:");

            View mView = Profile.this.getLayoutInflater().inflate(R.layout.status_dialog, null);

            final EditText tmp = mView.findViewById(R.id.status_text);

            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                final String newStatus = tmp.getText().toString();

                if (newStatus.length() < 1 || newStatus.length() > 1000) {
                    Toast.makeText(Profile.this, "Status must be between 1-1000 characters.", Toast.LENGTH_LONG).show();
                    dialogInterface.dismiss();
                } else {

                    // Updating on status on user data
                    userDatabase.child("status").setValue(newStatus).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Status updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            builder.setView(mView);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        menu.addButton(button3);
        // endregion

        // region Button To Edit Social Links
        button4 = new TitleFAB(Profile.this);
        button4.setTitle("Change Social Links");
        button4.setBackgroundColor(getResources().getColor(R.color.capitipalism_primary));
        button4.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
        button4.setImageResource(R.drawable.ic_edit_white_24dp);
        button4.setOnClickListener(view -> {
            menu.collapse();

            AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
            builder.setTitle("Enter your Social Links:");

            View mView = Profile.this.getLayoutInflater().inflate(R.layout.social_links_dialog, null);

            final EditText userFB = mView.findViewById(R.id.social_links_dialog_facebook);
            final EditText userTwi = mView.findViewById(R.id.social_links_dialog_twitter);
            final EditText userInsta = mView.findViewById(R.id.social_links_dialog_instagram);
            final EditText userLink4 = mView.findViewById(R.id.social_links_dialog_4);
            final EditText userLink5 = mView.findViewById(R.id.social_links_dialog_5);
            final EditText userLink6 = mView.findViewById(R.id.social_links_dialog_6);

            builder.setPositiveButton("Update", (dialogInterface, i) -> {
                final String newUserFacebook = userFB.getText().toString();
                final String newUserTwitter = userTwi.getText().toString();
                final String newUserInstagram = userInsta.getText().toString();
                final String newUserLink4 = userLink4.getText().toString();
                final String newUserLink5 = userLink5.getText().toString();
                final String newUserLink6 = userLink6.getText().toString();


                // Toast.makeText(Profile.this, "Length can't exceed no more than 255 characters.", Toast.LENGTH_LONG).show();
                // dialogInterface.dismiss();

                // Updating on status on user data

                // userDatabase.child("userFirstName").setValue(newStatus).addOnCompleteListener(task -> {
                //     if (task.isSuccessful()) {
                //         Toast.makeText(Profile.this, "Status updated", Toast.LENGTH_SHORT).show();
                //     } else {
                //         Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                //     }
                // });

                Map<String, Object> socialLinksUpdateMap = new HashMap<>();
                boolean shouldWeUpdate = false;
                if (newUserFacebook.length() > 2 && newUserFacebook.length() < 256) {
                    socialLinksUpdateMap.put("facebook", newUserFacebook);
                    shouldWeUpdate = true;
                }

                if (newUserTwitter.length() > 2 && newUserTwitter.length() < 256) {
                    socialLinksUpdateMap.put("instagram", newUserTwitter);
                    shouldWeUpdate = true;
                }

                if (newUserInstagram.length() > 2 && newUserInstagram.length() < 256) {
                    socialLinksUpdateMap.put("twitter", newUserInstagram);
                    shouldWeUpdate = true;
                }

                if (newUserLink4.length() > 2 && newUserLink4.length() < 256) {
                    socialLinksUpdateMap.put("userLink4", newUserLink4);
                    shouldWeUpdate = true;
                }

                if (newUserLink5.length() > 2 && newUserLink5.length() < 256) {
                    socialLinksUpdateMap.put("userLink5", newUserLink5);
                    shouldWeUpdate = true;
                }

                if (newUserLink6.length() > 2 && newUserLink6.length() < 256) {
                    socialLinksUpdateMap.put("userLink6", newUserLink6);
                    shouldWeUpdate = true;
                }

                if (shouldWeUpdate) {
                    userDatabase.updateChildren(socialLinksUpdateMap).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Profile.this, "Social Links updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            });

            builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

            builder.setView(mView);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        menu.addButton(button4);
        // endregion

        CapiUserManager.loadUserData(getApplicationContext());
        if (CapiUserManager.getUserType().equals("Super Admin")) {
            if (button5 != null) {
                menu.removeButton(button5);
            }

            if (button6 != null) {
                menu.removeButton(button6);
            }

            // region Button To Edit Super Admin Password
            button5 = new TitleFAB(Profile.this);
            button5.setTitle("Change Password");
            button5.setBackgroundColor(getResources().getColor(R.color.capitipalism_primary));
            button5.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
            button5.setImageResource(R.drawable.ic_edit_white_24dp);
            button5.setOnClickListener(view -> {
                menu.collapse();

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Enter New Password:");

                View mView = Profile.this.getLayoutInflater().inflate(R.layout.new_password_dialog, null);

                final EditText newPassword = mView.findViewById(R.id.name_dialog_first_name);
                final EditText confirmPassword = mView.findViewById(R.id.name_dialog_last_name);

                builder.setPositiveButton("Update", (dialogInterface, i) -> {
                    final String newPasswordConfirmed = newPassword.getText().toString();
                    final String confirmPasswordConfirmed = confirmPassword.getText().toString();

                    if (newPasswordConfirmed.length() < 6 || newPasswordConfirmed.length() > 63 || confirmPasswordConfirmed.length() < 6 || confirmPasswordConfirmed.length() > 63) {
                        AlertCreator.showErrorAlert(Profile.this, "Error", "Your password should be at least 6 characters and at most 63 characters.");
                    } else if (!newPasswordConfirmed.equals(confirmPasswordConfirmed)) {
                        AlertCreator.showErrorAlert(Profile.this, "Error", "Passwords don't match.");
                        // Toast.makeText(Profile.this, "Length can't exceed no more than 24 characters.", Toast.LENGTH_LONG).show();
                        // dialogInterface.dismiss();
                    } else {
                        // Updating on status on user data

                        // userDatabase.child("userFirstName").setValue(newStatus).addOnCompleteListener(task -> {
                        //     if (task.isSuccessful()) {
                        //         Toast.makeText(Profile.this, "Status updated", Toast.LENGTH_SHORT).show();
                        //     } else {
                        //         Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                        //     }
                        // });

                        Map<String, Object> userPasswordChangeMap = new HashMap<>();
                        userPasswordChangeMap.put("password", hashPassword(newPasswordConfirmed));
                        // userNameUpdateMap.put("userLastName", newLastName);

                        userDatabase.updateChildren(userPasswordChangeMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // Toast.makeText(Profile.this, "User Name updated", Toast.LENGTH_SHORT).show();
                                AlertCreator.showSuccessAlert(Profile.this, "Success", "Password updated!");
                            } else {
                                // Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                                AlertCreator.showSuccessAlert(Profile.this, "Error", task.getException().getMessage());
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.show();
            });
            menu.addButton(button5);
            // endregion

            // region Button to edit user app login links
            button6 = new TitleFAB(Profile.this);
            button6.setTitle("Change Login Page Links");
            button6.setBackgroundColor(getResources().getColor(R.color.capitipalism_primary));
            button6.setRippleColor(getResources().getColor(R.color.capitipalism_accent));
            button6.setImageResource(R.drawable.ic_edit_white_24dp);
            button6.setOnClickListener(view -> {
                menu.collapse();

                AlertDialog.Builder builder = new AlertDialog.Builder(Profile.this);
                builder.setTitle("Change Login Page Links:");

                View mView = Profile.this.getLayoutInflater().inflate(R.layout.ad_links_dialog, null);

                final EditText adLink1 = mView.findViewById(R.id.social_links_dialog_facebook);
                final EditText adLink2 = mView.findViewById(R.id.social_links_dialog_twitter);
                final EditText adLink3 = mView.findViewById(R.id.social_links_dialog_instagram);

                builder.setPositiveButton("Update", (dialogInterface, i) -> {
                    final String newAdLink1 = adLink1.getText().toString();
                    final String newAdLink2 = adLink2.getText().toString();
                    final String newAdLink3 = adLink3.getText().toString();

                    Map<String, Object> adLinksUpdateMap = new HashMap<>();
                    boolean shouldWeUpdateAdLinks = false;
                    if (newAdLink1.length() > 2 && newAdLink1.length() < 256) {
                        adLinksUpdateMap.put("ad_link_1", newAdLink1);
                        shouldWeUpdateAdLinks = true;
                    }

                    if (newAdLink2.length() > 2 && newAdLink2.length() < 256) {
                        adLinksUpdateMap.put("ad_link_2", newAdLink2);
                        shouldWeUpdateAdLinks = true;
                    }

                    if (newAdLink3.length() > 2 && newAdLink3.length() < 256) {
                        adLinksUpdateMap.put("ad_link_3", newAdLink3);
                        shouldWeUpdateAdLinks = true;
                    }

                    if (shouldWeUpdateAdLinks) {
                        userDatabase.updateChildren(adLinksUpdateMap).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                AlertCreator.showSuccessAlert(Profile.this, "Success", "User App Login Page Links updated");
                                // Toast.makeText(Profile.this, "User App Login Page Links updated", Toast.LENGTH_SHORT).show();
                            } else {
                                AlertCreator.showErrorAlert(Profile.this, "Error", task.getException().getMessage());
                                Toast.makeText(Profile.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });

                builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

                builder.setView(mView);
                AlertDialog dialog = builder.create();
                dialog.show();
            });
            menu.addButton(button6);
            // endregion
        }


    }
    // endregion

    // region TRY LOADING USERS SPONSORS
    private void initCurrentUsersOnes() {
        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();

        usersOnesDatabaseReference = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID);

        userOnesSignupsRecyclerView.setHasFixedSize(true);
        userOnesSignupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // userOnesSignupsRecyclerView
        Query query = usersOnesDatabaseReference.orderByChild("date");
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("########################################");
                System.out.println("GU: " + dataSnapshot.getChildrenCount());
                System.out.println("########################################");

                userNameList1stGen.clear();
                userOnesSignupsRecyclerView.removeAllViews();

                int counter = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uID = snapshot.getKey();
                    if (!(uID.compareTo(groupID + "-PREFERENCES") == 0)) {
                        String userSponsorID = snapshot.child("userSponsorID").getValue(String.class);
                        String userName = snapshot.child("userFirstName").getValue(String.class) + " " + snapshot.child("userLastName").getValue(String.class) + " - ID: " + uID;
                        String profilePic = snapshot.child("image").getValue(String.class);
                        String status = snapshot.child("status").getValue(String.class);
                        Long balance = snapshot.child("balance").getValue(Long.class);
                        String validation = snapshot.child("validation").getValue(String.class);
                        String searchTag = snapshot.child("payDue").getValue(String.class);
                        // Log.d("TAG", "typeOf: " + snapshot.child());
                        String newUser = snapshot.child("new_user").getValue(String.class);
                        System.out.println("?????????new user is" + newUser);

                        if (Objects.requireNonNull(userSponsorID).compareToIgnoreCase(userID) == 0) {
                            userIDList1stGen.add(uID);
                            userNameList1stGen.add(userName);
                            userBalanceList1stGen.add(balance);
                            profileImageList1stGen.add(profilePic);
                            userStatusList1stGen.add(status);
                            userValidationList1stGen.add(validation);
                            userSearchTagList1stGen.add(searchTag);
                            userNewUserList1stGen.add(newUser);
                            counter++;
                        }
                        userOnesAdapter.notifyItemChanged(counter);

                        if (counter == 25) {
                            break;
                        }
                    }
                }
                profile_direct_signup_description.setText(userID + " Registerd " + counter + " Users.");
                userOnesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        userOnesAdapter = new UsersSearchAdapter(Profile.this, userIDList1stGen, profileImageList1stGen, userNewUserList1stGen, userNameList1stGen, userBalanceList1stGen, userValidationList1stGen, userSearchTagList1stGen, userStatusList1stGen);
        userOnesSignupsRecyclerView.setAdapter(userOnesAdapter);
        usersOnesDatabaseReference.keepSynced(true);
    }

    private void initCurrentUserTwos() {
        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();

        usersTwosDatabaseReference = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID);

        userTwosSignupsRecyclerView.setHasFixedSize(true);
        userTwosSignupsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // userTwosSignupsRecyclerView

        userNameList2ndGen.clear();
        userTwosSignupsRecyclerView.removeAllViews();

        final int[] firstGenCounter = {0};
        final int[] secondGenCounter = {0};

        usersTwosDatabaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("\n\n########################################################\n\n");
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String firstGenUserID = snapshot.getKey();
                    if (!(firstGenUserID.compareTo(groupID + "-PREFERENCES") == 0)) {
                        String firstGenUserSponsorID = snapshot.child("userSponsorID").getValue(String.class);

                        // System.out.println("First Gen Spon ID: " + firstGenUserSponsorID + " firstGenUserID: " + firstGenUserID);

                        if (Objects.requireNonNull(firstGenUserSponsorID).compareToIgnoreCase(userID) == 0) {
                            usersTwosDatabaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot1) {
                                    for (DataSnapshot snapshot1 : dataSnapshot1.getChildren()) {
                                        String secondGenUserID = snapshot1.getKey();
                                        if (!(secondGenUserID.compareTo(groupID + "-PREFERENCES") == 0)) {
                                            String secondGenUserSponsorID = snapshot1.child("userSponsorID").getValue(String.class);
                                            String secondGenUserName = snapshot1.child("userFirstName").getValue(String.class) + " " + snapshot1.child("userLastName").getValue(String.class) + " - ID: " + secondGenUserID;
                                            String secondGenProfilePic = snapshot1.child("image").getValue(String.class);
                                            String secondGenStatus = snapshot1.child("status").getValue(String.class);
                                            String secondGenValidation = snapshot1.child("validation").getValue(String.class);
                                            String searchTag = snapshot1.child("payDue").getValue(String.class);
                                            Long secondbalance = snapshot1.child("balance").getValue(long.class);
                                            String secondUserNew = snapshot1.child("new_user").getValue(String.class);
                                            if (Objects.requireNonNull(secondGenUserSponsorID).compareToIgnoreCase(firstGenUserID) == 0) {

                                                userIDList2ndGen.add(secondGenUserID);
                                                userNameList2ndGen.add(secondGenUserName);
                                                userBalanceList2ndGen.add(secondbalance);
                                                profileImageList2ndGen.add(secondGenProfilePic);
                                                userStatusList2ndGen.add(secondGenStatus);
                                                userValidationList2ndGen.add(secondGenValidation);
                                                userSearchTagList2ndGen.add(searchTag);
                                                userNewUserList2ndGen.add(secondUserNew);
                                                secondGenCounter[0]++;
                                            }
                                            if (secondGenCounter[0] == 50) {
                                                break;
                                            }
                                        }
                                    }

                                    // profile_indirect_signup_description.setText(userID + " Registerd " + secondGenCounter[0] + " Users.");
                                    profile_indirect_signup_description.setText("Users Registered By " + firstGenUserSponsorID + " Registerd " + secondGenCounter[0] + " Users.");
                                    userTwosAdapter = new UsersSearchAdapter(Profile.this, userIDList2ndGen, profileImageList2ndGen, userNewUserList2ndGen, userNameList2ndGen, userBalanceList2ndGen, userValidationList2ndGen, userSearchTagList2ndGen, userStatusList2ndGen);
                                    userTwosSignupsRecyclerView.setAdapter(userTwosAdapter);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            firstGenCounter[0]++;
                        }


                        if (firstGenCounter[0] == 25) {
                            break;
                        }
                    }
                }
                System.out.println("\n\n########################################################\n\n");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // endregion

    AsyncOperationCallback mAsyncOperationCallback = new AsyncOperationCallback() {
        @Override
        public boolean performWrite(NfcWriteUtility writeUtility) throws ReadOnlyTagException, InsufficientCapacityException, TagNotPresentException, FormatException {
            return writeUtility.writeTextToTagFromIntent(userID, getIntent());
        }
    };

    public void updateUserInfo(String userId, String password, Context context) {
        userInfoChange = FirebaseDatabase.getInstance().getReference().child("Users").child(CapiUserManager.getCurrentUserID());
        Map<String, Object> userInfoUpdateMap = new HashMap<>();
        userInfoUpdateMap.put("userId", userId);
        userInfoUpdateMap.put("password", hashPassword(password));
        userDatabase.updateChildren(userInfoUpdateMap).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "User Info Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // region Hash Password
    private String hashPassword(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte b : messageDigest) hexString.append(Integer.toHexString(0xFF & b));
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
    // endregion
}
