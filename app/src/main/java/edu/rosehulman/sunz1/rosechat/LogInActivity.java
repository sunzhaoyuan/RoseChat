package edu.rosehulman.sunz1.rosechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class LogInActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonLogIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        mButtonLogIn = (Button) findViewById(R.id.button_login);
        mButtonLogIn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == mButtonLogIn){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish(); // once logged in, never go back
        }
    }
}
