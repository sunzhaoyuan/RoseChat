package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
import edu.rosehulman.sunz1.rosechat.activities.LogInActivity;
import edu.rosehulman.sunz1.rosechat.activities.SettingsActivity;
import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.MessageFragment;
import edu.rosehulman.sunz1.rosechat.models.Chat;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.models.Message;

/**
 * Created by agarwaa on 10-Jul-17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder>{
    private Context mContext;
    ArrayList<Contact> mContactList;
    ContactsFragment.Callback mCallback;
    private DatabaseReference mContactRef;
    DatabaseReference mUserRef;
    FirebaseAuth mAuth;
    FirebaseUser user;


    public ContactsAdapter(Context context, ContactsFragment.Callback callback){
        mCallback = callback;
        mContext = context;
        mContactList = new ArrayList<Contact>();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mContactRef = FirebaseDatabase.getInstance().getReference().child("contacts");
        mUserRef = mContactRef.orderByChild("uid").equalTo(user.getUid()).getRef();
        mContactRef.addChildEventListener(new ContactsChildEventListener());
        Contact temp = new Contact("Temp", "pictureURL");
        addContact(temp);
    }


    private void addContact(Contact contact) {
        mContactList.add(0, contact);
        notifyItemInserted(0);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_view, parent, false);
            return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ContactsAdapter.ViewHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.mContactName.setText(contact.getName());
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
    }

    private void deleteContact(int adapterPosition) {
        mContactList.remove(adapterPosition);
        notifyItemRemoved(adapterPosition);
    }

    private class ContactsChildEventListener implements ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {

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
