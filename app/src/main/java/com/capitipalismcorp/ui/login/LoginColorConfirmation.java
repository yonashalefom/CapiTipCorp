package com.capitipalismcorp.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.capitipalismcorp.R;
import com.capitipalismcorp.classes.CapiUserManager;
import com.capitipalismcorp.classes.GroupManager;
import com.capitipalismcorp.classes.SettingsManager;
import com.capitipalismcorp.ui.dashboard.Home;
import com.capitipalismcorp.ui.helpers.AlertCreator;
import com.capitipalismcorp.ui.helpers.ThemeManager;
import com.capitipalismcorp.ui.profile.Profile;
import com.capitipalismcorp.ui.search.SearchUser;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.wefor.circularanim.CircularAnim;

public class LoginColorConfirmation extends AppCompatActivity {
    @BindView(R.id.login_confirmation_page_main_background)
    ConstraintLayout login_confirmation_page_main_background;
    // region Initializations
    // region Color Inputs
    @BindView(R.id.color_code_input_1)
    ConstraintLayout color_code_input_1;
    @BindView(R.id.color_code_input_2)
    ConstraintLayout color_code_input_2;
    @BindView(R.id.color_code_input_3)
    ConstraintLayout color_code_input_3;
    @BindView(R.id.color_code_input_4)
    ConstraintLayout color_code_input_4;
    @BindView(R.id.color_code_input_5)
    ConstraintLayout color_code_input_5;
    @BindView(R.id.color_code_input_6)
    ConstraintLayout color_code_input_6;
    @BindView(R.id.color_code_input_7)
    ConstraintLayout color_code_input_7;
    @BindView(R.id.color_code_input_8)
    ConstraintLayout color_code_input_8;
    @BindView(R.id.color_code_input_9)
    ConstraintLayout color_code_input_9;
    @BindView(R.id.color_code_input_10)
    ConstraintLayout color_code_input_10;
    @BindView(R.id.color_code_input_clear)
    ConstraintLayout color_code_input_clear;
    @BindView(R.id.color_code_input_erase)
    ConstraintLayout color_code_input_erase;
    // endregion

    // region Color Codes Preview
    @BindView(R.id.color_code_preview_1)
    ConstraintLayout color_code_preview_1;
    @BindView(R.id.color_code_preview_2)
    ConstraintLayout color_code_preview_2;
    @BindView(R.id.color_code_preview_3)
    ConstraintLayout color_code_preview_3;
    @BindView(R.id.color_code_preview_4)
    ConstraintLayout color_code_preview_4;
    @BindView(R.id.color_code_preview_5)
    ConstraintLayout color_code_preview_5;
    // endregion

    @BindView(R.id.login_btn_login)
    Button login_btn_login;
    @BindView(R.id.login_progress_color_confirmation_page)
    SpinKitView login_progress_color_confirmation_page;

    StringBuilder finalColorCode = new StringBuilder();
    private String userID;
    private String userType;
    private int passwordStatus = 0; // 0 = neutral 1 = password_filled -1 = password_not_fully_filled
    final int[] currentColorCode = new int[]{0, 0, 0, 0, 0};
    // endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAppTheme();
        setContentView(R.layout.activity_login_color_confirmation);
        ButterKnife.bind(this);

        initUI();
        userID = getIntent().getStringExtra("userID");
        userType = getIntent().getStringExtra("userType");
        initColorCodeInput();
    }

    // region Set App Theme
    private void setAppTheme() {
        Map<String, Object> appSettings = SettingsManager.getAppSettings();

        String group_theme = String.valueOf(appSettings.get("group_theme"));

        ThemeManager.setApplicationTheme(this, group_theme);
    }
    // endregion

    // region Init UI
    private void initUI() {
//        Map<String, Object> appSettings = SettingsManager.getAppSettings();

//        String mainLoginBackground = String.valueOf(appSettings.get("group_theme"));
//        String mainAppName = String.valueOf(appSettings.get("group_name"));
//        String appLink1 = String.valueOf(appSettings.get("ad_link_1"));
//        String appLink2 = String.valueOf(appSettings.get("ad_link_2"));
//        String appLink3 = String.valueOf(appSettings.get("ad_link_3"));

//        login_confirmation_page_main_background.setBackgroundColor(Color.parseColor(mainLoginBackground));
//        login_lbl_title.setText(mainAppName);
//        login_advert_link_1.setText(appLink1);
//        login_advert_link_2.setText(appLink2);
//        login_advert_link_3.setText(appLink3);


//        CapiUserManager.saveUserData(getApplicationContext(), "Boss", "GroupAdmin");
        // CapiUserManager.saveUserData(getApplicationContext(), "Boss", "Admin");
        // Intent intent = new Intent(getApplicationContext(), Profile.class);
        // intent.putExtra("userID","Boss");

        // CapiUserManager.saveUserData(getApplicationContext(), "SuperAdmin", "Super Admin");
        // Intent intent = new Intent(getApplicationContext(), Profile.class);
        // intent.putExtra("userID", "SuperAdmin");

        // startActivity(intent);

        // ConstraintSet constraintSet = new ConstraintSet();
        // constraintSet.clone(login_form_container);
        // constraintSet.connect(R.id.login_scan_nfc_logo, ConstraintSet.BOTTOM, R.id.login_form_container, ConstraintSet.BOTTOM, 0);
        // // constraintSet.connect(R.id.imageView,ConstraintSet.TOP,R.id.check_answer1,ConstraintSet.TOP,0);
        // constraintSet.applyTo(login_form_container);
    }
    // endregion

    // region Init UI
    private void initColorCodeInput() {
        initColorCodeInputsEventHandler();
        renderColorCode();
    }

    // region Set click handlers for color password inputs
    private void initColorCodeInputsEventHandler() {
        color_code_input_1.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(1);
        });
        color_code_input_2.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(2);
        });
        color_code_input_3.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(3);
        });
        color_code_input_4.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(4);
        });
        color_code_input_5.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(5);
        });
        color_code_input_6.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(6);
        });
        color_code_input_7.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(7);
        });
        color_code_input_8.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(8);
        });
        color_code_input_9.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(9);
        });
        color_code_input_10.setOnClickListener(v -> {
            colorCodeInputOnClickHandler(10);
        });
        // ------------------------------
        color_code_input_clear.setOnClickListener(v -> {
            resetColorCodeInput();
        });
        color_code_input_erase.setOnClickListener(v -> {
            clearLastColorCode();
        });
        login_btn_login.setOnClickListener(v -> {
            if (passwordStatus == 1) {
                finishLogin();
            } else if (passwordStatus == 0) {
                AlertCreator.showErrorAlert(LoginColorConfirmation.this, "Enter Credentials", "Please enter your color password in order to login.");
            }
        });
    }
    // endregion
    // endregion

    // region Finish Login
    private void finishLogin() {
        if (finalColorCode.length() == 5) {
            login_progress_color_confirmation_page.setVisibility(View.VISIBLE);
            login_btn_login.setClickable(false);
//            login_btn_login.setBackground(getResources().getDrawable(R.drawable.hyphen_button_inactive));

            DatabaseReference databaseReference;
            databaseReference = FirebaseDatabase.getInstance().getReference("GroupUsers");

            String groupID = GroupManager.getGroupID();
            String ownerID = GroupManager.getOwnerID();

            databaseReference.child(ownerID).child(groupID).child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String uID = dataSnapshot.getKey();
                    String userColorPassword = dataSnapshot.child("colorPassword").getValue(String.class);
                    String userType = dataSnapshot.child("userType").getValue(String.class);

                    if (Objects.requireNonNull(userColorPassword).compareTo(String.valueOf(finalColorCode)) == 0) {
                        AlertCreator.showSuccessAlert(LoginColorConfirmation.this, "Login Success!", "Color Password Correct.");
                        login_progress_color_confirmation_page.setVisibility(View.GONE);

                        if (userType.equals("GroupDefaultUser")) {
                            databaseReference.child(ownerID).child(groupID).child(uID).child("logged_in").setValue("true").addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    CapiUserManager.saveUserData(getApplicationContext(), uID, userType);
                                    CapiUserManager.loadUserData(getApplicationContext());
                                    new Handler().postDelayed(() -> CircularAnim.fullActivity(LoginColorConfirmation.this, login_btn_login)
                                            .colorOrImageRes(R.color.capitipalism_success)
                                            .go(() -> {
                                                startActivity(new Intent(LoginColorConfirmation.this, SearchUser.class));
                                                finish();
                                            }), 0);
                                } else {
                                    AlertCreator.showErrorAlert(LoginColorConfirmation.this, "Unable To Login!", "We were not able to log you in. Please make sure you've got a valid NFC tag.");
                                }
                            });
                        } else if (userType.equals("GroupAdmin")) {
                            databaseReference.child(ownerID).child(groupID).child(uID).child("logged_in").setValue("true").addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    CapiUserManager.saveUserData(getApplicationContext(), uID, userType);
                                    CapiUserManager.loadUserData(getApplicationContext());
                                    new Handler().postDelayed(() -> CircularAnim.fullActivity(LoginColorConfirmation.this, login_btn_login)
                                            .colorOrImageRes(R.color.capitipalism_success)
                                            .go(() -> {
                                                startActivity(new Intent(LoginColorConfirmation.this, Profile.class).putExtra("userID", CapiUserManager.getCurrentUserID()));
                                                finish();
                                            }), 0);
                                } else {
                                    AlertCreator.showErrorAlert(LoginColorConfirmation.this, "Unable To Login!", "We were not able to log you in. Please make sure you've got a valid NFC tag.");
                                }
                            });
                        } else if (userType.equals("SupportRep")) {
                            databaseReference.child(ownerID).child(groupID).child(uID).child("logged_in").setValue("true").addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    CapiUserManager.saveUserData(getApplicationContext(), uID, userType);
                                    CapiUserManager.loadUserData(getApplicationContext());
                                    new Handler().postDelayed(() -> CircularAnim.fullActivity(LoginColorConfirmation.this, login_btn_login)
                                            .colorOrImageRes(R.color.capitipalism_success)
                                            .go(() -> {
                                                startActivity(new Intent(LoginColorConfirmation.this, SearchUser.class).putExtra("userID", CapiUserManager.getCurrentUserID()));
                                                finish();
                                            }), 0);
                                } else {
                                    AlertCreator.showErrorAlert(LoginColorConfirmation.this, "Unable To Login!", "We were not able to log you in. Please make sure you've got a valid NFC tag.");
                                }
                            });
                        } else if (userType.equals("Super Admin")) {

                        }
                    } else if (Objects.requireNonNull(userColorPassword).compareToIgnoreCase("Default") == 0) {
                        login_progress_color_confirmation_page.setVisibility(View.GONE);
                        login_btn_login.setClickable(true);
                        // login_btn_login.setBackground(getResources().getDrawable(R.drawable.hyphen_button_default));
                        AlertCreator.showErrorAlert(LoginColorConfirmation.this, "Color Password Not Set!", "Your don't have a color password yet. Contact your admin to set your color code.");
                    } else {
                        login_progress_color_confirmation_page.setVisibility(View.GONE);
                        login_btn_login.setClickable(true);
                        // login_btn_login.setBackground(getResources().getDrawable(R.drawable.hyphen_button_default));
                        AlertCreator.showErrorAlert(LoginColorConfirmation.this, "Invalid Credentials!", "Your credentials are not correct. Make sure you are entering a valid color code.");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    login_progress_color_confirmation_page.setVisibility(View.GONE);
                    login_btn_login.setClickable(true);
                    // login_btn_login.setBackground(getResources().getDrawable(R.drawable.hyphen_button_default));
                    System.out.println("Database Error: Something is wrong!");
                }
            });
        }
    }
    // endregion

    // region Color Code Related
    private void resetColorCodeInput() {
        Arrays.fill(currentColorCode, 0);
        renderColorCode();
    }

    private void clearLastColorCode() {
        for (int i = currentColorCode.length - 1; i > -1; i--) {
            if (currentColorCode[i] > 0) {
                currentColorCode[i] = 0;
                break;
            }
        }
        renderColorCode();
    }

    private void renderColorCode() {
        finalColorCode.setLength(0);
        int counter = 1;
        int filledPassword = 0;
        for (int a : currentColorCode) {
            if (a != 0) {
                filledPassword++;
            }
            if (counter == 1) {
                color_code_preview_1.setBackgroundResource(getColorCodeDrawableViaColorCode(a));
            } else if (counter == 2) {
                color_code_preview_2.setBackgroundResource(getColorCodeDrawableViaColorCode(a));
            } else if (counter == 3) {
                color_code_preview_3.setBackgroundResource(getColorCodeDrawableViaColorCode(a));
            } else if (counter == 4) {
                color_code_preview_4.setBackgroundResource(getColorCodeDrawableViaColorCode(a));
            } else if (counter == 5) {
                color_code_preview_5.setBackgroundResource(getColorCodeDrawableViaColorCode(a));
            }
            finalColorCode.append(a);
            counter++;
        }

        if (filledPassword == 0) {
            passwordStatus = 0;
            login_btn_login.setClickable(true);
            // login_btn_login.setBackground(getResources().getDrawable(R.drawable.hyphen_button_default));
        } else if (filledPassword > 0 && filledPassword < 5) {
            passwordStatus = -1;
            login_btn_login.setClickable(false);
//            login_btn_login.setBackground(getResources().getDrawable(R.drawable.hyphen_button_inactive));
        } else if (filledPassword == 5) {
            passwordStatus = 1;
            login_btn_login.setClickable(true);
            // login_btn_login.setBackground(getResources().getDrawable(R.drawable.hyphen_button_default));
        }

        System.out.println("*************************************");
        System.out.println("Color Code: " + finalColorCode);
        System.out.println("Password Status: " + passwordStatus);
        System.out.println("Filled Password: " + filledPassword);
        System.out.println("*************************************");
    }

    private int getColorCodeDrawableViaColorCode(int colorCode) {
        if (colorCode == 1) {
            return R.drawable.color_password_button_1;
        } else if (colorCode == 2) {
            return R.drawable.color_password_button_2;
        } else if (colorCode == 3) {
            return R.drawable.color_password_button_3;
        } else if (colorCode == 4) {
            return R.drawable.color_password_button_4;
        } else if (colorCode == 5) {
            return R.drawable.color_password_button_5;
        } else if (colorCode == 6) {
            return R.drawable.color_password_button_6;
        } else if (colorCode == 7) {
            return R.drawable.color_password_button_7;
        } else if (colorCode == 8) {
            return R.drawable.color_password_button_8;
        } else if (colorCode == 9) {
            return R.drawable.color_password_button_9;
        } else if (colorCode == 10) {
            return R.drawable.color_password_button_10;
        } else {
            return R.drawable.color_password_placeholder;
        }
    }

    private void colorCodeInputOnClickHandler(int colorCode) {
        int counter = 0;
        for (int a : currentColorCode) {
            if (a == 0) {
                currentColorCode[counter] = colorCode;
                break;
            }
            counter++;
        }
        renderColorCode();
    }
    // endregion
}