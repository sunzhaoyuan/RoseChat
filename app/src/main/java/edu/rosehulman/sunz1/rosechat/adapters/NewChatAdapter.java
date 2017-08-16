package edu.rosehulman.sunz1.rosechat.adapters;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.activities.NewChatActivity;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.models.Message;
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
    final private String DEBUG_KEY = "NewChatDebug ";

    public NewChatAdapter(Context context, NewChatActivity.Callback callback){

        mCallback = callback;
        mContext = context;
        mContactList = new ArrayList<String>();
        mFriendsRef = FirebaseDatabase.getInstance().getReference().child("friends/"+user.getUid());
        mFriendsRef.addChildEventListener(new FriendsChildEventListener());
    }

    private void addContact(String contact) {
        mSelectedContactsList.add(0, contact);
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
    public int getItemCount() {return mContactList.size();}


    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView mContactName;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String contactToMessage = mContactList.get(getAdapterPosition());
                    Log.d(DEBUG_KEY, contactToMessage);

                    final DatabaseReference mMessagesRef = FirebaseDatabase.getInstance().getReference().child("messages");
                    mMessagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            boolean containsChat = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d(DEBUG_KEY, contactToMessage);
                                Log.d(DEBUG_KEY, "current senderUid is : " + snapshot.child("senderUID").getValue() +
                                        "\ncurrent receiverUid is : " + snapshot.child("receiverUID").getValue());
                                if(snapshot.child("senderUID").getValue().equals(user.getUid())){
                                    if(snapshot.child("receiverUID").getValue().equals(contactToMessage)){
                                        Toast.makeText(mContext, "Chat already exists", Toast.LENGTH_LONG).show();
                                        Log.d(DEBUG_KEY, "I am inside error SENDER UID");
                                        containsChat = true;
                                        break;
                                    }
                                }else if(snapshot.child("receiverUID").getValue().equals(user.getUid())){
                                    if((snapshot.child("senderUID").getValue().equals(contactToMessage))){
                                        Log.d(DEBUG_KEY, "I am inside error RECEOVER UID");
                                        Toast.makeText(mContext, "Chat already exists", Toast.LENGTH_LONG).show();
                                        containsChat = true;
                                        break;
                                    }
                                }
                            }
                            if(!containsChat){
                                Toast.makeText(mContext, "Chat created", Toast.LENGTH_LONG).show();
                                //TODO What will happen if the chat does not exist yet
                                DatabaseReference mFriendRef = FirebaseDatabase.getInstance().getReference().child("contacts/" + contactToMessage);
                                Message message = new Message(contactToMessage ,"Start chatting now!", mFriendRef.child("profilePicUrl").toString(),user.getUid(), contactToMessage);
                                mMessagesRef.push().setValue(message);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            });

            mContactName = (TextView) itemView.findViewById(R.id.newChat_name);
        }
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
