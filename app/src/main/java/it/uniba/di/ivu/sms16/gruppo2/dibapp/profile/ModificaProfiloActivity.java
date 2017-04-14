package it.uniba.di.ivu.sms16.gruppo2.dibapp.profile;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase.FirebaseHandler;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;

/**
 * Created by Salvatore on 10/07/2016.
 */
public class ModificaProfiloActivity extends BaseActivity {

    private static final int RC_PHOTO_PICKER = 101;
    private static final int RC_CAPTURE_IMAGE = 102;
    private static final int PERMISSIONS_REQUEST_CAMERA = 201;
    private static final int PERMISSIONS_REQUEST_STORAGE = 202;
    private EditText editTextPhone;
    private EditText editTextCourse;
    private EditText editTextInterest;
    private CircleImageView profileimage_modifica;
    private Button cancelButton;
    private Button confirmButton;
    private FloatingActionButton floatingSceltaImmagine;
    private AlertDialog dialog;

    private FirebaseDatabase database;
    private FirebaseStorage storage;
    private DatabaseReference databaseRef;
    private FirebaseApp app;
    private StorageReference storageRef;
    private Uri cameraPhotoUri;
    private UploadTask mUploadImageTask;
    private File file;

    private ArrayList<String> interests;
    private ArrayList<String> coursesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifica_profilo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        toolbar.setTitle(R.string.edit_profile);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_clear);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        editTextPhone = (EditText) findViewById(R.id.editPhoneNumber);
        editTextCourse = (EditText) findViewById(R.id.editCourse);
        editTextInterest = (EditText) findViewById(R.id.editInterest);
        cancelButton = (Button) findViewById(R.id.cancelEditProfile);
        confirmButton = (Button) findViewById(R.id.editProfileButton);
        profileimage_modifica = (CircleImageView) findViewById(R.id.profileimage_modifica);
        floatingSceltaImmagine = (FloatingActionButton) findViewById(R.id.fab);

        app = FirebaseApp.getInstance();
        storage = FirebaseStorage.getInstance(app);
        database = FirebaseDatabase.getInstance(app);
        databaseRef = database.getReference().child("users").child(mCurrentUid).child("photoUrl");

        //Init dialog scelta corsi
        setMultichoiceDialog();

        //////////////////  UPLOAD IMAGE   //////////////////////////
        floatingSceltaImmagine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater = LayoutInflater.from(ModificaProfiloActivity.this);
                View promptView = inflater.inflate(R.layout.dialog_add_photo, null);
                final AlertDialog choose = new AlertDialog.Builder(ModificaProfiloActivity.this).create();

                ImageButton galleryImgButton = (ImageButton) promptView.findViewById(R.id.photoGallery);
                ImageButton cameraImgButton = (ImageButton) promptView.findViewById(R.id.photoCamera);

                galleryImgButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        choose.dismiss();

                        if (ContextCompat.checkSelfPermission(ModificaProfiloActivity.this,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(ModificaProfiloActivity.this,
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

                        if (ContextCompat.checkSelfPermission(ModificaProfiloActivity.this,
                                Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED ||
                                ContextCompat.checkSelfPermission(ModificaProfiloActivity.this,
                                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {


                            ActivityCompat.requestPermissions(ModificaProfiloActivity.this,
                                    new String[]{Manifest.permission.CAMERA,
                                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    PERMISSIONS_REQUEST_CAMERA);

                        }
                        else {
                            startCameraActivity();
                        }

                    }
                });

                choose.setView(promptView);
                choose.show();
            }
        });
        ///////////////////////////////////////////////////////////////

        editTextCourse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        editTextInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SceltaInteressiActivity.class);
                startActivity(intent);
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
                updateProfilo();
                onBackPressed();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        storageRef = storage.getReference("images");

        if (requestCode == RC_CAPTURE_IMAGE && resultCode == RESULT_OK) {

            final StorageReference photoRef = storageRef.child(cameraPhotoUri.getLastPathSegment());
            compressImage(cameraPhotoUri.getPath());

            mUploadImageTask = photoRef.putFile(cameraPhotoUri);
            mUploadImageTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    pushImageUri(downloadUrl.toString());
                }
            });


        } else if (requestCode == RC_PHOTO_PICKER && resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
            final StorageReference photoRef = storageRef.child(selectedImageUri.getLastPathSegment());

            mUploadImageTask = photoRef.putFile(selectedImageUri);
            mUploadImageTask.addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    pushImageUri(downloadUrl.toString());
                }
            });
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseHandler firebaseHandler = new FirebaseHandler();
        DatabaseReference myUserReference = firebaseHandler.getmUserReference(mCurrentUid);


        myUserReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User myUserData = dataSnapshot.getValue(User.class);

                if (myUserData.phoneNumber != null) {
                    editTextPhone.setText(myUserData.phoneNumber);
                }
                if(myUserData.followingCourses != null) {
                    editTextCourse.setText(myUserData.obtainFormattedCourses());
                }
                if (myUserData.interests != null) {
                    editTextInterest.setText(myUserData.obtainFormattedInterests());
                    interests = myUserData.interests;
                }
                if (myUserData.photoUrl != null) {
                    //Ottengo immagine da url e inserisco in imageview
                    Glide.with(getBaseContext()).load(myUserData.photoUrl).into(profileimage_modifica);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        editTextPhone.setText(mCurrentUser.phoneNumber);
        editTextInterest.setText(mCurrentUser.obtainFormattedInterests());
    }

    private void startGalleryActivity() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.complete_action)), RC_PHOTO_PICKER);
    }

    private void startCameraActivity() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraPhotoUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraPhotoUri);
        startActivityForResult(intent, RC_CAPTURE_IMAGE);
    }

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
        String path = mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg";
        File mediaFile = new File(path);

        return mediaFile;
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

    private void pushImageUri(String imageUri) {
        databaseRef.setValue(imageUri);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCameraActivity();}
                break;
            }

            case PERMISSIONS_REQUEST_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startGalleryActivity();}
                break;
            }

            default:
                break;
        }
    }

    private void updateProfilo() {
        User user = mCurrentUser;

        FirebaseHandler firebaseHandler = new FirebaseHandler();
        DatabaseReference myUserReference = firebaseHandler.getmUserReference(mCurrentUid);


        myUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User myUserData = dataSnapshot.getValue(User.class);
                myUserData.phoneNumber = editTextPhone.getText().toString();
                myUserData.followingCourses = null;
                myUserData.interests = interests;
                if(coursesList != null) {
                    myUserData.followingCourses = coursesList;
                }
                mCurrentUser = myUserData;
                String myId = mCurrentUid;
                FirebaseHandler firebaseHandler = new FirebaseHandler();
                DatabaseReference myUserReference = firebaseHandler.getmUserReference(myId);
                myUserReference.setValue(myUserData);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setMultichoiceDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String[] courses = getResources().getStringArray(R.array.corsi_disponibili);
        final int DIM_ARRAY = courses.length;
        // Boolean array for initial selected items
        final boolean[] checkedCourses = new boolean[DIM_ARRAY];
        for(int i=0; i<DIM_ARRAY-1; i++) {
            checkedCourses[i] = false;
        }

        builder.setMultiChoiceItems(courses, checkedCourses, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // Update the current focused item's checked status
                checkedCourses[which] = isChecked;
            }
        });

        builder.setCancelable(false);
        builder.setTitle(getString(R.string.change_favorite_course));
        builder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> followingCourses = new ArrayList<String>();

                for(int i=0; i<DIM_ARRAY; i++) {
                    if (checkedCourses[i] == true)
                        followingCourses.add(courses[i]);
                }
                coursesList = followingCourses;
                editTextCourse.setText(obtainFormattedCourses(coursesList));
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
    }

    public String obtainFormattedCourses(ArrayList<String> followingCourses) {
        String coursesString = "";
        if (followingCourses != null) {
            for (String s : followingCourses) {
                if (followingCourses.indexOf(s) == followingCourses.size() - 1) {
                    coursesString += s;
                } else {
                    coursesString += s + ", ";
                }
            }
        }
        return coursesString;
    }
}

