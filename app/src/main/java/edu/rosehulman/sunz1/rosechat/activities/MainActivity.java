package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.HashMap;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.NavigationPagerAdapter;
import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.MessageFragment;
import edu.rosehulman.sunz1.rosechat.fragments.ProfileFragment;
import edu.rosehulman.sunz1.rosechat.models.Contact;

//import edu.rosehulman.sunz1.rosechat.activities.NewChatActivity;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
//        MessageFragment.Callback,
        ContactsFragment.Callback,
        NewChatActivity.Callback {

    final private String DEBUG_KEY = "Debug";

    private NavigationPagerAdapter mNavigationPagerAdapter;
    private ViewPager mViewPager;
    private Fragment mFragmentMain;
    private BottomNavigationViewEx mNavigation;
    private BottomNavigationView mBottomNavigationView;

    private HashMap<Integer, Integer> mTitlesMap = new HashMap<Integer, Integer>() {{
        put(R.id.navigation_message, R.string.navi_message);
        put(R.id.navigation_contact, R.string.navi_contact);
        put(R.id.navigation_profile, R.string.navi_profile);
        put(R.id.action_settings, R.string.action_settings);
        put(R.id.edit_profile, R.string.profile_edit_name);
    }};

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNISListener;
    private BottomNavigationViewEx.OnNavigationItemSelectedListener mOnNISExListener;

    public void clearBackStack() {
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount() + 1; i++) {
            getSupportFragmentManager().popBackStackImmediate();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mNavigation = (BottomNavigationViewEx) findViewById(R.id.bnve);
        mOnNISExListener = new BottomNavigationViewEx.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id) {
                    case R.id.navigation_message:
                        Log.d(DEBUG_KEY, "message pressed");
                        mFragmentMain = new MessageFragment();
                        mViewPager.setCurrentItem(0);
                        clearBackStack();
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
//        mBottomNavigationView.setOnNavigationItemSelectedListener(mOnNISListener);


        mNavigation.setOnNavigationItemSelectedListener(mOnNISExListener);


        mNavigationPagerAdapter = new NavigationPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.fragment_container);
        setupViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(this);
        mNavigation.setupWithViewPager(mViewPager);

        mFragmentMain = new MessageFragment();
        setTitle(R.id.navigation_message);

    }

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
                Intent newChatIntent = new Intent(this, NewChatActivity.class);
                startActivity(newChatIntent);
                return true;
            case R.id.action_add_contact:
                addContact();
                return true;
            case R.id.action_settings:
                setTitle(id);
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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
                // This is overwritten
            }
        });

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;
                if (!mEmail.getText().toString().isEmpty() && !mMessage.getText().toString().isEmpty()) {
                    if (containsContact(mEmail.getText().toString())) {
                        Toast.makeText(MainActivity.this, R.string.error_add_contact_existing_contact, Toast.LENGTH_LONG).show();
                    }//TODO: May want to put a more thorough checks e.g. does the email ID exist?
                    else {
                        wantToCloseDialog = true;
                        Toast.makeText(MainActivity.this, R.string.successful_add_contact, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_add_contact_empty_field, Toast.LENGTH_LONG).show();
                }
                if (wantToCloseDialog)
                    dialog.dismiss();
            }
        });
    }

    private boolean containsContact(final String email) {
        //TODO: Check if email entered is already in contact or not.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friends/" + user.getUid());
        if (mFriendsRef.child(email) == null) {
            return true;
        }
        mFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(email)){

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return false;
    }

    private void setupViewPager(ViewPager viewPager) {
        NavigationPagerAdapter adapter = new NavigationPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MessageFragment(), getTitle(R.id.navigation_message));
        adapter.addFragment(new ContactsFragment(), getTitle(R.id.navigation_contact));
        adapter.addFragment(new ProfileFragment(), getTitle(R.id.navigation_profile));
//        adapter.addFragment(new EditProfileFragment(), getTitle(R.id.edit_profile));
        viewPager.setAdapter(adapter);
    }

    public void setViewPager(int fragmentNumber) {
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

    // ViewPager OnPageChange Listener: BEGINS
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//        mBottomNavigationView.setSelectedItemId(position);

    }

    @Override
    public void onPageSelected(int position) {
        if (mViewPager != null) {
            if (position < 3) {
//                mBottomNavigationView.setSelectedItemId(position);
                Log.d(DEBUG_KEY, "item id is " + position);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onContactSelected(Contact contact) {
        //TODO:
//        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//        ChatFragment fragment = ChatFragment();
//        ft.addToBackStack("detail");
//        ft.replace(R.id.fragment_container, fragment);
//        ft.commit();
    }
    //ViewPager OnPageChange Listener: ENDS

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public void onNewChatSelected(Contact contact) {

    }

}
