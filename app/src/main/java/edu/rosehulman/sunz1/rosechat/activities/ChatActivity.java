package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.fragments.ChatFragment;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

public class ChatActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    public static void startActivity(Context context, String receiver, String receiverUID, String messageKey) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(Constants.ARG_RECEIVER, receiver);
        intent.putExtra(Constants.ARG_RECEIVER_UID, receiverUID);
        intent.putExtra(Constants.ARG_MESSAGE_KEY, messageKey);
//        intent.putExtra(Constants.ARG_FIREBASE_TOKEN, firebaseToken);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
//        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        init();
    }

    private void init() {
//        setSupportActionBar(mToolbar);
//        mToolbar.setTitle(getIntent().getExtras().getString(Constants.ARG_RECEIVER));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_chat_container,
                ChatFragment.newInstance(getIntent().getExtras().getString(Constants.ARG_RECEIVER),
                        getIntent().getExtras().getString(Constants.ARG_RECEIVER_UID)),
                ChatFragment.class.getSimpleName());
//        fragmentTransaction.replace(R.id.frame_layout_chat_container,
//                ChatFragment.newInstance(Constants.FAKE_RECEIVER.get(0),
//                        "Agaraa",
//                        "111"),
//                ChatFragment.class.getSimpleName());
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
