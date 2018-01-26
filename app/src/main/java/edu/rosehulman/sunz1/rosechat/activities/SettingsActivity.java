package edu.rosehulman.sunz1.rosechat.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.fragments.FeedbackSettingsFragment;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonLanguage;
    private Button mButtonLogOut;
    private Button mButtonFeedback;
    private Button mButtonDeleteAccount;
    public static boolean NOTIFICATIONS = true;
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

        mButtonDeleteAccount.setOnClickListener(this);
        mButtonFeedback.setOnClickListener(this);
        mButtonLanguage.setOnClickListener(this);
        mButtonLogOut.setOnClickListener(this);

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
            case R.id.button_settings_feedback:
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                FeedbackSettingsFragment fragment = new FeedbackSettingsFragment();
                if(this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    transaction.addToBackStack("feedback");
                    transaction.add(R.id.settings_container, fragment);
                    transaction.commit();
                }else{
                    getSupportFragmentManager().popBackStack("feedback", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    transaction.commit();
                }
                return;
        }

    }
//
//    private void notificationDialog() {
//        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
//        mBuilder.setTitle("Notifications");
//        mBuilder.setMessage("Would you like to turn notifications on or off?");
//        mBuilder.setPositiveButton("On", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {NOTIFICATIONS =true;}
//        });
//
//        mBuilder.setNegativeButton("Off", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {NOTIFICATIONS = false;}
//        });
//        AlertDialog dialog = mBuilder.create();
//        dialog.show();
//    }

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
        SharedPreferencesUtils.removeCurrentUser(getApplicationContext());
        mAuth.signOut();
    }


    private void deleteAccount() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        SharedPreferencesUtils.removeCurrentUser(getApplicationContext());
//        assert user != null;
//        user.delete();
        mAuth.signOut(); //TODO: need improved
        new DeleteAccountTask().execute();
    }

    private class DeleteAccountTask extends AsyncTask<String, String, String>{

        protected  String doInBackground(String... str) {
            DatabaseConnectionService service = DatabaseConnectionService.getInstance();
            Connection connection = service.getConnection(); // should not be null
            String query = "delete from [User] where UID = ?";
            try {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setString(1, SharedPreferencesUtils.getCurrentUser(getApplicationContext()));
                stmt.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
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
