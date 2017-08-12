package edu.rosehulman.sunz1.rosechat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.activities.MainActivity;
import edu.rosehulman.sunz1.rosechat.sys.image_selector.UserPicture;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

import static android.app.Activity.RESULT_OK;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    public static final String IMAGE_TYPE = "image/*";
    public static final int SELECT_SINGLE_PICTURE = 101;

    private TextView mConfirm;
    private ImageView mProfileImgE;
    private TextView mEmailTxtE;
    private TextView mNickNameTxtE;
    private TextView mPhoneTxtE;

    private DatabaseReference mDBRef;
    private StorageReference mPPicStorageRef;
    private String mCurrentUID;

    public static EditProfileFragment newInstance(String email,
                                                  String nickName,
                                                  String phone,
                                                  String profilePicURL) {

        Bundle args = new Bundle();
        args.putString(Constants.PROF_EMAIL, email);
        args.putString(Constants.PROF_NICK_NAME, nickName);
        args.putString(Constants.PROF_PHONE, phone);
        //TODO: need to put the profile image, or maybe we can get the image from FireBase again
        args.putString(Constants.PROF_PROFILE_PIC_URL, profilePicURL);
        EditProfileFragment fragment = new EditProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public EditProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mConfirm.setOnClickListener(this);
        mProfileImgE.setOnClickListener(this);
        mDBRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_CONTACT); //there should be exactly one contact match this UID
        mPPicStorageRef = FirebaseStorage.getInstance().getReference();
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(getContext());

//        mEmailTxtE.setText(getArguments().getString(Constants.PROF_EMAIL));
//        mNickNameTxtE.setText(getArguments().getString(Constants.PROF_NICK_NAME));
//        mPhoneTxtE.setText(getArguments().getString(Constants.PROF_PHONE));
//        Glide.with(getContext())
//                .load(getArguments().getString(Constants.PROF_PROFILE_PIC_URL))
//                .into(mProfileImgE);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        bindViews(view);


        return view;
    }

    private void bindViews(View view) {
        mConfirm = (TextView) view.findViewById(R.id.profile_confirm_edit);
        mProfileImgE = (ImageView) view.findViewById(R.id.profile_image_edit);
        mEmailTxtE = (TextView) view.findViewById(R.id.profile_email_edit);
        mNickNameTxtE = (TextView) view.findViewById(R.id.profile_name_edit);
        mPhoneTxtE = (TextView) view.findViewById(R.id.profile_phone_edit);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_confirm_edit:
                confirmChanges();
                // TODO: switch back to profileFragment class
                break;
            case R.id.profile_image_edit:
                singleImgSelection();
                break;
            default:
                break;
        }
    }

    private void confirmChanges() {
        // TODO: write the txt from those EditTxts
        // TODO: update them onto FireBase
    }

    private void singleImgSelection() {
        Intent intent = new Intent();
        intent.setType(IMAGE_TYPE);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,
                getString(R.string.select_picture)), SELECT_SINGLE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_SINGLE_PICTURE) {

                Uri selectedImageUri = data.getData();
                try {
                    mProfileImgE.setImageBitmap(new UserPicture(selectedImageUri, getActivity().getContentResolver()).getBitmap());
                    //TODO: update new Profile Image here
                } catch (IOException e) {
                    Log.e(MainActivity.class.getSimpleName(), "Failed to load image", e);
                }
            }
        } else {
            // report failure
            Toast.makeText(getContext(), R.string.fail_to_get_intent_data, Toast.LENGTH_LONG).show();
            Log.d(MainActivity.class.getSimpleName(), "Failed to get intent data, result code is " + resultCode);
        }
    }
}
