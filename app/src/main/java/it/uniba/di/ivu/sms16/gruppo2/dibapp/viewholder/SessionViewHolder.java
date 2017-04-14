package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;


public class SessionViewHolder extends RecyclerView.ViewHolder {

    private TextView mTitleTextView;
    private TextView mSubtitleTextView;
    private TextView mLocationTextView;
    private TextView mTimeTextView;
    private TextView mTypeTextView;
    private TextView mParticipantsTextView;
    private TextView mDescriptionTextView;
    private Button mSessionCardDoActionButton;


    public SessionViewHolder(View itemView) {
        super(itemView);

        mTitleTextView = (TextView) itemView.findViewById(R.id.sessionCardTitleTextView);
        mSubtitleTextView = (TextView) itemView.findViewById(R.id.sessionCardSubtitleTextView);
        mLocationTextView = (TextView) itemView.findViewById(R.id.sessionCardLocationTextView);
        mTimeTextView = (TextView) itemView.findViewById(R.id.sessionCardTimeTextView);
        mParticipantsTextView = (TextView) itemView.findViewById(R.id.sessionCardParticipantsTextView);
        mDescriptionTextView = (TextView) itemView.findViewById(R.id.sessionCardDescriptionTextView);
        mSessionCardDoActionButton = (Button) itemView.findViewById(R.id.sessionCardDoActionButton);
        mTypeTextView = (TextView) itemView.findViewById(R.id.sessionTypeTextView);
    }

    public void bindToSession(StudySession session, LatLng userPosition, boolean isParticipant, View.OnClickListener listener) {

        if (userPosition != null) {
            mLocationTextView.setText(String.format("%.0f mt", SphericalUtil.computeDistanceBetween(userPosition,
                    new LatLng(
                            Double.valueOf(session.geoPosition.lat),
                            Double.valueOf(session.geoPosition.lng)))));
        } else {
            mLocationTextView.setText("n.d.");
        }

        if (isParticipant) {
            mSessionCardDoActionButton.setText("view session");

        } else {
            mSessionCardDoActionButton.setText("participate");
        }

        if (session.type.equals("leaderless")) {
            mTypeTextView.setText("Leaderless");
        } else {
            mTypeTextView.setText("Teaching");
        }



        mTitleTextView.setText(session.title);
        mSubtitleTextView.setText(session.degreeCourse + " - " + session.course);

        mTimeTextView.setText(session.date + ", " + session.hourStart);
        mParticipantsTextView.setText(String.valueOf(session.numParticipants));
        mDescriptionTextView.setText(session.description);

        if (listener == null) {
            mSessionCardDoActionButton.setVisibility(View.GONE);
        } else {
            mSessionCardDoActionButton.setVisibility(View.VISIBLE);
            mSessionCardDoActionButton.setOnClickListener(listener);
        }
    }
}
