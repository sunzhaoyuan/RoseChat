package edu.rosehulman.sunz1.rosechat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.MessageAdapter;

public class MessageFragment extends Fragment {

    MessageAdapter mAdapter;
//    private Callback mCallback;

    public MessageFragment(){

    }

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
        mAdapter = new MessageAdapter(getContext());
        view.setAdapter(mAdapter);
        return view;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof Callback) {
//            mCallback = (Callback) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement Callback");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mCallback = null;
    }

}
