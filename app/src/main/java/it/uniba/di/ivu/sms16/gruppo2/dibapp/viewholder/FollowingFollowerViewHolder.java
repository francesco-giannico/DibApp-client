package it.uniba.di.ivu.sms16.gruppo2.dibapp.viewholder;

import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.renderscript.Script;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase.FirebaseHandler;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;

/**
 * Created by Salvatore on 06/07/2016.
 */
public class FollowingFollowerViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public CircleImageView imgIcon;
    public TextView nameFollowItem;
    public ToggleButton toggleButton;

    private Drawable iconButtonFollow;
    private Drawable iconButtonDontFollow;

    private String myId;
    private String userId;
    private boolean isFollow;
    private User utenteFollower;

    public FollowingFollowerViewHolder(View v) {
        super(v);
        imgIcon = (CircleImageView) v.findViewById(R.id.profileimage_followItem);
        nameFollowItem = (TextView)v.findViewById(R.id.nomeCognome_followItem);
        toggleButton = (ToggleButton)v.findViewById(R.id.follow_toogleButton);

        initializeIcons(v);
    }

    public void bindToUser(User user, boolean isFollow, String userKey, String mioId, @Nullable User utenteFollower) {

        nameFollowItem.setText(user.name);
        if(user.photoUrl != null) {
            Glide.with(imgIcon.getContext()).load(user.photoUrl).crossFade().into(imgIcon);
        }
        else {
            imgIcon.setImageResource(R.drawable.ic_account_circle_dark);
        }
        this.isFollow = isFollow;
        toggleButton.setChecked(isFollow);
        userId = userKey;
        myId = mioId;
        this.utenteFollower = utenteFollower;

        //TO DO obtain true or false value
        initializeButton(toggleButton, isFollow, itemView);
        setClickBehavior(toggleButton, itemView, isFollow);
    }

    private void initializeIcons(View v) {

        iconButtonDontFollow = v.getContext().getResources().getDrawable(R.drawable.ic_clear);
        iconButtonDontFollow.setColorFilter(v.getContext().getResources().getColor(R.color.checkedTrue), PorterDuff.Mode.SRC_ATOP);

        iconButtonFollow = v.getContext().getResources().getDrawable(R.drawable.ic_follow_person);
        iconButtonFollow.setColorFilter(v.getContext().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    private void initializeButton(ToggleButton button, boolean isFollowing, View v) {

        System.out.println("Button " + userId + " = " + isFollowing);
        if(isFollowing) {
            button.setBackground(v.getContext().getResources().getDrawable(R.drawable.rounded_button_following));
            button.setTextColor(v.getContext().getResources().getColor(R.color.checkedTrue));
            button.setCompoundDrawablesWithIntrinsicBounds(iconButtonDontFollow, null, null, null);
            button.setChecked(isFollowing);
        }
        else {
            button.setBackground(v.getContext().getResources().getDrawable(R.drawable.rounded_button_follower));
            button.setTextColor(v.getContext().getResources().getColor(R.color.white));
            button.setCompoundDrawablesWithIntrinsicBounds(iconButtonFollow, null, null, null);
            button.setChecked(isFollowing);
        }
        button.setVisibility(View.VISIBLE);
    }

    private void setClickBehavior(final ToggleButton button, View v, final Boolean isFollow) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = v;
                if(button.isChecked()) {
                    button.setBackground(v.getContext().getResources().getDrawable(R.drawable.rounded_button_following));
                    button.setTextColor(v.getContext().getResources().getColor(R.color.checkedTrue));
                    button.setCompoundDrawablesWithIntrinsicBounds(iconButtonDontFollow, null, null, null);

                    FirebaseHandler refFollow = new FirebaseHandler();
                    final DatabaseReference followingReference = refFollow.getmFollowingReference(myId).child(userId);
                    final DatabaseReference himUuserReference = refFollow.getAllUsersReference().child(userId);

                    if(utenteFollower == null) {
                        himUuserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User utenteFollower = dataSnapshot.getValue(User.class);
                                User lightUser = new User(utenteFollower.name, null, utenteFollower.photoUrl);
                                followingReference.setValue(lightUser).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), "Operazione non riuscita", Toast.LENGTH_SHORT);
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        User lightUser = new User(utenteFollower.name, null, utenteFollower.photoUrl);
                        followingReference.setValue(lightUser).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(view.getContext(), "Operazione non riuscita", Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    final DatabaseReference followerReference = refFollow.getmFollowerReference(userId).child(myId);
                    final DatabaseReference myUserReference = refFollow.getAllUsersReference().child(myId);
                    myUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            User utenteFollower = dataSnapshot.getValue(User.class);
                            User lightUser = new User(utenteFollower.name, null, utenteFollower.photoUrl);
                            followerReference.setValue(lightUser).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(view.getContext(), "Operazione non riuscita", Toast.LENGTH_SHORT);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

                else {
                    FirebaseHandler refFollow = new FirebaseHandler();
                    DatabaseReference followingReference = refFollow.getmFollowingReference(myId).child(userId);
                    followingReference.removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Operazione non riuscita", Toast.LENGTH_SHORT);
                        }
                    });

                    DatabaseReference followerReference = refFollow.getmFollowerReference(userId).child(myId);
                    followerReference.removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Operazione non riuscita", Toast.LENGTH_SHORT);
                        }
                    });

                    button.setBackground(v.getContext().getResources().getDrawable(R.drawable.rounded_button_follower));
                    button.setTextColor(v.getContext().getResources().getColor(R.color.white));
                    button.setCompoundDrawablesWithIntrinsicBounds(iconButtonFollow, null, null, null);
                }
            }
        });
    }

}
