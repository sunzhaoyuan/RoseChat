package edu.rosehulman.sunz1.rosechat.adapters;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.models.Contact;

/**
 * Created by agarwaa on 21-Jul-17.
 */

public class NewChatAdapter extends RecyclerView.Adapter<NewChatAdapter.ViewHolder>{
    private Context mContext;
    ArrayList<Contact> mContactList;
    ArrayList<Contact> mSelectedContactsList;
    ContactsFragment.Callback mCallback;

    public NewChatAdapter(Context context, ContactsFragment.Callback callback){
        mCallback = callback;
        mContext = context;
        mContactList = new ArrayList<Contact>();
        mSelectedContactsList = new ArrayList<>();
        Contact temp = new Contact("Temp", "pictureURL");
        addContact(temp);
    }

    private void addContact(Contact contact) {
        mContactList.add(0, contact);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_view, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NewChatAdapter.ViewHolder holder, int position) {
        Contact contact = mContactList.get(position);
        holder.mContactName.setText(contact.getName());
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
                }
            });

            mContactName = (TextView) itemView.findViewById(R.id.newChat_name);
            mSelected = (CheckBox) itemView.findViewById(R.id.newChat_checkBox);
        }
    }

    private void select(int adapterPosition) {
        Contact subject = mContactList.get(adapterPosition);
        if(mSelectedContactsList.contains(subject)){
            mSelectedContactsList.remove(subject);
        }else{
            mSelectedContactsList.add(subject);
        }

    }


}
