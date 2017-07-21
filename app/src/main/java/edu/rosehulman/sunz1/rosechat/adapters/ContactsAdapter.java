package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
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

    public ContactsAdapter(Context context, ContactsFragment.Callback callback){
        mCallback = callback;
        mContext = context;
        mContactList = new ArrayList<Contact>();
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
}
