package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.activities.InvitationActivity;
import edu.rosehulman.sunz1.rosechat.models.Invitation;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by agarwaa on 14-Aug-17.
 */

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {


    private Context mContext;
    private InvitationActivity.Callback mCallback;
    ArrayList<Invitation> mInvitationList;
    final private String DEBUG_KEY = "Debug";

    private Connection mDBConnection;
    private String UID;
    private String inviterID = null;

    public InvitationAdapter(Context context, InvitationActivity.Callback callback){

        mCallback = callback;
        mContext = context;
        mInvitationList = new ArrayList<Invitation>();
        UID = SharedPreferencesUtils.getCurrentUser(context);
        mDBConnection = DatabaseConnectionService.getInstance().getConnection();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.interrupted()){
                    new GetInvitationTask().execute();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public InvitationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.invitation_view, parent, false);
        return new ViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(InvitationAdapter.ViewHolder holder, int position) {
        Invitation invite = mInvitationList.get(position);
        holder.mInviteName.setText(invite.getmName());
        holder.mInviteMessage.setText(invite.getmMessage());
        holder.mConfirmInvite.setVisibility(View.VISIBLE);
        holder.mDeclineInvite.setVisibility(View.VISIBLE);
    }

    @Override
    public int getItemCount() {return mInvitationList.size();}


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mInviteName;
        TextView mInviteMessage;
        TextView mInvitePending;
        Button mConfirmInvite;
        Button mDeclineInvite;

        public ViewHolder(View itemView) {
            super(itemView);
            mInviteMessage = (TextView) itemView.findViewById(R.id.invitation_message);
            mInviteName = (TextView) itemView.findViewById(R.id.invitation_name);
            mInvitePending = (TextView) itemView.findViewById(R.id.invitation_pending);
            mConfirmInvite = (Button) itemView.findViewById(R.id.invitation_confirm);
            mDeclineInvite = (Button) itemView.findViewById(R.id.invitation_decline);

            //set font size
            mInviteMessage.setTextSize(25*(float) Constants.FONT_SIZE_FACTOR);
            mInviteName.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);
            mInvitePending.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);
            mConfirmInvite.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);
            mDeclineInvite.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);

            //set font family
            if (Constants.FONT_FAMILY == 0) {
                mInviteMessage.setTypeface(Typeface.DEFAULT);
                mInviteName.setTypeface(Typeface.DEFAULT);
                mInvitePending.setTypeface(Typeface.DEFAULT);
                mConfirmInvite.setTypeface(Typeface.DEFAULT);
                mDeclineInvite.setTypeface(Typeface.DEFAULT);
            } else {
                mInviteMessage.setTypeface(Typeface.MONOSPACE);
                mInviteName.setTypeface(Typeface.MONOSPACE);
                mInvitePending.setTypeface(Typeface.MONOSPACE);
                mConfirmInvite.setTypeface(Typeface.MONOSPACE);
                mDeclineInvite.setTypeface(Typeface.MONOSPACE);
            }

            mConfirmInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Invitation confirmInvite = mInvitationList.get(getAdapterPosition());
                    inviterID = confirmInvite.getmName();
                    new HandleInvitationTask().execute(new Boolean(true));

                }
            });

            mDeclineInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Invitation confirmInvite = mInvitationList.get(getAdapterPosition());
                    inviterID = confirmInvite.getmName();
                    new HandleInvitationTask().execute(new Boolean(false));
                }
            });
        }
    }

    private class GetInvitationTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            try {
                CallableStatement cs = mDBConnection.prepareCall("{call Get_Invites(?)}");
                cs.setString(1, UID);
                cs.execute();
                ResultSet rs = cs.getResultSet();
                mInvitationList.clear();
                while (rs.next()) {
                    String s = rs.getString(1);
                    String msg = rs.getString(2 );
                    Invitation invite = new Invitation();
                    invite.setmName(s);
                    invite.setmMessage(msg);
                    mInvitationList.add(invite);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            notifyDataSetChanged();
        }
    }

    private class HandleInvitationTask extends AsyncTask<Boolean, String, String>{

        @Override
        protected String doInBackground(Boolean... booleans) {
            boolean isAccepted = booleans[0].booleanValue();
            CallableStatement cs=null;
            try {
                if(isAccepted){
                    cs=mDBConnection.prepareCall("{call Friend_Accept(?, ?)}");
                    cs.setString(1, inviterID);
                    cs.setString(2, UID);
                    cs.execute();
                }else{
                    cs=mDBConnection.prepareCall("{call FriendRequest_Decline(?, ?)}");
                    cs.setString(1, inviterID);
                    cs.setString(2, UID);
                    cs.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            inviterID = null;
            notifyDataSetChanged();
        }
    }

}
