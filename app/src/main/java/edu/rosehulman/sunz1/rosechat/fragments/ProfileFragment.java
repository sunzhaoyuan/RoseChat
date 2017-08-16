package edu.rosehulman.sunz1.rosechat.fragments;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import java.util.Objects;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

public class ProfileFragment extends Fragment implements View.OnClickListener {

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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        profileHandler();
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
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        bindViews(view);
        if (!Objects.equals(getArguments().getString(Constants.PROF_NEW_UID), mCurrentUID)) {
            bottomNavigationViewEx.setVisibility(View.GONE);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bottomNavigationViewEx.setVisibility(View.VISIBLE);
    }
}
