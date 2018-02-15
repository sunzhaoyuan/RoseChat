package edu.rosehulman.sunz1.rosechat.adapters;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.fragments.ContactsFragment;
import edu.rosehulman.sunz1.rosechat.fragments.ProfileFragment;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * Created by agarwaa on 10-Jul-17.
 */

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    private Context mContext;
    ArrayList<String> mContactList;
    ContactsFragment.Callback mCallback;
    final private String DEBUG_KEY = "Debug";
    private String UID;
    private Connection mDBConnection;

    public ContactsAdapter(Context context, ContactsFragment.Callback callback) {
        mCallback = callback;
        mContext = context;
        mContactList = new ArrayList<String>();
        UID = SharedPreferencesUtils.getCurrentUser(context);
        mDBConnection = DatabaseConnectionService.getInstance().getConnection();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!Thread.interrupted()) {
                    new GetFriendsTask().execute(UID);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
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
    public int getItemCount() {
        return mContactList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView mContactName;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
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
        FragmentTransaction transaction = ((AppCompatActivity) mContext).getSupportFragmentManager().beginTransaction();
        Fragment profileFragment = ProfileFragment.newInstance(currentUID);
        transaction.addToBackStack("view_profile");
        transaction.replace(R.id.container, profileFragment);
        transaction.commit();
    }

    private void deleteContact(int adapterPosition) {

    }

    private class GetFriendsTask extends AsyncTask<String, String, Integer> {

        @Override
        protected Integer doInBackground(String... strings) {
            try {
                CallableStatement cs = ContactsAdapter.this.mDBConnection.prepareCall("{call Get_Friends(?)}");
                cs.setString(1, UID);
                cs.execute();
                ResultSet rs = cs.getResultSet();
                mContactList.clear();
                while (rs.next()) {
                    String s = rs.getString(1);
                    ContactsAdapter.this.mContactList.add(s);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            notifyDataSetChanged();
        }
    }

}
