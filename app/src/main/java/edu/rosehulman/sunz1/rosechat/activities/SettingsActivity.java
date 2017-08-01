package edu.rosehulman.sunz1.rosechat.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.rosehulman.sunz1.rosechat.activities.MainActivity;
import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.fragments.FeedbackSettingsFragment;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonProfile;
    private Button mButtonLanguage;
    private Button mButtonNotification;
    private Button mButtonLogOut;
    private Button mButtonFeedback;
    private Button mButtonDeleteAccount;
    public static boolean NOTIFICATIONS = true;
    private boolean feedbackPressed = false;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    private OnLogoutListener mLogoutListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mButtonDeleteAccount = (Button) findViewById(R.id.button_settings_deleteAccount);
        mButtonFeedback= (Button) findViewById(R.id.button_settings_feedback);
        mButtonLanguage = (Button) findViewById(R.id.button_settings_Language);
        mButtonLogOut= (Button) findViewById(R.id.button_settings_logOut);
        mButtonNotification= (Button) findViewById(R.id.button_settings_notification);

        mButtonDeleteAccount.setOnClickListener(this);
        mButtonFeedback.setOnClickListener(this);
        mButtonLanguage.setOnClickListener(this);
        mButtonLogOut.setOnClickListener(this);
        mButtonNotification.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        initializeListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    private void initializeListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // empty
                } else {
                    LogInActivity.startActivity(SettingsActivity.this);
                }
            }
        };
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.button_settings_Language:
                switchLanguage();
                return;
            case R.id.button_settings_logOut:
                logOutConfirmationDialog();
                return;
            case R.id.button_settings_deleteAccount:
                deleteAccountConfirmationDialog();
                return;
            case R.id.button_settings_notification:
                notificationDialog();
                
                return;
            case R.id.button_settings_feedback:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                FeedbackSettingsFragment fragment = new FeedbackSettingsFragment();
                if(!feedbackPressed) {
                    transaction.addToBackStack("settings");
                    transaction.add(R.id.settings_container, fragment);
                    transaction.commit();
                    feedbackPressed = true;
                }else{
                    getSupportFragmentManager().popBackStack("settings", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commit();
                    feedbackPressed = false;
                }
                return;
        }

    }

    private void notificationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Notifications");
        mBuilder.setMessage("Would you like to turn notifications on or off?");
        mBuilder.setPositiveButton("On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {NOTIFICATIONS =true;}
        });

        mBuilder.setNegativeButton("Off", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {NOTIFICATIONS = false;}
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void logOutConfirmationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.logout_title);
        mBuilder.setMessage(R.string.logout_message);
        mBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logOut();
            }
        });

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void logOut() {
        mAuth.signOut();
    }


    private void deleteAccount() {
        //TODO:
    }

    private void deleteAccountConfirmationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle(R.string.delete_account_title);
        mBuilder.setMessage(R.string.delete_account_message);
        mBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void switchLanguage(){
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);
    }
}
