package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.activities.ChatActivity;
import edu.rosehulman.sunz1.rosechat.models.Message;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by agarwaa on 10-Jul-17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<Message> mMessageList;
    //    MessageFragment.Callback mCallback;
    private DatabaseReference mMessageRef;
    private String mCurrentUID;


    public MessageAdapter(Context context) {
//        mCallback = callback;
        mContext = context;
        mMessageList = new ArrayList<>();
        mMessageRef = FirebaseDatabase.getInstance().getReference().child(Constants.PATH_MESSAGE);
        mCurrentUID = SharedPreferencesUtils.getCurrentUser(mContext);
        getMessage();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        holder.mNameTextView.setText(message.getSenderUID().equals(mCurrentUID) ? message.getReceiverUID() : message.getSenderUID());
        holder.mLastInteraction.setText(message.getLastInteraction());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mNameTextView;
        private TextView mLastInteraction;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    messageOptions(getAdapterPosition());
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    enterChat(getAdapterPosition());
                }
            });

            mNameTextView = (TextView) itemView.findViewById(R.id.message_name);
            mLastInteraction = (TextView) itemView.findViewById(R.id.message_last_interaction);
        }


    }

    public void addChat(Message message) {
        //TODO: Adds names from firebase
        mMessageList.add(0, message);
        notifyItemInserted(0);
        //   notifyDataSetChanged();
    }

    private void getMessage() {
        mMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.d(Constants.TAG_MESSAGE, "current senderUid is : " + snapshot.child("senderUid").getValue() +
                            "\ncurrent receiverUid is : " + snapshot.child("receiverUid").getValue());
                    if (snapshot.child("senderUID").getValue().equals(mCurrentUID)
                            || snapshot.child("receiverUID").getValue().equals(mCurrentUID)) {
                        Message currentMessage = snapshot.getValue(Message.class);
                        currentMessage.setKey(snapshot.getKey());
                        Log.d(Constants.TAG_CHAT, "IN MessageAdapter onDataChange\nmessageKey: " + currentMessage.getKey());
                        addChat(currentMessage);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(mContext, R.string.could_not_get_message, Toast.LENGTH_LONG).show();
            }
        });
    }

    public void removeChat(int position) {
        //TODO: Removes name from firebase
        mMessageList.remove(position);
        notifyItemRemoved(position);
    }

    private void enterChat(int adapterPosition) {
        //TODO: link ChatFragment here
        Message currentMessage = mMessageList.get(adapterPosition);
        String messageName = currentMessage.getName();
        String messageKey = currentMessage.getKey();
        String receiversUID = currentMessage.getReceiverUID();
        String senderUID = currentMessage.getSenderUID();
        Log.d(Constants.TAG_CHAT, "IN MESSAGE_ADAPTER\nreceiverUID: " + receiversUID);
        ChatActivity.startActivity(mContext, messageName, senderUID, receiversUID, messageKey);
    }

    private void messageOptions(int adapterPosition) {
    }


}
