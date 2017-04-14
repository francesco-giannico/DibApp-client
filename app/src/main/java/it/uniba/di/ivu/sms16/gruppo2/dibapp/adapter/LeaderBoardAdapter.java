package it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.AggiornaInfoElementiAggiornati;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolder;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolderOthers;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.UserViewHolderThree;


public class LeaderBoardAdapter extends BaseActivity {
    private FirebaseStorage storage;
    private AggiornaInfoElementiAggiornati news;
    private FirebaseRecyclerAdapter<User, UserViewHolderOthers> adapterOthers;
    private FirebaseRecyclerAdapter<User, UserViewHolderThree> adapterThree;
    private LinearLayout info_currentUserLayout;
    private ProgressBar progBarThree;
    private ProgressBar progBarOthers;

    public LeaderBoardAdapter(FirebaseStorage storage, LinearLayout l, ProgressBar three,
                              ProgressBar others) {

        this.storage = storage;
        news = new AggiornaInfoElementiAggiornati();
        info_currentUserLayout = l;
        this.progBarThree = three;
        this.progBarOthers = others;
    }

    public FirebaseRecyclerAdapter<User, UserViewHolderThree> getAdapterThree(Query avgRatingQuery) {
        adapterThree = new FirebaseRecyclerAdapter<User, UserViewHolderThree>(User.class,
                R.layout.primi_tre_leaderboard_item_singolo_layout, UserViewHolderThree.class, avgRatingQuery) {
            @Override
            protected void populateViewHolder(final UserViewHolderThree UserViewHolder, User s, int position) {
                progBarThree.setVisibility(ProgressBar.GONE);
                // if(!settaPrimiTreClassifica(UserViewHolder,s,position)) {
                UserViewHolder.vRating.setRating(Float.valueOf(-1)*s.avgRating);//prendo i valori negativi e li setto a positivi. Cosi ho l'ordine giusto
                UserViewHolder.vNome.setText(s.name);
                DatabaseReference userRef = getRef(position);
                UserViewHolder.userId = userRef.getKey();
                CircleImageView img = ( CircleImageView) UserViewHolder.getView().findViewById(R.id.imgClassificato);

                //Setta l'immagine
                if (s.photoUrl != null) {
                    scaricaEImpostaImmagine(img,s.photoUrl,UserViewHolder.getView().getContext());
                } else {
                    img.setImageResource(R.drawable.ic_account_circle_white);
                }

                news.aggiornaTextViewPosizioneThree(UserViewHolder, position);

            }
        };
        return adapterThree;
    }

    public FirebaseRecyclerAdapter<User, UserViewHolderOthers> getAdapterOthers(Query avgRatingQuery) {
        adapterOthers = new FirebaseRecyclerAdapter<User, UserViewHolderOthers>(User.class,
                R.layout.altri_items_leaderboard_singolo_layout, UserViewHolderOthers.class, avgRatingQuery) {
            @Override
            protected void populateViewHolder(final UserViewHolderOthers UserViewHolder, User s, final int position) {
                progBarOthers.setVisibility(ProgressBar.GONE);
                DatabaseReference userRef = getRef(position);
                UserViewHolder.userId = userRef.getKey();
                UserViewHolder.vRating.setRating(Float.valueOf(-1)*s.avgRating);//prendo i valori negativi e li setto a positivi. Cosi ho l'ordine giusto
                UserViewHolder.vNome.setText(s.name);

                CircleImageView img = ( CircleImageView) UserViewHolder.getView().findViewById(R.id.profile_photo);
                if(s.photoUrl!=null)
                 scaricaEImpostaImmagine(img,s.photoUrl,UserViewHolder.getView().getContext());
                else{

                    img.setImageResource(R.drawable.ic_account_circle_dark);
                }
                news.aggiornaTextViewPosizioneOthers(UserViewHolder, position);
                if(mCurrentUid!=null)
                    currentUserPositionUpdate();
            }
        };
        return adapterOthers;
    }
    private void scaricaEImpostaImmagine(CircleImageView img,String url,
                                         Context context){
            Glide.with(context)
                    .load(url)
                    .into(img);
            img.setImageDrawable(img.getDrawable());
    }
    public void currentUserPositionUpdate() {
        ((TextView) info_currentUserLayout.findViewById(R.id.txtPosUserCurrent)).setText(String.valueOf(AggiornaInfoElementiAggiornati.pos_currentUser+1));
        ((RatingBar)info_currentUserLayout.findViewById(R.id.current_u_rating)).setRating(Float.valueOf(-1)*AggiornaInfoElementiAggiornati.rating_currentUser);
    }
}
