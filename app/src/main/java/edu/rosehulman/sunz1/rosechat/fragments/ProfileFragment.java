package edu.rosehulman.sunz1.rosechat.fragments;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.activities.MainActivity;
import edu.rosehulman.sunz1.rosechat.adapters.NavigationPagerAdapter;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class ProfileFragment
        extends Fragment
        implements View.OnClickListener {

    private TextView mEdit;
    private ImageView mProfileImg;
    private TextView mEmailTxt;
    private TextView mNickNameTxt;
    private TextView mPhoneTxt;
    //    private Bitmap mBitmap;
    //    private TableLayout
    private String mCurrentUID;
    private Contact mFireBaseContact;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private String nickName;
    private String email;
    private String phone;
    private String avatarURL;

    private Handler handler;
    private NavigationPagerAdapter adapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(String UID) {
        Bundle args = new Bundle();
        args.putString(Constants.PROF_NEW_UID, UID);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setAdapter(NavigationPagerAdapter adapter){
        this.adapter = adapter;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
//        profileHandler();
        handler = new Handler(Looper.getMainLooper());
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()){
                    profileViewerTask task = new profileViewerTask();
                    task.execute(mCurrentUID);
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(adapter!=null){
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

//    @Override
//    public void getStringArray(String[] texts) {
//        mNickNameTxt.setText(texts[0]);
//        mEmailTxt.setText(texts[1]);
//        mPhoneTxt.setText(texts[2]);
//        Picasso.with(getContext())
//                .load(texts[3])
//                .into(mProfileImg);
//    }

    private  class profileViewerTask extends AsyncTask<String, String, String[]> {

        private String[] result;
//        private ResultCallback callback = null;

//        public profileViewerTask(ResultCallback callback) {
//            this.callback = callback;
//        }

        protected String[] doInBackground(String... params) {
            Log.d(Constants.TAG_PROFILE, "In ProfileHandler.");
            DatabaseConnectionService service = DatabaseConnectionService.getInstance();
            Connection connection = service.getConnection(); // should not be null

            String[] toReturn = null;

            String query = "select * from [User] where UID = '" + mCurrentUID + "'"; //param[0] is current user
            Statement stmt = null;
            try {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                nickName = rs.getString("NickName");
                email = rs.getString("Email");
                phone = rs.getString("Phone");
                avatarURL = rs.getString("AvatarURL");

                result = new String[]{nickName, email, phone, avatarURL};

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return result;
        }

        protected void onProgressUpdate(String... progress) {

        }

        protected void onPostExecute(String[] toReturn) {
            mNickNameTxt.setText(nickName);
            mEmailTxt.setText(email);
            mPhoneTxt.setText(phone);
        }

//        public interface ResultCallback {
//            void getStringArray(String[] toReturn);
//        }

    }

    /**
     * this method gets profile from FireBase and set every fields correctly.
     */
    public void profileHandler() {
        Log.d(Constants.TAG_PROFILE, "In ProfileHandler.");
        DatabaseReference mDBRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT);
        Query query = mDBRef.orderByChild("uid").equalTo(mCurrentUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                Log.d(Constants.TAG_PROFILE, "this snapshot is" + dataSnapshot.toString());
                Log.d(Constants.TAG_PROFILE, "about to sync profile data");
                mFireBaseContact = dataSnapshot.getChildren().iterator().next().getValue(Contact.class); //this works cuz there will be only one matches
                assert mFireBaseContact != null;
                //get Profile pic - worked!
                StorageReference profileRef = FirebaseStorage.getInstance().getReference().child("profile_pics/" + mCurrentUID);

                profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String profilePicURL = uri.toString();
                        Picasso.with(getContext())
                                .load(profilePicURL)
                                .into(mProfileImg);
                        Log.d(Constants.TAG_PROFILE, "profile pic url is\n" +
                                profilePicURL +
                                "\nset profile pic -DONE");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Picasso.with(getContext())
                                .load(mFireBaseContact.getProfilePicUrl())
                                .into(mProfileImg);
                        Log.d(Constants.TAG_PROFILE, "Doesn't have custom profile yet.");
                    }
                });

                //get Text Data
                mNickNameTxt.setText(mFireBaseContact.getNickName());
//                Log.d(Constants.TAG_PROFILE, "nick name is\n" +
//                        mFireBaseContact.getNickName() +
//                        "\nset NickName -DONE");
                mEmailTxt.setText(mFireBaseContact.getEmail());
//                Log.d(Constants.TAG_PROFILE, "email is\n" +
//                        mFireBaseContact.getEmail() +
//                        "\nset email -DONE");
                mPhoneTxt.setText(mFireBaseContact.getPhoneNumber());
//                Log.d(Constants.TAG_PROFILE, "phone number is\n" +
//                        mFireBaseContact.getPhoneNumber() +
//                        "\nset phone number -DONE");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.TAG_PROFILE, "failed to handle query for the existed profile");
            }
        });
    }

    private void init() {
        mEdit.setOnClickListener(this);
        mCurrentUID = getArguments().getString(Constants.PROF_NEW_UID);
        ((MainActivity) getActivity()).setTitle(mCurrentUID);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        bindViews(view);

        if (!SharedPreferencesUtils.getCurrentUser(getContext()).equals(getArguments().getString(Constants.PROF_NEW_UID))) {
            Log.d(Constants.TAG_PROFILE, "Current user: " + SharedPreferencesUtils.getCurrentUser(getContext())
                    + "\nmCurrentUID: " + mCurrentUID);
            bottomNavigationViewEx.setVisibility(View.GONE);
            mEdit.setVisibility(View.GONE);
        }

        // get all data from sql server

//        String[] texts = task.getResult();

//        mNickNameTxt.setText(nickName);
//        mEmailTxt.setText(email);
//        mPhoneTxt.setText(phone);
//        Picasso.with(getContext())
//                .load(avatarURL)
//                .into(mProfileImg);

        return view;
    }

    private void bindViews(View view) {
        mEdit = (TextView) view.findViewById(R.id.profile_edit);
        mProfileImg = (ImageView) view.findViewById(R.id.profile_image);
        mEmailTxt = (TextView) view.findViewById(R.id.profile_email);
        mNickNameTxt = (TextView) view.findViewById(R.id.profile_name);
        mPhoneTxt = (TextView) view.findViewById(R.id.profile_phone);
        bottomNavigationViewEx = (BottomNavigationViewEx) getActivity().findViewById(R.id.bnve);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_edit) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment editProfileFragment = EditProfileFragment.newInstance(
                    mEmailTxt.getText().toString(),
                    mNickNameTxt.getText().toString(),
                    mPhoneTxt.getText().toString(),
                    ""
//                    mFireBaseContact.getProfilePicUrl()
            );
            transaction.addToBackStack("edit");
            transaction.replace(R.id.container, editProfileFragment);
            transaction.commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // TODO: stop listeners?
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bottomNavigationViewEx.setVisibility(View.VISIBLE);
    }
}
