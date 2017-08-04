package edu.rosehulman.sunz1.rosechat.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.ChatRecyclerAdapter;
import edu.rosehulman.sunz1.rosechat.models.Chat;
import edu.rosehulman.sunz1.rosechat.sys.chat.ChatPresenter;
import edu.rosehulman.sunz1.rosechat.sys.chat.ChatSystem;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

public class ChatFragment extends Fragment implements ChatSystem.View, TextView.OnEditorActionListener {

    private ProgressDialog mProgressDialog;
    private ChatRecyclerAdapter mChatAdapter;
    private ChatPresenter mChatPresenter;
    private RecyclerView mRecyclerViewChat;
    private EditText mEditTextChat;


    public ChatFragment() {
        // Required empty public constructor
    }

    public static ChatFragment newInstance(String receiver,
                                           String receiverUid,
                                           String firebaseToken) {

        Bundle args = new Bundle();
        args.putString(Constants.ARG_RECEIVER, receiver);
        args.putString(Constants.ARG_RECEIVER_UID, receiverUid);
        args.putString(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        ChatFragment fragment = new ChatFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        bindViews(view);
        return view;
    }

    private void bindViews(View view) {
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mEditTextChat = (EditText) view.findViewById(R.id.edit_text_chat);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mEditTextChat.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
//        mChatPresenter.getMessage(FirebaseAuth.getInstance().getCurrentUser().getUid(),
//                getArguments().getString(Constants.ARG_RECEIVER));
        mChatPresenter.getMessage(Constants.FAKE_USER, Constants.FAKE_RECEIVER.get(0));
    }

    @Override
    public void onStart() {
        super.onStart();
//        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        EventBus.getDefault().unregister(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEND) {
            sendMessage();
            return true;
        }
        return false;
    }

    private void sendMessage() {
        String text = mEditTextChat.getText().toString();
        String receiver = getArguments().getString(Constants.ARG_RECEIVER);
        String receiverUid = getArguments().getString(Constants.ARG_RECEIVER_UID);
        String sender = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String senderUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String receiverFirebaseToken = getArguments().getString(Constants.ARG_FIREBASE_TOKEN);
        Chat chat = new Chat(sender, receiver, senderUid, receiverUid, text, System.currentTimeMillis());
        mChatPresenter.sendMessage(getActivity().getApplicationContext(), chat, receiverFirebaseToken);
    }

    @Override
    public void onSendMessageSuccess() {
        mEditTextChat.setText(""); // clear EditText
        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Chat chat) {
        if (mChatAdapter == null) {
            mChatAdapter = new ChatRecyclerAdapter(new ArrayList<Chat>());
            mRecyclerViewChat.setAdapter(mChatAdapter);
        }
        mChatAdapter.add(chat);
        mRecyclerViewChat.smoothScrollToPosition(mChatAdapter.getItemCount());
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
