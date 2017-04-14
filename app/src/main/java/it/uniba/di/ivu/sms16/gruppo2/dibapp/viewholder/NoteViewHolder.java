package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;

/**
 * Created by antoniolategano on 01/07/16.
 */
public class NoteViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView vTitle;
    public TextView vSubtitle;
    public RatingBar vRating;
    public TextView vFormat;

    public NoteViewHolder(View v) {
        super(v);
        vTitle = (TextView) v.findViewById(R.id.appunto_title);
        vSubtitle = (TextView) v.findViewById(R.id.appunto_subtitle);
        vRating = (RatingBar) v.findViewById(R.id.appunto_ratingBar);
        vFormat = (TextView) v.findViewById(R.id.format_list_text_view);
    }

    public void bindToNote(Note note) {
        vTitle.setText(note.title);
        String s = note.degreeCourse + " - " + note.course;
        vSubtitle.setText(s);
        vRating.setRating(note.avgRating);
        vFormat.setText(note.format);
    }
}