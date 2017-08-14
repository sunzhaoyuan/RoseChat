package edu.rosehulman.sunz1.rosechat.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.NewChatAdapter;
import edu.rosehulman.sunz1.rosechat.models.Contact;
import edu.rosehulman.sunz1.rosechat.utils.Constants;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Callback} interface
 * to handle interaction events.
 */
public class NewChatActivity extends AppCompatActivity {

    private Callback mCallback;
    private NewChatAdapter mAdapter;
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

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.newChat_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAdapter = new NewChatAdapter(this, mCallback);
        FloatingActionButton addFAB = (FloatingActionButton) findViewById(R.id.newChat_fab);
        recyclerView.setAdapter(mAdapter);

        addFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = mAdapter.selectedContacts().size();
                Log.d(DEBUG_KEY, "" + size);
                if(size == 1){
                    DatabaseReference mMessagesRef = FirebaseDatabase.getInstance().getReference().child("messages");
                    mMessagesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Log.d(DEBUG_KEY, mAdapter.selectedContacts().get(0));
                                Log.d(DEBUG_KEY, "current senderUid is : " + snapshot.child("senderUid").getValue() +
                                        "\ncurrent receiverUid is : " + snapshot.child("receiverUid").getValue());
                                if ((snapshot.child("senderUid").getValue().equals(user.getUid())
                                        || snapshot.child("receiverUid").getValue().equals(user.getUid()))
                                        && (snapshot.child("senderUid").getValue().equals(mAdapter.selectedContacts().get(0))
                                        || snapshot.child("receiverUid").getValue().equals(mAdapter.selectedContacts().get(0)))) {

                                    Toast.makeText(NewChatActivity.this, "Chat already exists", Toast.LENGTH_LONG);
                                    break;
                                }else{
                                    Toast.makeText(NewChatActivity.this, "Chat created", Toast.LENGTH_LONG);
                                    //TODO What will happen if the chat does not exist yet
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    finish();
                }else if(size == 0){
                    Toast.makeText(NewChatActivity.this ,"Please choose at least one contact", Toast.LENGTH_LONG ).show();
                }else{
                    Toast.makeText(NewChatActivity.this ,"Group chat not supported right now", Toast.LENGTH_LONG ).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public interface Callback {
        // TODO: Update argument type and name
        void onNewChatSelected(Contact contact);
    }
}
