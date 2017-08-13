package edu.rosehulman.sunz1.rosechat.adapters;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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
import edu.rosehulman.sunz1.rosechat.activities.NewChatActivity;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.ViewHolder>{
    private Context mContext;
    ArrayList<String> mContactList;
    ArrayList<String> mSelectedContactsList;
    NewChatActivity.Callback mCallback;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    DatabaseReference mFriendsRef;

    public NewChatAdapter(Context context, NewChatActivity.Callback callback){

        mCallback = callback;
        mContext = context;
        mContactList = new ArrayList<String>();
        mSelectedContactsList = new ArrayList<>();
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friends/"+user.getUid());
        mFriendsRef.addChildEventListener(new FriendsChildEventListener());
    }

    private void addContact(String contact) {
        mContactList.add(0, contact);
    }

    public ArrayList<String> selectedContacts(){return mSelectedContactsList;}

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
    public int getItemCount() {return mContactList.size();}


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mContactName;
        CheckBox mSelected;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(getAdapterPosition());
                    if(mSelected.isChecked()){
                        mSelected.setChecked(false);
                    }else{
                        mSelected.setChecked(true);
                    }
                }
            });

            mContactName = (TextView) itemView.findViewById(R.id.newChat_name);
            mSelected = (CheckBox) itemView.findViewById(R.id.newChat_checkBox);
        }
    }

    private void select(int adapterPosition) {
        String subject = mContactList.get(adapterPosition);
        if(mSelectedContactsList.contains(subject)){
            mSelectedContactsList.remove(subject);
        }else{
            mSelectedContactsList.add(subject);
        }
        notifyDataSetChanged();

    }


    private class FriendsChildEventListener implements ChildEventListener {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            if(dataSnapshot.getValue().equals(true)) {
                String contactName = dataSnapshot.getKey();
                mContactList.add(0, contactName);
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
