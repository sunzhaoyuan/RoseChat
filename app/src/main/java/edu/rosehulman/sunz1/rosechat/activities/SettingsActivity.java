package edu.rosehulman.sunz1.rosechat.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.fragments.FeedbackSettingsFragment;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    //TODO: sync with DB server

    public static boolean NOTIFICATIONS = true;

    private Button mButtonLanguage;
    private Button mButtonLogOut;
    private Button mButtonFeedback;
    private Button mButtonDeleteAccount;
    private Button mButtonFontSize;
    private Button mButtonFontFamily;
    private View mProcessBar;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    private OnLogoutListener mLogoutListener;

    private DatabaseConnectionService mConService;
    private Connection mConnection;

    private ArrayList<String> mSettingsArray; //store all settings here

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mButtonDeleteAccount = (Button) findViewById(R.id.button_settings_deleteAccount);
        mButtonFeedback = (Button) findViewById(R.id.button_settings_feedback);
        mButtonLanguage = (Button) findViewById(R.id.button_settings_Language);
        mButtonLogOut = (Button) findViewById(R.id.button_settings_logOut);
        mButtonFontSize = (Button) findViewById(R.id.button_settings_fontsize);
        mButtonFontFamily = (Button) findViewById(R.id.button_settings_fontfamily);


        mButtonDeleteAccount.setOnClickListener(this);
        mButtonFeedback.setOnClickListener(this);
        mButtonLanguage.setOnClickListener(this);
        mButtonLogOut.setOnClickListener(this);
        mButtonFontSize.setOnClickListener(this);
        mButtonFontFamily.setOnClickListener(this);

        mConService = DatabaseConnectionService.getInstance();
        mConnection = mConService.getConnection();

        mSettingsArray = new ArrayList<>();

        mAuth = FirebaseAuth.getInstance();
        initializeListener();

        mProcessBar = findViewById(R.id.processbar_setting);
        mProcessBar.setVisibility(View.GONE);
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
        switch (id) {
            case R.id.button_settings_Language:
                switchLanguage();
                return;
            case R.id.button_settings_logOut:
                Log.d("setting", "logout clicked");
                logOutConfirmationDialog();
                return;
            case R.id.button_settings_deleteAccount:
                deleteAccountConfirmationDialog();
                return;
            case R.id.button_settings_feedback:
                FragmentTransaction feedbackTransaction = getSupportFragmentManager().beginTransaction();
                FeedbackSettingsFragment fragment = new FeedbackSettingsFragment();
                if (this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    feedbackTransaction.addToBackStack("feedback");
                    feedbackTransaction.add(R.id.settings_container, fragment);
                    feedbackTransaction.commit();
                } else {
                    getSupportFragmentManager().popBackStack("feedback", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    feedbackTransaction.commit();
                }
                return;
            case R.id.button_settings_fontsize:
                showFontSizeDialog();
                return;
            case R.id.button_settings_fontfamily:
                showFontFamilyDialog();
                return;
        }

    }

    private void showFontFamilyDialog() {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Log.d("setting", "fontSize in dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                //BE CAREFUL: if R.array.fontfamily_array is changed, this variable needs to be changed as well
                final String[] fontfamilyArray = {"Arial", "Times New Roma"};

                builder.setTitle("Pick a font family")
                        .setItems(R.array.fontfamily_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO:
                                String sizeFactor = fontfamilyArray[which];
//                                mProcessBar.setVisibility(View.VISIBLE);
//
//                                mProcessBar.setVisibility(View.GONE);
                            }
                        });

                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "fontSize");
    }

    private void showFontSizeDialog() {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Log.d("setting", "fontSize in dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

                builder.setTitle("Pick a font size factor")
                        .setItems(R.array.fontsize_array, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //TODO:
                            }
                        });

                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "fontSize");
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
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferencesUtils.removeCurrentUser(getApplicationContext());
        assert user != null;
        user.delete();
        mAuth.signOut(); //TODO: need improved
        new DeleteAccountTask().execute();
    }

    private class DeleteAccountTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... str) {
            String query = "delete from [User] where UID = ?";
            try {
                PreparedStatement stmt = mConnection.prepareStatement(query);
                stmt.setString(1, SharedPreferencesUtils.getCurrentUser(getApplicationContext()));
                stmt.executeQuery();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //TODO: does it do its job?
    private class SyncSettingTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... str) {
            String UID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
            try {

                //we need to create current user
                CallableStatement cs = null;

                cs = mConnection.prepareCall("{?=call SyncDisplaySettings(?, ?, ?, ?, ?)}");
                cs.setString(2, UID); //@UID varchar(50)
                cs.setInt(3, Integer.parseInt(mSettingsArray.get(2))); //@FontSize int
                cs.setString(4, mSettingsArray.get(3)); //@FontFamily nvarchar(20)
                cs.setString(5, mSettingsArray.get(4)); //@FontLanguage nvarchar(10)
                cs.setInt(6, Integer.parseInt(mSettingsArray.get(5))); //@Notification bit
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute(); //add these data into db
                int out = cs.getInt(1);

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

    private void switchLanguage() {
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);
    }
}
