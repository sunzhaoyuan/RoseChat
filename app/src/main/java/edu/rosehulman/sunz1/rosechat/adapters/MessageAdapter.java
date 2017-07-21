package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.fragments.MessageFragment;
import edu.rosehulman.sunz1.rosechat.models.Message;

/**
 * Created by agarwaa on 10-Jul-17.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<Message> mMessageList;
    MessageFragment.Callback mCallback;

    public MessageAdapter(Context context, MessageFragment.Callback callback){
        mCallback = callback;
        mContext = context;
        mMessageList = new ArrayList<Message>();
        Message temp = new Message("Temp", "LastInteractionTemp", "pictureURL");
        addChat(temp);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessageAdapter.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);
        holder.mNameTextView.setText(message.getName());
        holder.mLastInteraction.setText(message.getLastMessage());
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView mNameTextView;
        private TextView mLastInteraction;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v){
                    messageOptions(getAdapterPosition());
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener(){
                public void onClick(View v){
                    enterChat(getAdapterPosition());
                }
            });

            mNameTextView = (TextView) itemView.findViewById(R.id.message_name);
            mLastInteraction = (TextView) itemView.findViewById(R.id.message_last_interaction);
        }


    }

    public void addChat(Message message){
        //TODO: Adds names from firebase
        mMessageList.add(0, message);
        notifyItemInserted(0);
        //   notifyDataSetChanged();
    }

    public void removeChat(int position){
        //TODO: Removes name from firebase
        mMessageList.remove(position);
        notifyItemRemoved(position);
    }
    private void enterChat(int adapterPosition) {
        //TODO: link ChatFragment here
    }

    private void messageOptions(int adapterPosition) {
    }


}
