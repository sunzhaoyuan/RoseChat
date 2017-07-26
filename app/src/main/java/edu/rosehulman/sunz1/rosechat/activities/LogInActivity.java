package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.fragments.LoginFragment;

public class LogInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initializeFragment();

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
}
