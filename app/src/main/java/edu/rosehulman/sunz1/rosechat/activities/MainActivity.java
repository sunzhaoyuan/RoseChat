package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
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

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.adapters.NavigationPagerAdapter;
import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.MessageFragment;
import edu.rosehulman.sunz1.rosechat.fragments.ProfileFragment;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, ContactsFragment.Callback {

    final private String DEBUG_KEY = "Debug";

    private NavigationPagerAdapter mNavigationPagerAdapter;
    public static ViewPager mViewPager;
    private Fragment mFragmentMain;
    private BottomNavigationViewEx mNavigation;
    private BottomNavigationView mBottomNavigationView;
    private Contact mContact;

    /**
     * For SQL
     */

    private Connection mDBConnection;


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

    /**
     * When create MainActivity, we set three main fragments which are in the PageViewer
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDBConnection = DatabaseConnectionService.getInstance().getConnection();
        setupProfile();



    }


    /**
     * If we have current user in the DB, we get its data from User table.
     * If we don't have current user, we call CreateUser procedure and create one.
     */
    private void setupProfile() {
        new NetworkTask().execute();
    }

    private class NetworkTask extends AsyncTask<String, Integer, ResultSet> {

        protected ResultSet doInBackground(String... params) {

            String UID = SharedPreferencesUtils.getCurrentUser(MainActivity.this.getApplicationContext());
            try {
                String uid = "";

                //we need to create current user
                CallableStatement cs = null;
                String defaultNickName = UID;
                String defaultEmail = UID + "@rose-hulman.edu";
                String defaultPhone = getString(R.string.profile_sample_phone_number);
                String defaultAvatarURL = "https://firebasestorage.googleapis.com/v0/b/rosechat-64ae9.appspot.com/o/profile_pics%2Fdefault.png?alt=media&token=2cc54fe8-da2f-49f9-ab18-0ef0d2e8fea6";

                cs = DatabaseConnectionService.getInstance().getConnection().prepareCall("{?=call CreateUser(?, ?, ?, ?, ?)}");
                cs.setString(2, UID);
                cs.setString(3, defaultNickName);
                cs.setString(4, defaultPhone);
                cs.setString(5, defaultEmail);
                cs.setString(6, defaultAvatarURL);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute();
                int out = cs.getInt(1);
                Log.d(DEBUG_KEY, out + "wo gan");
                new FontSetting().execute();

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ResultSet resultSet) {
            setContentView(R.layout.activity_main);

            mNavigation = (BottomNavigationViewEx) findViewById(R.id.bnve);
            mNavigation.setTextSize(15*(float)Constants.FONT_SIZE_FACTOR);
            if (Constants.FONT_FAMILY == 0) {
                mNavigation.setTypeface(Typeface.DEFAULT);
            } else {
                mNavigation.setTypeface(Typeface.MONOSPACE);
            }

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
                            mFragmentMain = ProfileFragment.newInstance(SharedPreferencesUtils.getCurrentUser(getApplicationContext()));
                            mViewPager.setCurrentItem(2);
                            break;
                    }
                    setTitle(id);
                    return true;
                }
            };


            mNavigation.setOnNavigationItemSelectedListener(mOnNISExListener);


            mNavigationPagerAdapter = new NavigationPagerAdapter(getSupportFragmentManager());
            mViewPager = (ViewPager) findViewById(R.id.fragment_container);
            setupViewPager(mViewPager);

            mViewPager.addOnPageChangeListener(MainActivity.this);
            mNavigation.setupWithViewPager(mViewPager);

            mFragmentMain = new MessageFragment();
            setTitle(R.id.navigation_message);
            super.onPostExecute(resultSet);
        }
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
                setTitle("New Chat");
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
            case R.id.action_invitations:
                setTitle("Invitations");
                Intent invitationIntent = new Intent(this, InvitationActivity.class);
                startActivity(invitationIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class Indicator {
        int result = -1;
    }

    private Indicator indicator;

    private class AddContactTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                CallableStatement cs = mDBConnection.prepareCall("{? = call Friend_Invite(?, ?, ?)}");
                cs.registerOutParameter(1, Types.INTEGER);
                cs.setString(2, strings[0]);
                cs.setString(3, strings[1]);
                cs.setString(4, strings[2]);
                cs.execute();
                int result = cs.getInt(1);
                indicator.result = result;
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
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
                String user = mEmail.getText().toString();
                String message = mMessage.getText().toString();
                if (user.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Rose ID cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    indicator = new Indicator();
                    new AddContactTask().execute(SharedPreferencesUtils.getCurrentUser(getApplicationContext()), user, message);
                    while (indicator.result == -1) {
                        try {
                            Thread.sleep(200);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (indicator.result == 0) {
                        Toast.makeText(MainActivity.this, "Invitation sent successfully.", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    } else {
                        switch (indicator.result) {
                            case 2:
                                Toast.makeText(MainActivity.this, "User does not exist.", Toast.LENGTH_LONG).show();
                                break;
                            case 1:
                                Toast.makeText(MainActivity.this, "You are already friends.", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                }
            }
        });

    }

    private void sendInvite(String mEmail, String mMessage, String user) {
        DatabaseReference mUserInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/" + user + "/" + mEmail);
        mUserInvitationRef.child("message").setValue(mMessage);
        mUserInvitationRef.child("status").setValue("Pending");

        DatabaseReference mFriendInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/" + mEmail + "/" + user);
        mFriendInvitationRef.child("message").setValue(mMessage);
        mFriendInvitationRef.child("status").setValue("Waiting");
    }

    private boolean inviteAlreadySentCheck(final String mEmail, String user) {

        return false;
    }

    NavigationPagerAdapter adapter;

    public void setupViewPager(ViewPager viewPager) {
        adapter = new NavigationPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MessageFragment(), getTitle(R.id.navigation_message));
        adapter.addFragment(new ContactsFragment(), getTitle(R.id.navigation_contact));
        ProfileFragment frag = ProfileFragment.newInstance(SharedPreferencesUtils.getCurrentUser(getApplicationContext()));
        adapter.addFragment(frag, getTitle(R.id.navigation_profile));
        frag.setAdapter(adapter);
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

    public void setTitle(String title) {
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
//        ChatRoomFragment fragment = ChatRoomFragment();
//        ft.addToBackStack("detail");
//        ft.replace(R.id.fragment_container, fragment);
//        ft.commit();
    }
    //ViewPager OnPageChange Listener: ENDS

    /**
     * For the startActivity callback in LoginFragment
     */
    public static void startActivity(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    private class FontSetting extends AsyncTask<String,Integer,Long>{
        @Override
        protected Long doInBackground(String... strings) {
            try {
                CallableStatement statement = DatabaseConnectionService.getInstance().getConnection().prepareCall("{call GetAllSettings(?)}");
                statement.setString(1,SharedPreferencesUtils.getCurrentUser(getApplicationContext()));
                statement.execute();
                ResultSet rs = statement.getResultSet();
                while (rs.next()){
                    Constants.FONT_SIZE_FACTOR = rs.getDouble("FontSize");
                    if (rs.getString("FontFamily").equals("Default"))
                        Constants.FONT_FAMILY = 0;
                    else
                        Constants.FONT_FAMILY = 1;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
