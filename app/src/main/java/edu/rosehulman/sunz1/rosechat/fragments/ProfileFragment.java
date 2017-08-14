package edu.rosehulman.sunz1.rosechat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private TextView mEdit;
    private ImageView mProfileImg;
    private TextView mEmailTxt;
    private TextView mNickNameTxt;
    private TextView mPhoneTxt;
    //    private Bitmap mBitmap;
    //    private TableLayout
    private DatabaseReference mDBRef;
    private StorageReference mPPicStorageRef;
    private String mCurrentUID;
    private Contact mFireBaseContact;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        dataBaseHandler();
    }

    private void dataBaseHandler() { //TODO: need to be tested.
        Log.d(Constants.TAG_PROFILE, "In DataBaseHandler.");
        mDBRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT);
        profileHandler();
    }

    public void profileHandler() {
        Log.d(Constants.TAG_PROFILE, "In ProfileHandler.");
        Query query = mDBRef.orderByChild("uid").equalTo(mCurrentUID);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Constants.TAG_PROFILE, "about to sync profile data");
                mFireBaseContact = dataSnapshot.getValue(Contact.class); //TODO: check if it gets mFireBaseContact type
                assert mFireBaseContact != null;
                //get Profile pic - worked!
                Glide.with(getContext())
                        .load(mFireBaseContact.getProfilePicUrl())
                        .into(mProfileImg);
                Log.d(Constants.TAG_PROFILE, "set profile pic -DONE");
                //get Text Data
                mNickNameTxt.setText(mFireBaseContact.getNickName());
                Log.d(Constants.TAG_PROFILE, "set NickName -DONE");
                mEmailTxt.setText(mFireBaseContact.getEmail());
                Log.d(Constants.TAG_PROFILE, "set email -DONE");
                mPhoneTxt.setText(mFireBaseContact.getPhoneNumber());
                Log.d(Constants.TAG_PROFILE, "set phone number -DONE");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(Constants.TAG_PROFILE, "failed to handle query for the existed profile");
            }
        });
    }

    private void init() {
        mEdit.setOnClickListener(this);
        mDBRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT);
        mPPicStorageRef = FirebaseStorage.getInstance().getReference().child(Constants.PATH_PROFILE_PIC);
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
        mEdit = (TextView) view.findViewById(R.id.profile_edit);
        mProfileImg = (ImageView) view.findViewById(R.id.profile_image);
        mEmailTxt = (TextView) view.findViewById(R.id.profile_email);
        mNickNameTxt = (TextView) view.findViewById(R.id.profile_name);
        mPhoneTxt = (TextView) view.findViewById(R.id.profile_phone);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.profile_edit) {

            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment editProfileFragment = EditProfileFragment.newInstance(
                    mEmailTxt.getText().toString(),
                    mNickNameTxt.getText().toString(),
                    mPhoneTxt.getText().toString(),
                    mFireBaseContact.getProfilePicUrl()
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
}
