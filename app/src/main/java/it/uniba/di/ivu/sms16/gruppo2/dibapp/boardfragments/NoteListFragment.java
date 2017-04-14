package it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments;

import android.app.ProgressDialog;
import android.content.Intent;
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

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.note.NoteDetailsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.DividerItemDecoration;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.NoteViewHolder;

/**
 * Created by antoniolategano on 01/07/16.
 */
public abstract class NoteListFragment extends Fragment {

    private static final String TAG = "NoteListFragment";

    private DatabaseReference mDatabase;
    private RecyclerView mRecycler;
    private FirebaseRecyclerAdapter<Note, NoteViewHolder> mAdapter;

    private ProgressBar mProgressBar;


    public NoteListFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View rootView = inflater.inflate(R.layout.notes_fragment, container, false);

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

        Query notesQuery = getQuery(mDatabase);
        mAdapter = new FirebaseRecyclerAdapter<Note, NoteViewHolder>(Note.class, R.layout.note_list_item,
                NoteViewHolder.class, notesQuery) {

            @Override
            protected void populateViewHolder(final NoteViewHolder viewHolder, final Note model, final int position) {
                mProgressBar.setVisibility(View.INVISIBLE);

                final DatabaseReference noteRef = getRef(position);
                String v = getUid();

                // Set click listener for the whole post view
                final String noteKey = noteRef.getKey();
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), NoteDetailsActivity.class);
                        intent.putExtra(NoteDetailsActivity.EXTRA_NOTE_KEY, noteKey);
                        startActivity(intent);
                    }
                });

                viewHolder.bindToNote(model);
            }
        };

        mRecycler.setAdapter(mAdapter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    public String getUid() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        }

        return null;
    }

    public abstract Query getQuery(DatabaseReference databaseReference);
}
