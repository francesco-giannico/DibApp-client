package it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.note.NoteDetailsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.NoteViewHolder;


public class MyNoteAdapter extends RecyclerView.Adapter<NoteViewHolder> {

    private List<Note> mNoteList;
    private Context mContext;

    public MyNoteAdapter() {

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyNoteAdapter(List<Note> mNoteList, Context context) {
        this.mNoteList = mNoteList;
        this.mContext = context;
    }

    public void setListNote(List<Note> mNoteList) {
        this.mNoteList = mNoteList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_list_item, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new NoteViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Note note = mNoteList.get(position);
        holder.vTitle.setText(note.title);
        String s = note.degreeCourse + " - " + note.course;
        holder.vSubtitle.setText(s);
        holder.vRating.setRating(note.avgRating);
        holder.vFormat.setText(note.format);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, NoteDetailsActivity.class);
                intent.putExtra(NoteDetailsActivity.EXTRA_NOTE_KEY, note.id);
                mContext.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mNoteList.size();
    }


}