package it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments;

import android.app.ProgressDialog;
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
import it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter.MyNoteAdapter;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.DividerItemDecoration;

/**
 * Created by antoniolategano on 05/07/16.
 */
public class QueryNotesFragment extends Fragment {

    private static final String TAG = "QueryNotesFragment";

    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;

    private List<Note> noteList = new ArrayList<>();

    private Note note;
    private MyNoteAdapter myNoteAdapter;
    private String query;
    private ProgressBar mProgressBar;


    public QueryNotesFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.notes_fragment, container, false);

        query = getArguments().getString("query");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mRecycler.setHasFixedSize(true);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.notesProgressBar);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecycler.setLayoutManager(linearLayoutManager);
        mRecycler.addItemDecoration(new DividerItemDecoration(getActivity()));

        Query notesQuery = mDatabase.child("notes");

        notesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(View.INVISIBLE);

                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    note = ds.getValue(Note.class);
                    note.id = ds.getKey();
                    if (note.title.toLowerCase().contains(query.toLowerCase()) || note.course.toLowerCase().contains(query.toLowerCase())) {
                        noteList.add(note);
                    }
                }

                myNoteAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });

        myNoteAdapter = new MyNoteAdapter(noteList, getContext());

        mRecycler.setAdapter(myNoteAdapter);
    }
}
