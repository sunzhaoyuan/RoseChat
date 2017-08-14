package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

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

/**
 * Created by agarwaa on 14-Aug-17.
 */

public class InvitationAdapter extends RecyclerView.Adapter<InvitationAdapter.ViewHolder> {


    private Context mContext;
    private InvitationActivity.Callback mCallback;
    DatabaseReference mInvitationRef;
    ArrayList<String> mInvitationList;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public InvitationAdapter(Context context, InvitationActivity.Callback callback){

        mCallback = callback;
        mContext = context;
        mInvitationList = new ArrayList<String>();
        mInvitationRef = FirebaseDatabase.getInstance().getReference().child("invitations/"+user.getUid());
        mInvitationRef.addChildEventListener(new InvitationChildEventListener());
    }

    @Override
    public InvitationAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(InvitationAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder{


        public ViewHolder(View itemView) {
            super(itemView);

        }
    }

    private class InvitationChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if(dataSnapshot.getValue().equals(true)) {
                String mInvitation = dataSnapshot.getKey();
                mInvitationList.add(0, mInvitation);
                notifyDataSetChanged();
            }
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
