package it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

/**
 * Created by antoniolategano on 01/07/16.
 */
public class BestNotesFragment extends NoteListFragment {

    public BestNotesFragment() {
    }

    @Override
    public Query getQuery(DatabaseReference databaseReference) {

        return databaseReference.child("notes").orderByChild("avgRating");

    }
}
