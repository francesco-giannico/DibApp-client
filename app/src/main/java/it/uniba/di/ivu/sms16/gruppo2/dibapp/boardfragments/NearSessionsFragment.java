package it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter.MySessionsAdapter;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;

public class NearSessionsFragment extends SessionsListFragment {

    private static final int AUTH_REQUEST_ACTIVITY = 902;

    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String ARG_USER_ID = "ARG_USER_ID";

    private DatabaseReference mDatabase;

    private MySessionsAdapter mAdapter;
    private RecyclerView mRecycler;
    private LinearLayoutManager mManager;
    private SessionBoardCallBack mSessionBoardCallBack;
    private ArrayList<StudySession> mSessionsList;

    private String mUserId;
    private AsyncTask<Void, Void, Void> mAsyncPositionCheck;

    private ProgressBar mProgressBar;

    public NearSessionsFragment() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof SessionBoardCallBack) {
            mSessionBoardCallBack = (SessionBoardCallBack) context;
        }

    }

    public static NearSessionsFragment newInstance(String userId) {
        NearSessionsFragment fragment = new NearSessionsFragment();
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

        mProgressBar = (ProgressBar) rootView.findViewById(R.id.sessionsProgressBar);

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
        mSessionsList = new ArrayList<>();

        mAsyncPositionCheck = new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... params) {
                while (mLastLocation == null) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                getNearSessions();

                mAdapter = new MySessionsAdapter(mSessionsList, getContext(),
                        new GeoPosition(mLastLocation.getLatitude(), mLastLocation.getLongitude(), null),
                        mUserId);

                mRecycler.setAdapter(mAdapter);
            }
        };

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);


        } else {

            mAsyncPositionCheck.execute();

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

                mAdapter.notifyDataSetChanged();

            } else {

                mSessionBoardCallBack.showSnackBar(getString(R.string.authentication_failed), null, null, Snackbar.LENGTH_LONG);
            }
    }



    private void getNearSessions() {
        DatabaseReference sessionsRef = mDatabase.child("studySessions");

        sessionsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(View.INVISIBLE);
                StudySession currentSession;
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    currentSession = ds.getValue(StudySession.class);

                    if (SphericalUtil.computeDistanceBetween(
                            new LatLng(mLastLocation.getLatitude(),
                                    mLastLocation.getLongitude()
                            ),
                            new LatLng(
                                    Double.valueOf(currentSession.geoPosition.lat),
                                    Double.valueOf(currentSession.geoPosition.lng))) < 1000) {

                        currentSession.id = ds.getKey();
                        mSessionsList.add(currentSession);
                        mAdapter.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}