package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Connection;

import edu.rosehulman.rosefire.Rosefire;
import edu.rosehulman.rosefire.RosefireResult;
import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.fragments.LoginFragment;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * This class provides support for LogIn
 *
 */
public class LogInActivity extends AppCompatActivity implements LoginFragment.OnLoginListener{
    private static final String TAG = "Login";
    private static final int RC_ROSEFIRE_LOGIN = 1;

    // For RoseFire Login
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private OnCompleteListener mOnCompleteListener;

    /*
     * This chunk is for SQL connection
     */
    private DatabaseConnectionService dbConSer;
    private Connection mDBConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initializeFragment();

        mAuth = FirebaseAuth.getInstance();

        dbConSer = DatabaseConnectionService.getInstance();
        dbConSer.connect();
        mDBConnection = dbConSer.getConnection();

//        dbConnectionService.connect(); //connect to sql db

        initializeListeners();

//        dbConnectionService = DatabaseConnectionService.getInstance();
//        this.mDBConnection = dbConnectionService.getConnection();

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    private void initializeFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        LoginFragment fragment = new LoginFragment();
        ft.replace(R.id.frame_layout_login_container, fragment.newInstance(), fragment.getTag());
        ft.commit();
    }

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, LogInActivity.class);
        context.startActivity(intent);
    }

    // For RoseFire Login
    @Override
    public void onRosefireLogin() {
        Log.d(TAG, "RoseFire logging in");
        Intent signInIntent = Rosefire.getSignInIntent(this, getString(R.string.rosefire_key));
        startActivityForResult(signInIntent, RC_ROSEFIRE_LOGIN);
    }

    /**
     * After login successfully, we store the current user into SharedPreferencesUtils
     * and switch the activity to MainActivity
     */
    private void initializeListeners() {
        Log.d(TAG, "initialize listeners");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Log.d(TAG, "User: " + user);
                if (user != null) { //log in successfully
                    SharedPreferencesUtils.setCurrentUser(getApplicationContext(), user.getUid());
                    switchToMainActivity();
                } else {
//                    switchToLoginActivity();
                    // keep it in here
                }
            }
        };
        mOnCompleteListener = new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (!task.isSuccessful()) {
                    showLoginError(getString(R.string.login_failed));
                }
            }
        };
    }

    private void switchToLoginActivity() {
        Log.d(TAG, "try to switch Login Activity");
//        LogInActivity.startActivity(MainActivity.this);
    }

    private void switchToMainActivity() {
        MainActivity.startActivity(LogInActivity.this);
    }

    private void showLoginError(String message) {
        LoginFragment loginFragment = (LoginFragment) getSupportFragmentManager().findFragmentByTag("Login");
        loginFragment.onLoginError(message);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_ROSEFIRE_LOGIN) {
            RosefireResult result = Rosefire.getSignInResultFromIntent(data);
            if (result.isSuccessful()) {
//                Log.d(TAG, "Log in successful" + "\nuser is " + mAuth.getCurrentUser().getUid());
                mAuth.signInWithCustomToken(result.getToken())
                        .addOnCompleteListener(this, mOnCompleteListener);
            } else {
                showLoginError(getString(R.string.rosefire_login_failed));
            }
        } else {
            showLoginError(getString(R.string.request_code_error));
        }
    }
}
