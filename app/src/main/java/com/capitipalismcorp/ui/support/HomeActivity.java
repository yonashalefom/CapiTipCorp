package com.capitipalismcorp.ui.support;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;


import com.capitipalismcorp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support); // activity_home (ChatApp1)

        initUI();
    }

    private void initUI() {
        // region Action Bar Related Initialization
        Toolbar toolbar = findViewById(R.id.main_app_bar);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Support Center");
        // endregion
        // region View Pager Initialization
        // region Fragments Handler Using SmartTabLayout
        FragmentPagerItemAdapter allFragmentsContainer = new FragmentPagerItemAdapter(
                getSupportFragmentManager(), FragmentPagerItems.with(this)
                // .add("Requests", RequestsFragment.class)
                .add("Chat", ChatFragment.class)
                // .add("Friends", FriendsFragment.class)
                .create());
        // endregion

        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(allFragmentsContainer);
        viewPager.setCurrentItem(1);

        SmartTabLayout viewPagerTab = findViewById(R.id.viewpagertab);
        viewPagerTab.setViewPager(viewPager);
        // endregion
    }

    @Override
    public void onStart() {
        super.onStart();

//        // region Check User's Session
//        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
//            Intent welcomeIntent = new Intent(HomeActivity.this, LoginActivity.class);
//            startActivity(welcomeIntent);
//            finish();
//        }
//        // endregion
    }

    @Override
    protected void onResume() {
        super.onResume();

        // region Set current user online-state to online
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (currentUser != null) {
//            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue("true");
//        }
        // endregion
    }

    @Override
    protected void onPause() {
        super.onPause();

        // region Set current user online-state to offline
//        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (currentUser != null) {
//            FirebaseDatabase.getInstance().getReference().child("Users").child(currentUser.getUid()).child("online").setValue(ServerValue.TIMESTAMP);
//        }
        // endregion
    }
//
//    @Override
//    public void onBackPressed() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(com.github.h01d.chatapp.activities.HomeActivity.this);
//        builder.setTitle("Exit");
//        builder.setMessage("Are you sure you want to close the application?");
//        builder.setPositiveButton("YES", (dialog, id) -> {
//            finish();
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_HOME);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//        });
//        builder.setNegativeButton("NO", (dialog, id) -> dialog.dismiss());
//        AlertDialog dialog = builder.create();
//        dialog.show();
//    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        super.onCreateOptionsMenu(menu);
//
//        getMenuInflater().inflate(R.menu.main_menu, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        super.onOptionsItemSelected(item);

//        switch (item.getItemId()) {
//            case R.id.menuLogout:
//                AlertDialog.Builder logoutBuilder = new AlertDialog.Builder(com.github.h01d.chatapp.activities.HomeActivity.this);
//                logoutBuilder.setTitle("Logout");
//                logoutBuilder.setMessage("Are you sure you want to logout?");
//                logoutBuilder.setPositiveButton("YES", (dialog, id) -> {
//                    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("online").setValue(ServerValue.TIMESTAMP);
//
//                    FirebaseAuth.getInstance().signOut();
//
//                    Intent welcomeIntent = new Intent(com.github.h01d.chatapp.activities.HomeActivity.this, LoginActivity.class);
//                    startActivity(welcomeIntent);
//                    finish();
//                });
//                logoutBuilder.setNegativeButton("NO", (dialog, id) -> dialog.dismiss());
//                AlertDialog logoutDialog = logoutBuilder.create();
//                logoutDialog.show();
//                return true;
//            case R.id.menuAbout:
//                AlertDialog.Builder aboutBuilder = new AlertDialog.Builder(com.github.h01d.chatapp.activities.HomeActivity.this);
//                aboutBuilder.setTitle("Hyphen v1.0");
//                aboutBuilder.setMessage("This project is developed by AfroMina Dev Group.");
//                aboutBuilder.setNegativeButton("Close", (dialog, id) -> dialog.dismiss());
//                AlertDialog aboutDialog = aboutBuilder.create();
//                aboutDialog.show();
//                return true;
//            case R.id.menuProfile:
//                Intent profileIntent = new Intent(com.github.h01d.chatapp.activities.HomeActivity.this, ProfileActivity.class);
//                profileIntent.putExtra("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
//                startActivity(profileIntent);
//                return true;
//            case R.id.menuSearch:
//                Intent usersIntent = new Intent(com.github.h01d.chatapp.activities.HomeActivity.this, UsersActivity.class);
//                startActivity(usersIntent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
}