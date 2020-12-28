package com.capitipalismcorp.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.classes.SettingsManager;
import com.capitipalismcorp.ui.helpers.ThemeManager;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AdminSettings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Group Preferences");

        }
        setupSharedPreferences();
    }

    // region Set App Theme
    private void setAppTheme() {
        Map<String, Object> appSettings = SettingsManager.getAppSettings();

        String group_theme = String.valueOf(appSettings.get("group_theme"));

        ThemeManager.setApplicationThemeActionBar(this, group_theme);
    }
    // endregion

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    private void setupSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Map<String, Object> toUpdateSetting = new HashMap<>();

        if (key.equals("group_name") || key.equals("group_theme") || key.equals("ad_link_1") || key.equals("ad_link_2") || key.equals("ad_link_3")) {
            toUpdateSetting.put(key, sharedPreferences.getString(key, "NULL"));
        } else if (key.equals("color_confirmation_feature") || key.equals("ones_twos_feature")) {
            toUpdateSetting.put(key, sharedPreferences.getBoolean(key, true));
        }

        String groupID = GroupManager.getGroupID();
        String ownerID = GroupManager.getOwnerID();
        DatabaseReference userDatabase = FirebaseDatabase.getInstance().getReference("GroupUsers").child(ownerID).child(groupID).child(groupID + "-PREFERENCES");
        userDatabase.updateChildren(toUpdateSetting).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(AdminSettings.this, "Setting Updated!", Toast.LENGTH_LONG).show();
//                AlertCreator.showSuccessAlert(AdminSettings.this, "Setting Updated", "Changed Key: " + key);
            } else {
                Toast.makeText(AdminSettings.this, "Unable to Update Setting!", Toast.LENGTH_LONG).show();
//                AlertCreator.showErrorAlert(AdminSettings.this, "Error", Objects.requireNonNull(task.getException()).getMessage());
            }
        });


//        if (key.equals("display_text")) {
//            setTextVisible(sharedPreferences.getBoolean("display_text",true));
//        }
    }
}