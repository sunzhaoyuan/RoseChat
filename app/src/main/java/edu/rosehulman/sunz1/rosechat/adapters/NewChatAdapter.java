package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<String> mContactList;
    ArrayList<String> mSelectedContactsList;
    //For SQL
    private Connection mDBConnection;
    private String UID;

    public NewChatAdapter(Context context) {
        mDBConnection = DatabaseConnectionService.getInstance().getConnection();
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
                    CheckedTextView checkedTextView = (CheckedTextView) view.findViewById(R.id.newChat_name);
                    checkedTextView.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);
                    if (Constants.FONT_FAMILY == 0) {
                        checkedTextView.setTypeface(Typeface.DEFAULT);
                    } else {
                        checkedTextView.setTypeface(Typeface.MONOSPACE);
                    }
                    if(checkedTextView.isChecked()){
                        checkedTextView.setChecked(false);
                        checkedTextView.setCheckMarkDrawable(null);
                        FriendID = ((TextView) view.findViewById(R.id.newChat_name)).getText().toString();
                        mSelectedContactsList.remove(FriendID);
                    }else{
                        checkedTextView.setChecked(true);
                        checkedTextView.setCheckMarkDrawable(R.mipmap.ic_check_box);
                        FriendID = ((TextView) view.findViewById(R.id.newChat_name)).getText().toString();
                        mSelectedContactsList.add(FriendID);
                    }


                }
            });

            mContactName = (TextView) itemView.findViewById(R.id.newChat_name);
        }
    }


    public ArrayList<String> getNameList(){
        return  mSelectedContactsList;
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


}


