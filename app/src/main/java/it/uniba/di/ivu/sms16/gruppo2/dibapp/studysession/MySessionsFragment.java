package it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.model.LatLng;
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
import it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter.MyNoteAdapter;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter.MySessionsAdapter;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.ComplexObject;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.note.MyNotesActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.DividerItemDecoration;


/**
 * Created by antoniolategano on 10/07/16.
 */
public class MySessionsFragment extends Fragment {

    private static final String TAG = "MySessionFragment";
    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;

    OnDataPass dataPass;

    private List<StudySession> sessionsList = new ArrayList<>();

    private StudySession studySession;
    private MySessionsAdapter myAdapter;
    //private TextView mPlaceholder;
    private ProgressBar mProgressBar;

    public MySessionsFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPass = (OnDataPass) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_all_sessions, container, false);

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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(linearLayoutManager);

        if (getArguments() == null) {

            getQuery(mDatabase).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    DatabaseReference sessionsRef = mDatabase.child("studySessions");
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        for (DataSnapshot d : ds.getChildren()) {
                            if (d.getKey().equals(getUid())) {

                                sessionsRef.child(ds.getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        studySession = dataSnapshot.getValue(StudySession.class);
                                        studySession.id = dataSnapshot.getKey();
                                        sessionsList.add(studySession);
                                        myAdapter.notifyDataSetChanged();
                                        mProgressBar.setVisibility(View.INVISIBLE);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                        }
                    }

                    passData(sessionsList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });

        } else if (getArguments().getString(MySessionsActivity.TYPE).equals("search_my_sessions")) {

            String q = getArguments().getString(MySessionsActivity.QUERY).toLowerCase();
            ComplexObject<StudySession> object = (ComplexObject<StudySession>) getArguments().getSerializable(MySessionsActivity.SESSION_LIST);
            List<StudySession> sessions = object.getList();
            for (StudySession n : sessions) {
                if (n.title.toLowerCase().contains(q) || n.course.contains(q) || n.degreeCourse.contains(q)) {
                    sessionsList.add(n);
                }
            }

            /*if (sessionsList.isEmpty()) {
                mRecycler.setVisibility(View.GONE);
                //mPlaceholder.setVisibility(View.VISIBLE);
            } else {
                mRecycler.setVisibility(View.VISIBLE);
                //mPlaceholder.setVisibility(View.GONE);
            }*/
            mProgressBar.setVisibility(View.INVISIBLE);

        }

        myAdapter = new MySessionsAdapter(sessionsList, getContext(), null, getUid());
        mRecycler.setAdapter(myAdapter);
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child("sessionParticipants");
    }


    public interface OnDataPass {
        void onDataPass(List<StudySession> studySessions);
    }

    public void passData(List<StudySession> studySessions) {
        dataPass.onDataPass(studySessions);
    }
}
