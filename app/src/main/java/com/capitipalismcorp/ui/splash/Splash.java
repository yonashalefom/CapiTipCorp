package com.capitipalismcorp.ui.splash;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.classes.SettingsManager;
import com.capitipalismcorp.ui.login.Login;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

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

        loadAppSettingsFromServer();
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

    // region Load App Main Settings From Server
    private void loadAppSettingsFromServer() {
        splash_loading_text.setText("Loading App Settings");
        DatabaseReference superAdminDatabaseReference;
        superAdminDatabaseReference = FirebaseDatabase.getInstance().getReference("GroupUsers");

        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();


        superAdminDatabaseReference.child(ownerID).child(groupID).child((groupID + "-PREFERENCES")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ad_link_1 = dataSnapshot.child("ad_link_1").getValue(String.class);
                String ad_link_2 = dataSnapshot.child("ad_link_2").getValue(String.class);
                String ad_link_3 = dataSnapshot.child("ad_link_3").getValue(String.class);
                Boolean color_confirmation_feature = dataSnapshot.child("color_confirmation_feature").getValue(Boolean.class);
                String group_image = dataSnapshot.child("group_image").getValue(String.class);
                String group_name = dataSnapshot.child("group_name").getValue(String.class);
                String group_theme = dataSnapshot.child("group_theme").getValue(String.class);
                Boolean ones_twos_feature = dataSnapshot.child("ones_twos_feature").getValue(Boolean.class);

                splash_loading_text.setText("App Settings Loaded");

                Map<String, Object> appSettings = new HashMap<>();
                appSettings.put("ad_link_1", ad_link_1);
                appSettings.put("ad_link_2", ad_link_2);
                appSettings.put("ad_link_3", ad_link_3);
                appSettings.put("color_confirmation_feature", color_confirmation_feature);
                appSettings.put("group_image", group_image);
                appSettings.put("group_name", group_name);
                appSettings.put("group_theme", group_theme);
                appSettings.put("ones_twos_feature", ones_twos_feature);

                SettingsManager.setAppSettings(appSettings);

                CapiUserManager.loadUserData(getApplicationContext());

                if (CapiUserManager.userDataExists()) {
                    if (CapiUserManager.getUserType().equals("GroupAdmin")) {
                        syncLocalSettingsWithServerSettings(appSettings);
                    } else {
                        startActivity(new Intent(Splash.this, Login.class));
                        finish();
                    }
                } else {
                    startActivity(new Intent(Splash.this, Login.class));
                    finish();
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onCancelled(DatabaseError databaseError) {
                splash_loading_text.setText("Unable To Load App Settings, Please Check Your Network!");
            }
        });
    }
    // endregion

    // region Load App Settings And Update Values According to the Settings Loaded From The Server
    public void syncLocalSettingsWithServerSettings(Map settingsLoadedFromServer) {
        splash_loading_text.setText("Synchronizing Local Settings With Server Settings");

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor localSettings = settings.edit();

        localSettings.putString("ad_link_1", String.valueOf(settingsLoadedFromServer.get("ad_link_1")));
        localSettings.putString("ad_link_2", String.valueOf(settingsLoadedFromServer.get("ad_link_2")));
        localSettings.putString("ad_link_3", String.valueOf(settingsLoadedFromServer.get("ad_link_3")));
        localSettings.putBoolean("color_confirmation_feature", Boolean.parseBoolean(String.valueOf(settingsLoadedFromServer.get("color_confirmation_feature"))));
        localSettings.putString("group_image", String.valueOf(settingsLoadedFromServer.get("group_image")));
        localSettings.putString("group_name", String.valueOf(settingsLoadedFromServer.get("group_name")));
        localSettings.putString("group_theme", String.valueOf(settingsLoadedFromServer.get("group_theme")));
        localSettings.putBoolean("ones_twos_feature", Boolean.parseBoolean(String.valueOf(settingsLoadedFromServer.get("ones_twos_feature"))));

        localSettings.apply();
        startActivity(new Intent(Splash.this, Login.class));
        finish();
    }
    // endregion
}