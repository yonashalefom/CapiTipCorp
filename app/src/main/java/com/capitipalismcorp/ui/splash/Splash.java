package com.capitipalismcorp.ui.splash;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.ui.login.Login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Splash extends AppCompatActivity {
    @BindView(R.id.splash_loading_text)
    TextView splash_loading_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);
        ButterKnife.bind(this);

        loadAppSettings();
    }

//    // region Init UI
//    private void initUI() {
//        bindUIElements();
//    }
//
//    // region Bind UI Elements
//    private void bindUIElements() {
//
//    }
//    // endregion
//    // endregion

    // region Load App Main Settings
    private void loadAppSettings() {
        splash_loading_text.setText("Loading App Settings");
        DatabaseReference superAdminDatabaseReference;
        superAdminDatabaseReference = FirebaseDatabase.getInstance().getReference("GroupUsers");

        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();


        superAdminDatabaseReference.child(ownerID).child(groupID).child((groupID + "-PREFERENCES")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String group_image = dataSnapshot.child("group_image").getValue(String.class);
                String group_name = dataSnapshot.child("group_name").getValue(String.class);
                String group_theme = dataSnapshot.child("group_theme").getValue(String.class);

                String ad_link_1 = dataSnapshot.child("ad_link_1").getValue(String.class);
                String ad_link_2 = dataSnapshot.child("ad_link_2").getValue(String.class);
                String ad_link_3 = dataSnapshot.child("ad_link_3").getValue(String.class);

                System.out.println("*************************************");
                System.out.println("URL: " + ownerID + "/" + groupID + "/" + groupID + "-PREFERENCES");
                System.out.println("group_image: " + group_image);
                System.out.println("group_name: " + group_name);
                System.out.println("group_theme: " + group_theme);
                System.out.println("ad_link_1: " + ad_link_1);
                System.out.println("ad_link_2: " + ad_link_2);
                System.out.println("ad_link_3: " + ad_link_3);
                System.out.println("*************************************");
                splash_loading_text.setText("App Settings Loaded");
                startActivity(new Intent(Splash.this, Login.class));
                finish();
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                splash_loading_text.setText("Unable To Load App Settings, Please Check Your Network!");
            }
        });
    }
    // endregion
}