package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.profile.ProfiloUtenteNonPersonaleActivity;


/**
 * classe che permette di fare il binding , per item dal 3 in poi
 */
public class UserViewHolderOthers extends UserViewHolder {

    public UserViewHolderOthers(final View itemView) {
        super(itemView);
        view = itemView;
        vNome = (TextView) view.findViewById(R.id.studente_nome);
        vRating = (RatingBar) view.findViewById(R.id.studente_ratingBar);
        vImageProfile = (CircleImageView) view.findViewById(R.id.profile_photo);
        vPosition = (TextView) view.findViewById(R.id.txtPosizione);
        setOnClickListener();
    }

    private void setOnClickListener() {
        LinearLayout l = (LinearLayout) itemView.findViewById(R.id.intestazione);
        l.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ProfiloUtenteNonPersonaleActivity.class);
                intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_PROFILE_KEY, userId);
                itemView.getContext().startActivity(intent);
                //((LeaderBoardActivity) v.getContext()).finish();
            }
        });
    }


}
