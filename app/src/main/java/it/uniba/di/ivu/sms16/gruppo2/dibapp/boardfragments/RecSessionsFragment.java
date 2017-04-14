package it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments;


import android.app.Activity;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.SessionsBoardActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter.MySessionsAdapter;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication.AuthActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.profile.ModificaProfiloActivity;

public class RecSessionsFragment extends SessionsListFragment {

    private static final int AUTH_REQUEST_ACTIVITY = 902;
    public static final int REC_REQUEST = 101;

    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String ARG_USER_ID = "ARG_USER_ID";
    public static final String ARG_USER = "ARG_USER";

    private DatabaseReference mDatabase;
    private SessionBoardCallBack mSessionBoardCallBack;

    private MySessionsAdapter mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private ArrayList<StudySession> mSessionsList;


    private ProgressBar mProgressBar;
    private String mUserId;
    private User mUser;

    public RecSessionsFragment() {

    }

    public static RecSessionsFragment newInstance(String userId, User user) {
        RecSessionsFragment fragment = new RecSessionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, userId);
        args.putSerializable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SessionBoardCallBack) {
            mSessionBoardCallBack = (SessionBoardCallBack) context;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_all_sessions, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mSessionsList = new ArrayList<>();

        mRecycler = (RecyclerView) rootView.findViewById(R.id.sessions_list);
        mRecycler.setHasFixedSize(true);

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.sessionsProgressBar);

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USER_ID);
            mUser = (User) getArguments().getSerializable(ARG_USER);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        mManager = new LinearLayoutManager(getActivity());
        mManager.setReverseLayout(true);
        mManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(mManager);



        if (mUser != null) {
            if (mUser.followingCourses != null) {

                getRecSessions();

                if (mLastLocation != null) {
                    GeoPosition currentUserPosition = new GeoPosition(mLastLocation.getLatitude(), mLastLocation.getLongitude(), null);
                    mAdapter = new MySessionsAdapter(mSessionsList, getContext(), currentUserPosition,
                            mUserId);
                } else {
                    mAdapter = new MySessionsAdapter(mSessionsList, getContext(), null, mUserId);
                }


                mRecycler.setAdapter(mAdapter);

            } else {

                mSessionBoardCallBack.showSnackBar(getString(R.string.set_up_for_recommendations),
                        getString(R.string.set_up), new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(getActivity(), ModificaProfiloActivity.class));
                            }
                        }, Snackbar.LENGTH_INDEFINITE);

            }
        } else {
            mSessionBoardCallBack.showSnackBar(getString(R.string.login_for_recommendations),
                    getString(R.string.sign_in), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), AuthActivity.class);
                            startActivityForResult(intent, SessionsBoardActivity.AUTH_REQUEST_ACTIVITY);
                        }
                    }, Snackbar.LENGTH_INDEFINITE);
        }
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

                if (mAdapter != null) {
                    mAdapter.notifyDataSetChanged();
                }

            } else {

                mSessionBoardCallBack.showSnackBar(getString(R.string.authentication_failed), null, null, Snackbar.LENGTH_LONG);
            }
    }

    private void getRecSessions() {
        DatabaseReference sessionsRef = mDatabase.child("studySessions");

        sessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(View.INVISIBLE);

                StudySession currentSession;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    currentSession = ds.getValue(StudySession.class);

                    for (String course :mUser.followingCourses) {
                        if (currentSession.course.equals(course)) {
                            currentSession.id = ds.getKey();
                            mSessionsList.add(currentSession);
                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}