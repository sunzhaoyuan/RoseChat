package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import edu.rosehulman.sunz1.rosechat.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_MS = 1500;

    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                    MainActivity.startActivity(SplashActivity.this);
                } else {
                    LogInActivity.startActivity(SplashActivity.this);
                }
                finish();
            }
        };
        mHandler.postAtTime(mRunnable, SPLASH_TIME_MS);
    }
}
