package edu.rosehulman.sunz1.rosechat.fragments;

import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
    private String mCurrentUID;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private String nickName;
    private String email;
    private String phone;
    private String avatarURL;

    private String[] profileElement;

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

    private  class profileViewerTask extends AsyncTask<String, String, String[]> {

        protected String[] doInBackground(String... params) {
            Log.d(Constants.TAG_PROFILE, "In ProfileHandler.");
            DatabaseConnectionService service = DatabaseConnectionService.getInstance();
            Connection connection = service.getConnection(); // should not be null

            String query = "select * from [User] where UID = '" + mCurrentUID + "'"; //param[0] is current user
            Statement stmt;
            try {
                stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query);
                rs.next();
                nickName = rs.getString("NickName");
                email = rs.getString("Email");
                phone = rs.getString("Phone");
                avatarURL = rs.getString("AvatarURL");

                profileElement = new String[]{nickName, email, phone, avatarURL};

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {

        }

        protected void onPostExecute(final String[] toReturn) {
            mNickNameTxt.setText(nickName);
            mEmailTxt.setText(email);
            mPhoneTxt.setText(phone);
            // ProfilePic Thread
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!Thread.interrupted()){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("picasso", "Trying to get profile picture");
                                Picasso.with(getContext())
                                        .load(profileElement[3]) //get url
                                        .placeholder(R.drawable.rose_logo)
                                        .error(R.drawable.rosenamelogo)
                                        .into(mProfileImg);
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
                    profileElement[3]
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
