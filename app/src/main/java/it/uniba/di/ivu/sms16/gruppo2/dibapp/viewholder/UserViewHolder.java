package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    public View view;
    public TextView vNome;
    public RatingBar vRating;
    public CircleImageView vImageProfile;
    public TextView vPosition;
    public String userId;

    public UserViewHolder(View itemView) {
        super(itemView);

    }

    public View getView() {
        return view;
    }

    public String getUid() {
        return userId;
    }


}
