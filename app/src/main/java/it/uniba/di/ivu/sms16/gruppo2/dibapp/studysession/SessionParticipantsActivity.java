package it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.profile.ProfiloUtenteNonPersonaleActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.FollowingFollowerViewHolder;


public class SessionParticipantsActivity extends BaseActivity {

    private FirebaseApp app;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;

    private String mUserId;
    private String mSessionId;
    private FirebaseRecyclerAdapter<User, FollowingFollowerViewHolder> mFirebaseAdapter;
    private ProgressBar mProgressBar;
    private LinearLayoutManager mLinearLayoutManager;
    private RecyclerView mParticipantsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_participants);

        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.session_participants_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.participants_title);

        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);

        mUserId = mCurrentUid;
        mSessionId = extras.getString(SessionDetailsActivity.EXTRA_SESSION_ID);

        mProgressBar = (ProgressBar) findViewById(R.id.session_participants_progress);
        mParticipantsRecyclerView = (RecyclerView) findViewById(R.id.session_participants_recycler);
        mLinearLayoutManager = new LinearLayoutManager(getBaseContext());
        mParticipantsRecyclerView.setLayoutManager(mLinearLayoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        databaseRef = database.getReference("sessionParticipants/" + mSessionId);
        final DatabaseReference followingRef = database.getReference("following/" + mUserId);

        mFirebaseAdapter = new FirebaseRecyclerAdapter<User, FollowingFollowerViewHolder>(
                User.class,
                R.layout.listitem_follower_following,
                FollowingFollowerViewHolder.class,
                databaseRef) {

            @Override
            public DatabaseReference getRef(int position) {
                return super.getRef(position);
            }

            @Override
            protected void populateViewHolder(
                    final FollowingFollowerViewHolder viewHolder,
                    final User model,
                    final int position) {

                mProgressBar.setVisibility(ProgressBar.INVISIBLE);


                followingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (mUserId != null) {
                            if (getRef(position).getKey().equals(mUserId)) {
                                viewHolder.bindToUser(model, false, getRef(position).getKey(), mCurrentUid, model);
                                return;
                            }

                            if (dataSnapshot.hasChild(getRef(position).getKey())) {
                                viewHolder.bindToUser(model, true, getRef(position).getKey(), mCurrentUid, model);
                            } else {
                                viewHolder.bindToUser(model, false, getRef(position).getKey(), mCurrentUid, model);
                            }

                            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getBaseContext(), ProfiloUtenteNonPersonaleActivity.class);
                                    intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_PROFILE_KEY, getRef(position).getKey());
                                    startActivity(intent);
                                }
                            });
                        } else {
                            viewHolder.bindToUser(model, false, getRef(position).getKey(), null, null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };





        /*mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                int MessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (MessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mParticipantsRecyclerView.scrollToPosition(positionStart);
                }
            }
        });*/

        mParticipantsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mParticipantsRecyclerView.setAdapter(mFirebaseAdapter);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);

            case android.R.id.home:
                finish();
                return true;
        }
    }
}
