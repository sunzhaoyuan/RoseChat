package edu.rosehulman.sunz1.rosechat.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import edu.rosehulman.sunz1.rosechat.R;
import edu.rosehulman.sunz1.rosechat.adapters.InvitationAdapter;

/**
 * Created by agarwaa on 14-Aug-17.
 */

public class InvitationActivity extends AppCompatActivity {
    final private String DEBUG_KEY = "Debug";
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    InvitationAdapter mAdapter;
    private Callback mCallback;

    public InvitationActivity(){
        //Default constructor
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitation);

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.invitation_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        mAdapter = new InvitationAdapter(this, mCallback);
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
        void onInvitationSelected(String invitation);
    }
}
