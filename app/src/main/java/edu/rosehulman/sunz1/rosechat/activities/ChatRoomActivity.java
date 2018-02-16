package edu.rosehulman.sunz1.rosechat.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.fragments.ChatRoomFragment;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_font, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.set_message_font) {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    DialogFragment df = new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            final float[] sizefactorArray = {(float) 0.5, 1, 2};
                            builder.setTitle("Pick a font size factor for you following messages")
                                    .setItems(R.array.fontsize_array, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            float sizefactor = sizefactorArray[which];//get fontfactor
                                            Constants.MESSAGE_FONT_SIZE = sizefactor;
                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, null);
                            return builder.create();
                        }
                    };
                    df.show(getFragmentManager(), "fontMessage");

                    return true;
                }
            });
        } else {
            item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    DialogFragment df = new DialogFragment() {
                        @Override
                        public Dialog onCreateDialog(Bundle savedInstanceState) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            final String[] sizefactorArray = {"Default","Monospace"};
                            builder.setTitle("Pick a font family for you following messages")
                                    .setItems(R.array.fontfamily_array, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String fontfamily = sizefactorArray[which];//get fontfactor
                                            if(fontfamily.equals("Default")){
                                                Constants.MEESSAGE_FONT_FAMILY = 0;
                                            }else{
                                                Constants.MEESSAGE_FONT_FAMILY = 1;
                                            }

                                        }
                                    });
                            builder.setNegativeButton(android.R.string.cancel, null);
                            return builder.create();
                        }
                    };
                    df.show(getFragmentManager(), "fontMessage");
                    return true;
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private void init() {

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(
                R.id.frame_layout_chat_container,
                ChatRoomFragment.newInstance(
                        getIntent().getExtras().getString(Constants.ARG_CHATROOM_NAME),
                        getIntent().getExtras().getInt(Constants.ARG_CHATROOM_ID)
                ),
                ChatRoomFragment.class.getSimpleName()
        );
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
