package edu.rosehulman.sunz1.rosechat;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.MessageFragment;
import edu.rosehulman.sunz1.rosechat.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    final private String DEBUG_KEY = "Debug";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_message:
                    Log.d(DEBUG_KEY, "message pressed");
                    fragment = new MessageFragment();
                    break;
                case R.id.navigation_contact:
                    Log.d(DEBUG_KEY, "contact pressed");
                    fragment = new ContactsFragment();
                    break;
                case R.id.navigation_profile:
                    Log.d(DEBUG_KEY, "profile pressed");
                    fragment = new ProfileFragment();
                    break;
                default:
                    fragment = new MessageFragment();
                    break;
            }
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_main, fragment);
            ft.commit();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
