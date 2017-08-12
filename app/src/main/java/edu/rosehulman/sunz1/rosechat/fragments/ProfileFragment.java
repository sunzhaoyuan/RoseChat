package edu.rosehulman.sunz1.rosechat.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.sys.task.GetImgTask;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class ProfileFragment extends Fragment implements View.OnClickListener, GetImgTask.PicConsumer {

    private TextView mEdit;
    private ImageView mProfileImg;
    private TextView mEmailTxt;
    private TextView mNickNameTxt;
    private TextView mPhoneTxt;
    private Bitmap mBitmap;
    //    private TableLayout
    private boolean isFirstTime;
    private DatabaseReference mDBRef;
    private StorageReference mPPicStorageRef;
    private String mCurrentUID;
    private String mDefaultPicUrl;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        dataBaseHandler();
    }

    private void dataBaseHandler() {
        Log.d(Constants.TAG_PROFILE, "In DataBaseHandler.");
        mDBRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT);
        Query query = mDBRef.orderByChild("uid").equalTo(mCurrentUID);
        Log.d(Constants.TAG_PROFILE, "Finished the Query just now.");
        if (query != null) {
            Log.d(Constants.TAG_PROFILE, "the Query is not null");
            // TODO: get Contact from FireBase
            isFirstTime = false;
        } else {
            Log.d(Constants.TAG_PROFILE, "the Query is null");
            // TODO: push this contact
            isFirstTime = true;
            HashMap<String, Boolean> friendHashmap = new HashMap<>();
            final Contact contact = new Contact(mCurrentUID, mCurrentUID, mDefaultPicUrl, friendHashmap, null, null);
            mDBRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    contact.setKey(dataSnapshot.getKey());
                    mDBRef.push().setValue(contact);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "Cannot initiate current user.", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void init() {
        mEdit.setOnClickListener(this);
        mDBRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT);
        mPPicStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(getContext());
        mDefaultPicUrl = mPPicStorageRef.child(Constants.PATH_PP_DEFAULT).getPath();
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
            EditProfileFragment editProfileFragment = EditProfileFragment.newInstance(mEmailTxt.getText().toString(),
                    mNickNameTxt.getText().toString(), mPhoneTxt.getText().toString());
            transaction.addToBackStack("edit");
            transaction.replace(R.id.container, editProfileFragment);
            transaction.commit();
            Fragment editProfileFragment = EditProfileFragment.newInstance(
                    mEmailTxt.getText().toString(),
                    mNickNameTxt.getText().toString(),
                    mPhoneTxt.getText().toString()
            );
        }
    }

    @Override
    public void onPicLoaded(Bitmap bitmap) {
        Log.d(Constants.TAG_PROFILE, "Pic Object\n" + bitmap);
        mBitmap = bitmap;
        mProfileImg.setImageBitmap(mBitmap);
        mProfileImg.setDrawingCacheEnabled(true);
        mProfileImg.buildDrawingCache();
        Bitmap cacheBitmap = mProfileImg.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        cacheBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        if (isFirstTime) {
            // not sure if it will execute onPicLoaded first or
            // onActivityCreated first, so I chose to use this boolean
            // TODO: load the default pic
            Log.d(Constants.TAG_PROFILE, "First Time : Load Default");
        } else {


        }
    }
}
