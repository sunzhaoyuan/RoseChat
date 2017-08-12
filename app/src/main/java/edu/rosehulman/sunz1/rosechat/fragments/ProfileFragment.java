package edu.rosehulman.sunz1.rosechat.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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

import java.util.HashMap;

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
    private boolean isFirstTime; //TODO: probably don't need this boolean
    private DatabaseReference mDBRef;
    private StorageReference mPPicStorageRef;
    private String mCurrentUID;
    private String mDefaultPicUrl;
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
        //TODO: logic is wrong here. I shouldn't wait for user to click profile button to check the existences of a contact.
        profileHandler();
//        getProfileData();
    }

    public void profileHandler() {
        Log.d(Constants.TAG_PROFILE, "In ProfileHandler.");
//        Query query = mDBRef.orderByChild("uid").equalTo(mCurrentUID);
//        query.addValueEventListener(new ValueEventListener() {
        mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Contact contact;
                if (!dataSnapshot.hasChild(mCurrentUID)) { //TODO: check if this works
                    Log.d(Constants.TAG_PROFILE, "dataSnapshot is null.");
                    HashMap<String, Boolean> friendHashmap = new HashMap<>();
                    contact = new Contact(mCurrentUID, mCurrentUID, mDefaultPicUrl, friendHashmap,
                            getString(R.string.profile_sample_phone_number), getString(R.string.profile_sample_email));
                    Log.d(Constants.TAG_PROFILE, "contact key is " + dataSnapshot.getKey());
                    contact.setKey(dataSnapshot.getKey()); //TODO: can it get key if it's null?
                    Log.d(Constants.TAG_PROFILE, "just pushed a new profile");
                    mDBRef.push().setValue(contact);
                }
                Query query = mDBRef.orderByChild("uid").equalTo(mCurrentUID);
                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.d(Constants.TAG_PROFILE, "enter the path for the existed contact");
                        mFireBaseContact = dataSnapshot.getValue(Contact.class); //TODO: check if it gets contact type
                        assert mFireBaseContact != null;
                        //get Profile pic - worked!
                        Glide.with(getContext())
                                .load("https://www.mariowiki.com/images/thumb/9/96/TanookiMario_SMB3.jpg/180px-TanookiMario_SMB3.jpg")
//                        .load(mFireBaseContact.getProfilePicUrl())
                                .into(mProfileImg);
                        //get Text Data
                        mNickNameTxt.setText(mFireBaseContact.getNickName());
                        mEmailTxt.setText(mFireBaseContact.getEmail());
                        mPhoneTxt.setText(mFireBaseContact.getPhoneNumber());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d(Constants.TAG_PROFILE, "failed to handle query for the existed profile");
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), R.string.contact_initiate_error, Toast.LENGTH_LONG).show();
                Log.d(Constants.TAG_PROFILE, "fail to initiate profile");
            }
        });
    }

    private void init() {
        mEdit.setOnClickListener(this);
        mDBRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT);
        mPPicStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(getContext());
        mPPicStorageRef.child(Constants.PATH_PP_DEFAULT).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() { //TODO: cannot get img from storage
            @Override
            public void onSuccess(Uri uri) {
                mDefaultPicUrl = uri.getPath();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(Constants.TAG_PROFILE, "fail to get default profile pic url");
            }
        });
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
