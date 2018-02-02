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

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.fragments.FeedbackSettingsFragment;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener{

    //TODO: sync with DB server

    public static boolean NOTIFICATIONS = true;

    private Button mButtonLanguage;
    private Button mButtonLogOut;
    private Button mButtonFeedback;
    private Button mButtonDeleteAccount;
    private Button mButtonFont;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
//    private OnLogoutListener mLogoutListener;

    DatabaseConnectionService service;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mButtonDeleteAccount = (Button) findViewById(R.id.button_settings_deleteAccount);
        mButtonFeedback= (Button) findViewById(R.id.button_settings_feedback);
        mButtonLanguage = (Button) findViewById(R.id.button_settings_Language);
        mButtonLogOut= (Button) findViewById(R.id.button_settings_logOut);
        mButtonFont = (Button) findViewById(R.id.button_settings_Font);

        mButtonDeleteAccount.setOnClickListener(this);
        mButtonFeedback.setOnClickListener(this);
        mButtonLanguage.setOnClickListener(this);
        mButtonLogOut.setOnClickListener(this);
        mButtonFont.setOnClickListener(this);

        service = DatabaseConnectionService.getInstance();
        Connection connection = service.getConnection();

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
                Log.d("setting", "logout clicked");
                logOutConfirmationDialog();
                return;
            case R.id.button_settings_deleteAccount:
                deleteAccountConfirmationDialog();
                return;
            case R.id.button_settings_feedback:
                FragmentTransaction feedbackTransaction = getSupportFragmentManager().beginTransaction();
                FeedbackSettingsFragment fragment = new FeedbackSettingsFragment();
                if(this.getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    feedbackTransaction.addToBackStack("feedback");
                    feedbackTransaction.add(R.id.settings_container, fragment);
                    feedbackTransaction.commit();
                }else{
                    getSupportFragmentManager().popBackStack("feedback", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    feedbackTransaction.commit();
                }
                return;
            case R.id.button_settings_Font:
                Log.d("setting", "fontSize clicked");
                showFontDialog();
                return;
        }

    }

    private void showFontDialog() {
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Log.d("setting", "fontSize in dialog");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                View view = getActivity().getLayoutInflater().inflate(R.layout.dialogfragment_font, null);
//                builder.setView(view);

//                builder.setTitle("Pick a font size factor")
//                        .setItems()

//                final EditText fontSizeEditText = (EditText) view.findViewById(R.id.dialogfragment_fontsize_editext);
//                final EditText fontFamilyEditText = (EditText) view.findViewById(R.id.dialogfragment_fontfamily_editext);

                builder.setNegativeButton(android.R.string.cancel, null);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: be able to edit font family and font size
                    }
                });
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
        //TODO: how do we log out in this case? -by using firbaseAuth.signOut
    }


    private void deleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();
        SharedPreferencesUtils.removeCurrentUser(getApplicationContext());
        assert user != null;
        user.delete();
        mAuth.signOut(); //TODO: need improved
        new DeleteAccountTask().execute();
    }

    private class DeleteAccountTask extends AsyncTask<String, String, String>{

        protected  String doInBackground(String... str) {
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

    //TODO: does it do its job?
    private class SyncSettingTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... str) {
            String UID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
            try {
                String uid = "";

                //we need to create current user
                CallableStatement cs = null;
                String defaultNickName = UID;
                String defaultEmail = UID + "@rose-hulman.edu";
                String defaultPhone = getString(R.string.profile_sample_phone_number);
                String defaultAvatarURL = "https://firebasestorage.googleapis.com/v0/b/rosechat-64ae9.appspot.com/o/profile_pics%2Fdefault.png?alt=media&token=2cc54fe8-da2f-49f9-ab18-0ef0d2e8fea6";

//                cs = MainActivity.this.mDBConnection.prepareCall("{?=call CreateUser(?, ?, ?, ?, ?)}");
                cs.setString(2, UID);
                cs.setString(3, defaultNickName);
                cs.setString(4, defaultPhone);
                cs.setString(5, defaultEmail);
                cs.setString(6, defaultAvatarURL);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute();
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

    private void switchLanguage(){
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);
    }
}
