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

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.NewChatAdapter;
import edu.rosehulman.sunz1.rosechat.models.Contact;

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
                if(size > 0){
                    finish();
                }else{
                    Toast.makeText(NewChatActivity.this ,"Please choose at least one contact", Toast.LENGTH_LONG ).show();
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
