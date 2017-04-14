package it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.SessionsBoardActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.SessionsMapActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication.AuthActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.profile.ProfiloUtenteNonPersonaleActivity;

public class SessionDetailsActivity extends BaseActivity {

    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";
    public static final String EXTRA_NOT_FROM_SESSION = "EXTRA_NOT_FROM_SESSION";

    private static final int AUTH_REQUEST_ACTIVITY = 902;

    ValueEventListener mValueEventListener;
    ValueEventListener mAsyncValueEventListener;
    DatabaseReference mParticipantsRef;
    private CoordinatorLayout mCoordinatorLayout;
    private LinearLayout mOrganizerLayout;
    private CircleImageView mOrganizerCircleImageView;
    private TextView mOrganizerTextView;
    private RatingBar mOrganizerRatingBar;
    private TextView mTypeTextView;
    private TextView mDateTextView;
    private TextView mDegreeTextView;
    private TextView mCourseTextView;
    private TextView mDescriptionTextView;
    private LinearLayout mParticipantsLayout;
    private TextView mParticipantsTextView;
    private LinearLayout mMapLayout;
    private TextView mMapTextView;
    private TextView mPlaceTextView;
    private Button mParticipateButton;
    private Button mViewSessionButton;
    private ImageView mTypeImageView;
    private ImageView mDegreeImageView;
    private ImageView mCourseImageView;
    private ImageView mDateImageView;
    private ImageView mDescriptionImageView;
    private ImageView mDetailsPlaceImageView;
    private FirebaseApp app;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;
    private StudySession mSession;
    private String mSessionId;

    private Menu mMenu;

    private boolean mFromSession;
    private boolean mLeftSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_details);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.sessionDetailsPlaceSnackBar);

        final Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        mFromSession = !extras.containsKey(EXTRA_NOT_FROM_SESSION);

        Toolbar toolbar = (Toolbar) findViewById(R.id.details_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        getSupportActionBar().dispatchMenuVisibilityChanged(true);

        mOrganizerLayout = (LinearLayout) findViewById(R.id.organizerLayout);
        mOrganizerCircleImageView = (CircleImageView) findViewById(R.id.organizerCircleImageView);
        mOrganizerTextView = (TextView) findViewById(R.id.organizerTextView);
        mOrganizerRatingBar = (RatingBar) findViewById(R.id.organizerRatingBar);
        mTypeTextView = (TextView) findViewById(R.id.typeTextView);
        mDateTextView = (TextView) findViewById(R.id.dateTextView);
        mDegreeTextView = (TextView) findViewById(R.id.degreeTextView);
        mCourseTextView = (TextView) findViewById(R.id.courseTextView);
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        mParticipantsLayout = (LinearLayout) findViewById(R.id.participantsLayout);
        mParticipantsTextView = (TextView) findViewById(R.id.participantsTextView);
        mMapLayout = (LinearLayout) findViewById(R.id.mapLayout);
        mMapTextView = (TextView) findViewById(R.id.mapTextView);
        mPlaceTextView = (TextView) findViewById(R.id.placeTextView);
        mParticipateButton = (Button) findViewById(R.id.participateButton);
        mViewSessionButton = (Button) findViewById(R.id.viewSessionButton);

        mTypeImageView = (ImageView) findViewById(R.id.detailsTypeImageView);
        mDegreeImageView = (ImageView) findViewById(R.id.detailsDegreeImageView);
        mCourseImageView = (ImageView) findViewById(R.id.detailsCourseImageView);
        mDateImageView = (ImageView) findViewById(R.id.detailsDateImageView);
        mDescriptionImageView = (ImageView) findViewById(R.id.detailsDescritptionImageView);
        mDetailsPlaceImageView = (ImageView) findViewById(R.id.detailsPlaceImageView);


        mSessionId = extras.getString(EXTRA_SESSION_ID);

        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance();

        String photoUrl = null;
        if (mCurrentUser != null) {
            if (mCurrentUser.photoUrl != null) {
                photoUrl = mCurrentUser.photoUrl;
            }
            mCurrentUser = new User(mCurrentUser.name, null, photoUrl);
        }

        mViewSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SessionDetailsActivity.this, SessionActivity.class);
                intent.putExtra(EXTRA_SESSION_ID, mSessionId);
                startActivity(intent);
                finish();
            }
        });

        mParticipateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentUser != null) {
                    joinSession();
                } else {
                    Intent intent = new Intent(SessionDetailsActivity.this, AuthActivity.class);
                    startActivityForResult(intent, AUTH_REQUEST_ACTIVITY);
                }

            }
        });

        mOrganizerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mCurrentUid != null && !mCurrentUid.equals(mSession.organizer)) {

                    Intent intent = new Intent(getBaseContext(), ProfiloUtenteNonPersonaleActivity.class);
                    intent.putExtra(ProfiloUtenteNonPersonaleActivity.EXTRA_PROFILE_KEY, mSession.organizer);
                    startActivity(intent);
                }
            }
        });

        mParticipantsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mSession.numParticipants == 1) {

                    Snackbar.make(mCoordinatorLayout, R.string.only_organizer, Snackbar.LENGTH_SHORT)
                            .show();

                } else {
                    Intent intent = new Intent(getBaseContext(), SessionParticipantsActivity.class);
                    intent.putExtra(EXTRA_SESSION_ID, mSessionId);
                    intent.putExtra(EXTRA_USER_ID, mCurrentUid);
                    startActivity(intent);
                }
            }
        });

        mMapLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GeoPosition coordinate;
                if (mCurrentUser.geoPosition != null) {
                    coordinate = mCurrentUser.geoPosition;
                } else {
                    //francesco deve risolvere i valori null
                    coordinate = new GeoPosition(41.16, 16.4, null);
                }
                Intent intent = new Intent(SessionDetailsActivity.this, SessionsMapActivity.class);
                intent.putExtra(SessionsMapActivity.CALLING_FROM_SESSION_DETAILS_ACTIVITY,
                        coordinate);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        databaseRef = database.getReference("studySessions/" + mSessionId);
        mParticipantsRef = database.getReference("sessionParticipants/" + mSessionId);

        showProgressDialog(getString(R.string.loading));

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mSession = dataSnapshot.getValue(StudySession.class);

                UpdateLayoutAsyncTask updateLayoutAsyncTask = new UpdateLayoutAsyncTask();
                updateLayoutAsyncTask.execute();

                getSupportActionBar().setTitle(mSession.title);
                mTypeTextView.setText(mSession.type);
                String date = mSession.date + ", " + mSession.hourStart + " - " + mSession.hourEnd;
                mDateTextView.setText(date);
                mDegreeTextView.setText(mSession.degreeCourse);
                mCourseTextView.setText(mSession.course);
                mDescriptionTextView.setText(mSession.description);

                String place;
                if (mSession.geoPosition.placeName == null) {
                    place = mSession.geoPosition.lat + "," + mSession.geoPosition.lng;
                } else {
                    place = mSession.geoPosition.placeName;
                }
                mPlaceTextView.setText(place);

                mParticipantsTextView.setText(mSession.numParticipants + " Participants");

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                dismissProgressDialog();

                Snackbar.make(mCoordinatorLayout, databaseError.getMessage(), Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.invalidate();
                            }
                        })
                        .show();

            }
        };

        mAsyncValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mCurrentUid != null) {
                    if (mCurrentUid.equals(mSession.organizer)) {

                        mOrganizerLayout.setOnClickListener(null);
                        mParticipateButton.setVisibility(View.GONE);

                        if (!mFromSession) {
                            mViewSessionButton.setVisibility(View.VISIBLE);
                        } else {
                            mViewSessionButton.setVisibility(View.GONE);
                        }

                    } else if (mSession.numParticipants != 0 && dataSnapshot.hasChild(mCurrentUid)) {
                        mParticipateButton.setVisibility(View.GONE);
                        mMenu.removeItem(R.id.action_edit);

                        if (!mFromSession) {
                            mViewSessionButton.setVisibility(View.VISIBLE);
                        } else {
                            mViewSessionButton.setVisibility(View.GONE);
                        }

                    } else {
                        mParticipateButton.setVisibility(View.VISIBLE);
                        mMenu.removeItem(R.id.action_edit);
                        mMenu.removeItem(R.id.action_leave);

                        mViewSessionButton.setVisibility(View.GONE);
                    }
                } else {
                    mParticipateButton.setVisibility(View.VISIBLE);
                    mMenu.removeItem(R.id.action_edit);
                    mMenu.removeItem(R.id.action_leave);

                    mViewSessionButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        databaseRef.addValueEventListener(mValueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.menu_session_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(menuItem);

            case android.R.id.home:
                if (mFromSession && mLeftSession) {
                    Intent intent = new Intent(this, SessionsBoardActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                finish();
                return true;

            case R.id.action_edit:
                startEditSessionActivity();
                return true;

            case R.id.action_leave:
                leaveSession();
                return true;

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        databaseRef.removeEventListener(mValueEventListener);
        mParticipantsRef.removeEventListener(mAsyncValueEventListener);
        mValueEventListener = null;
        databaseRef = null;
        invalidateOptionsMenu();
    }

    private void startEditSessionActivity() {
        Intent intent = new Intent(getBaseContext(), AddSessionActivity.class);
        Bundle extras = new Bundle();

        extras.putBoolean(AddSessionActivity.EXTRA_MODE_TYPE, true);
        extras.putSerializable(AddSessionActivity.EXTRA_SESSION, mSession);
        extras.putString(AddSessionActivity.EXTRA_SESSION_ID, mSessionId);
        extras.putString(AddSessionActivity.EXTRA_USER_ID, mCurrentUid);
        intent.putExtras(extras);
        startActivity(intent);

    }

    private void leaveSession() {

        AlertDialog.Builder builder = new AlertDialog.Builder(SessionDetailsActivity.this);
        if (mCurrentUid.equals(mSession.organizer)) {

            if (mSession.type.equals("leader")) {
                builder.setMessage(getString(R.string.leader_session_warn))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeSession();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

            } else if (mSession.numParticipants == 1) {
                builder.setMessage(getString(R.string.only_participant_session))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeSession();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
            } else {
                builder.setMessage(getString(R.string.session_leave_random))
                        .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                removeUserAsOrganizer();
                            }
                        })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });
            }

        } else {

            builder.setMessage(getString(R.string.sure_to_leave_session))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            removeUserFromParticipants();
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });

        }

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void removeSession() {
        showProgressDialog(getString(R.string.deleting_session));
        databaseRef.removeEventListener(mValueEventListener);
        mParticipantsRef.removeEventListener(mAsyncValueEventListener);
        databaseRef.removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        dismissProgressDialog();
                        Toast.makeText(getBaseContext(), "Session deleted", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(getBaseContext(), SessionsBoardActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        databaseRef.addValueEventListener(mValueEventListener);
                        mParticipantsRef.addValueEventListener(mAsyncValueEventListener);
                        dismissProgressDialog();
                        Snackbar.make(mCoordinatorLayout, "Session deletion failed", Snackbar.LENGTH_SHORT)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        removeSession();
                                    }
                                })
                                .show();

                    }
                });
    }

    private void removeUserFromParticipants() {
        showProgressDialog(getString(R.string.updating_info));

        DatabaseReference participantsRef = database.getReference("sessionParticipants/" + mSessionId);
        participantsRef.child(mCurrentUid).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        databaseRef.child("numParticipants")
                                .setValue(mSession.numParticipants - 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mLeftSession = true;
                                        dismissProgressDialog();
                                        Snackbar.make(mCoordinatorLayout, "Session info updated", Snackbar.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void removeUserAsOrganizer() {
        showProgressDialog(getString(R.string.updating_info));

        final DatabaseReference participantsRef = database.getReference("sessionParticipants/" + mSessionId);
        participantsRef.child(mCurrentUid).removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        participantsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                DataSnapshot data = dataSnapshot.getChildren().iterator().next();
                                databaseRef.child("organizer")
                                        .setValue(data.getKey())
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                mLeftSession = true;
                                                dismissProgressDialog();
                                                Snackbar.make(mCoordinatorLayout, R.string.session_info_updated, Snackbar.LENGTH_SHORT).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                        databaseRef.child("numParticipants").setValue(mSession.numParticipants - 1);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void joinSession() {

        showProgressDialog(getString(R.string.joining_session));

        DatabaseReference participantsRef = database.getReference("sessionParticipants/" + mSessionId);
        participantsRef.child(mCurrentUid).setValue(mCurrentUser)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        databaseRef.child("numParticipants")
                                .setValue(mSession.numParticipants + 1)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mLeftSession = false;
                                        dismissProgressDialog();
                                        Snackbar.make(mCoordinatorLayout, R.string.session_info_updated, Snackbar.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTH_REQUEST_ACTIVITY)
            if (resultCode == RESULT_OK) {

                Snackbar.make(mCoordinatorLayout, getString(R.string.authentication_success), Snackbar.LENGTH_SHORT).show();
                //joinSession();
            } else {
                Snackbar.make(mCoordinatorLayout, getString(R.string.authentication_failed), Snackbar.LENGTH_SHORT).show();
            }
    }

    private class UpdateLayoutAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (mMenu == null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!mMenu.hasVisibleItems()) {
                getMenuInflater().inflate(R.menu.menu_session_details, mMenu);
            }


            mParticipantsRef.addValueEventListener(mAsyncValueEventListener);

            final DatabaseReference usersRef = database.getReference("users");
            usersRef.child(mSession.organizer).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User organizer = dataSnapshot.getValue(User.class);

                    mOrganizerTextView.setText(organizer.name);

                    if (organizer.avgRating == 0) {
                        mOrganizerRatingBar.setRating(0);
                    } else {
                        mOrganizerRatingBar.setRating(organizer.avgRating * (-1));
                    }

                    if (organizer.photoUrl != null) {
                        Glide.with(getBaseContext())
                                .load(organizer.photoUrl)
                                .into(mOrganizerCircleImageView);
                    } else {
                        mOrganizerCircleImageView
                                .setImageDrawable(ContextCompat
                                        .getDrawable(getBaseContext(),
                                                R.drawable.ic_account_circle_white));
                    }

                    dismissProgressDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
    }
}