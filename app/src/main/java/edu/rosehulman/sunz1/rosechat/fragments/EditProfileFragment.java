package edu.rosehulman.sunz1.rosechat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

public class EditProfileFragment extends Fragment implements View.OnClickListener {

    private TextView mConfirm;
    private ImageView mProfileImgE;
    private TextView mEmailTxtE;
    private TextView mNickNameTxtE;
    private TextView mPhoneTxtE;

    public static EditProfileFragment newInstance(String email,
                                                  String nickName,
                                                  String phone) {

        Bundle args = new Bundle();
        args.putString(Constants.PROF_EMAIL, email);
        args.putString(Constants.PROF_NICK_NAME, nickName);
        args.putString(Constants.PROF_PHONE, phone);
        //TODO: need to put the profile image
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
        if (v.getId() == R.id.profile_confirm_edit) {
            // TODO: switch back to profileFragment class
        }
    }
}
