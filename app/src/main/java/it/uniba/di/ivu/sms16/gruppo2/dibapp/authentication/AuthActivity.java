package it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.List;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.SessionsBoardActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;


public class AuthActivity extends BaseActivity implements AuthFragmentsInteraction {

    private final String FRAGMENT_AUTH_EMAIL_TAG = "it.uniba.di.ivu.sms16.gruppo2.dibapp.FRAGMENT_AUTH_START";
    private final String FRAGMENT_AUTH_SIGNIN_TAG = "it.uniba.di.ivu.sms16.gruppo2.dibapp.FRAGMENT_AUTH_SIGNIN";
    private final String FRAGMENT_AUTH_SIGNUP_TAG = "it.uniba.di.ivu.sms16.gruppo2.dibapp.FRAGMENT_AUTH_SIGNUP";

    private CoordinatorLayout mCoordinatorLayout;
    private FragmentManager mFragmentManager;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private boolean mLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        Toolbar toolbar = (Toolbar) findViewById(R.id.auth_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mFragmentManager = getSupportFragmentManager();
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.auth_activity_layout);

        if (getIntent().getExtras() != null) {
            Bundle extras = getIntent().getExtras();
            mLogout = extras.getBoolean("logout", false);

        }

        mAuth = FirebaseAuth.getInstance();

        if (mLogout) {
            mAuth.signOut();
        }

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (mLogout) {

                    mCurrentUid = null;
                    mCurrentUser = null;
                    nullUserEventListener();

                    Toast.makeText(getBaseContext(), R.string.auth_sign_out, Toast.LENGTH_SHORT).show();

                    startActivity(new Intent(AuthActivity.this, SessionsBoardActivity.class));
                    finish();
                }

                FirebaseUser fireUser = firebaseAuth.getCurrentUser();
                if (fireUser != null) {

                    String token = FirebaseInstanceId.getInstance().getToken();
                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                    final DatabaseReference devicesRef = firebaseDatabase.getReference("backend/devices");

                    devicesRef.child(fireUser.getUid()).setValue(token);

                    mCurrentUid = fireUser.getUid();
                    String photoUrl = null;
                    if (fireUser.getPhotoUrl() != null) {
                         photoUrl = fireUser.getPhotoUrl().toString();
                    }
                    mCurrentUser = new User(fireUser.getDisplayName(), fireUser.getEmail(), photoUrl);

                    setUserValueEventListener();

                    setResult(Activity.RESULT_OK);
                    finish();

                } else {

                    changeBehavior(FRAGMENT_AUTH_EMAIL_TAG, null);
                }
            }
        };
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {

            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }


    public void changeBehavior(String behavior, String email) {
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();

        switch (behavior) {
            case FRAGMENT_AUTH_EMAIL_TAG:

                getSupportActionBar().setTitle(R.string.auth_email_title);

                fragmentTransaction.replace(R.id.auth_fragment_container,
                        AuthEmailFragment.newInstance(), FRAGMENT_AUTH_EMAIL_TAG);
                fragmentTransaction.commit();

                break;

            case FRAGMENT_AUTH_SIGNIN_TAG:

                getSupportActionBar().setTitle(R.string.auth_signin_title);

                fragmentTransaction.replace(R.id.auth_fragment_container,
                        AuthSignInFragment.newInstance(email), FRAGMENT_AUTH_SIGNIN_TAG);

                fragmentTransaction.commit();
                break;

            case FRAGMENT_AUTH_SIGNUP_TAG:

                getSupportActionBar().setTitle(R.string.auth_signup_title);

                fragmentTransaction.replace(R.id.auth_fragment_container,
                        AuthSignUpFragment.newInstance(email), FRAGMENT_AUTH_SIGNUP_TAG);

                fragmentTransaction.commit();
                break;
        }
    }

    private void authHelper(String email, List<String> providers) {
        if (providers == null || providers.isEmpty()) {

            changeBehavior(FRAGMENT_AUTH_SIGNUP_TAG, email);
        } else {

            changeBehavior(FRAGMENT_AUTH_SIGNIN_TAG, email);
        }
    }


    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    @Override
    public void onCheckEmail(final String email) {
        hideSoftKeyboard();

        showProgressDialog(getString(R.string.email_check_message));

        mAuth.fetchProvidersForEmail(email)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        hideProgressDialog();
                        Snackbar.make(mCoordinatorLayout, e.getMessage(),
                                Snackbar.LENGTH_LONG)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onCheckEmail(email);
                                    }
                                }).show();
                    }
                })
                .addOnCompleteListener(
                        new OnCompleteListener<ProviderQueryResult>() {

                            @Override
                            public void onComplete(@NonNull Task<ProviderQueryResult> task) {

                                dismissProgressDialog();

                                if (task.isSuccessful()) {
                                    authHelper(email, task.getResult().getProviders());
                                }
                            }
                        });
    }

    @Override
    public void onSignIn(final String email, final String psw) {
        hideSoftKeyboard();

        showProgressDialog(getString(R.string.signin_message));

        mAuth.signInWithEmailAndPassword(email, psw)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        hideProgressDialog();
                        Snackbar.make(mCoordinatorLayout, e.getMessage(),
                                Snackbar.LENGTH_LONG)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onSignIn(email, psw);
                                    }
                                }).show();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        dismissProgressDialog();

                    }
                });
    }

    @Override
    public void onSignUp(final User user, final String psw) {
        hideSoftKeyboard();

        showProgressDialog(getString(R.string.signup_message));

        mAuth.createUserWithEmailAndPassword(user.email, psw)
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        hideProgressDialog();
                        Snackbar.make(mCoordinatorLayout, e.getMessage(),
                                Snackbar.LENGTH_LONG)
                                .setAction(R.string.retry, new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onSignUp(user, psw);
                                    }
                                }).show();
                    }
                })
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            hideProgressDialog();

                        } else {

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(user.name)
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdates);

                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            final DatabaseReference usersRef = database.getReference("users");

                            FirebaseUser fireUser = task.getResult().getUser();
                            usersRef.child(fireUser.getUid()).setValue(user);
                        }
                    }
                });
    }

    @Override
    public void onPasswordReset(final String email) {

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            Snackbar.make(mCoordinatorLayout,
                                    getString(R.string.password_forget_send) + " " + email,
                                    Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }

}