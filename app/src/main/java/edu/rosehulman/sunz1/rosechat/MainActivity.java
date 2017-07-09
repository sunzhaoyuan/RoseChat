package edu.rosehulman.sunz1.rosechat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_message:
                    mTextMessage.setText(R.string.navi_message);
                    return true;
                case R.id.navigation_contact:
                    mTextMessage.setText(R.string.navi_contact);
                    return true;
                case R.id.navigation_profile:
                    mTextMessage.setText(R.string.navi_profile);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    /**
     * //TODO: need to add icons for each menu options - Zhaoyuan
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_chat:
                // TODO: pop up a new activity probably (no bottom navi bar)
                return true;
            case R.id.action_add_contact:
                addContact();
                return true;
            case R.id.aciton_settings:
                // TODO: settings screen
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addContact() {
    }
}
