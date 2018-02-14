package edu.rosehulman.sunz1.rosechat.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
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
    //    private Contact mNewContact;
    private String mCurrentUID;
    private String mProfilePicURL;
    private String mToken;
    private BottomNavigationViewEx bottomNavigationViewEx;

    private String newNickName;
    private String newPhone;
    private String newEmail;
    private String newAvatarURL;
    public static EditProfileFragment newInstance(String email,
                                                  String nickName,
                                                  String phone,
                                                  final String profilePicURL) {

        Bundle args = new Bundle();
        args.putString(Constants.PROF_EMAIL, email);
        args.putString(Constants.PROF_NICK_NAME, nickName);
        args.putString(Constants.PROF_PHONE, phone);
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
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(getContext());

        mEmailTxtE.setText(getArguments().getString(Constants.PROF_EMAIL));
        mNickNameTxtE.setText(getArguments().getString(Constants.PROF_NICK_NAME));
        mPhoneTxtE.setText(getArguments().getString(Constants.PROF_PHONE));
        mProfilePicURL = getArguments().getString(Constants.PROF_PROFILE_PIC_URL);
        ((MainActivity)getActivity()).setTitle(mCurrentUID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        bindViews(view);
        bottomNavigationViewEx.setVisibility(View.GONE);
        new Thread(new Runnable() {
            Handler handler = new Handler(Looper.getMainLooper());
            @Override
            public void run() {
                while (!Thread.interrupted()){
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("picasso", "Edit: Trying to get profile picture");
                            Picasso.with(getContext())
                                    .load(mProfilePicURL) //get url
                                    .placeholder(R.drawable.rose_logo)
                                    .error(R.drawable.rosenamelogo)
                                    .into(mProfileImgE);
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
        return view;
    }

    private void bindViews(View view) {
        mConfirm = (TextView) view.findViewById(R.id.profile_confirm_edit);
        mProfileImgE = (ImageView) view.findViewById(R.id.profile_image_edit);
        mEmailTxtE = (TextView) view.findViewById(R.id.profile_email_edit);
        mNickNameTxtE = (TextView) view.findViewById(R.id.profile_name_edit);
        mPhoneTxtE = (TextView) view.findViewById(R.id.profile_phone_edit);
        bottomNavigationViewEx = (BottomNavigationViewEx) getActivity().findViewById(R.id.bnve);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_confirm_edit:
                confirmChanges();
                // TODO: switch back to profileFragment class
                getActivity().getSupportFragmentManager().popBackStack();
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
        newNickName = mNickNameTxtE.getText().toString();
        newPhone = mPhoneTxtE.getText().toString();
        newEmail = mEmailTxtE.getText().toString();
        // TODO: update all and go back
        Handler handler = new Handler();
        handler.postAtTime(new Runnable() {
            @Override
            public void run() {
                new EditProfTask().execute();
            }
        }, 1000); // 1000ms = 1s
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
                    //update new Profile Image here
                    updateProfilePic();
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

    private void updateProfilePic() {
        StorageReference profilePicStorageRef = FirebaseStorage.getInstance().getReference()
                .child(Constants.PATH_PROFILE_PIC + "/" + mCurrentUID);
        mProfileImgE.setDrawingCacheEnabled(true);
        mProfileImgE.buildDrawingCache();
        Bitmap bitmap = mProfileImgE.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        profilePicStorageRef.putBytes(data);
        mProfilePicURL = data.toString();
        Log.d(Constants.TAG_PROFILE, "JUST UPLOAD PIC TO STORAGE");
    }

    private class EditProfTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... str) {

            DatabaseConnectionService service = DatabaseConnectionService.getInstance();
            Connection connection = service.getConnection(); // should not be null

            CallableStatement cs = null;

            try {
                cs = connection.prepareCall("{?=call EditProfile(?, ?, ?, ?, ?)}");
                cs.setString(2, mCurrentUID);
                cs.setString(3, newNickName);
                cs.setString(4, newPhone);
                cs.setString(5, newEmail);
                cs.setString(6, newAvatarURL);
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String str) {
            mEmailTxtE.setText(newEmail);
            mNickNameTxtE.setText(newNickName);
            mPhoneTxtE.setText(newPhone);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        bottomNavigationViewEx.setVisibility(View.VISIBLE);
    }
}
