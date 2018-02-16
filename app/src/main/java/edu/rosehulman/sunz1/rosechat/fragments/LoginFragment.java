package edu.rosehulman.sunz1.rosechat.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment
        extends Fragment
        implements View.OnClickListener{

    private Button mLoginBtn;
//    private Button mLoginBtnTest;
    private boolean mLoggingIn;
    private View mProgressSpinner;
    private OnLoginListener mListener;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {

        Bundle args = new Bundle();

        LoginFragment fragment = new LoginFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoggingIn = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        mLoginBtn = (Button) view.findViewById(R.id.button_login);
        mLoginBtn.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);
//        mLoginBtnTest = (Button) view.findViewById(R.id.button_login_fake);
        mProgressSpinner = view.findViewById(R.id.login_progress);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mLoginBtn.setOnClickListener(this);
//        mLoginBtnTest.setOnClickListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mListener = (OnLoginListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnLoginListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loginWithRoseFire() {
        if (mLoggingIn) {
            return;
        }
        showProgress(true);
        mLoggingIn = true;
        mListener.onRosefireLogin();
    }

    private void showProgress(boolean show) {
        mProgressSpinner.setVisibility(show ? View.VISIBLE : View.GONE);
        mLoginBtn.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    public void onLoginError(String message) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getResources().getString(R.string.Login_error))
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, null)
                .create()
                .show();
        showProgress(false);
        mLoggingIn = false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_login:
                loginWithRoseFire();
//                return;
//            case R.id.button_login_fake:
//                Intent intent = new Intent(getContext(), MainActivity.class);
//                startActivity(intent);
//                getActivity().finish();
        }
    }

    public interface OnLoginListener {
        void onRosefireLogin();
    }
}
