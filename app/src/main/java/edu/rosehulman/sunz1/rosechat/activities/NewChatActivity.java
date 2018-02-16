package edu.rosehulman.sunz1.rosechat.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.SQLService.DatabaseConnectionService;
import edu.rosehulman.sunz1.rosechat.adapters.NewChatAdapter;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.Constants;
import edu.rosehulman.sunz1.rosechat.utils.SharedPreferencesUtils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 */
public class NewChatActivity extends AppCompatActivity implements View.OnClickListener {

    private NewChatAdapter mAdapter;
    private Button button;
    private RecyclerView mRecyclerView;
    final private String DEBUG_KEY = "Debug";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public NewChatActivity() {
        // Required empty public constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);
        button = (Button) findViewById(R.id.newChat_confirm_button);
        button.setTextSize(20*(float) Constants.FONT_SIZE_FACTOR);
        button.setOnClickListener(this);
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.newChat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAdapter = new NewChatAdapter(this);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    private void selectFriendDialog() {
        FragmentManager manager = this.getFragmentManager();
        DialogFragment df = new DialogFragment() {
            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                Log.d("chatroom", "create chatroom");
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view = getActivity().getLayoutInflater().inflate(R.layout.dialogfragment_newchat, null);
                builder.setView(view);
                final EditText editText = (EditText) view.findViewById(R.id.dialogfragment_newchat_editext);
                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String UID  = SharedPreferencesUtils.getCurrentUser(getApplicationContext());
                        String ChatroomName = editText.getText().toString();
                        ArrayList<String> nameList = mAdapter.getNameList();
                        new addMemberAndCreateTask(UID,ChatroomName,nameList).execute();
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
        };
        df.show(manager, "fontSize");
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.newChat_confirm_button){
            selectFriendDialog();
        }
    }

    private class addMemberAndCreateTask extends AsyncTask<String, Integer, ResultSet> {
        private String UID;
        private String chatroomName;
        private ArrayList<String> memberName;
        private ResultSet set;
        private int CID;

        private addMemberAndCreateTask(String UID, String chatroomName, ArrayList<String> memberName) {
            this.UID = UID;
            this.chatroomName = chatroomName;
            this.memberName = memberName;
        }

        @Override
        protected ResultSet doInBackground(String... strings) {
            try {
                CallableStatement stem = DatabaseConnectionService.getInstance().getConnection().prepareCall("{? = call UserCreateChatRoom(?, ?)}");
                stem.registerOutParameter(1, Types.INTEGER);
                stem.setString(2, UID);
                stem.setString(3, chatroomName);
                stem.execute();
                CID = stem.getInt(1);
                for(int i = 0; i < memberName.size(); i++) {
                    CallableStatement stem2 = DatabaseConnectionService.getInstance().getConnection().prepareCall("{call AddUserChatRoom(?,?)}");
                    stem2.setString(1,memberName.get(i));
                    stem2.setInt(2, CID);
                    stem2.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return set;
        }

    }
}
