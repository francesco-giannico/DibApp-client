package it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;


public class AddSessionActivity extends BaseActivity implements View.OnClickListener {

    public static final String EXTRA_USER_ID = "ADD_SESSION_EXTRA_USER_ID";
    public static final String EXTRA_MODE_TYPE = "EXTRA_MODE_TYPE";
    public static final String EXTRA_SESSION = "EXTRA_SESSION";
    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    private static final int PLACE_PICKER_REQUEST = 501;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 203;


    private String mUserId;

    private CoordinatorLayout mCoordinatorLayout;
    private EditText mTitleEditText;
    private EditText mDegreeEditText;
    private AutoCompleteTextView mCourseAutoComplete;
    private EditText mDateEditText;
    private EditText mHourStartEditText;
    private EditText mHourEndEditText;
    private EditText mAddSessionPlaceEditText;
    private EditText mDescriptionEditText;
    private ToggleButton mTypeToggleBtn;

    private Button mCancelButton;
    private Button mDoneButton;

    private ScrollView mScrollView;
    private LinearLayout mBottomLayout;
    private ImageView mTypeImageView;
    private ImageView mDegreeImageView;
    private ImageView mCourseImageView;
    private ImageView mDateImageView;
    private ImageView mDescriptionImageView;

    private FirebaseApp app;
    private FirebaseDatabase database;
    private DatabaseReference databaseRef;
    private FirebaseAuth auth;

    private User mUser;
    private StudySession mSession;
    private String mSessionId;

    private boolean editMode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(it.uniba.di.ivu.sms16.gruppo2.dibapp.R.layout.activity_add_session);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.addSessionPlaceSnackBar);

        Intent intent = getIntent();
        final Bundle extras = intent.getExtras();
        mUserId = extras.getString(EXTRA_USER_ID);

        editMode = false;
        if (extras.containsKey(EXTRA_MODE_TYPE)) {
            editMode = true;
            mSession = (StudySession) extras.getSerializable(EXTRA_SESSION);
            mSessionId = extras.getString(EXTRA_SESSION_ID);
        } else {
            mSession = new StudySession();
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.add_session_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_close_white);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (editMode) {
            getSupportActionBar().setTitle(R.string.edit_session);
        } else {
            getSupportActionBar().setTitle(R.string.add_session);
        }

        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        databaseRef = database.getReference("studySessions");
        auth = FirebaseAuth.getInstance();

        String photoUrl = null;
        if (auth.getCurrentUser().getPhotoUrl() != null) {
            photoUrl = auth.getCurrentUser().getPhotoUrl().toString();
        }
        mUser = new User(auth.getCurrentUser().getDisplayName(), null, photoUrl);

        mScrollView = (ScrollView) findViewById(R.id.addSessionScrollView);
        mBottomLayout = (LinearLayout) findViewById(R.id.addSessionBottomLayout);
        mBottomLayout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mScrollView.getLayoutParams();
            int lpBottom = lp.bottomMargin;

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                lp.bottomMargin = lpBottom + (bottom - top);
            }
        });

        mDoneButton = (Button) findViewById(R.id.addSessionDoneButton);
        mCancelButton = (Button) findViewById(R.id.addSessionCancelButton);

        mTitleEditText = (EditText) findViewById(R.id.addSessionTitleEditText);
        mDegreeEditText = (EditText) findViewById(R.id.addSessionDegreeEditText);
        mCourseAutoComplete = (AutoCompleteTextView) findViewById(R.id.addSessionCourseAutoComplete);
        mDateEditText = (EditText) findViewById(R.id.addSessionDateEditText);
        mHourStartEditText = (EditText) findViewById(R.id.addSessionHourStartEditText);
        mHourEndEditText = (EditText) findViewById(R.id.addSessionHourEndEditText);
        mDescriptionEditText = (EditText) findViewById(R.id.addSessionDescriptionEditText);
        mTypeToggleBtn = (ToggleButton) findViewById(R.id.addSessionTypeToggleBtn);

        mTypeImageView = (ImageView) findViewById(R.id.addSessionTypeImageView);
        mDegreeImageView = (ImageView) findViewById(R.id.addSessionDegreeImageView);
        mCourseImageView = (ImageView) findViewById(R.id.addSessionCourseImageView);
        mDateImageView = (ImageView) findViewById(R.id.addSessionDateImageView);
        mAddSessionPlaceEditText = (EditText) findViewById(R.id.addSessionPlaceEditText);
        mDescriptionImageView = (ImageView) findViewById(R.id.addSessionDescriptionImageView);

        String[] courses = getResources().getStringArray(R.array.corsi_disponibili);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(AddSessionActivity.this, android.R.layout.simple_list_item_1, courses);
        mCourseAutoComplete.setAdapter(adapter);

        mDoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fieldsNotEmpty()) {
                    mSession.organizer = mUserId;
                    mSession.title = mTitleEditText.getText().toString();
                    mSession.course = mCourseAutoComplete.getText().toString();
                    mSession.description = mDescriptionEditText.getText().toString();

                    if (mTypeToggleBtn.getText().equals(mTypeToggleBtn.getTextOn())) {
                        mSession.type = "leaderless";
                    } else {
                        mSession.type = "leader";
                    }

                    if (editMode) {
                        updateSession();
                    } else {
                        pushSession();
                    }

                } else {

                    Snackbar.make(mCoordinatorLayout, R.string.check_fields, Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(AddSessionActivity.this);
                if (editMode) {
                    builder.setMessage(getString(R.string.cancel_session_answ));
                } else {
                    builder.setMessage(getString(R.string.cancel_add_session));
                }
                builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                        .setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        mTitleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mTitleEditText.getError() == null) {
                    return;
                }

                if (mTitleEditText.getText().toString().isEmpty()) {
                    mTitleEditText.setError(getString(R.string.field_not_empty));
                }
            }
        });

        mCourseAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mCourseAutoComplete.getError() == null) {
                    return;
                }

                if (mCourseAutoComplete.getText().toString().isEmpty()) {
                    mCourseAutoComplete.setError(getString(R.string.field_not_empty));
                }

            }
        });

        mDescriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && mDescriptionEditText.getError() == null) {
                    return;
                }

                if (mDescriptionEditText.getText().toString().isEmpty()) {
                    mDescriptionEditText.setError(getString(R.string.field_not_empty));
                }
            }
        });

        mDateEditText.setKeyListener(null);
        mDateEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            SimpleDateFormat dayFormatter = new SimpleDateFormat("d MMM");

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    final Calendar c = Calendar.getInstance();
                    int year = c.get(Calendar.YEAR);
                    final int month = c.get(Calendar.MONTH);
                    int day = c.get(Calendar.DAY_OF_MONTH);


                    final DatePickerDialog mDatePicker;
                    mDatePicker = new DatePickerDialog(AddSessionActivity.this,
                            new DatePickerDialog.OnDateSetListener() {


                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    String gotDate = dayFormatter.format(new Date(year, monthOfYear, dayOfMonth));

                                    mDateEditText.setText(gotDate);
                                    mSession.date = gotDate;

                                    mDateEditText.setError(null);

                                }

                            }, year, month, day);

                    mDatePicker.setTitle(getString(R.string.select_time));
                    mDatePicker.show();

                    mDateEditText.clearFocus();
                    return;
                }

                if (mDateEditText.getText().toString().isEmpty()) {
                    mDateEditText.setError(getString(R.string.field_not_empty));
                }
            }
        });

        mHourStartEditText.setKeyListener(null);
        mHourStartEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(AddSessionActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    String gotHourStart = String.format(Locale.ITALY, "%02d", selectedHour)
                                            + ":" + String.format(Locale.ITALY, "%02d", selectedMinute);

                                    mHourStartEditText.setText(gotHourStart);
                                    mSession.hourStart = gotHourStart;

                                    mHourStartEditText.setError(null);

                                }

                            }, hour, minute, true);

                    mTimePicker.setTitle(getString(R.string.select_time));
                    mTimePicker.show();

                    mHourStartEditText.clearFocus();
                    return;
                }


                if (mHourStartEditText.getText().toString().isEmpty()) {
                    mHourStartEditText.setError(getString(R.string.field_not_empty));
                }
            }
        });

        mHourEndEditText.setKeyListener(null);
        mHourEndEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    final Calendar c = Calendar.getInstance();
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);

                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(AddSessionActivity.this,
                            new TimePickerDialog.OnTimeSetListener() {

                                @Override
                                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                                    String gotHourEnd = String.format(Locale.ITALY, "%02d", selectedHour)
                                            + ":" + String.format(Locale.ITALY, "%02d", selectedMinute);

                                    mHourEndEditText.setText(gotHourEnd);
                                    mSession.hourEnd = gotHourEnd;

                                    mHourEndEditText.setError(null);
                                }

                            }, hour, minute, true);

                    mTimePicker.setTitle(getString(R.string.select_time));
                    mTimePicker.show();

                    mHourEndEditText.clearFocus();
                    return;
                }

                if (mHourEndEditText.getText().toString().isEmpty()) {
                    mHourEndEditText.setError(getString(R.string.field_not_empty));
                }
            }
        });

        mAddSessionPlaceEditText.setKeyListener(null);
        mAddSessionPlaceEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {

                    startPlacePickerActivity();
                    mAddSessionPlaceEditText.clearFocus();
                    return;
                }


                if (mHourEndEditText.getText().toString().isEmpty()) {
                    mHourEndEditText.setError(getString(R.string.field_not_empty));
                }
            }
        });



        mDescriptionImageView.setOnClickListener(this);/*new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mCoordinatorLayout, "Indica una breve descrizione utile per i partecipanti.", Snackbar.LENGTH_INDEFINITE)
                        .setAction("GOT IT", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.invalidate();
                            }
                        })
                        .show();
            }
        });*/

        /*mDescriptionEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mDoneButton.performClick();
                    return true;
                }
                return false;
            }
        });*/


        if (editMode) {
            mTitleEditText.setText(mSession.title);
            mTitleEditText.setEnabled(false);

            if (mSession.type.equals("leaderless")) {
                mTypeToggleBtn.setText(mTypeToggleBtn.getTextOn());
            } else {
                mTypeToggleBtn.setText(mTypeToggleBtn.getTextOff());
            }
            mTypeToggleBtn.setEnabled(false);

            mDegreeEditText.setText(mSession.degreeCourse);
            mDegreeEditText.setEnabled(false);
            mCourseAutoComplete.setText(mSession.course);
            mCourseAutoComplete.setEnabled(false);

            mDateEditText.setText(mSession.date);
            mHourStartEditText.setText(mSession.hourStart);
            mHourEndEditText.setText(mSession.hourEnd);
            mAddSessionPlaceEditText.setText(mSession.geoPosition.placeName);
            mDescriptionEditText.setText(mSession.description);
        }
    }

    private void startPlacePickerActivity() {

        showProgressDialog(getString(R.string.opening_map));

        try {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            Intent intent = builder.build(this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);

        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        showProgressDialog("Loading...");

        DatabaseReference usersRef = database.getReference("users");
        if (!editMode) {
            usersRef.child(mUserId).child("degreeCourse").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String degree = (String) dataSnapshot.getValue();

                    mDegreeEditText.setText(degree);
                    mDegreeEditText.setKeyListener(null);
                    mSession.degreeCourse = degree;
                    mSession.numParticipants = 1;

                    hideProgressDialog();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            hideProgressDialog();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            default:
                return super.onOptionsItemSelected(menuItem);

            case android.R.id.home:
                finish();
                return true;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PLACE_PICKER_REQUEST) {

            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);

                if (place.getName() != null) {
                    mSession.geoPosition = new GeoPosition(place.getLatLng().latitude,
                            place.getLatLng().longitude, place.getName().toString());
                } else {
                    mSession.geoPosition = new GeoPosition(place.getLatLng().latitude,
                            place.getLatLng().longitude, null);
                }

                mAddSessionPlaceEditText.setError(null);
                mAddSessionPlaceEditText.setText(place.getName());

            } else {
                mAddSessionPlaceEditText.setError(getString(R.string.choice_place));
            }
        }
    }

    private void pushSession() {

        showProgressDialog(getString(R.string.creating_session));

        final DatabaseReference participantsRef = database.getReference("sessionParticipants/");
        final DatabaseReference resultRef = databaseRef.push();

        resultRef.setValue(mSession)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        participantsRef.child(resultRef.getKey()).child(mUserId)
                                .setValue(new User(mUser.name, null, mUser.photoUrl))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        dismissProgressDialog();
                                        Toast.makeText(getBaseContext(), R.string.session_created, Toast.LENGTH_LONG).show();
                                        finish();
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
                        dismissProgressDialog();
                        Snackbar.make(mCoordinatorLayout, e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                                .setAction(getString(R.string.retry), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pushSession();
                                    }
                                })
                                .setAction(getString(R.string.cancel), new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.invalidate();
                                    }
                                })
                                .show();
                    }
                });

    }


    private void updateSession() {
        showProgressDialog(getString(R.string.updating_session));

        databaseRef.child(mSessionId).setValue(mSession)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        dismissProgressDialog();
                        Toast.makeText(getBaseContext(), R.string.session_updated, Toast.LENGTH_LONG).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dismissProgressDialog();
                        Snackbar.make(mCoordinatorLayout, e.getMessage(), Snackbar.LENGTH_INDEFINITE)
                                .setAction("RETRY", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        pushSession();
                                    }
                                })
                                .setAction("CANCEL", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        v.invalidate();
                                    }
                                })
                                .show();
                    }
                });
    }


    private boolean fieldsNotEmpty() {
        return !(mTitleEditText.getText().toString().isEmpty() ||
                mCourseAutoComplete.getText().toString().isEmpty() ||
                mDateEditText.getText().toString().isEmpty() ||
                mHourStartEditText.getText().toString().isEmpty() ||
                mHourEndEditText.getText().toString().isEmpty() ||
                mDescriptionEditText.getText().toString().isEmpty() ||
                mAddSessionPlaceEditText.getText().toString().isEmpty()
        );

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addSessionDescriptionImageView:
                Snackbar.make(mCoordinatorLayout, R.string.set_brief_description, Snackbar.LENGTH_INDEFINITE)
                        .setAction("GOT IT", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                v.invalidate();
                            }
                        })
                        .show();
                break;

        }
    }
}