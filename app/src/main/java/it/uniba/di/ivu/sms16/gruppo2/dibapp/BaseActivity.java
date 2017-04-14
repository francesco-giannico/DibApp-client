package it.uniba.di.ivu.sms16.gruppo2.dibapp;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;


public class BaseActivity extends AppCompatActivity {

    protected static User mCurrentUser;
    protected static String mCurrentUid;
    private static ValueEventListener mUserEventListener;
    private ProgressDialog mProgressDialog;
    private static DatabaseReference userRef;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userRef = FirebaseDatabase.getInstance().getReference("users");

            if (mCurrentUid == null) {
                FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();

                if (fireUser != null) {
                    mCurrentUid = fireUser.getUid();

                    String photoUrl = null;
                    if (fireUser.getPhotoUrl() != null) {
                        photoUrl = fireUser.getPhotoUrl().toString();
                    }

                    mCurrentUser = new User(fireUser.getDisplayName(), fireUser.getEmail(), photoUrl);
                }
            }

            if (mUserEventListener == null && mCurrentUid != null) {
                setUserValueEventListener();
            }
    }

    public void showProgressDialog(String message) {

        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }

        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    public void dismissProgressDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mUserEventListener != null) {
            userRef.child(mCurrentUid).addValueEventListener(mUserEventListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mUserEventListener != null) {
            userRef.child(mCurrentUid).removeEventListener(mUserEventListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dismissProgressDialog();
    }

    protected void setUserValueEventListener() {
        if (mUserEventListener == null) {
            mUserEventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    mCurrentUser = dataSnapshot.getValue(User.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };
        }

        userRef.child(mCurrentUid).addValueEventListener(mUserEventListener);
    }

    protected void nullUserEventListener() {
        mUserEventListener = null;
    }
}