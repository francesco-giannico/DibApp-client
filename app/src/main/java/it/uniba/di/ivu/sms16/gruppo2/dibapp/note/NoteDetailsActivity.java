package it.uniba.di.ivu.sms16.gruppo2.dibapp.note;

import android.Manifest;
import android.app.ActivityManager;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.service.NoteDownloadService;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.RatingDialogFragment;

public class NoteDetailsActivity extends BaseActivity implements View.OnClickListener, RatingDialogFragment.RatingDialogListener {

    public static final String EXTRA_NOTE_KEY = "note_key";
    public static final String NOTIFICATION_ID = "notificationId_download";
    private static final String TAG = "NoteDetailsActivity";
    private static final int MY_PERMISSION_WRITE_EXTERNAL_STORAGE = 4;
    private TextView mTitleTextView;
    private TextView mDegreeCourseTextView;
    private TextView mCourseTextView;
    private TextView mTimeUploadTextView;
    private TextView mFormatTextView;
    private RatingBar mNoteRatingBarFix;
    private CircleImageView mAvatarNoteOwner;
    private TextView mInformationOwner;
    private RatingBar mOwnerRatingBar;
    private TextView mDescriptionTextView;
    private String mNoteKey;
    private TextView mDownloadsTextView;
    private TextView mVisitsTextView;
    private FloatingActionButton rateFAB;
    private DialogFragment mRateDialog;
    private Note note;
    private StorageReference mNoteStorageReference;
    private DatabaseReference mNoteReference;
    private DatabaseReference mDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.note_detailsToolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.close);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mNoteKey = getIntent().getStringExtra(EXTRA_NOTE_KEY);
        if (mNoteKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_NOTE_KEY");
        }

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mNoteReference = mDatabaseReference.child("notes").child(mNoteKey);
        mNoteStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://dibapp-dev.appspot.com").child("notes");

        mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mDegreeCourseTextView = (TextView) findViewById(R.id.degreeCourseTextView);
        mCourseTextView = (TextView) findViewById(R.id.courseTextView);
        mTimeUploadTextView = (TextView) findViewById(R.id.timeUploadTextView);
        mFormatTextView = (TextView) findViewById(R.id.formatTextView);
        mNoteRatingBarFix = (RatingBar) findViewById(R.id.noteRatingBarFix);
        mAvatarNoteOwner = (CircleImageView) findViewById(R.id.avatarNoteOwner);
        mInformationOwner = (TextView) findViewById(R.id.informationOwner);
        mOwnerRatingBar = (RatingBar) findViewById(R.id.ownerRatingBar);
        mDescriptionTextView = (TextView) findViewById(R.id.descriptionTextView);
        mDownloadsTextView = (TextView) findViewById(R.id.downloadsTextView);
        mVisitsTextView = (TextView) findViewById(R.id.viewsTextView);
        rateFAB = (FloatingActionButton) findViewById(R.id.rateFab);

        rateFAB.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        mNoteReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                note = dataSnapshot.getValue(Note.class);
                getSupportActionBar().setTitle(getResources().getString(R.string.note_details));

                mTitleTextView.setText(note.title);
                mDegreeCourseTextView.setText(note.degreeCourse);
                mCourseTextView.setText(note.course);
                String s = note.dateUp + " " + note.hourUp;
                mTimeUploadTextView.setText(s);
                mFormatTextView.setText(note.format);
                mNoteRatingBarFix.setRating(note.avgRating);

                FirebaseDatabase.getInstance().getReference().child("users").child(note.owner)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User u = dataSnapshot.getValue(User.class);

                                mInformationOwner.setText(u.name);
                                mOwnerRatingBar.setRating(u.avgRating);

                                if (u.photoUrl == null) {
                                    mAvatarNoteOwner
                                            .setImageDrawable(ContextCompat
                                                    .getDrawable(getApplicationContext(),
                                                            R.drawable.ic_account_circle_dark));
                                } else {
                                    Glide.with(getApplicationContext())
                                            .load(u.photoUrl)
                                            .into(mAvatarNoteOwner);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                mDescriptionTextView.setText(note.description);
                mDownloadsTextView.setText(String.valueOf(note.downloads));

                mNoteReference.child("views").setValue(++note.views);

                mVisitsTextView.setText(String.valueOf(note.views));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting note failed, log a message
                Log.w(TAG, "loadNote:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(NoteDetailsActivity.this, R.string.fail_load_note,
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.note_details_action_button, menu);

        MenuItem delete_item = menu.findItem(R.id.delete_note);
        MenuItem modify_item = menu.findItem(R.id.modify_note);
        MenuItem add_to_my_notes = menu.findItem(R.id.add_my_notes);


        if (note.owner.equals(mCurrentUid)) {
            delete_item.setVisible(true);
            modify_item.setVisible(true);
            add_to_my_notes.setVisible(false);
            this.invalidateOptionsMenu();
        } else {
            delete_item.setVisible(false);
            modify_item.setVisible(false);
            add_to_my_notes.setVisible(true);
            this.invalidateOptionsMenu();
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.note_download) {
            if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(NoteDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSION_WRITE_EXTERNAL_STORAGE);
            } else {
                performDownload();
            }

        } else if (id == R.id.add_my_notes) {
            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("note", note);
            startActivity(intent);
        } else if (id == R.id.delete_note) {
            mNoteReference.removeValue();
            onBackPressed();
        } else if (id == R.id.modify_note) {
            Intent intent = new Intent(this, AddNoteActivity.class);
            intent.putExtra("note", note);
            intent.putExtra("note_key", mNoteKey);
            intent.putExtra("is_modifying", true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    performDownload();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.error_read_write_ext_sto_permission), Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void performDownload() {

        if (isMyServiceRunning(NoteDownloadService.class)) {
            Toast.makeText(NoteDetailsActivity.this, "Attendi lo scaricamento della nota", Toast.LENGTH_SHORT).show();
        } else {
            mNoteReference.child("downloads").setValue(++note.downloads);

            Intent intent = new Intent(this, NoteDownloadService.class);
            intent.setAction(NoteDownloadService.ACTION_DOWNLOAD);
            intent.putExtra(NoteDownloadService.EXTRA_TITLE_PATH, note.title);
            intent.putExtra(NoteDownloadService.EXTRA_FORMAT_PATH, note.format);
            intent.putExtra(NoteDownloadService.EXTRA_REF_STORAGE_PATH, note.refStorage);

            startService(intent);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rateFab: {
                showNoticeDialog();
                break;
            }
        }
    }

    @Override
    public void onRateButtonClick(DialogFragment dialog) {
        RatingDialogFragment ratingDialog = (RatingDialogFragment) dialog;

        float rating = ratingDialog.mRatingBar.getRating();
        float newRating = (((note.avgRating * note.numRating) + rating) / ++note.numRating);
        mNoteReference.child("avgRating").setValue(newRating);
        mNoteReference.child("numRating").setValue(note.numRating);
        mNoteRatingBarFix.setRating(newRating);
        mRateDialog.dismiss();
    }

    public void showNoticeDialog() {
        mRateDialog = new RatingDialogFragment();
        mRateDialog.setCancelable(true);
        mRateDialog.show(getFragmentManager(), "NoticeDialogFragment");
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
