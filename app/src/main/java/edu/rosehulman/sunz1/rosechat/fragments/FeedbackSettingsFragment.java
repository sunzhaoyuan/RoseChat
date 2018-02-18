package edu.rosehulman.sunz1.rosechat.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

public class FeedbackSettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference feedBackRef;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public FeedbackSettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_feedback_settings, container, false);
        feedBackRef = FirebaseDatabase.getInstance().getReference().child("feedback/" + user.getUid());
        final EditText feedback = (EditText) view.findViewById(R.id.feedback_edit_text);
        Button mSend = (Button) view.findViewById(R.id.feedback_send);

        mSend.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);
        if (Constants.FONT_FAMILY == 0)
            mSend.setTypeface(Typeface.DEFAULT);
        else
            mSend.setTypeface(Typeface.MONOSPACE);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(feedback.getText().length() == 0){
                    Toast.makeText(getContext(), "No feedback entered", Toast.LENGTH_LONG).show();
                }else{
                    feedBackRef.push().setValue(feedback.getText().toString());
                    Toast.makeText(getContext(), "Feedback sent, thank you", Toast.LENGTH_LONG).show();
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            }
        });
        return view;
    }

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

}
