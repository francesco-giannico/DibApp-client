package it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Message;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.note.AddNoteActivity;

public class SessionActivity extends BaseActivity {

    private static final int RC_PHOTO_PICKER = 101;
    private static final int RC_CAPTURE_IMAGE = 102;

    private static final int PERMISSIONS_REQUEST_CAMERA = 201;
    private static final int PERMISSIONS_REQUEST_STORAGE = 202;

    public static final String EXTRA_SESSION_ID = "EXTRA_SESSION_ID";
    public static final String EXTRA_USER_ID = "EXTRA_USER_ID";

    private String mSessionID;

    private FirebaseApp app;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseStorage storage;

    private DatabaseReference databaseRef;
    private StorageReference storageRef;

    private EditText mMessageEditText;
    private ImageButton mSendButton;
    private ImageButton mAddPhotoButton;
    private ImageButton mCancelImageUploadButton;
    private Uri cameraPhotoUri;
    private ProgressBar mImageProgressBar;
    private LinearLayout mSendMessageBarContainer;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private FloatingActionButton mFab;

    private View.OnLayoutChangeListener mOnLayoutChangeListener;
    private View.OnSystemUiVisibilityChangeListener mOnVisibilityChangeListener;


    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {

            if (position == 0) {

                mFab.setVisibility(View.GONE);
                mSendMessageBarContainer.setVisibility(View.VISIBLE);

            } else {

                mFab.setVisibility(View.VISIBLE);
                mSendMessageBarContainer.setVisibility(View.GONE);

                ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
                lp.bottomMargin = 0;
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            //DO NOTHING
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            //DO NOTHING
        }
    };


    private UploadTask mUploadImageTask;

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "DibApp");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("DibApp", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        mSessionID = extras.getString(SessionDetailsActivity.EXTRA_SESSION_ID);


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mImageProgressBar = (ProgressBar) findViewById(R.id.imageProgressBar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setVisibility(View.GONE);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(SessionActivity.this, AddNoteActivity.class);
                intent.putExtra(AddNoteActivity.EXTRA_IS_SESSION_NEW_NOTE_KEY, true);
                intent.putExtra(AddNoteActivity.EXTRA_ID_SESSION_KEY, mSessionID);
                startActivity(intent);
            }
        });


        mSendMessageBarContainer = (LinearLayout) findViewById(R.id.sendMessageBarContainer);

        mOnLayoutChangeListener = new View.OnLayoutChangeListener() {

            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) mViewPager.getLayoutParams();
            int lpBottom = lp.bottomMargin;

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                                       int oldLeft, int oldTop, int oldRight, int oldBottom) {

                lp.bottomMargin = lpBottom + (bottom - top);
            }
        };

        mOnVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if (visibility == View.GONE) {
                    mSendMessageBarContainer.removeOnLayoutChangeListener(mOnLayoutChangeListener);
                }

                if (visibility == View.VISIBLE) {
                    mSendMessageBarContainer.addOnLayoutChangeListener(mOnLayoutChangeListener);
                }
            }
        };


        app = FirebaseApp.getInstance();
        database = FirebaseDatabase.getInstance(app);
        auth = FirebaseAuth.getInstance(app);
        storage = FirebaseStorage.getInstance(app);

        databaseRef = database.getReference("messages/" + mSessionID);
        DatabaseReference sessionsRef = database.getReference("studySessions/" + mSessionID);

        sessionsRef.child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getSupportActionBar().setTitle((String) dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                getSupportActionBar().setTitle("Session");
            }
        });


        mMessageEditText = (EditText) findViewById(R.id.messageEditText);
        mMessageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mSendButton.setImageResource(R.drawable.ic_send_active);
                    mSendButton.setEnabled(true);

                    mAddPhotoButton.setImageResource(R.drawable.ic_add_image_inactive);
                    mAddPhotoButton.setEnabled(false);

                } else {
                    mSendButton.setImageResource(R.drawable.ic_send_inactive);
                    mSendButton.setEnabled(false);

                    mAddPhotoButton.setImageResource(R.drawable.ic_add_image_active);
                    mAddPhotoButton.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        mSendButton = (ImageButton) findViewById(R.id.sendButton);
        mSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pushMessage(mMessageEditText.getText().toString());
            }
        });


        mAddPhotoButton = (ImageButton) findViewById(R.id.addPhotoButton);
        mAddPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(SessionActivity.this);
                View promptView = inflater.inflate(R.layout.dialog_add_photo, null);
                final AlertDialog choose = new AlertDialog.Builder(SessionActivity.this).create();

                ImageButton galleryImgButton = (ImageButton) promptView.findViewById(R.id.photoGallery);
                ImageButton cameraImgButton = (ImageButton) promptView.findViewById(R.id.photoCamera);

                galleryImgButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        choose.dismiss();

                        if (ContextCompat.checkSelfPermission(SessionActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(SessionActivity.this,
                                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                    PERMISSIONS_REQUEST_STORAGE);

                        } else {
                            startGalleryActivity();
                        }

                    }
                });

                cameraImgButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        choose.dismiss();

                        if (ContextCompat.checkSelfPermission(SessionActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(SessionActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {


                            ActivityCompat.requestPermissions(SessionActivity.this,
                                    new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSIONS_REQUEST_CAMERA);

                        } else {
                            startCameraActivity();
                        }

                    }
                });

                choose.setView(promptView);
                choose.show();
            }
        });

        mCancelImageUploadButton = (ImageButton) findViewById(R.id.cancelImageUploadButton);
        mCancelImageUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUploadImageTask.cancel();
                mMessageEditText.setText("");
                mMessageEditText.setEnabled(true);
                mImageProgressBar.setVisibility(View.GONE);
                mCancelImageUploadButton.setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        mSendMessageBarContainer.addOnLayoutChangeListener(mOnLayoutChangeListener);
        mSendMessageBarContainer.setOnSystemUiVisibilityChangeListener(mOnVisibilityChangeListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        mSendMessageBarContainer.removeOnLayoutChangeListener(mOnLayoutChangeListener);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startCameraActivity();
                }
                break;
            }

            case PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    startGalleryActivity();
                }
                break;

            }

            default:
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_session, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);

            case android.R.id.home:
                finish();
                return true;

            case R.id.action_info:
                Intent intent = new Intent(this, SessionDetailsActivity.class);
                intent.putExtra(SessionDetailsActivity.EXTRA_SESSION_ID, mSessionID);
                intent.putExtra(SessionDetailsActivity.EXTRA_USER_ID, mCurrentUid);
                startActivity(intent);
                return true;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        storageRef = storage.getReference("images");

        if (requestCode == RC_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            mMessageEditText.setText(R.string.uploading_image);
            mSendButton.setImageResource(R.drawable.ic_send_inactive);
            mMessageEditText.setEnabled(false);
            mCancelImageUploadButton.setVisibility(View.VISIBLE);
            mImageProgressBar.setVisibility(View.VISIBLE);

            final StorageReference photoRef = storageRef.child(cameraPhotoUri.getLastPathSegment());
            compressImage(cameraPhotoUri.getPath());
            mUploadImageTask = photoRef.putFile(cameraPhotoUri);
            mUploadImageTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    pushMessage(downloadUrl.toString());
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mImageProgressBar.setIndeterminate(true);
                        }
                    });


        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {
            mMessageEditText.setText(R.string.uploading_image);
            mSendButton.setImageResource(R.drawable.ic_send_inactive);
            mMessageEditText.setEnabled(false);
            mCancelImageUploadButton.setVisibility(View.VISIBLE);
            mImageProgressBar.setVisibility(View.VISIBLE);

            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());

            mUploadImageTask = photoRef.putFile(selectedImageUri);
            mUploadImageTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    pushMessage(downloadUrl.toString());
                }
            })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            mImageProgressBar.setIndeterminate(true);
                        }
                    });
        }
    }

    private void pushMessage(String content) {
        SimpleDateFormat hourFormatter = new SimpleDateFormat("HH:mm");
        SimpleDateFormat dayFormatter = new SimpleDateFormat("d MMM");


        Message message = new Message(
                mCurrentUid,
                mCurrentUser.name,
                mCurrentUser.photoUrl,
                content,
                dayFormatter.format(new Date()),
                hourFormatter.format(new Date()));


        databaseRef.push().setValue(message);

        mMessageEditText.setText("");
        mMessageEditText.setEnabled(true);
        mImageProgressBar.setVisibility(View.GONE);
        mCancelImageUploadButton.setVisibility(View.GONE);
    }

    private void startCameraActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraPhotoUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
        startActivityForResult(intent, RC_CAPTURE_IMAGE);
    }

    private void startGalleryActivity() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action)), RC_PHOTO_PICKER);
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {

                return ChannelFragment.newInstance(mCurrentUid, mSessionID);

            }
            else return NotesFragment.newInstance(mCurrentUid, mSessionID);
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getString(R.string.channel_fragment_title);
                case 1:
                    return getString(R.string.notes_fragment_title);
            }
            return null;
        }
    }

    private static File compressImage(String pathBigImage) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap bmpPic = BitmapFactory.decodeFile(pathBigImage);
        bmpPic.compress(Bitmap.CompressFormat.JPEG, 50, out);

        OutputStream outputFileCompressed;
        try {
            outputFileCompressed = new FileOutputStream(pathBigImage);
            out.writeTo(outputFileCompressed);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File fileCompressed = new File(pathBigImage);
        return fileCompressed;
    }
}
