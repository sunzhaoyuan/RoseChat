package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.activities.InvitationActivity;
import edu.rosehulman.sunz1.rosechat.models.Invitation;

/**
 * Created by agarwaa on 14-Aug-17.
 */

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {


    private Context mContext;
    private InvitationActivity.Callback mCallback;
    DatabaseReference mInvitationRef;
    ArrayList<Invitation> mInvitationList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    final private String DEBUG_KEY = "Debug";

    public InvitationAdapter(Context context, InvitationActivity.Callback callback){

        mCallback = callback;
        mContext = context;
        mInvitationList = new ArrayList<Invitation>();
        mInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/"+user.getUid());
        mInvitationRef.addChildEventListener(new InvitationChildEventListener());
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
        if(invite.getmStatus().equals("Pending")){
            holder.mInvitePending.setVisibility(View.VISIBLE);
        }else {
            holder.mConfirmInvite.setVisibility(View.VISIBLE);
            holder.mDeclineInvite.setVisibility(View.VISIBLE);
        }
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

            mConfirmInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Invitation confirmInvite = mInvitationList.get(getAdapterPosition());
                    mInvitationRef.child(confirmInvite.getmName()).removeValue();
                    mInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/"+confirmInvite.getmName());
                    mInvitationRef.child(user.getUid()).removeValue();

                    DatabaseReference mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friends/"+user.getUid());
                    mFriendsRef.child(confirmInvite.getmName()).setValue(true);
                    mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friends/"+confirmInvite.getmName());
                    mFriendsRef.child(user.getUid()).setValue(true);

//                    mInvitationList.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    mInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/"+user.getUid());

                }
            });

            mDeclineInvite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Invitation confirmInvite = mInvitationList.get(getAdapterPosition());
                    mInvitationRef.child(confirmInvite.getmName()).removeValue();
                    mInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/"+confirmInvite.getmName());
                    mInvitationRef.child(user.getUid()).removeValue();

//                    mInvitationList.remove(getAdapterPosition());
                    notifyDataSetChanged();

                    mInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/"+user.getUid());
                }
            });

        }
    }

    private class InvitationChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    Invitation invite = new Invitation();
                    invite.setmName(dataSnapshot.getKey().toString());
                    invite.setmMessage("Message: " + dataSnapshot.child("message").getValue().toString());
                    invite.setmStatus(dataSnapshot.child("status").getValue().toString());
                    mInvitationList.add(0, invite);
                    notifyDataSetChanged();
                }
            }, 250);
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(final DataSnapshot dataSnapshot) {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Do something after 5s = 5000ms
                    String key = dataSnapshot.getKey();
                    for(Invitation mq: mInvitationList){
                        if(mq.getmName().equals(key)){
                            mInvitationList.remove(mq);
                            notifyDataSetChanged();
                            return;
                        }
                    }
                }
            }, 250);
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
