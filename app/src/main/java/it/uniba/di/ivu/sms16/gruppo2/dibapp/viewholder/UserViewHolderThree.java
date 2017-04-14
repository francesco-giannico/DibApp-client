package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.profile.ProfiloUtenteNonPersonaleActivity;


/**
 * classe che permette di fare il binding dal 4 elemento in poi presi dal db in ordine del rating medio
 */
public class UserViewHolderThree extends UserViewHolder {

    public UserViewHolderThree(final View itemView) {
        super(itemView);
        view = itemView;
        vNome = (TextView) view.findViewById(R.id.txtNomeClassificato);
        vRating = (RatingBar) view.findViewById(R.id.ratingBarClassificato);
        vImageProfile = (CircleImageView) view.findViewById(R.id.imgClassificato);
        vPosition = (TextView) view.findViewById(R.id.txtPosClass);
        FrameLayout immagine = (FrameLayout) itemView.findViewById(R.id.frmClassificato);
        FrameLayout dati = (FrameLayout) itemView.findViewById(R.id.frmClassificatoDati);
        immagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ProfiloUtenteNonPersonaleActivity.class);
                intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_PROFILE_KEY, userId);
                itemView.getContext().startActivity(intent);
            }
        });
        dati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(itemView.getContext(), ProfiloUtenteNonPersonaleActivity.class);
                intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_PROFILE_KEY, userId);
                itemView.getContext().startActivity(intent);
            }
        });
    }
}
