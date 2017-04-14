package it.uniba.di.ivu.sms16.gruppo2.dibapp.note;

import android.app.ProgressDialog;
import android.content.Context;
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
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.ComplexObject;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.DividerItemDecoration;


public class MyNotesFragment extends Fragment {

    private static final String TAG = "MyNotesFragment";

    OnDataPass dataPass;

    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;

    private List<Note> noteList = new ArrayList<>();

    private Note note;
    private MyNoteAdapter myNoteAdapter;
    private ProgressBar mProgressBar;


    public MyNotesFragment() {
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

        View rootView = inflater.inflate(R.layout.notes_fragment, container, false);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mRecycler = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.notesProgressBar);
        mRecycler.setHasFixedSize(true);

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

        if(getArguments() == null) {
            Query notesQuery = getQuery(mDatabase);

            notesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mProgressBar.setVisibility(View.INVISIBLE);

                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        note = ds.getValue(Note.class);
                        note.id = ds.getKey();
                        if (note.owner.equals(getUid())) {
                            noteList.add(note);
                        }
                    }
                    myNoteAdapter.notifyDataSetChanged();

                    passData(noteList);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });

        } else if (getArguments().getString(MyNotesActivity.TYPE).equals("search_my_notes")) {

            String q = getArguments().getString(MyNotesActivity.QUERY).toLowerCase();
            ComplexObject<Note> object = (ComplexObject<Note>) getArguments().getSerializable(MyNotesActivity.NOTE_LIST);
            List<Note> notes = object.getList();
            for (Note n : notes) {
                if (n.title.toLowerCase().contains(q) || n.course.contains(q) || n.degreeCourse.contains(q)) {
                    noteList.add(n);
                }
            }
            if (noteList.isEmpty()) {
                mRecycler.setVisibility(View.INVISIBLE);
            } else {
                mRecycler.setVisibility(View.VISIBLE);
            }
            mProgressBar.setVisibility(View.INVISIBLE);

        }

        myNoteAdapter = new MyNoteAdapter(noteList, getContext());
        mRecycler.setAdapter(myNoteAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myNoteAdapter != null) {
            //myNoteAdapter.cleanup();
        }
    }

    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    private Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child("notes");
    }

    public interface OnDataPass {
        void onDataPass(List<Note> notes);
    }

    public void passData(List<Note> notes) {
        dataPass.onDataPass(notes);
    }
}
