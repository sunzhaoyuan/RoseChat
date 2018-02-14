package edu.rosehulman.sunz1.rosechat.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.activities.NewChatActivity;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<String> mContactList;
    ArrayList<String> mSelectedContactsList;
    NewChatActivity.Callback mCallback;
    //For SQL
    private Connection mDBConnection;
    private String UID;

    public NewChatAdapter(Context context, NewChatActivity.Callback callback) {
        mDBConnection = DatabaseConnectionService.getInstance().getConnection();
        mCallback = callback;
        mContext = context;
        UID = SharedPreferencesUtils.getCurrentUser(mContext);
        mContactList = new ArrayList<>();
        mSelectedContactsList = new ArrayList<>();
        new getFriendTask(UID).execute();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_chat_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewChatAdapter.ViewHolder holder, int position) {
        String contact = mContactList.get(position);
        holder.mContactName.setText(contact);
    }

    @Override
    public int getItemCount() {
        return mContactList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mContactName;
        String FriendID;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    //create chat room
                    FriendID = ((TextView) view.findViewById(R.id.newChat_name)).getText().toString();
                    selectFriendDialog(FriendID);
                }
            });

            mContactName = (TextView) itemView.findViewById(R.id.newChat_name);
        }
    }

    private void selectFriendDialog(final String FriendID) {
        FragmentManager manager = ((Activity) mContext).getFragmentManager();
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Log.d("chatroom", "create chatroom");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialogfragment_newchat, null);
                builder.setView(view);
                final EditText editText = (EditText) view.findViewById(R.id.dialogfragment_newchat_editext);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        new addMemberAndCreateTask(UID, editText.getText().toString(), FriendID).execute();
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(manager, "fontSize");
    }


    //----------------------------------------------------------Connection SQL Tasks------------------------------------------//
    private class getFriendTask extends AsyncTask<String, Integer, ResultSet> {
        private String UID = null;
        private ResultSet set = null;

        private getFriendTask(String UID) {
            this.UID = UID;
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            Log.d("gerFriend", "?");
            System.out.print("........");
            try {
                System.out.print(UID);
                CallableStatement stem = NewChatAdapter.this.mDBConnection.prepareCall("{? = call Get_Friends(?)}");
                stem.registerOutParameter(1, Types.INTEGER);
                stem.setString(2, UID);
                stem.execute();
                set = stem.getResultSet();
                System.out.print(set.toString());
                while (set.next()) {
                    String friend;
                    friend = set.getString("FriendID");
                    NewChatAdapter.this.mContactList.add(friend);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return set;
        }

    }

    private class addMemberAndCreateTask extends AsyncTask<String, Integer, ResultSet> {
        private String UID;
        private String chatroomName;
        private String memberName;
        private ResultSet set;
        private int CID;

        private addMemberAndCreateTask(String UID, String chatroomName, String memberName) {
            this.UID = UID;
            this.chatroomName = chatroomName;
            this.memberName = memberName;
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            try {
                CallableStatement stem = NewChatAdapter.this.mDBConnection.prepareCall("{? = call UserCreateChatRoom(?, ?)}");
                stem.registerOutParameter(1, Types.INTEGER);
                stem.setString(2, UID);
                stem.setString(3, chatroomName);
                stem.execute();
                CID = stem.getInt(1);
                CallableStatement stem2 = NewChatAdapter.this.mDBConnection.prepareCall("{call AddUserChatRoom(?,?)}");
                stem2.setString(1, memberName);
                stem2.setInt(2, CID);
                stem2.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return set;
        }

    }

}


