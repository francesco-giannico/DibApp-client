package it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;


/**
 * Created by Salvatore on 06/07/2016.
 */
public class FirebaseHandler {

    private DatabaseReference mDatabase;
    private DatabaseReference mUserReference;
    private DatabaseReference mFollowerReference;
    private DatabaseReference mFollowingference;
    private User user = new User();

    public FirebaseHandler() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public DatabaseReference getmUserReference(String idUser) {
        mUserReference = FirebaseDatabase.getInstance().getReference("users").child(idUser);
        return mUserReference;
    }

    public DatabaseReference getAllUsersReference() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    public DatabaseReference getmFollowerReference(String idUser) {
        mFollowerReference = FirebaseDatabase.getInstance().getReference("followers").child(idUser);
        return mFollowerReference;
    }

    public DatabaseReference getmFollowingReference(String idUser) {
        mFollowingference = FirebaseDatabase.getInstance().getReference("following").child(idUser);
        return mFollowingference;
    }

    public void followNewUserFromNfc(final String myId, final String idUserToFollow, User mioUser) {

        // Ottengo riferimenti database al mio profilo e al profilo del nuovo follower
        final DatabaseReference myFollowing = getmFollowingReference(myId).child(idUserToFollow);
        final DatabaseReference himFollower = getmFollowerReference(idUserToFollow).child(myId);
        final DatabaseReference himFollowing = getmFollowingReference(idUserToFollow).child(myId);
        final DatabaseReference myFollower = getmFollowerReference(myId).child(idUserToFollow);

        User him = getUserFromDb(idUserToFollow);
        myFollowing.setValue(him);
        himFollower.setValue(mioUser);
        himFollowing.setValue(mioUser);
        myFollower.setValue(him);

    }

    private static User getUserFromDb(String userId){
        // Ottengo riferimenti database al profilo dell'userId
        final DatabaseReference myUser = FirebaseDatabase.getInstance().getReference("users").child(userId);
        final User[] user = new User[1];
        myUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user[0] = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return user[0];
    }
}
