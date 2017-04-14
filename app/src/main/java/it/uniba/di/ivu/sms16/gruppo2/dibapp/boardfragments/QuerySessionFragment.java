package it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.SessionsBoardActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter.MySessionsAdapter;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.DividerItemDecoration;

/**
 * Created by antoniolategano on 12/07/16.
 */
public class QuerySessionFragment  extends Fragment {

    private static final String TAG = "QuerySessionFragment";

    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;

    private List<StudySession> mStudySessionList = new ArrayList<>();

    private StudySession mStudySession;
    private MySessionsAdapter mySessionsAdapter;
    private String mQuery;
    private ProgressBar mProgressBar;


    public QuerySessionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_all_sessions, container, false);

        mQuery = getArguments().getString(SessionsBoardActivity.QUERY);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) rootView.findViewById(R.id.sessions_list);
        //mPlaceholder = (TextView) rootView.findViewById(R.id.sessions_placeholder);
        mRecycler.setHasFixedSize(true);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.sessionsProgressBar);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //showProgressDialog();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity()));

        Query sessionsQuery = getQuery(mDatabase);

        sessionsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mProgressBar.setVisibility(View.INVISIBLE);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    mStudySession = ds.getValue(StudySession.class);
                    mStudySession.id = ds.getKey();

                    if (mStudySession.title.toLowerCase().contains(mQuery.toLowerCase()) ||
                            mStudySession.course.toLowerCase().contains(mQuery.toLowerCase()) ||
                            mStudySession.degreeCourse.toLowerCase().contains(mQuery.toLowerCase())) {
                        mStudySessionList.add(mStudySession);
                    }
                }
                mySessionsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        mySessionsAdapter = new MySessionsAdapter(mStudySessionList, getActivity().getBaseContext(), null, getUid());

        mRecycler.setAdapter(mySessionsAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mySessionsAdapter != null) {
            //mySessionsAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child("studySessions");
    }
}
