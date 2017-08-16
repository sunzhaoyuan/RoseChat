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
import com.google.firebase.iid.FirebaseInstanceId;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.HashMap;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.NavigationPagerAdapter;
import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.MessageFragment;
import edu.rosehulman.sunz1.rosechat.fragments.ProfileFragment;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

//import edu.rosehulman.sunz1.rosechat.activities.NewChatActivity;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener,
//        MessageFragment.Callback,
        ContactsFragment.Callback,
        NewChatActivity.Callback {

    final private String DEBUG_KEY = "Debug";

    private NavigationPagerAdapter mNavigationPagerAdapter;
    public static ViewPager mViewPager;
    private Fragment mFragmentMain;
    private BottomNavigationViewEx mNavigation;
    private BottomNavigationView mBottomNavigationView;
    private Contact mContact;

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
                        mFragmentMain = ProfileFragment.newInstance(SharedPreferencesUtils.getCurrentUser(getApplicationContext()));
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

        setupProfileHandler();
    }


    /**
     * it only creates a contact if there is no contact for this user existed
     */
    private void setupProfileHandler() {
        final DatabaseReference profRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT);
        profRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String currentUID = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
//                if (!dataSnapshot.child) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(Constants.TAG_PROFILE, "CURRENT UID : " + snapshot.child("uid").getValue());
                    if (snapshot.child("uid").getValue().equals(currentUID)) {
                        i++;
                    }
                }
                if (i == 0) {
                    String fcmToken = FirebaseInstanceId.getInstance().getToken();
                    mContact = new Contact(currentUID, currentUID,
                            "https://firebasestorage.googleapis.com/v0/b/rosechat-64ae9.appspot.com/o/profile_pics%2Fdefault.png?alt=media&token=2cc54fe8-da2f-49f9-ab18-0ef0d2e8fea6",
                            getString(R.string.profile_sample_phone_number),
                            currentUID + "@rose-hulman.edu",
                            fcmToken);
                    DatabaseReference newProfRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT + "/" + currentUID);
//                    mContact.setKey(dataSnapshot.getKey());
                    newProfRef.child("email").setValue(mContact.getEmail());
                    newProfRef.child("phoneNumber").setValue(mContact.getPhoneNumber());
                    newProfRef.child("nickName").setValue(mContact.getNickName());
                    newProfRef.child("profilePicUrl").setValue(mContact.getProfilePicUrl());
                    newProfRef.child("uid").setValue(currentUID);
                    newProfRef.child("fireBaseToken").setValue(mContact.getFireBaseToken());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.TAG_PROFILE, "PROFILE REFERENCE ERROR\n" + databaseError.getMessage());
            }
        });
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
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    final DatabaseReference mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friends/" + user.getUid());
                    mFriendsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(mEmail.getText().toString()) && dataSnapshot.child(mEmail.getText().toString()).getValue().equals(true)) {
                                Toast.makeText(MainActivity.this, R.string.error_add_contact_existing_contact, Toast.LENGTH_LONG).show();
                            } else if (dataSnapshot.hasChild(mEmail.getText().toString()) && dataSnapshot.child(mEmail.getText().toString()).getValue().equals(false)) {
                                DatabaseReference mUserInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/" + user.getUid());
                                mUserInvitationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(mEmail.getText().toString())) {
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this, "Invite already sent", Toast.LENGTH_LONG).show();
                                        } else {
                                            sendInvite(mEmail.getText().toString(), mMessage.getText().toString(), user.getUid());
                                            dialog.dismiss();
                                            Toast.makeText(MainActivity.this, R.string.successful_add_contact, Toast.LENGTH_LONG).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            } else {
                                DatabaseReference mContactRef = FirebaseDatabase.getInstance().getReference().child("contacts");
                                mContactRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.hasChild(mEmail.getText().toString())) {
                                            Toast.makeText(MainActivity.this, "User doesn't exist", Toast.LENGTH_LONG).show();
                                        } else {
                                            DatabaseReference mUserInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/" + user.getUid());
                                            mUserInvitationRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.hasChild(mEmail.getText().toString())) {
                                                        dialog.dismiss();
                                                        Toast.makeText(MainActivity.this, "Invite already sent", Toast.LENGTH_LONG).show();
                                                    } else {
                                                        sendInvite(mEmail.getText().toString(), mMessage.getText().toString(), user.getUid());
                                                        dialog.dismiss();
                                                        Toast.makeText(MainActivity.this, R.string.successful_add_contact, Toast.LENGTH_LONG).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });//TODO: May want to put a more thorough checks e.g. does the email ID exist?
                } else {
                    Toast.makeText(MainActivity.this, R.string.error_add_contact_empty_field, Toast.LENGTH_LONG).show();
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

    public void setupViewPager(ViewPager viewPager) {
        NavigationPagerAdapter adapter = new NavigationPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MessageFragment(), getTitle(R.id.navigation_message));
        adapter.addFragment(new ContactsFragment(), getTitle(R.id.navigation_contact));
        adapter.addFragment(ProfileFragment.newInstance(SharedPreferencesUtils.getCurrentUser(getApplicationContext())), getTitle(R.id.navigation_profile));
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
