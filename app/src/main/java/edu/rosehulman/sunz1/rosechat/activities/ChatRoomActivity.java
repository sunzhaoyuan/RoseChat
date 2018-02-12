package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.fragments.ChatFragment;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

public class ChatRoomActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    public static void startActivity(Context context, Integer chatRoomID, String chatRoomName) {
        Intent intent = new Intent(context, ChatRoomActivity.class);
        intent.putExtra(Constants.ARG_CHATROOM_NAME, chatRoomName);
        intent.putExtra(Constants.ARG_CHATROOM_ID, chatRoomID);
        Log.d(Constants.TAG_CHAT, "IN CHAT ACTIVITY\n ChatRoom ID: " + chatRoomID);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        init();
    }

    private void init() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_chat_container,
                ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_MESSAGE_NAME),
                        getIntent().getExtras().getString(Constants.ARG_SENDER_UID),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID),
                        getIntent().getExtras().getString(Constants.ARG_MESSAGE_KEY)),
                ChatFragment.class.getSimpleName());
        fragmentTransaction.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // TODO: notification
        RoseChatFirebaseStatus.setChatActivityOpen(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //TODO: update last message
        RoseChatFirebaseStatus.setChatActivityOpen(false);
    }


}
