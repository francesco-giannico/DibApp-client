package it.uniba.di.ivu.sms16.gruppo2.dibapp.profile;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase.FirebaseHandler;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.RatingDialogFragment;


public class ProfiloUtenteNonPersonaleActivity extends BaseActivity
        implements RatingDialogFragment.RatingDialogListener, View.OnClickListener {

    private DialogFragment mRateDialog;
    public static final String EXTRA_PROFILE_KEY = "profile_key";
    public static final String EXTRA_IMFOLLOW_KEY = "imfollow_key";
    public static final String EXTRA_USER_KEY = "user_key";
    private DatabaseReference mUserReference;
    private DatabaseReference mDatabaseReference;
    private String thisUserKey;
    private User utenteFollower;
    private HashMap<String, Boolean> mImFollowKey;
    private AppBarLayout appToolbarLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private View bottom_shadow;
    private View top_shadow;
    private RatingBar ratingBar;
    private TextView nomeC;
    private User utente;
    private View listDettagliProfilo;
    private TextView nickname;
    private TextView corsoStudi;
    private TextView mail;
    private TextView phone;
    private View listDettagliInterestProfilo;
    private TextView primoInteresse;
    private TextView secondoInteresse;
    private TextView terzoInteresse;
    private TextView quartoInteresse;
    private TextView titleInteressi;
    private TextView subTitleToolbar;
    private ToggleButton toggleButtonSegui;
    private ProgressDialog mProgressDialog;

    private ImageView profileImage;
    private ImageView primaImmagine;
    private ImageView secondaImmagine;
    private ImageView terzaImmagine;
    private ImageView quartaImmagine;

    //Stile button follow
    private Drawable iconButtonFollow;
    private Drawable iconButtonDontFollow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profilo_utente_nonpersonale);
        appToolbarLayout = (AppBarLayout) findViewById(R.id.app_bar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        bottom_shadow = findViewById(R.id.shadow_gradient_bottom);
        top_shadow = findViewById(R.id.shadow_gradient_top);
        ratingBar = (RatingBar) findViewById(R.id.ratingBarProfiloUtente);
        nomeC = (TextView) findViewById(R.id.title_nome_cognome);
        profileImage = (ImageView) findViewById(R.id.profileImageView);
        toggleButtonSegui = (ToggleButton) findViewById(R.id.follow_user_from_details);
        subTitleToolbar = (TextView) findViewById(R.id.details_user_title);

        listDettagliProfilo = findViewById(R.id.listItemDettagli);
        nickname = (TextView) listDettagliProfilo.findViewById(R.id.firstTextViewProfilo);
        corsoStudi = (TextView) listDettagliProfilo.findViewById(R.id.secondTextViewProfilo);
        mail = (TextView) listDettagliProfilo.findViewById(R.id.thirdTextViewProfilo);
        phone = (TextView) listDettagliProfilo.findViewById(R.id.fourthTextViewProfilo);

        listDettagliInterestProfilo = findViewById(R.id.listItemDettagliInteressi);

        titleInteressi = (TextView) listDettagliInterestProfilo.findViewById(R.id.label_intestazione_profilo);
        primoInteresse = (TextView) listDettagliInterestProfilo.findViewById(R.id.firstTextViewProfilo);
        secondoInteresse = (TextView) listDettagliInterestProfilo.findViewById(R.id.secondTextViewProfilo);
        terzoInteresse = (TextView) listDettagliInterestProfilo.findViewById(R.id.thirdTextViewProfilo);
        quartoInteresse = (TextView) listDettagliInterestProfilo.findViewById(R.id.fourthTextViewProfilo);

        primaImmagine = (ImageView) listDettagliInterestProfilo.findViewById(R.id.firstImageViewProfilo);
        secondaImmagine = (ImageView) listDettagliInterestProfilo.findViewById(R.id.secondImageViewProfilo);
        terzaImmagine = (ImageView) listDettagliInterestProfilo.findViewById(R.id.thirdImageViewProfilo);
        quartaImmagine = (ImageView) listDettagliInterestProfilo.findViewById(R.id.fourthImageViewProfilo);

        listDettagliInterestProfilo.setOnClickListener(this);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);

        // Set behaviour dello ScrollLayout per gestire scomparsa elementi
        setScrollBehaviour();

        //OTTENGO CHIAVE ELEMENTO DB DEL PROFILO SCELTO
        thisUserKey = getIntent().getStringExtra(EXTRA_PROFILE_KEY);
        if (thisUserKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_PROFILE_KEY");
        }

        mImFollowKey = (HashMap<String, Boolean>) getIntent().getSerializableExtra(EXTRA_IMFOLLOW_KEY);
        utenteFollower = (User) getIntent().getSerializableExtra(EXTRA_USER_KEY);

        //INIT DATABASE REFERENCE E OTTENGO UTENTE
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mUserReference = mDatabaseReference.child("users").child(thisUserKey);

        initializeIcons();
        initializeButton(toggleButtonSegui, false);
        setClickBehavior(toggleButtonSegui);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_clear));
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog("Loading...");
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                utente = dataSnapshot.getValue(User.class);
                nomeC.setText(utente.name);
                ratingBar.setRating(-1 * utente.avgRating);
                subTitleToolbar.setText(R.string.user_details);

                nickname.setText(utente.name);
                corsoStudi.setText(getResources().getString(R.string.toStudyVerb) + " " + utente.degreeCourse);
                mail.setText(utente.email);
                if (utente.phoneNumber != null) {
                    phone.setText(utente.phoneNumber);
                }
                else {
                    phone.setText(getResources().getString(R.string.numberNotPresent));
                }

                if(utente.photoUrl != null) {
                    //Ottengo immagine da url e inserisco in imageview
                    Glide.with(getBaseContext()).load(utente.photoUrl).crossFade().into(profileImage);
                }

                if(utente.interests != null) {
                    titleInteressi.setText(R.string.interestTitle);
                    primoInteresse.setText(utente.interests.get(0));
                    secondoInteresse.setText(utente.interests.get(1));
                    terzoInteresse.setText(utente.interests.get(2));
                    quartoInteresse.setText(utente.interests.get(3));

                    primaImmagine.setImageResource(getImageId(utente.interests.get(0)));
                    secondaImmagine.setImageResource(getImageId(utente.interests.get(1)));
                    terzaImmagine.setImageResource(getImageId(utente.interests.get(2)));
                    quartaImmagine.setImageResource(getImageId(utente.interests.get(3)));
                }
                else {

                    titleInteressi.setText(R.string.interestEmptyTitle);
                    primaImmagine.setVisibility(View.GONE);
                    secondaImmagine.setVisibility(View.GONE);
                    terzaImmagine.setVisibility(View.GONE);
                    quartaImmagine.setVisibility(View.GONE);
                }

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfiloUtenteNonPersonaleActivity.this, R.string.unable_to_load_user,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        showProgressDialog(getString(R.string.loading));
        mUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                utente = dataSnapshot.getValue(User.class);
                nomeC.setText(utente.name);
                ratingBar.setRating(-1 * utente.avgRating);

                nickname.setText(utente.name);
                corsoStudi.setText(getResources().getString(R.string.toStudyVerb) + " " + utente.degreeCourse);
                mail.setText(utente.email);
                if (utente.phoneNumber != null) {
                    phone.setText(utente.phoneNumber);
                }
                else {
                    phone.setText(getResources().getString(R.string.numberNotPresent));
                }

                if(utente.interests != null) {
                    primoInteresse.setText(utente.interests.get(0));
                    secondoInteresse.setText(utente.interests.get(1));
                    terzoInteresse.setText(utente.interests.get(2));
                    quartoInteresse.setText(utente.interests.get(3));

                    primaImmagine.setImageResource(getImageId(utente.interests.get(0)));
                    secondaImmagine.setImageResource(getImageId(utente.interests.get(1)));
                    terzaImmagine.setImageResource(getImageId(utente.interests.get(2)));
                    quartaImmagine.setImageResource(getImageId(utente.interests.get(3)));
                }
                else {

                    primaImmagine.setVisibility(View.GONE);
                    secondaImmagine.setVisibility(View.GONE);
                    terzaImmagine.setVisibility(View.GONE);
                    quartaImmagine.setVisibility(View.GONE);

                    primoInteresse.setVisibility(View.GONE);
                    secondoInteresse.setVisibility(View.GONE);
                    terzoInteresse.setVisibility(View.GONE);
                    quartoInteresse.setVisibility(View.GONE);
                }

                hideProgressDialog();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ProfiloUtenteNonPersonaleActivity.this, R.string.unable_to_load_user,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setScrollBehaviour() {

        appToolbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

                float denominator = appToolbarLayout.getTotalScrollRange();

                Log.i("Height ", collapsingToolbarLayout.getHeight() + " Toolbar: " + toolbar.getHeight() +
                        " TotalOffset: " + appToolbarLayout.getTotalScrollRange());
                Log.i("Offset ", String.valueOf(verticalOffset));
                float alpha = (denominator + verticalOffset) / denominator;
                Log.i("Alpha: ", String.valueOf(alpha));

                bottom_shadow.setAlpha(alpha - 0.3f);
                top_shadow.setAlpha(alpha - 0.3f);
                ratingBar.setAlpha(alpha);

                nomeC.setTextSize(20.0f + alpha * 15);
                subTitleToolbar.setAlpha(1 - alpha);


            }
        });

    }

    private int getImageId(String interest) {

        if(interest.equals(R.string.matematica)) {
            return R.drawable.ic_matematica;
        }
        else if(interest.equals(R.string.fisica)) {
            return R.drawable.ic_fisica;
        }
        else if(interest.equals(R.string.sicurezza)) {
            return R.drawable.ic_sicurezza;
        }
        else if(interest.equals(R.string.programmazione)) {
            return R.drawable.ic_coding;
        }
        else if (interest.equals(R.string.programmazione_android)) {
            return R.drawable.ic_android_24;
        }else if(interest.equals(R.string.programmazione_web)) {
            return R.drawable.ic_web;
        }
        else if(interest.equals(R.string.ux)) {
            return R.drawable.ic_ux_24;
        }
        else if(interest.equals(R.string.database)) {
            return R.drawable.ic_db;
        }
        else {
            return R.drawable.ic_machine;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab: {
                showNoticeDialog();
                break;
            }
        }
    }

    @Override
    public void onRateButtonClick(DialogFragment dialog) {
        RatingDialogFragment ratingDialog = (RatingDialogFragment) dialog;

        float rating = ratingDialog.mRatingBar.getRating();
        int newNumRating = ++utente.numRatings;
        float newRating = ((utente.avgRating * utente.numRatings) + - 1*rating) / newNumRating;
        if(newRating>0) {
            newRating = newRating * -1;
        }

        mUserReference.child("avgRating").setValue(newRating);
        mUserReference.child("numRatings").setValue(newNumRating);

        ratingBar.setRating(newRating * -1);
        Toast.makeText(getBaseContext(), "Hai valutato con " + rating + " stelle", Toast.LENGTH_SHORT).show();
        mRateDialog.dismiss();
    }

    public void showNoticeDialog() {
        mRateDialog = new RatingDialogFragment();
        mRateDialog.setCancelable(false);
        mRateDialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    ////// Style button follow //////////////////////
    private void initializeIcons() {

        iconButtonDontFollow = this.getBaseContext().getResources().getDrawable(R.drawable.ic_clear);
        iconButtonDontFollow.setColorFilter(this.getBaseContext().getResources().getColor(R.color.checkedTrue), PorterDuff.Mode.SRC_ATOP);

        iconButtonFollow = this.getBaseContext().getResources().getDrawable(R.drawable.ic_follow_person);
        iconButtonFollow.setColorFilter(this.getBaseContext().getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);

    }

    private void initializeButton(ToggleButton button, boolean isFollowing) {

        if(isFollowing) {
            button.setChecked(isFollowing);
            button.setBackground(this.getBaseContext().getResources().getDrawable(R.drawable.rounded_button_following));
            button.setTextColor(this.getBaseContext().getResources().getColor(R.color.checkedTrue));
            button.setCompoundDrawablesWithIntrinsicBounds(iconButtonDontFollow, null, null, null);
        }
        else {
            button.setChecked(isFollowing);
            button.setBackground(this.getBaseContext().getResources().getDrawable(R.drawable.rounded_button_follower));
            button.setTextColor(this.getBaseContext().getResources().getColor(R.color.white));
            button.setCompoundDrawablesWithIntrinsicBounds(iconButtonFollow, null, null, null);
        }
        button.setVisibility(View.VISIBLE);
    }

    private void setClickBehavior(final ToggleButton button) {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final View view = v;
                if(button.isChecked()) {
                    button.setBackground(v.getContext().getResources().getDrawable(R.drawable.rounded_button_following));
                    button.setTextColor(v.getContext().getResources().getColor(R.color.checkedTrue));
                    button.setCompoundDrawablesWithIntrinsicBounds(iconButtonDontFollow, null, null, null);

                    FirebaseHandler refFollow = new FirebaseHandler();
                    final DatabaseReference followingReference = refFollow.getmFollowingReference(mCurrentUid).child(thisUserKey);
                    final DatabaseReference himUuserReference = refFollow.getAllUsersReference().child(thisUserKey);

                    if(utenteFollower == null) {
                        himUuserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User utenteFollower = dataSnapshot.getValue(User.class);
                                User lightUser = new User(utenteFollower.name, null, utenteFollower.photoUrl);
                                followingReference.setValue(lightUser).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(view.getContext(), R.string.operation_failed, Toast.LENGTH_SHORT);
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
                                Toast.makeText(view.getContext(), R.string.operation_failed, Toast.LENGTH_SHORT);
                            }
                        });
                    }

                    final DatabaseReference followerReference = refFollow.getmFollowerReference(thisUserKey).child(mCurrentUid);
                    final DatabaseReference myUserReference = refFollow.getAllUsersReference().child(mCurrentUid);
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
                    DatabaseReference followingReference = refFollow.getmFollowingReference(mCurrentUid).child(thisUserKey);
                    followingReference.removeValue().addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(view.getContext(), "Operazione non riuscita", Toast.LENGTH_SHORT);
                        }
                    });

                    DatabaseReference followerReference = refFollow.getmFollowerReference(thisUserKey).child(mCurrentUid);
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
