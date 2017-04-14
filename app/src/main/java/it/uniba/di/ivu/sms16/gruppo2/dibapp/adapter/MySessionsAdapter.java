package it.uniba.di.ivu.sms16.gruppo2.dibapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession.SessionDetailsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder.SessionViewHolder;

/**
 * Created by antoniolategano on 10/07/16.
 */
public class MySessionsAdapter extends RecyclerView.Adapter<SessionViewHolder> {

    private List<StudySession> mSessionList;
    private Context mContext;
    private GeoPosition mUserPosition;
    private String mUserId;

    public MySessionsAdapter() {

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MySessionsAdapter(List<StudySession> mSessionList, Context context, GeoPosition userPosition, String userId) {
        this.mSessionList = mSessionList;
        this.mContext = context;
        this.mUserPosition = userPosition;
        this.mUserId = userId;
    }

    public void setListSession(List<StudySession> mSessionList) {
        this.mSessionList = mSessionList;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SessionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_session, parent, false);

        // set the view's size, margins, paddings and layout parameters
        return new SessionViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(SessionViewHolder holder, int position) {

        final StudySession session = mSessionList.get(position);

        if (mUserPosition != null) {
            holder.bindToSession(session, new LatLng(mUserPosition.lat, mUserPosition.lng), false, null);
        } else {
            holder.bindToSession(session, null, false, null);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SessionDetailsActivity.class);
                intent.putExtra(SessionDetailsActivity.EXTRA_SESSION_ID, session.id);
                intent.putExtra(SessionDetailsActivity.EXTRA_USER_ID, mUserId);
                intent.putExtra(SessionDetailsActivity.EXTRA_NOT_FROM_SESSION, true);
                mContext.startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mSessionList.size();
    }
}

