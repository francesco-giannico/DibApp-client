package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.github.siyamed.shapeimageview.CircularImageView;

import org.w3c.dom.Text;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;

/**
 * Created by Salvatore on 08/07/2016.
 */
public class PersonalDetailsHolder extends RecyclerView.ViewHolder {

    public TextView nickname;
    public TextView corsoStudi;
    public TextView mail;
    public TextView phone;

    public TextView primoInteresse;
    public TextView secondoInteresse;
    public TextView terzoInteresse;
    public TextView quartoInteresse;

    public ImageView primaImmagineDettagli;
    public ImageView primaImmagine;
    public ImageView secondaImmagine;
    public ImageView terzaImmagine;
    public ImageView quartaImmagine;

    public TextView titleDettagliInteressi;

    public PersonalDetailsHolder(View v) {
        super(v);
        nickname = (TextView) v.findViewById(R.id.firstTextViewProfilo);
        corsoStudi = (TextView) v.findViewById(R.id.secondTextViewProfilo);
        mail = (TextView) v.findViewById(R.id.thirdTextViewProfilo);
        phone = (TextView) v.findViewById(R.id.fourthTextViewProfilo);

        primaImmagineDettagli = (ImageView) v.findViewById(R.id.fourthImageViewProfilo);

        primoInteresse = (TextView) v.findViewById(R.id.firstInterest);
        secondoInteresse = (TextView) v.findViewById(R.id.secondInterest);
        terzoInteresse = (TextView) v.findViewById(R.id.thirdInterest);
        quartoInteresse = (TextView) v.findViewById(R.id.fourthInterest);

        primaImmagine = (ImageView) v.findViewById(R.id.firstImageInterest);
        secondaImmagine = (ImageView) v.findViewById(R.id.secondImageInterest);
        terzaImmagine = (ImageView) v.findViewById(R.id.thirdImageInterest);
        quartaImmagine = (ImageView) v.findViewById(R.id.fourthImageInterest);

        titleDettagliInteressi = (TextView) v.findViewById(R.id.label_intestazione_interessi);
    }

    public void bindToDetails(User user) {

        nickname.setText(user.name);
        corsoStudi.setText(itemView.getResources().getString(R.string.toStudyVerb) + " " + user.degreeCourse);
        mail.setText(user.email);

        if (user.phoneNumber != null) {
            phone.setText(user.phoneNumber);
        }
        else {
            phone.setText(itemView.getResources().getString(R.string.numberNotPresent));
        }

        if (user.interests != null) {
            primoInteresse.setText(user.interests.get(0));
            secondoInteresse.setText(user.interests.get(1));
            terzoInteresse.setText(user.interests.get(2));
            quartoInteresse.setText(user.interests.get(3));

            primaImmagine.setImageResource(getImageId(user.interests.get(0)));
            secondaImmagine.setImageResource(getImageId(user.interests.get(1)));
            terzaImmagine.setImageResource(getImageId(user.interests.get(2)));
            quartaImmagine.setImageResource(getImageId(user.interests.get(3)));
        }

        else {
            titleDettagliInteressi.setText("Nessun interesse scelto");

            primoInteresse.setVisibility(View.GONE);
            secondoInteresse.setVisibility(View.GONE);
            terzoInteresse.setVisibility(View.GONE);
            quartoInteresse.setVisibility(View.GONE);

            primaImmagine.setVisibility(View.GONE);
            secondaImmagine.setVisibility(View.GONE);
            terzaImmagine.setVisibility(View.GONE);
            quartaImmagine.setVisibility(View.GONE);
        }
    }

    private int getImageId(String interest) {

        if(interest.equals(itemView.getResources().getString(R.string.matematica))) {
            return R.drawable.ic_matematica;
        }
        else if(interest.equals(itemView.getResources().getString(R.string.fisica))) {
            return R.drawable.ic_fisica;
        }
        else if(interest.equals(itemView.getResources().getString(R.string.sicurezza))) {
            return R.drawable.ic_sicurezza;
        }
        else if(interest.equals(itemView.getResources().getString(R.string.programmazione))) {
            return R.drawable.ic_coding;
        }
        else if (interest.equals(itemView.getResources().getString(R.string.programmazione_android))) {
            return R.drawable.ic_android_24;
        }else if(interest.equals(itemView.getResources().getString(R.string.programmazione_web))) {
            return R.drawable.ic_web;
        }
        else if(interest.equals(itemView.getResources().getString(R.string.ux))) {
            return R.drawable.ic_ux_24;
        }
        else if(interest.equals(itemView.getResources().getString(R.string.database))) {
            return R.drawable.ic_db;
        }
        else if(interest.equals(itemView.getResources().getString(R.string.machine_learning))){
            return R.drawable.ic_machine;
        }
        else
            return 0;
    }

}
