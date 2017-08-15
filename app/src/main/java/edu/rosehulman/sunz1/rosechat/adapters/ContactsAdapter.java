package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.ProfileFragment;

/**
 * Created by agarwaa on 10-Jul-17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{
    private Context mContext;
    ArrayList<String> mContactList;
    ContactsFragment.Callback mCallback;
    private DatabaseReference mFriendsRef;
    FirebaseAuth mAuth;
    FirebaseUser user;
    final private String DEBUG_KEY = "Debug";



    public ContactsAdapter(Context context, ContactsFragment.Callback callback){
        mCallback = callback;
        mContext = context;
        mContactList = new ArrayList<String>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friends/" + user.getUid());
        mFriendsRef.addChildEventListener(new ContactsChildEventListener());
        Log.d(DEBUG_KEY, "laaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        Log.d(DEBUG_KEY, user.getUid());
        addContact("temp");
    }


    private void addContact(String contact) {
        mFriendsRef.child(contact).setValue(true);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_view, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
        String contact = mContactList.get(position);
        holder.mContactName.setText(contact);
    }

    @Override
    public int getItemCount() {return mContactList.size();}


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mContactName;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    deleteContact(getAdapterPosition());
                    return false;
                }
            });
            
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterProfile(getAdapterPosition());
                }
            });

            mContactName = (TextView) itemView.findViewById(R.id.contact_name);
            
        }
    }

    private void enterProfile(int adapterPosition) {
        //TODO:
        String currentUID = mContactList.get(adapterPosition);
        FragmentTransaction transaction = ((AppCompatActivity)mContext).getSupportFragmentManager().beginTransaction();
        Fragment profileFragment = ProfileFragment.newInstance(currentUID);
        transaction.addToBackStack("view_profile");
        transaction.replace(R.id.container, profileFragment);
        transaction.commit();
    }

    private void deleteContact(int adapterPosition) {
        Log.d(DEBUG_KEY, "user selected" + mFriendsRef.child(mContactList.get(adapterPosition)).getKey());
        mFriendsRef.child(mContactList.get(adapterPosition)).setValue(false);
    }

    private class ContactsChildEventListener implements ChildEventListener {

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
            if(dataSnapshot.getValue().equals(true)) {
                String contactName = dataSnapshot.getKey();
                mContactList.add(0, contactName);
                notifyDataSetChanged();
            }else{
                mContactList.remove(dataSnapshot.getKey());
                notifyDataSetChanged();
            }
            DatabaseReference mOtherFriendRef = FirebaseDatabase.getInstance().getReference().child("friends/" + dataSnapshot.getKey());
            mOtherFriendRef.child(user.getUid()).setValue(false);
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            mContactList.remove(dataSnapshot.getKey());
            notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    }
}
