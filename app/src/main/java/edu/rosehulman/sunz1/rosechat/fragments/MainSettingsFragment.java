package edu.rosehulman.sunz1.rosechat.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import edu.rosehulman.sunz1.rosechat.activities.MainActivity;
import edu.rosehulman.sunz1.rosechat.R;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MainSettingsFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MainSettingsFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MainSettingsFragment extends Fragment implements View.OnClickListener{

    private Button mButtonProfile;
    private Button mButtonLanguage;
    private Button mButtonNotification;
    private Button mButtonLogOut;
    private Button mButtonFeedback;
    private Button mButtonDeleteAccount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_main_settings, container, false);

        mButtonDeleteAccount = (Button) view.findViewById(R.id.button_settings_deleteAccount);
        mButtonFeedback= (Button) view.findViewById(R.id.button_settings_feedback);
        mButtonLanguage = (Button) view.findViewById(R.id.button_settings_Language);
        mButtonLogOut= (Button) view.findViewById(R.id.button_settings_logOut);
        mButtonNotification= (Button) view.findViewById(R.id.button_settings_notification);
        mButtonProfile= (Button) view.findViewById(R.id.button_settings_profile);

        mButtonDeleteAccount.setOnClickListener(this);
        mButtonProfile.setOnClickListener(this);
        mButtonFeedback.setOnClickListener(this);
        mButtonLanguage.setOnClickListener(this);
        mButtonLogOut.setOnClickListener(this);
        mButtonNotification.setOnClickListener(this);


        return view;
    }


    /* Giving each button instructions */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.button_settings_profile:
                ((MainActivity)getActivity()).setViewPager(2);
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(getResources().getString(R.string.navi_profile).toUpperCase());
                return;
            case R.id.button_settings_Language:
                //TODO: the method has error. Don't use. -Sun
//                showLanguageDialog();
                //TODO: Use this instead -Sun
                switchLanguage();
                return;
            case R.id.button_settings_logOut:
                logOutConfirmationDialog();
                return;
            case R.id.button_settings_deleteAccount:
                deleteAccountConfirmationDialog();
                return;
            case R.id.button_settings_notification:
                notificationDialog();
                return;
            case R.id.button_settings_feedback:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                FeedbackSettingsFragment fragment = new FeedbackSettingsFragment();
                transaction.addToBackStack("detail");
                transaction.add(R.id.settings_container ,fragment);
//                transaction.replace(R.id.container, fragment);
                transaction.commit();
                return;
        }

    }

    /*If notification button is pressed, opens a dialog to turn it on or off */
    private void notificationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle("Notifications");
        mBuilder.setMessage("Would you like to turn notifications on or off?");
        mBuilder.setPositiveButton("On", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                NOTIFICATIONS =true;
            }
        });

        mBuilder.setNegativeButton("Off", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                NOTIFICATIONS = false;
            }
        });
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void logOutConfirmationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle(R.string.logout_title);
        mBuilder.setMessage(R.string.logout_message);
        mBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                logOut();
            }
        });

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void logOut() {
        //TODO:
    }


    private void deleteAccount() {
        //TODO:
    }

    private void deleteAccountConfirmationDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
        mBuilder.setTitle(R.string.delete_account_title);
        mBuilder.setMessage(R.string.delete_account_message);
        mBuilder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteAccount();
            }
        });

        mBuilder.setNegativeButton(android.R.string.cancel, null);
        AlertDialog dialog = mBuilder.create();
        dialog.show();
    }

    private void switchLanguage(){
        Intent intent = new Intent(Settings.ACTION_LOCALE_SETTINGS);
        startActivity(intent);
    }

    private void showLanguageDialog(){
        LanguageDialog df = new LanguageDialog(){
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.Title_Language);
                builder.setItems(new String[]{getResources().getString(R.string.language_english),
                        getResources().getString(R.string.language_Chinese)}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: switch languages
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(getFragmentManager(), "language");
    }

    public static class LanguageDialog extends DialogFragment{

        public LanguageDialog(){
            super();
        }
    }
//
//    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }
//
//    /**
//     * This interface must be implemented by activities that contain this
//     * fragment to allow an interaction in this fragment to be communicated
//     * to the activity and potentially other fragments contained in that
//     * activity.
//     * <p>
//     * See the Android Training lesson <a href=
//     * "http://developer.android.com/training/basics/fragments/communicating.html"
//     * >Communicating with Other Fragments</a> for more information.
//     */
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
}
