package it.uniba.di.ivu.sms16.gruppo2.dibapp.utils;

import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolder;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolderOthers;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolderThree;


public class AggiornaInfoElementiAggiornati extends BaseActivity {

    private SparseArray<UserViewHolderOthers> viewHolderOthers = new SparseArray<>();
    private SparseArray<UserViewHolderThree> viewHolderThree = new SparseArray<>();
    public static int pos_currentUser;
    public static float rating_currentUser;

    public AggiornaInfoElementiAggiornati() {
    }


    public int aggiornaTextViewPosizioneOthers(UserViewHolderOthers UserViewHolder, final int position) {
        if (position<viewHolderOthers.size()&& viewHolderOthers.valueAt(position) != null) {
            SparseArray<UserViewHolderOthers> newY = new SparseArray<>();
            for (int i = 0; i < viewHolderOthers.size(); i++) {
                UserViewHolderOthers u = viewHolderOthers.get(i);
                if (u != null) {
                    //creo uno sparse array con gli elementi in base all'ordine che hanno su schermo attualmente.
                    newY.put(u.getAdapterPosition(), u);
                    u.vPosition.setText(String.valueOf(u.getAdapterPosition() + 1));
                    //setto posizione ad utente corrente , suoi dettagli
                    if (mCurrentUid!=null && u.getUid().equals(mCurrentUid)) {
                        pos_currentUser = position;
                        rating_currentUser = mCurrentUser.avgRating;
                    }

                }
            }
            newY.put(position, UserViewHolder);
            UserViewHolder.vPosition.setText(String.valueOf(position + 1));
            //setto posizione ad utente corrente , suoi dettagli
            if (mCurrentUid!=null && UserViewHolder.getUid().equals(mCurrentUid)) {
                pos_currentUser = position;
                rating_currentUser = mCurrentUser.avgRating;
            }
            viewHolderOthers = newY;
        } else {
            viewHolderOthers.put(position, UserViewHolder);
            UserViewHolder.vPosition.setText(String.valueOf(position + 1));
            //setto posizione ad utente corrente , suoi dettagli
            if (mCurrentUid!=null && UserViewHolder.getUid().equals(mCurrentUid)) {
                pos_currentUser = position;
                rating_currentUser = mCurrentUser.avgRating;
            }
        }
        return pos_currentUser;
    }

    /**
     * imposta la dimensione delle immagini per i primi tre utenti e la text view che indica la posizione
     */
    public void aggiornaTextViewPosizioneThree(UserViewHolderThree UserViewHolder, final int position) {
        if (viewHolderThree.valueAt(position) != null) {
            SparseArray<UserViewHolderThree> newY = new SparseArray<>();
            for (int i = 0; i < viewHolderThree.size(); i++) {
                UserViewHolderThree u = viewHolderThree.get(i);
                if (u != null && u.getAdapterPosition() >= 0) {
                    //creo uno sparse array con gli elementi in base all'ordine che hanno su schermo attualmente.
                    newY.put(u.getAdapterPosition(), u);
                    u.vPosition.setText(String.valueOf(u.getAdapterPosition() + 1));
                }
            }
            newY.put(position, UserViewHolder);
            UserViewHolder.vPosition.setText(String.valueOf(position + 1));
            viewHolderThree = newY;
        } else {
            viewHolderThree.put(position, UserViewHolder);
            UserViewHolder.vPosition.setText(String.valueOf(position + 1));
        }

    }


}
