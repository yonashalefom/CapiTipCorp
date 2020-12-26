package com.capitipalismcorp.ui.register;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.ui.helpers.AlertCreator;
import com.capitipalismcorp.ui.profile.Profile;
import com.capitipalismcorp.ui.search.SearchUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Register extends AppCompatActivity {
    private EditText registerTextID;
    private EditText registerSponsorID;
    private EditText registerURL;
    private Button registerButton;
    private ProgressBar registerProgress;
    private ImageView searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CapiUserManager.loadUserData(getApplicationContext());
//        if (CapiUserManager.userDataExists()) {
//            System.out.println("*********FDSLKFJDSKLFJDSKLF: " + CapiUserManager.getUserType());
//            if (!CapiUserManager.getUserType().equals("Super Admin") || !CapiUserManager.getUserType().equals("GroupUser")) {
//                AlertCreator.showErrorAlert(this, "Invalid User", "You don't have the privileges to access this page!");
//                startActivity(new Intent(Register.this, Login.class));
//                finish();
//            }
//        }
        setContentView(R.layout.activity_register);
        initUI();
        initEventHandlers();
        loadUserData();
    }

    // region Init UI
    private void initUI() {
        bindUIElements();
    }
    // endregion

    // region Bind UI Elements
    private void bindUIElements() {
        registerTextID = findViewById(R.id.register_txt_id);
        registerSponsorID = findViewById(R.id.register_sponsor_id);
        registerURL = findViewById(R.id.register_txt_url);
        registerButton = findViewById(R.id.register_btn_register);
        registerProgress = findViewById(R.id.register_progress_register);
        searchButton = findViewById(R.id.register_page_search);
    }
    // endregion

    // region Init Event Handlers
    private void initEventHandlers() {
        // registerButton.setOnClickListener(view -> startActivity(new Intent(Register.this, Profile.class)));
        registerButton.setOnClickListener(view -> register());
        searchButton.setOnClickListener(view -> startActivity(new Intent(Register.this, SearchUser.class)));
    }
    // endregion

    // region Load User Data
    private void loadUserData() {
        CapiUserManager.loadUserData(getApplicationContext());
        if (CapiUserManager.userDataExists()) {
            registerSponsorID.setText(CapiUserManager.getCurrentUserID());
        }
    }
    // endregion

    // region Register Users
    private void register() {
        registerButton.setClickable(false);
        registerButton.setBackground(getResources().getDrawable(R.drawable.hyphen_button_inactive));

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(registerButton.getWindowToken(), 0);

        if (registerTextID.getText().toString().length() == 0 || registerSponsorID.getText().toString().length() == 0 || registerURL.getText().toString().length() == 0) {
            AlertCreator.showErrorAlert(this, "Incomplete!", "Please fill all fields in order to register.");

            registerProgress.setVisibility(View.INVISIBLE);
            registerButton.setClickable(true);
            registerButton.setBackground(getResources().getDrawable(R.drawable.hyphen_button_default));
        } else {
            CapiUserManager.loadUserData(getApplicationContext());

            // If current user data exists
            if (CapiUserManager.userDataExists()) {
                finishRegistration(true);
            } else {
                finishRegistration(false);
            }
        }
    }

    private void finishRegistration(boolean userAlreadyExists) {
        registerProgress.setVisibility(View.VISIBLE);
        String userID = registerTextID.getText().toString();
        String userSponsorID = registerSponsorID.getText().toString();
        String userURL = registerURL.getText().toString();

        // region REGISTER USER
        // region Prepare User Registration Data
        Map<Object, Object> map = new HashMap<>();
        map.put("balance", 0);
        map.put("colorPassword", "default");
        map.put("date", ServerValue.TIMESTAMP);
        map.put("facebook", "https://example.com/" + userID);
        map.put("image", "default");
        map.put("instagram", "https://example.com/" + userID);
        map.put("logged_in", "false");
        map.put("new_user", "true");
        map.put("payDue", "");
        map.put("searchTag", userID);
        map.put("status", "I am " + userID + " working on CapiTiPalism. You can contact me with the social links provided in the contact section.");
        map.put("twitter", "https://example.com/user");
        map.put("userFirstName", "default");
        map.put("userID", userID);
        map.put("userLastName", "default");
        map.put("userLink4", "https://example.com/" + userID);
        map.put("userLink5", "https://example.com/" + userID);
        map.put("userLink6", "https://example.com/" + userID);
        map.put("userSponsorID", userSponsorID);
        map.put("userType", "GroupDefaultUser");
        map.put("userURL", userURL);
        map.put("validation", "null");
        // endregion

        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();

        // region Uploading User Registration Data To Firebase Realtime Database
        FirebaseDatabase.getInstance().getReference().child("GroupUsers").child(ownerID).child(groupID).child(userID).setValue(map).addOnCompleteListener(saveUserInfoToDBTask -> {
            if (saveUserInfoToDBTask.isSuccessful()) {
                registerButton.setBackground(getResources().getDrawable(R.drawable.hyphen_button_success));
                registerButton.setClickable(true);
                registerProgress.setVisibility(View.INVISIBLE);
                // AppState.setUserLoaded(true);
                // AppState.setCurrentUserID(userID);

                // region User Successfully Registered
                if (!userAlreadyExists) {
                    CapiUserManager.saveUserData(getApplicationContext(), "Boss", userID);
                    CapiUserManager.loadUserData(getApplicationContext());
                }

                Intent userProfileIntent = new Intent(Register.this, Profile.class);
                userProfileIntent.putExtra("userID", userID);
                startActivity(userProfileIntent);
            } else {
                AlertCreator.showErrorAlert(Register.this, "Error!", Objects.requireNonNull(saveUserInfoToDBTask.getException()).getMessage());
                registerProgress.setVisibility(View.INVISIBLE);
            }
        });
    }
    // endregion
}
// endregion

