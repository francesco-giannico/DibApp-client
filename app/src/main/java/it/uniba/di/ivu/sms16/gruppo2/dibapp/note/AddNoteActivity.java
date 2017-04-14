package it.uniba.di.ivu.sms16.gruppo2.dibapp.note;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.Calendar;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.service.NoteUploadService;

public class AddNoteActivity extends BaseActivity {

    public static final String NOTIFICATION_ID = "notificationId_upload";
    private static final int READ_REQUEST_CODE = 42;
    private static final int MY_PERMISSION_READ_EXTERNAL_STORAGE = 3;
    public static final String EXTRA_IS_SESSION_NEW_NOTE_KEY = "isSessionNewNote";
    public static final String EXTRA_ID_SESSION_KEY = "studySessionId";

    private TextView srcTextView;
    private ImageButton srcImageButton;
    private EditText titleEditText;
    private AutoCompleteTextView cdlEditText;
    private AutoCompleteTextView cdsEditText;
    private EditText descriptionEditText;
    private Button cancelButton;
    private Button confirmButton;
    private CheckBox mBoardAvailableCheckBox;

    private Uri uri;
    private File file;
    private String fileName;

    private Note note;
    private boolean mIsPrivateNote;
    private boolean mIsSessionNewNote;
    private String mSessionId;

    private DatabaseReference mDatabaseReference;

    private boolean addToMyNotes;
    private boolean modifyNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);
        Toolbar toolbar = (Toolbar) findViewById(R.id.new_noteToolbar);
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

        addToMyNotes = false;
        modifyNote = false;
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        srcTextView = (TextView) findViewById(R.id.srcTextView);
        srcImageButton = (ImageButton) findViewById(R.id.srcImageButton);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        cdlEditText = (AutoCompleteTextView) findViewById(R.id.cdlEditText);
        cdsEditText = (AutoCompleteTextView) findViewById(R.id.cdsEditText);
        descriptionEditText = (EditText) findViewById(R.id.descriptionEditText);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        confirmButton = (Button) findViewById(R.id.confirmButton);
        mBoardAvailableCheckBox = (CheckBox) findViewById(R.id.boardAvailableCheckBox);

        String[] degreeCourses = getResources().getStringArray(R.array.degree_courses_array);
        ArrayAdapter<String> adapter_degree_courses =
                new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, degreeCourses);
        cdlEditText.setAdapter(adapter_degree_courses);

        String[] courses = getResources().getStringArray(R.array.corsi_disponibili);
        ArrayAdapter<String> adapter_courses =
                new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, degreeCourses);
        cdsEditText.setAdapter(adapter_courses);

        Intent intent = getIntent();

        note = (Note) intent.getSerializableExtra("note");
        mIsSessionNewNote = intent.getBooleanExtra(EXTRA_IS_SESSION_NEW_NOTE_KEY, false);
        mSessionId = intent.getStringExtra(EXTRA_ID_SESSION_KEY);


        if (mIsSessionNewNote) {
            mBoardAvailableCheckBox.setVisibility(View.VISIBLE);
        } else {
            mBoardAvailableCheckBox.setVisibility(View.GONE);
        }

        if (intent.getBooleanExtra("is_modifying", false)) {
            modifyNote = true;
            getSupportActionBar().setTitle(R.string.note_details_modify_note);
            srcImageButton.setVisibility(View.INVISIBLE);
            srcTextView.setText(note.title + "." + note.format);
            titleEditText.setText(note.title);
            cdlEditText.setText(note.degreeCourse);
            cdsEditText.setText(note.course);
            descriptionEditText.setText(note.description);

        } else if (note != null) {
            addToMyNotes = true;
            srcImageButton.setVisibility(View.INVISIBLE);
            srcTextView.setText(note.title + "." + note.format);
            titleEditText.setText(note.title);
            cdlEditText.setText(note.degreeCourse);
            cdsEditText.setText(note.course);
            descriptionEditText.setText(note.description);
        }

        srcImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddNoteActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    performFileSearch();
                }
            }
        });

        srcTextView.setKeyListener(null);

        titleEditText.requestFocus();

        titleEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                CharSequence s = titleEditText.getText();
                if (TextUtils.isEmpty(s)) {
                    titleEditText.setError(getString(R.string.empty_field));
                }
                if (s.length() < 3) {
                    titleEditText.setError(getString(R.string.error_title));
                } else {
                    titleEditText.setError(null);
                }
            }
        });

        descriptionEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (TextUtils.isEmpty(descriptionEditText.getText())) {
                    descriptionEditText.setError(getString(R.string.empty_field));
                } else {
                    descriptionEditText.setError(null);
                }
            }
        });

        cdlEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (TextUtils.isEmpty(cdlEditText.getText())) {
                    cdlEditText.setError(getString(R.string.empty_field));
                } else {
                    cdlEditText.setError(null);
                }
            }
        });

        cdsEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (TextUtils.isEmpty(cdsEditText.getText())) {
                    cdsEditText.setError(getString(R.string.empty_field));
                } else {
                    cdsEditText.setError(null);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addToMyNotes) {
                    String key = mDatabaseReference.child("notes").push().getKey();
                    mDatabaseReference.child("notes").child(key).setValue(createNote());
                    onBackPressed();
                } else if (modifyNote) {
                    String note_key = getIntent().getStringExtra("note_key");
                    mDatabaseReference.child("notes").child(note_key).setValue(createNote());
                    onBackPressed();
                } else {
                    if (TextUtils.isEmpty(srcTextView.getText()) || TextUtils.isEmpty(titleEditText.getText()) ||
                            TextUtils.isEmpty(cdlEditText.getText()) || TextUtils.isEmpty(cdsEditText.getText()) ||
                            TextUtils.isEmpty(descriptionEditText.getText())) {
                        Snackbar.make(v, getString(R.string.empty_fields), Snackbar.LENGTH_SHORT).show();

                    } else {
                        uploadFile();
                    }
                }
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    srcTextView.setError(null);
                    performFileSearch();
                } else {
                    srcTextView.setError(getString(R.string.error_read_write_ext_sto_permission));
                }
                return;
            }
        }
    }

    private Note createNote() {
        String title = String.valueOf(titleEditText.getText());
        String degreeCourse = String.valueOf(cdlEditText.getText());
        String course = String.valueOf(cdsEditText.getText());
        String description = String.valueOf(descriptionEditText.getText());
        Calendar calendar = Calendar.getInstance();

        String hourUp = DateFormat.format("HH:mm", calendar).toString();
        String dateUp = DateFormat.format("dd MMM yy", calendar).toString();

        String owner = mCurrentUid;

        String format;
        String refStorage;
        if (addToMyNotes) {
            refStorage = note.refStorage;
            format = note.format;
        } else if (modifyNote) {
            refStorage = note.refStorage;
            format = note.format;
            return new Note(title, note.avgRating, description, note.downloads, note.owner, format,
                    degreeCourse, course, note.hourUp, note.dateUp, note.views, note.notesBoard, note.numRating, refStorage);
        } else {
            refStorage = fileName;
            String[] s = fileName.split("\\.");
            format = s[s.length - 1];
        }

        return new Note(refStorage, dateUp, hourUp, course, degreeCourse, format, owner, description, title, mBoardAvailableCheckBox.isChecked(), mSessionId);
    }

    public void performFileSearch() {

        // ACTION_OPEN_DOCUMENT is the intent to choose a file via the system's file
        // browser.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Filter to only show results that can be "opened", such as a
        // file (as opposed to a list of contacts or timezones)
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        // Filter to show only images, using the image MIME data type.
        // If one wanted to search for ogg vorbis files, the type would be "audio/ogg".
        // To search for all documents available via installed storage providers,
        // it would be "*/*".
        intent.setType("*/*");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            if (resultData != null) {
                uri = resultData.getData();
                String uriString = uri.toString();
                file = new File(uri.getPath());

                if (uriString.startsWith("content://")) {
                    Cursor cursor = null;
                    try {
                        cursor = getApplicationContext().getContentResolver().query(uri, null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                        }
                    } finally {
                        cursor.close();
                    }
                } else if (uriString.startsWith("file://")) {
                    fileName = file.getName();
                }


                //uri = resultData.getData();
                //file = new File(uri.getPath());
                srcTextView.setText(fileName);
            }
        }
    }

    private void uploadFile() {

        if (isMyServiceRunning(NoteUploadService.class)) {
            Toast.makeText(AddNoteActivity.this, R.string.wait_loading_note, Toast.LENGTH_SHORT).show();
        } else {
            Note note = createNote();

            Intent intent = new Intent(this, NoteUploadService.class);
            intent.setAction(NoteUploadService.ACTION_UPLOAD);
            intent.putExtra(NoteUploadService.EXTRA_FILE_NAME, fileName);
            intent.putExtra(NoteUploadService.EXTRA_URI_FILE, uri.toString());
            intent.putExtra(NoteUploadService.EXTRA_NOTE, note);

            startService(intent);

            onBackPressed();
        }
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
