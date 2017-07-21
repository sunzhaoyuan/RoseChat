package edu.rosehulman.sunz1.rosechat.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.MessageAdapter;
import edu.rosehulman.sunz1.rosechat.models.Message;

///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MessageFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MessageFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MessageFragment extends Fragment {

    MessageAdapter mAdapter;
    private Callback mCallback;

    public MessageFragment(){

    }
//    // TODO: Rename parameter arguments, choose names that match
//    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";
//
//    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;
//
//    private OnFragmentInteractionListener mListener;
//
//    public MessageFragment() {
//        // Required empty public constructor
//    }
//
//    /**
//     * Use this factory method to create a new instance of
//     * this fragment using the provided parameters.
//     *
//     * @param param1 Parameter 1.
//     * @param param2 Parameter 2.
//     * @return A new instance of fragment MessageFragment.
//     */
//    // TODO: Rename and change types and number of parameters
//    public static MessageFragment newInstance(String param1, String param2) {
//        MessageFragment fragment = new MessageFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize dataset, this data would usually come from a local content provider or
        // remote server.
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RecyclerView view = (RecyclerView)inflater.inflate(R.layout.fragment_message, container, false);
//        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        final LinearLayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        view.setLayoutManager(mLayoutManager);
        mAdapter = new MessageAdapter(getContext(), mCallback);
        view.setAdapter(mAdapter);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Callback) {
            mCallback = (Callback) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement Callback");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }
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

    public interface Callback {
        // TODO: Update argument type and name
        void onMessageSelected(Message message);
    }

}
