package edu.rosehulman.sunz1.rosechat;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.MainSettingsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.MessageFragment;
import edu.rosehulman.sunz1.rosechat.fragments.ProfileFragment;

public class MainActivity extends AppCompatActivity {

    final private String DEBUG_KEY = "Debug";
    private Fragment mFragmentMain;
    private HashMap<Integer, Integer> mTitlesMap = new HashMap<Integer, Integer>() {{
        put(R.id.navigation_message, R.string.navi_message);
        put(R.id.navigation_contact, R.string.navi_contact);
        put(R.id.navigation_profile, R.string.navi_profile);
        put(R.id.action_settings, R.string.action_settings);
    }};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();
            switch (id) {
                case R.id.navigation_message:
                    Log.d(DEBUG_KEY, "message pressed");
                    mFragmentMain = new MessageFragment();
                    for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() + 1; i++) {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                    break;
                case R.id.navigation_contact:
                    Log.d(DEBUG_KEY, "contact pressed");
                    mFragmentMain = new ContactsFragment();
                    break;
                case R.id.navigation_profile:
                    Log.d(DEBUG_KEY, "profile pressed");
                    mFragmentMain = new ProfileFragment();
                    break;
//                default:
//                    mFragmentMain = new MessageFragment();
//                    break;
            }
            setTitle(id);
            drawFragmentMain();
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mFragmentMain = new MessageFragment();
        setTitle(R.id.navigation_message);

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
        int id = item.getItemId();
        switch (id) {
            case R.id.action_new_chat:
                // TODO: pop up a new activity probably (no bottom navi bar)
                return true;
            case R.id.action_add_contact:
                addContact();
                return true;
            case R.id.action_settings:
                // TODO: settings screen
                mFragmentMain = new MainSettingsFragment();
                drawFragmentMain();
                setTitle(id);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addContact() {
        final AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_add_contact, null);
        final EditText mEmail = (EditText) mView.findViewById(R.id.add_contact_email);
        final EditText mMessage = (EditText) mView.findViewById(R.id.add_contact_message);
        Button mAddContactButton = (Button) mView.findViewById(R.id.button_add_contact);
        Button mCancel = (Button) mView.findViewById(R.id.button_add_contact_cancel);
        mAddContactButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEmail.getText().toString().isEmpty() && !mMessage.getText().toString().isEmpty()) {
                    if (containsContact(mEmail.getText().toString())) {
                        Toast.makeText(MainActivity.this, R.string.error_add_contact_existing_contact, Toast.LENGTH_SHORT).show();
                    }//TODO: May want to put a more thorough checks e.g. does the email ID exist?
                    else {
                        Toast.makeText(MainActivity.this, R.string.successful_add_contact, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_add_contact_empty_field, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //TODO: Finish cancel button
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Tried finish(); but that quits the whole application.
            }
        });
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private boolean containsContact(String email) {
        //TODO: Check if email entered is already in contact or not.

        return false;
    }

    private void drawFragmentMain() {
        if (mFragmentMain != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.addToBackStack("detail");
            ft.replace(R.id.fragment_main, mFragmentMain);
            ft.commit();
        }
    }

    public void setTitle(int id) {
        String title = getString(getTitle(id)).toUpperCase();
        Log.d(DEBUG_KEY, title);
        getSupportActionBar().setTitle(title);
    }

    private Integer getTitle(Integer itemID) {
        return this.mTitlesMap.get(itemID);
    }
}
