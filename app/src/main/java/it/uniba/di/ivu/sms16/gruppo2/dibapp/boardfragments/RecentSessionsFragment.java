package it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.SessionsBoardActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication.AuthActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession.SessionActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession.SessionDetailsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.SessionViewHolder;

public class RecentSessionsFragment extends SessionsListFragment {

    private static final int AUTH_REQUEST_ACTIVITY = 902;

    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String ARG_USER_ID = "ARG_USER_ID";

    private DatabaseReference mDatabase;

    private FirebaseRecyclerAdapter<StudySession, SessionViewHolder> mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private SessionBoardCallBack mSessionBoardCallBack;


    private String mUserId;

    private ProgressBar progressBar;

    public RecentSessionsFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SessionBoardCallBack) {
            mSessionBoardCallBack = (SessionBoardCallBack) context;
        }

    }

    public static RecentSessionsFragment newInstance(String userId) {
        RecentSessionsFragment fragment = new RecentSessionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_sessions, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.sessions_list);
        mRecycler.setHasFixedSize(true);

        progressBar = (ProgressBar) rootView.findViewById(R.id.sessionsProgressBar);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USER_ID);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);


        DatabaseReference sessionsRef = mDatabase.child("studySessions");
        mAdapter = new FirebaseRecyclerAdapter<StudySession, SessionViewHolder>(StudySession.class, R.layout.list_item_session,
                SessionViewHolder.class, sessionsRef) {

            @Override
            protected void populateViewHolder(final SessionViewHolder viewHolder, final StudySession model, final int position) {
                progressBar.setVisibility(View.INVISIBLE);

                final DatabaseReference sessionsRef = getRef(position);
                final LatLng userPosition;

                if (mLastLocation != null) {
                    userPosition = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                } else {
                    userPosition = null;
                }

                final View.OnClickListener viewOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SessionActivity.class);
                        intent.putExtra(EXTRA_SESSION_ID, getRef(position).getKey());
                        startActivity(intent);
                    }
                };

                final View.OnClickListener participateOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        joinSession(model, getRef(position).getKey(), mUserId);
                        viewHolder.bindToSession(model, userPosition, true, viewOnClickListener);
                    }
                };

                final View.OnClickListener authOnClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        startActivityForResult(intent, SessionsBoardActivity.AUTH_REQUEST_ACTIVITY);
                    }
                };


                if (mUserId != null) {
                    DatabaseReference sessionParticipantsRef =
                            FirebaseDatabase.getInstance().getReference("sessionParticipants").child(sessionsRef.getKey());

                    sessionParticipantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                if (ds.getKey().equals(mUserId)) {
                                    viewHolder.bindToSession(model, userPosition, true, viewOnClickListener);

                                    return;
                                }
                            }
                            viewHolder.bindToSession(model, userPosition, false, participateOnClickListener);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                    });

                } else {
                    viewHolder.bindToSession(model, userPosition, false, authOnClickListener);
                }

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SessionDetailsActivity.class);
                        intent.putExtra(SessionDetailsActivity.EXTRA_SESSION_ID, sessionsRef.getKey());
                        intent.putExtra(SessionDetailsActivity.EXTRA_NOT_FROM_SESSION, true);
                        startActivity(intent);
                    }
                });
            }
        };

        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mSessionBoardCallBack = null;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTH_REQUEST_ACTIVITY)
            if (resultCode == Activity.RESULT_OK) {

                mSessionBoardCallBack.showSnackBar(getString(R.string.authentication_success), null, null, Snackbar.LENGTH_LONG);

                mAdapter.notifyDataSetChanged();

            } else {

                mSessionBoardCallBack.showSnackBar(getString(R.string.authentication_failed), null, null, Snackbar.LENGTH_LONG);
            }
    }
}