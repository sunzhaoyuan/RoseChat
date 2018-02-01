package edu.rosehulman.sunz1.rosechat.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_TIME_MS = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Connectiontask().execute();
    }

    private class Connectiontask extends AsyncTask<String, Integer, Long> {

        @Override
        protected Long doInBackground(String... strings) {
            DatabaseConnectionService connectionService = DatabaseConnectionService.getInstance();
            connectionService.connect();
            return null;
        }

        protected void onPostExecute(Long result) {

            Runnable mRunnable;
            Handler mHandler = new Handler();
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    if (FirebaseAuth.getInstance().getCurrentUser() != null) {
                        MainActivity.startActivity(SplashActivity.this);
                    } else {
                        LogInActivity.startActivity(SplashActivity.this);
                    }
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                    finish();
                }
            };
            mHandler.postDelayed(mRunnable, SPLASH_TIME_MS);
        }
    }
}
