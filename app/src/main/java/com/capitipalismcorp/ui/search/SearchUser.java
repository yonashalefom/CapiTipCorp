package com.capitipalismcorp.ui.search;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.ui.adapters.UsersSearchAdapter;
import com.capitipalismcorp.ui.dashboard.Home;
import com.capitipalismcorp.ui.login.Login;
import com.capitipalismcorp.ui.profile.Profile;
import com.capitipalismcorp.ui.register.Register;
import com.capitipalismcorp.ui.support.HomeActivity;
import com.capitipalismcorp.ui.supportchat.ChatActivity;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.util.Objects;

import be.appfoundry.nfclibrary.utilities.sync.NfcReadUtilityImpl;
import de.hdodenhof.circleimageview.CircleImageView;
import top.wefor.circularanim.CircularAnim;

public class SearchUser extends AppCompatActivity {
    // private FirebaseRecyclerAdapter allUsersFirebaseRecyclerAdapter;
    private boolean isSearching = false;

    // region Firebase Database Reference Variable
    DatabaseReference userFirebaseDatabaseReference;
    // endregion

    // region Variables for searching users
    EditText searchUsersEditText;
    // endregion


    RecyclerView foundUsersRecyclerView;

    ArrayList<String> userIDList;
    ArrayList<String> userNameList;
    ArrayList<String> userSearchTagList;
    ArrayList<Long> userBalanceList;
    ArrayList<String> profileImageList;
    ArrayList<String> userStatusList;
    ArrayList<String> newUserList;
    ArrayList<String> userValidationList;
    UsersSearchAdapter searchAdapter;
    ConstraintLayout nfcStatusIndicator;
    ImageView searchByNFCTag, registerNewUsers, search_users_contact_support;
    CircleImageView search_user_profile_picture;
    TextView tagResult, foundUsersCount;
    SpinKitView search_user_progress_indicator;
    FloatingActionButton search_user_support_center;


    // region NFC Reader
    private PendingIntent pendingIntent;
    private IntentFilter[] mIntentFilters;
    private String[][] mTechLists;
    private NfcAdapter mNfcAdapter;
    private Vibrator myVibrator;
    private boolean isReadingNFC = false;
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_user);

        initSearch();
        initUI();
        initEventHandlers();
        loadUserData();

        listAllUsersNew();
        initNFCReader();
    }

    // region Init UI
    private void initUI() {
        bindUIElements();
    }
    // endregion

    // region Bind UI Elements
    @SuppressLint("RestrictedApi")
    private void bindUIElements() {
        searchUsersEditText = findViewById(R.id.search_users_activity_search_box);
        foundUsersRecyclerView = findViewById(R.id.search_users_activity_list_of_found_usres);
        nfcStatusIndicator = findViewById(R.id.nfc_status_indicator);
        searchByNFCTag = findViewById(R.id.search_user_by_nfc_tag);
        tagResult = findViewById(R.id.search_user_tag_data);
        foundUsersCount = findViewById(R.id.users_list_count);
        registerNewUsers = findViewById(R.id.profile_register_new_users);
        search_user_progress_indicator = findViewById(R.id.search_user_progress_indicator);
        search_user_profile_picture = findViewById(R.id.search_user_profile_picture);
        search_user_support_center = findViewById(R.id.search_user_support_center);
        search_users_contact_support = findViewById(R.id.search_users_contact_support);

        CapiUserManager.loadUserData(getApplicationContext());
        if (CapiUserManager.getUserType().equals("SupportRep")) {
            search_user_support_center.setVisibility(View.VISIBLE);
            search_users_contact_support.setVisibility(View.GONE);
            registerNewUsers.setVisibility(View.GONE);
            registerNewUsers.setImageResource(R.drawable.ic_capitipalism_register_new_user);
        } else if (CapiUserManager.getUserType().equals("GroupDefaultUser")) {
            search_users_contact_support.setVisibility(View.VISIBLE);
            search_user_support_center.setVisibility(View.GONE);
            registerNewUsers.setVisibility(View.VISIBLE);
            registerNewUsers.setImageResource(R.drawable.ic_capitipalism_register_new_user);
        } else if (CapiUserManager.getUserType().equals("GroupAdmin")) {
            search_users_contact_support.setVisibility(View.GONE);
            search_user_support_center.setVisibility(View.GONE);
            registerNewUsers.setVisibility(View.VISIBLE);
            registerNewUsers.setImageResource(R.drawable.ic_capitipalism_register_new_user);
        } else if (CapiUserManager.getUserType().equals("Super Admin")) {
            search_users_contact_support.setVisibility(View.GONE);
            search_user_support_center.setVisibility(View.GONE);
            registerNewUsers.setVisibility(View.VISIBLE);
            registerNewUsers.setImageResource(R.drawable.ic_capitipalism_dashboard_1);
        }

        foundUsersRecyclerView.setHasFixedSize(true);
        foundUsersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
    // endregion

    // region Load User Data
    private void loadUserData() {
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
        System.out.println("Load User Data Called");
        CapiUserManager.loadUserData(getApplicationContext());
        if (CapiUserManager.userDataExists()) {
            if (CapiUserManager.getUserType().equals("Super Admin")) {
                System.out.println("Profile Handler For Super Admin");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference("SuperAdmins").child(CapiUserManager.getCurrentUserID());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userID = dataSnapshot.getKey();
                        final String layoutImage = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                        if (!layoutImage.equals("default")) {
                            Picasso.get()
                                    .load(layoutImage)
                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                    .centerCrop()
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.hyphen_user_filled)
                                    .error(R.drawable.hyphen_user_filled)
                                    .into(search_user_profile_picture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get()
                                                    .load(layoutImage)
                                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                                    .centerCrop()
                                                    .placeholder(R.drawable.hyphen_user_filled)
                                                    .error(R.drawable.hyphen_user_filled)
                                                    .into(search_user_profile_picture);
                                        }
                                    });

                            search_user_profile_picture.setOnClickListener(view -> {
                                Intent intent = new Intent(SearchUser.this, Profile.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                            });
                        } else {
                            search_user_profile_picture.setOnClickListener(view -> {
                                Intent intent = new Intent(SearchUser.this, Profile.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            } else {
                String groupID = GroupManager.getGroupID();
                String ownerID = GroupManager.getOwnerID();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference reference = database.getReference().child("GroupUsers").child(ownerID).child(groupID).child(CapiUserManager.getCurrentUserID());
                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final String userID = dataSnapshot.getKey();
                        final String layoutImage = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
                        if (!layoutImage.equals("default")) {
                            Picasso.get()
                                    .load(layoutImage)
                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                    .centerCrop()
                                    .networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(R.drawable.hyphen_user_filled)
                                    .error(R.drawable.hyphen_user_filled)
                                    .into(search_user_profile_picture, new Callback() {
                                        @Override
                                        public void onSuccess() {

                                        }

                                        @Override
                                        public void onError(Exception e) {
                                            Picasso.get()
                                                    .load(layoutImage)
                                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
                                                    .centerCrop()
                                                    .placeholder(R.drawable.hyphen_user_filled)
                                                    .error(R.drawable.hyphen_user_filled)
                                                    .into(search_user_profile_picture);
                                        }
                                    });

                            search_user_profile_picture.setOnClickListener(view -> {
                                Intent intent = new Intent(SearchUser.this, Profile.class);
                                intent.putExtra("userID", userID);
                                startActivity(intent);
                            });
                        }

                        Toast.makeText(SearchUser.this, "Creating Event Handler For Your Profile", Toast.LENGTH_SHORT).show();
                        search_user_profile_picture.setOnClickListener(view -> {
                            Toast.makeText(SearchUser.this, "Showing Your Profile", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SearchUser.this, Profile.class);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        } else {
            search_user_profile_picture.setOnClickListener(view -> {
                Toast.makeText(getApplicationContext(), "No user found! Please register to see your profile.", Toast.LENGTH_LONG).show();
            });
        }
        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
    }
    // endregion

    // region Load User Data Corp Group Default
//    private void loadUserData() {
//        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//        System.out.println("Load User Data Called");
//        CapiUserManager.loadUserData(getApplicationContext());
//        if (CapiUserManager.userDataExists()) {
//            Toast.makeText(this, "User Data Exists", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this, "User ID: " + CapiUserManager.getCurrentUserID(), Toast.LENGTH_SHORT).show();
//
//            String groupID = GroupManager.getGroupID();
//            String ownerID = GroupManager.getOwnerID();
//
//            FirebaseDatabase database = FirebaseDatabase.getInstance();
//            DatabaseReference reference = database.getReference().child("GroupUsers").child(ownerID).child(groupID).child(CapiUserManager.getCurrentUserID());
//            reference.addValueEventListener(new ValueEventListener() {
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    final String userID = dataSnapshot.getKey();
//                        final String layoutImage = Objects.requireNonNull(dataSnapshot.child("image").getValue()).toString();
//                        if (!layoutImage.equals("default")) {
//                            Picasso.get()
//                                    .load(layoutImage)
//                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
//                                    .centerCrop()
//                                    .networkPolicy(NetworkPolicy.OFFLINE)
//                                    .placeholder(R.drawable.hyphen_user_filled)
//                                    .error(R.drawable.hyphen_user_filled)
//                                    .into(search_user_profile_picture, new Callback() {
//                                        @Override
//                                        public void onSuccess() {
//
//                                        }
//
//                                        @Override
//                                        public void onError(Exception e) {
//                                            Picasso.get()
//                                                    .load(layoutImage)
//                                                    .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics()))
//                                                    .centerCrop()
//                                                    .placeholder(R.drawable.hyphen_user_filled)
//                                                    .error(R.drawable.hyphen_user_filled)
//                                                    .into(search_user_profile_picture);
//                                        }
//                                    });
//
//                            search_user_profile_picture.setOnClickListener(view -> {
//                                Intent intent = new Intent(SearchUser.this, Profile.class);
//                                intent.putExtra("userID", userID);
//                                startActivity(intent);
//                            });
//                        }
//
//                        Toast.makeText(SearchUser.this, "Creating Event Handler For Your Profile", Toast.LENGTH_SHORT).show();
//                        search_user_profile_picture.setOnClickListener(view -> {
//                            Toast.makeText(SearchUser.this, "Showing Your Profile", Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(SearchUser.this, Profile.class);
//                            intent.putExtra("userID", userID);
//                            startActivity(intent);
//                        });
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        } else {
//            search_user_profile_picture.setOnClickListener(view -> {
//                Toast.makeText(getApplicationContext(), "No user found! Please register to see your profile.", Toast.LENGTH_LONG).show();
//            });
//        }
//        System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
//    }
    // endregion

    // region Init Search
    private void initSearch() {
        userFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        userIDList = new ArrayList<>();
        userNameList = new ArrayList<>();
        userSearchTagList = new ArrayList<>();
        userBalanceList = new ArrayList<>();
        profileImageList = new ArrayList<>();
        userStatusList = new ArrayList<>();
        userValidationList = new ArrayList<>();
        newUserList = new ArrayList<>();

        // listAllUsers();
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        // region Old Search Event Handler
        searchUsersEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    searchUsersByID(s.toString());
                    isSearching = true;
                } else {
                    isSearching = false;
                    userIDList.clear();
                    userNameList.clear();
                    profileImageList.clear();
                    userStatusList.clear();
                    foundUsersRecyclerView.removeAllViews();
                    listAllUsersNew();
                }
            }
        });
        // endregion

        // region Search User By ID new Event Handler
        // searchUsersEditText.addTextChangedListener(new TextWatcher() {
        //     @Override
        //     public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //     }
        //
        //     @Override
        //     public void onTextChanged(CharSequence s, int start, int before, int count) {
        //     }
        //
        //     @Override
        //     public void afterTextChanged(Editable s) {
        //         if (!s.toString().isEmpty()) {
        //             searchUsersByIDNew(s.toString());
        //         } else {
        //             userIDList.clear();
        //             userNameList.clear();
        //             profileImageList.clear();
        //             userStatusList.clear();
        //             // Toast.makeText(getApplicationContext(), foundUsersRecyclerView.getChildCount(), Toast.LENGTH_LONG).show();
        //             // if (foundUsersRecyclerView.getChildCount() > 0) {
        //
        //             foundUsersRecyclerView.removeAllViews();
        //
        //             // }
        //             //
        //             // Toast.makeText(getApplicationContext(), foundUsersRecyclerView.getChildCount(), Toast.LENGTH_LONG).show();
        //         }
        //     }
        // });
        // endregion

        if (CapiUserManager.getUserType().equals("Super Admin")) {
            registerNewUsers.setOnClickListener(view -> startActivity(new Intent(SearchUser.this, Home.class)));

        } else {
            registerNewUsers.setOnClickListener(view -> startActivity(new Intent(SearchUser.this, Register.class)));
        }

        searchByNFCTag.setOnClickListener(view -> {
            if (!isReadingNFC) {
                nfcStatusIndicator.setVisibility(View.VISIBLE);
                isReadingNFC = true;
            } else {
                nfcStatusIndicator.setVisibility(View.GONE);
                isReadingNFC = false;
                if (mNfcAdapter != null) {
                    mNfcAdapter.disableForegroundDispatch(this);
                }
            }
        });

        search_user_support_center.setOnClickListener(view -> {
            new Handler().postDelayed(() -> CircularAnim.fullActivity(SearchUser.this, search_user_support_center)
                    .colorOrImageRes(R.color.capitipalism_primary)
                    .go(() -> {
                        startActivity(new Intent(SearchUser.this, HomeActivity.class));
                        finish();
                    }), 0);
        });

        search_users_contact_support.setOnClickListener(view1 -> {
            Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
            chatIntent.putExtra("userID", "SupportRep");
            startActivity(chatIntent);
        });
    }
    // endregion

    // region NFC Reader
    private void initNFCReader() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        mIntentFilters = new IntentFilter[]{new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)};
        mTechLists = new String[][]{new String[]{Ndef.class.getName()}, new String[]{NdefFormatable.class.getName()}};
    }

    public void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, pendingIntent, mIntentFilters, mTechLists);
        }
    }

    public void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (isReadingNFC) {
            SparseArray<String> res = new NfcReadUtilityImpl().readFromTagWithSparseArray(intent);
            StringBuilder tagData = new StringBuilder();
            for (int i = 0; i < res.size(); i++) {
                Toast.makeText(this, res.valueAt(i), Toast.LENGTH_SHORT).show();
                tagData.append(res.valueAt(i)).append("\n");
            }

            // myVibrator.vibrate(50);
            searchUsersEditText.setText(tagData.toString());
        }
    }
    // endregion

    // region List All Users
    private void listAllUsersNew() {
        DatabaseReference rootDatabaseReference = FirebaseDatabase.getInstance().getReference();

        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();


        DatabaseReference allUsers = rootDatabaseReference.child("GroupUsers").child(ownerID).child(groupID);
        System.out.println("*************************************");
        System.out.println("*************************************");
        System.out.println("*************************************");
        System.out.println("uID: " + "Listing all users");
        System.out.println("*************************************");
        System.out.println("*************************************");
        System.out.println("*************************************");

        ValueEventListener eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                System.out.println("$*************************************$");
                System.out.println("$*************************************$");
                System.out.println("$*************************************$");
                userIDList.clear();
                userNameList.clear();
                profileImageList.clear();
                userStatusList.clear();
                foundUsersRecyclerView.removeAllViews();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uID = snapshot.getKey();
                    System.out.println("*************************************");
                    System.out.println("*************************************");
                    System.out.println("*************************************");
                    System.out.println("uID: " + uID);
                    System.out.println("*************************************");
                    System.out.println("*************************************");
                    System.out.println("*************************************");
                    if (!(uID.compareTo(groupID + "-PREFERENCES") == 0)) {
                        String status = snapshot.child("status").getValue(String.class);
                        String userName = snapshot.child("userFirstName").getValue(String.class) + " " + snapshot.child("userLastName").getValue(String.class) + " - ID: " + uID;
                        String profilePic = snapshot.child("image").getValue(String.class);
                        String validation = snapshot.child("validation").getValue(String.class);
                        String searchTag = snapshot.child("searchTag").getValue(String.class);
                        String newUser = snapshot.child("new_user").getValue(String.class);
                        Long balance = snapshot.child("balance").getValue(Long.class);

                        userIDList.add(uID);
                        userNameList.add(userName);
                        profileImageList.add(profilePic);
                        userStatusList.add(status);
                        userBalanceList.add(balance);
                        userValidationList.add(validation);
                        userSearchTagList.add(searchTag);
                        newUserList.add(newUser);
                    }


                    // if (searchedString.length() > 0) {
                    //     if (counter == 15)
                    //         break;
                    // } else {
                    //     foundUsersCount.setText("ALL USERS");
                    //     if (counter == 50)
                    //         break;
                    // }
                }

                foundUsersCount.setText("ALL USERS");
                foundUsersCount.setVisibility(View.VISIBLE);
                search_user_progress_indicator.setVisibility(View.GONE);

                searchAdapter = new UsersSearchAdapter(SearchUser.this, userIDList, profileImageList, newUserList, userNameList, userBalanceList, userValidationList, userSearchTagList, userStatusList);
                foundUsersRecyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                foundUsersCount.setText("ERROR! UNABLE TO LOAD. PLEASE CHECK YOUR CONNECTION.");
                foundUsersCount.setVisibility(View.VISIBLE);
                search_user_progress_indicator.setVisibility(View.GONE);
            }
        };
        allUsers.addListenerForSingleValueEvent(eventListener);
    }
    // endregion

    // region Search User By ID new Implementation
    private void searchUsersByIDNew(String userIDToSearch) {
        Query firebaseSearchUserByIDQuery = userFirebaseDatabaseReference.child("Users").orderByChild("userID")
                .startAt(userIDToSearch)
                .endAt(userIDToSearch + "\uf8ff");
        firebaseSearchUserByIDQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userIDList.clear();
                userNameList.clear();
                profileImageList.clear();
                userStatusList.clear();
                foundUsersRecyclerView.removeAllViews();

                int counter = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String status = snapshot.child("status").getValue(String.class);
                    String userName = snapshot.child("userFirstName").getValue(String.class) + " " + snapshot.child("userLastName").getValue(String.class) + " - ID: " + uid;
                    String profilePic = snapshot.child("image").getValue(String.class);
                    Long balance = snapshot.child("balance").getValue(Long.class);
                    String validation = snapshot.child("validation").getValue(String.class);
                    String searchTag = snapshot.child("searchTag").getValue(String.class);
                    String newUser = snapshot.child("new_user").getValue(String.class);


                    // if (userIDToSearch.length() > 0) {
                    //     if (uid.toLowerCase().contains(userIDToSearch.toLowerCase())) {
                    userNameList.add(userName);
                    profileImageList.add(profilePic);
                    userStatusList.add(status);
                    userBalanceList.add(balance);
                    userValidationList.add(validation);
                    userSearchTagList.add(searchTag);
                    newUserList.add(newUser);
                    counter++;
                    //     }
                    // } else {
                    //     userIDList.add(uid);
                    //     userNameList.add(userName);
                    //     profileImageList.add(profilePic);
                    //     userStatusList.add(status);
                    //     counter++;
                    // }


                    // if (searchedString.length() > 0) {
                    //     if (counter == 15)
                    //         break;
                    // } else {
                    //     foundUsersCount.setText("ALL USERS");
                    //     if (counter == 50)
                    //         break;
                    // }

                }

                foundUsersCount.setText("FOUND " + counter + " USERS");
                searchAdapter = new UsersSearchAdapter(SearchUser.this, userIDList, profileImageList, newUserList, userNameList, userBalanceList, userValidationList, userSearchTagList, userStatusList);
                foundUsersRecyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // endregion

    // region Search Users By ID
    private void searchUsersByID(final String searchedString) {
        userFirebaseDatabaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userIDList.clear();
                userNameList.clear();
                profileImageList.clear();
                userStatusList.clear();
                foundUsersRecyclerView.removeAllViews();

                int counter = 0;

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uID = snapshot.getKey();
                    String status = snapshot.child("status").getValue(String.class);
                    String searchTag = snapshot.child("searchTag").getValue(String.class);
                    String userName = snapshot.child("userFirstName").getValue(String.class) + " " + snapshot.child("userLastName").getValue(String.class) + " - ID: " + uID;
                    String profilePic = snapshot.child("image").getValue(String.class);
                    Long balance = snapshot.child("balance").getValue(Long.class);
                    String validation = snapshot.child("validation").getValue(String.class);
                    String newUser = snapshot.child("new_user").getValue(String.class);


                    if (searchedString.length() > 0) {
                        if (uID.toLowerCase().contains(searchedString.toLowerCase()) || searchTag.toLowerCase().contains(searchedString.toLowerCase().trim())) {
                            userIDList.add(uID);
                            userNameList.add(userName);
                            profileImageList.add(profilePic);
                            userStatusList.add(status);
                            userBalanceList.add(balance);
                            userValidationList.add(validation);
                            userSearchTagList.add(searchTag);
                            newUserList.add(newUser);
                            counter++;
                        }
                    } else {
                        userIDList.add(uID);
                        userNameList.add(userName);
                        profileImageList.add(profilePic);
                        userStatusList.add(status);
                        userBalanceList.add(balance);
                        userValidationList.add(validation);
                        userSearchTagList.add(searchTag);
                        counter++;
                    }

                    foundUsersCount.setText("FOUND " + counter + " USERS");

                    if (searchedString.length() > 0) {
                        if (counter == 15)
                            break;
                    } else {
                        foundUsersCount.setText("ALL USERS");
                        if (counter == 50)
                            break;
                    }

                }

                searchAdapter = new UsersSearchAdapter(SearchUser.this, userIDList, profileImageList, newUserList, userNameList, userBalanceList, userValidationList, userSearchTagList, userStatusList);
                foundUsersRecyclerView.setAdapter(searchAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    // endregion

    @Override
    protected void onStart() {
        super.onStart();

        // if (!isSearching) {
        //     adapter.startListening();
        // }
    }

    @Override
    protected void onStop() {
        super.onStop();

        // if (!isSearching) {
        //     adapter.stopListening();
        // }
    }
}