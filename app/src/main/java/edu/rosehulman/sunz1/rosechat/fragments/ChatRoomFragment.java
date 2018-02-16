package edu.rosehulman.sunz1.rosechat.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.MessageRecyclerAdapter;
import edu.rosehulman.sunz1.rosechat.models.Message;
import edu.rosehulman.sunz1.rosechat.sys.chat.ChatPresenter;
import edu.rosehulman.sunz1.rosechat.sys.chat.ChatSystem;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

public class ChatRoomFragment extends Fragment implements ChatSystem.View, TextView.OnEditorActionListener {

    public static final String TAG = Constants.TAG_CHAT;

    private ProgressDialog mProgressDialog;
    private MessageRecyclerAdapter mChatAdapter;
    private ChatPresenter mChatPresenter;
    private RecyclerView mRecyclerViewChat;
    private EditText mEditTextChat;

    private String mCurrentUID;

    public ChatRoomFragment() {
        // Required empty public constructor
    }

    /**
     * Get values that were passed from ChatRoomActivity : init()
     *
     * @param chatRoomName
     * @param chatRoomID
     * @return fragment - a ChatRoomFragment callback that ChatRoomActivity
     *                    uses to replace fragment container
     */
    public static ChatRoomFragment newInstance(String chatRoomName,
                                               Integer chatRoomID)
    {
        Bundle args = new Bundle();
        args.putString(Constants.ARG_CHATROOM_NAME, chatRoomName);
        args.putInt(Constants.ARG_CHATROOM_ID, chatRoomID);
        ChatRoomFragment fragment = new ChatRoomFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        mRecyclerViewChat = (RecyclerView) view.findViewById(R.id.recycler_view_chat);
        mEditTextChat = (EditText) view.findViewById(R.id.edit_text_chat);
        mRecyclerViewChat.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if(bottom < oldBottom){
                    mRecyclerViewChat.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mRecyclerViewChat.smoothScrollToPosition(mRecyclerViewChat.getAdapter().getItemCount() - 1);
                        }
                    }, 100);
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(getContext());
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setTitle(getString(R.string.loading));
        mProgressDialog.setMessage(getString(R.string.please_wait));
        mProgressDialog.setIndeterminate(true);

        mEditTextChat.setOnEditorActionListener(this);

        mChatPresenter = new ChatPresenter(this);
        Log.d(Constants.TAG_CHAT, "IN CHAT FRAGMENT\nChatRoom ID: " + getArguments().getInt(Constants.ARG_CHATROOM_ID));
        String receiverUID = getArguments().getString(Constants.ARG_RECEIVER_UID);
        mChatPresenter.getMessage(
                getContext(),
                mCurrentUID,
                getArguments().getInt(Constants.ARG_CHATROOM_ID)
        );
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
            // after the user pushes "Send" button
            sendMessage();
            return true;
        }
        return false;
    }

    /**
     *
     *
     */
    private void sendMessage() {
        Integer chatRoomID = getArguments().getInt(Constants.ARG_CHATROOM_ID);
        String text = mEditTextChat.getText().toString();
        mChatPresenter.sendMessage(getActivity().getApplicationContext(), chatRoomID, text, mCurrentUID);
    }



    @Override
    public void onSendMessageSuccess() {
        mEditTextChat.setText(""); // clear EditText
//        Toast.makeText(getActivity(), "Message sent", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSendMessageFailure(String message) {
//        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onGetMessagesSuccess(Message message) {
        if (mChatAdapter == null) {
            mChatAdapter = new MessageRecyclerAdapter(getContext(), new ArrayList<Message>());
            mRecyclerViewChat.setAdapter(mChatAdapter);
        }
        if (!mChatAdapter.contains(message.getMID())) {
            mChatAdapter.add(message);
            mRecyclerViewChat.smoothScrollToPosition(mChatAdapter.getItemCount());
        }
    }

    @Override
    public void onGetMessagesFailure(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
}
