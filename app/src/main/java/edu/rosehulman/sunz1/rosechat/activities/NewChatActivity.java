package edu.rosehulman.sunz1.rosechat.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
        recyclerView.setAdapter(mAdapter);

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
