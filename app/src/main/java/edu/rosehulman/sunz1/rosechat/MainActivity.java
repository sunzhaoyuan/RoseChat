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
import android.support.v4.view.ViewPager;
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

    private NavigationPagerAdapter mNavigationPagerAdapter;
    private ViewPager mViewPager;
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
                    mViewPager.setCurrentItem(0);

                    //TODO: maybe this loop can be wrapped up as a method? - Sun
                    for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() + 1; i++) {
                        getSupportFragmentManager().popBackStackImmediate();
                    }
                    break;
                case R.id.navigation_contact:
                    Log.d(DEBUG_KEY, "contact pressed");
                    mFragmentMain = new ContactsFragment();
                    mViewPager.setCurrentItem(1);
                    break;
                case R.id.navigation_profile:
                    Log.d(DEBUG_KEY, "profile pressed");
                    mFragmentMain = new ProfileFragment();
                    mViewPager.setCurrentItem(2);
                    break;
            }
            setTitle(id);
            return true;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mNavigationPagerAdapter = new NavigationPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        setupViewPager(mViewPager);

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
                setTitle(id);
                mViewPager.setCurrentItem(3);
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
        mBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        mBuilder.setView(mView);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private boolean containsContact(String email) {
        //TODO: Check if email entered is already in contact or not.

        return false;
    }

    private void setupViewPager(ViewPager viewPager){
        NavigationPagerAdapter adapter = new NavigationPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MessageFragment(), getTitle(R.id.navigation_message));
        adapter.addFragment(new ContactsFragment(), getTitle(R.id.navigation_contact));
        adapter.addFragment(new ProfileFragment(), getTitle(R.id.navigation_profile));
        adapter.addFragment(new MainSettingsFragment(), getTitle(R.id.action_settings));
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber){
        mViewPager.setCurrentItem(fragmentNumber);
    }

    public void setTitle(int id) {
        String title = getTitle(id).toUpperCase();
        Log.d(DEBUG_KEY, title);
        getSupportActionBar().setTitle(title);
    }

    private String getTitle(Integer itemID) {
        return getString(this.mTitlesMap.get(itemID));
    }
}
