package com.capitipalismcorp.ui.supportchat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NavUtils;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.models.Message;
import com.capitipalismcorp.ui.adapters.MessageAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import gun0912.tedbottompicker.TedBottomPicker;
import gun0912.tedbottompicker.TedBottomSheetDialogFragment;

public class ChatActivity extends AppCompatActivity {
    private final String TAG = "CA/ChatActivity";

    // Will handle all changes happening in database
    private DatabaseReference userDatabase, chatDatabase;
    private ValueEventListener userListener, chatListener;

    // Will handle old/new messages between users
    private Query messagesDatabase;
    private ChildEventListener messagesListener;

    private MessageAdapter messagesAdapter;
    private final List<Message> messagesList = new ArrayList<>();

    // Currently logged in user data
    private String currentUserId;

    // activity_chat views
    private ConstraintLayout mainContainer;
    private EditText messageEditText;
    private RecyclerView messagesContainer;
    private ImageView sendButton;
    private ImageView sendPictureButton;

    // chat_bar views
    private TextView appBarName, appBarSeen;
    private ImageView appBarBackButton;
    private CircleImageView appBarProfilePicture;

    // Will be used on Notifications to determinate if user has chat window open
    public static String otherUserId;
    public static boolean running = false;

    // region On Create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initUI();
        initVariables();
        initEventHandlers();
    }
    // endregion

    // region Important variable initializations
    private void initVariables() {
        running = true;

        CapiUserManager.loadUserData(getApplicationContext());
        currentUserId = CapiUserManager.getCurrentUserID();
        otherUserId = getIntent().getStringExtra("userID");

        messagesContainer.setHasFixedSize(true);
        messagesContainer.setLayoutManager(new LinearLayoutManager(this));

        messagesAdapter = new MessageAdapter(messagesList);

        messagesContainer.setAdapter(messagesAdapter);
    }
    // endregion

    // region UI initializations
    private void initUI() {
        bindUIElements();
    }
    // endregion

    // region Bind UI elements
    private void bindUIElements() {
        mainContainer = findViewById(R.id.chat_root);
        messagesContainer = findViewById(R.id.chat_recycler);
        messageEditText = findViewById(R.id.chat_message);
        sendButton = findViewById(R.id.chat_send);
        sendPictureButton = findViewById(R.id.chat_send_picture);

        // Action bar related
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("");


        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View actionBarView = inflater.inflate(R.layout.chat_bar, null);

        actionBar.setCustomView(actionBarView);
        appBarName = actionBarView.findViewById(R.id.chat_bar_name);
        appBarSeen = actionBarView.findViewById(R.id.chat_bar_seen);
        appBarBackButton = actionBarView.findViewById(R.id.chat_bar_backbutton);
        appBarProfilePicture = actionBarView.findViewById(R.id.chat_bar_profile_picture);
    }
    // endregion

    // region Add event listeners
    private void initEventHandlers() {
        // region Send text message
        sendButton.setOnClickListener(view -> sendMessage());
        // endregion

        // region Send picture message
        sendPictureButton.setOnClickListener(view -> {
//            Intent galleryIntent = new Intent();
//            galleryIntent.setType("image/*");
//            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
//
//            startActivityForResult(Intent.createChooser(galleryIntent, "Select Image"), 1);

//            ImagePicker.Companion.with(this)
//                    .crop()	    			//Crop image(Optional), Check Customization for more option
//                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
//                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
//                    .start();

            //call back after permission granted
            PermissionListener permissionlistener = new PermissionListener() {
                @Override
                public void onPermissionGranted() {
//                    Toast.makeText(ChatActivity.this, "Permission Granted", Toast.LENGTH_SHORT).show();
//                    Matisse.from(ChatActivity.this)
//                            .choose(MimeType.ofImage())
//                            .countable(false)
//                            .maxSelectable(1)
//                            .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
//                            .thumbnailScale(0.85f)
//                            .imageEngine(new GlideEngine())
//                            .showPreview(false)
//                            .forResult(1);
                    TedBottomPicker.with(ChatActivity.this)
                            .show(new TedBottomSheetDialogFragment.OnImageSelectedListener() {
                                @Override
                                public void onImageSelected(Uri uri) {
                                    // here is selected image uri
                                    Uri url = uri;
//                                    Toast.makeText(getApplicationContext(), "Image URL: " + url, Toast.LENGTH_SHORT).show();

                                    // region Generate ID for the currently being sent message
                                    DatabaseReference newMessageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
                                    final String newMessageID = newMessageDatabaseReference.getKey();
                                    // endregion

                                    // region Generate ID for the currently being sent notification
                                    DatabaseReference newNotificationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
                                    final String newNotificationID = newNotificationDatabaseReference.getKey();
                                    // endregion

                                    // region Prepare Image to be sent
                                    StorageReference imageToBeSent = FirebaseStorage.getInstance().getReference().child("message_images").child(newMessageID + ".jpg");
                                    // endregion

                                    // region Upload image to Firebase Storage
                                    imageToBeSent.putFile(url).addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            // Get uploaded image URL
                                            String newMessageImageURL = task.getResult().getDownloadUrl().toString();

                                            // region Prepare message to be sent
                                            Map messageToBeSent = new HashMap();
                                            messageToBeSent.put("message", newMessageImageURL);
                                            messageToBeSent.put("type", "image");
                                            messageToBeSent.put("from", currentUserId);
                                            messageToBeSent.put("to", otherUserId);
                                            messageToBeSent.put("timestamp", ServerValue.TIMESTAMP);
                                            // endregion

                                            // region Prepare notification to be sent
                                            HashMap<String, String> notificationToBeSent = new HashMap<>();
                                            notificationToBeSent.put("from", currentUserId);
                                            notificationToBeSent.put("type", "message");
                                            // endregion

                                            // region Prepare and merge all data to be sent to Firebase Realtime Database
                                            Map userMap = new HashMap();
                                            // Update current user's (sender's) messages database child
                                            userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + newMessageID, messageToBeSent);
                                            // Update other user's (receiver's) messages database child
                                            userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + newMessageID, messageToBeSent);

                                            // region Update current user's (sender's) chat database child
                                            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", "You have sent a picture.");
                                            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
                                            userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);
                                            // endregion

                                            // region Update other user's (receiver's) chat database child
                                            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", "Has send you a picture.");
                                            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
                                            userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);
                                            // endregion

                                            // Update other user's (receiver's) notification database child
                                            userMap.put("Notifications/" + otherUserId + "/" + newNotificationID, notificationToBeSent);
                                            // endregion

                                            // region Update database with the new data including message, chat and notification
                                            FirebaseDatabase.getInstance().getReference().updateChildren(userMap, (databaseError, databaseReference) -> {
                                                sendButton.setEnabled(true);

                                                if (databaseError != null) {
                                                    Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                                                }
                                            });
                                            // endregion
                                        }
                                    });
                                    // endregion
                                }
                            });
                }

                @Override
                public void onPermissionDenied(List<String> deniedPermissions) {
                    Toast.makeText(ChatActivity.this, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                }
            };

            //check all needed permissions together
            TedPermission.with(this)
                    .setPermissionListener(permissionlistener)
                    .setDeniedMessage("You must allow permissions in order to send images.\n\nPlease allow permissions to use this feature.")
                    .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    .check();
        });
        // endregion

        // region Update typing status (0 means no typing, 1 typing, 2 deleting and 3 thinking (5+ sec delay))
        messageEditText.addTextChangedListener(new TextWatcher() {
            private Timer timer = new Timer();

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                if (messagesList.size() > 0) {
                    if (charSequence.length() == 0) {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(0);

                        timer.cancel();
                    } else if (count > 0) {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(1);

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(3);
                            }
                        }, 5000);
                    } else if (before > 0) {
                        FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(2);

                        timer.cancel();
                        timer = new Timer();
                        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(3);
                            }
                        }, 5000);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        // endregion

        // region Checking if root layout changed to detect soft keyboard
        mainContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            int previousHeight = mainContainer.getRootView().getHeight() - mainContainer.getHeight() - messagesContainer.getHeight();

            @Override
            public void onGlobalLayout() {
                int height = mainContainer.getRootView().getHeight() - mainContainer.getHeight() - messagesContainer.getHeight();

                if (previousHeight != height) {
                    if (previousHeight > height) {
                        previousHeight = height;
                    } else if (previousHeight < height) {
                        messagesContainer.scrollToPosition(messagesList.size() - 1);
                        previousHeight = height;
                    }
                }
            }
        });
        // endregion

        // region AppBar back button on click listener
        appBarBackButton.setOnClickListener(view -> {
            onBackPressed();
        });
        // endregion
    }
    // endregion

    // region Update user's online status to 'online'
    @Override
    protected void onResume() {
        super.onResume();

        running = true;

//        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("online").setValue("true");

        loadMessages();
        initDatabases();
    }
    // endregion

    // region Update user's online status to 'offline'
    @Override
    protected void onPause() {
        super.onPause();

        running = false;

//        FirebaseDatabase.getInstance().getReference().child("Users").child(currentUserId).child("online").setValue(ServerValue.TIMESTAMP);

        if (messagesList.size() > 0 && messageEditText.getText().length() > 0) {
            FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId).child("typing").setValue(0);
        }

        removeListeners();
    }
    // endregion

    // region Handle Back Button
    @Override
    public void onBackPressed() {
        NavUtils.navigateUpFromSameTask(this);
    }
    // endregion

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.R.id.home:
//                NavUtils.navigateUpFromSameTask(this);
//                break;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    // region Initialize Firebase Realtime Database
    private void initDatabases() {

        // region Initialize/Update realtime other user data such as name and online status
        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();
        userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(otherUserId);
        userListener = new ValueEventListener() {
            Timer timer;

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    String name = dataSnapshot.child("userFirstName").getValue().toString() + " " + dataSnapshot.child("userLastName").getValue().toString();
                    String image = dataSnapshot.child("image").getValue().toString();
                    System.out.println("Image URL :- " + image);

                    appBarName.setText(name);
                    Picasso.get()
                            .load(image)
                            .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, ChatActivity.this.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, ChatActivity.this.getResources().getDisplayMetrics()))
                            .centerCrop()
                            .networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.hyphen_profile_picture_placeholder)
                            .into(appBarProfilePicture, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError(Exception e) {
                                    Picasso.get()
                                            .load(image)
                                            .resize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, ChatActivity.this.getResources().getDisplayMetrics()), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, ChatActivity.this.getResources().getDisplayMetrics()))
                                            .centerCrop()
                                            .placeholder(R.drawable.hyphen_profile_picture_placeholder)
                                            .error(R.drawable.hyphen_profile_picture_placeholder)
                                            .into(appBarProfilePicture);
                                }
                            });

//                    final String online = dataSnapshot.child("online").getValue().toString();
//
//                    if (online.equals("true")) {
//                        if (timer != null) {
//                            timer.cancel();
//                            timer = null;
//                        }
//
//                        appBarSeen.setText("Online");
//                    } else {
//                        if (appBarSeen.getText().length() == 0) {
//                            appBarSeen.setText("Last Seen: " + getTimeAgo(Long.parseLong(online)));
//                        } else {
//                            timer = new Timer();
//                            timer.schedule(new TimerTask() {
//                                @Override
//                                public void run() {
//                                    ChatActivity.this.runOnUiThread(() -> appBarSeen.setText("Last Seen: " + getTimeAgo(Long.parseLong(online))));
//                                }
//                            }, 2000);
//                        }
//                    }
                } catch (Exception e) {
                    Log.d(TAG, "setDatabase(): usersOtherUserListener exception: " + e.getMessage());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "setDatabase(): usersOtherUserListener failed: " + databaseError.getMessage());
            }
        };
        userDatabase.addValueEventListener(userListener);
        // endregion

        // region Check if last message is unseen and mark it as seen with current timestamp
        chatDatabase = FirebaseDatabase.getInstance().getReference().child("Chat").child(currentUserId).child(otherUserId);
        chatListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    if (dataSnapshot.hasChild("seen")) {
                        long seen = (long) dataSnapshot.child("seen").getValue();

                        if (seen == 0) {
                            chatDatabase.child("seen").setValue(ServerValue.TIMESTAMP);
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "setDatabase(): chatCurrentUserListener exception: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "setDatabase(): chatCurrentUserListener failed: " + databaseError.getMessage());
            }
        };
        chatDatabase.addValueEventListener(chatListener);
        // endregion
    }
    // endregion

    // region Load Messages
    private void loadMessages() {
        messagesList.clear();

        // region Load/Update all messages between current and other user
        messagesDatabase = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId);
        messagesListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Message message = dataSnapshot.getValue(Message.class);
                    System.out.println();
                    System.out.println("++++++++++++++++++++++++++++");
                    System.out.println("From: " + message.getFrom());
                    System.out.println("To: " + message.getTo());
                    System.out.println("Message Element: " + message.getMessage());
                    System.out.println("++++++++++++++++++++++++++++");
                    System.out.println();

                    messagesList.add(message);
                    messagesAdapter.notifyDataSetChanged();

                    messagesContainer.scrollToPosition(messagesList.size() - 1);
                } catch (Exception e) {
                    Log.d(TAG, "loadMessages(): messegesListener exception: " + e.getMessage());
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                messagesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "loadMessages(): messagesListener failed: " + databaseError.getMessage());
            }
        };
        messagesDatabase.addChildEventListener(messagesListener);
        // endregion
    }
    // endregion

    private void removeListeners() {
        try {
            chatDatabase.removeEventListener(chatListener);
            chatListener = null;

            userDatabase.removeEventListener(userListener);
            userListener = null;

            messagesDatabase.removeEventListener(messagesListener);
            messagesListener = null;
        } catch (Exception e) {
            Log.d(TAG, "exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // region Send Message
    private void sendMessage() {
        sendButton.setEnabled(false);

        String messageBody = messageEditText.getText().toString();

        if (messageBody.length() == 0) {
            Toast.makeText(getApplicationContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();

            sendButton.setEnabled(true);
        } else {
            messageEditText.setText("");

            // region Generate ID for the currently being sent message
            DatabaseReference newMessageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
            String newMessageID = newMessageDatabaseReference.getKey();
            // endregion

            // region Generate ID for the currently being sent notification
            DatabaseReference newNotificationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
            String newNotificationID = newNotificationDatabaseReference.getKey();
            // endregion

            // region Prepare message to be sent
            Map messageToBeSent = new HashMap();
            messageToBeSent.put("message", messageBody);
            messageToBeSent.put("type", "text");
            messageToBeSent.put("from", currentUserId);
            messageToBeSent.put("to", otherUserId);
            messageToBeSent.put("timestamp", ServerValue.TIMESTAMP);
            // endregion

            // region Prepare notification to be sent
            HashMap<String, String> notificationToBeSent = new HashMap<>();
            notificationToBeSent.put("from", currentUserId);
            notificationToBeSent.put("type", "message");
            // endregion

            // region Prepare and merge all data to be sent to Firebase Realtime Database
            Map dataToBeSendToFirebaseRealtimeDatabase = new HashMap();
            // Update current user's (sender's) messages database child
            dataToBeSendToFirebaseRealtimeDatabase.put("Messages/" + currentUserId + "/" + otherUserId + "/" + newMessageID, messageToBeSent);
            // Update other user's (receiver's) messages database child
            dataToBeSendToFirebaseRealtimeDatabase.put("Messages/" + otherUserId + "/" + currentUserId + "/" + newMessageID, messageToBeSent);

            // region Update current user's (sender's) chat database child
            dataToBeSendToFirebaseRealtimeDatabase.put("Chat/" + currentUserId + "/" + otherUserId + "/message", messageBody);
            dataToBeSendToFirebaseRealtimeDatabase.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
            dataToBeSendToFirebaseRealtimeDatabase.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);
            // endregion

            // region Update other user's (receiver's) chat database child
            dataToBeSendToFirebaseRealtimeDatabase.put("Chat/" + otherUserId + "/" + currentUserId + "/message", messageBody);
            dataToBeSendToFirebaseRealtimeDatabase.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
            dataToBeSendToFirebaseRealtimeDatabase.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);
            // endregion

            // Update other user's (receiver's) notification database child
            dataToBeSendToFirebaseRealtimeDatabase.put("Notifications/" + otherUserId + "/" + newNotificationID, notificationToBeSent);
            // endregion

            // region Update database with the new data including message, chat and notification
            FirebaseDatabase.getInstance().getReference().updateChildren(dataToBeSendToFirebaseRealtimeDatabase, (databaseError, databaseReference) -> {
                sendButton.setEnabled(true);

                if (databaseError != null) {
                    Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
                }
            });
            // endregion
        }
    }
    // endregion

    // region Send Picture Message
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            Uri url = data.getData();
//            System.out.println("Image URL: " + url);
//
//            // region Generate ID for the currently being sent message
//            DatabaseReference newMessageDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserId).child(otherUserId).push();
//            final String newMessageID = newMessageDatabaseReference.getKey();
//            // endregion
//
//            // region Generate ID for the currently being sent notification
//            DatabaseReference newNotificationDatabaseReference = FirebaseDatabase.getInstance().getReference().child("Notifications").child(otherUserId).push();
//            final String newNotificationID = newNotificationDatabaseReference.getKey();
//            // endregion
//
//            // region Prepare Image to be sent
//            StorageReference imageToBeSent = FirebaseStorage.getInstance().getReference().child("message_images").child(newMessageID + ".jpg");
//            // endregion
//
//            // region Upload image to Firebase Storage
//            imageToBeSent.putFile(url).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    // Get uploaded image URL
//                    String newMessageImageURL = task.getResult().getDownloadUrl().toString();
//
//                    // region Prepare message to be sent
//                    Map messageToBeSent = new HashMap();
//                    messageToBeSent.put("message", newMessageImageURL);
//                    messageToBeSent.put("type", "image");
//                    messageToBeSent.put("from", currentUserId);
//                    messageToBeSent.put("to", otherUserId);
//                    messageToBeSent.put("timestamp", ServerValue.TIMESTAMP);
//                    // endregion
//
//                    // region Prepare notification to be sent
//                    HashMap<String, String> notificationToBeSent = new HashMap<>();
//                    notificationToBeSent.put("from", currentUserId);
//                    notificationToBeSent.put("type", "message");
//                    // endregion
//
//                    // region Prepare and merge all data to be sent to Firebase Realtime Database
//                    Map userMap = new HashMap();
//                    // Update current user's (sender's) messages database child
//                    userMap.put("Messages/" + currentUserId + "/" + otherUserId + "/" + newMessageID, messageToBeSent);
//                    // Update other user's (receiver's) messages database child
//                    userMap.put("Messages/" + otherUserId + "/" + currentUserId + "/" + newMessageID, messageToBeSent);
//
//                    // region Update current user's (sender's) chat database child
//                    userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/message", "You have sent a picture.");
//                    userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/timestamp", ServerValue.TIMESTAMP);
//                    userMap.put("Chat/" + currentUserId + "/" + otherUserId + "/seen", ServerValue.TIMESTAMP);
//                    // endregion
//
//                    // region Update other user's (receiver's) chat database child
//                    userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/message", "Has send you a picture.");
//                    userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/timestamp", ServerValue.TIMESTAMP);
//                    userMap.put("Chat/" + otherUserId + "/" + currentUserId + "/seen", 0);
//                    // endregion
//
//                    // Update other user's (receiver's) notification database child
//                    userMap.put("Notifications/" + otherUserId + "/" + newNotificationID, notificationToBeSent);
//                    // endregion
//
//                    // region Update database with the new data including message, chat and notification
//                    FirebaseDatabase.getInstance().getReference().updateChildren(userMap, (databaseError, databaseReference) -> {
//                        sendButton.setEnabled(true);
//
//                        if (databaseError != null) {
//                            Log.d(TAG, "sendMessage(): updateChildren failed: " + databaseError.getMessage());
//                        }
//                    });
//                    // endregion
//                }
//            });
//            // endregion
//        }
    }
    // endregion

    private String getTimeAgo(long time) {
        final long diff = System.currentTimeMillis() - time;

        if (diff < 1) {
            return " just now";
        }
        if (diff < 60 * 1000) {
            if (diff / 1000 < 2) {
                return diff / 1000 + " second ago";
            } else {
                return diff / 1000 + " seconds ago";
            }
        } else if (diff < 60 * (60 * 1000)) {
            if (diff / (60 * 1000) < 2) {
                return diff / (60 * 1000) + " minute ago";
            } else {
                return diff / (60 * 1000) + " minutes ago";
            }
        } else if (diff < 24 * (60 * (60 * 1000))) {
            if (diff / (60 * (60 * 1000)) < 2) {
                return diff / (60 * (60 * 1000)) + " hour ago";
            } else {
                return diff / (60 * (60 * 1000)) + " hours ago";
            }
        } else {
            if (diff / (24 * (60 * (60 * 1000))) < 2) {
                return diff / (24 * (60 * (60 * 1000))) + " day ago";
            } else {
                return diff / (24 * (60 * (60 * 1000))) + " days ago";
            }
        }
    }
}