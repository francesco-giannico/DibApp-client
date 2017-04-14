package it.uniba.di.ivu.sms16.gruppo2.dibapp.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;


public class NoteUploadService extends Service {

    /**
     * Actions
     **/
    public static final String ACTION_UPLOAD = "action_upload";
    /**
     * Extras
     **/
    public static final String EXTRA_FILE_NAME = "extra_file_name";
    public static final String EXTRA_URI_FILE = "extra_uri_file";
    public static final String EXTRA_NOTE = "extra_note";
    private static final String TAG = "NoteUploadService";
    private DatabaseReference mDatabaseReference;
    private StorageReference mNoteStorageReference;
    private int mNumTasks = 0;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mNoteStorageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://dibapp-dev.appspot.com").child("notes");
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(getBaseContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand:" + intent + ":" + startId);

        if (ACTION_UPLOAD.equals(intent.getAction())) {
            // Get the path to upload from the intent
            final String fileName = intent.getStringExtra(EXTRA_FILE_NAME);
            final Uri uri = Uri.parse(intent.getStringExtra(EXTRA_URI_FILE));
            final Note note = (Note) intent.getSerializableExtra(EXTRA_NOTE);

            // Mark task started
            Log.d(TAG, ACTION_UPLOAD + ":" + fileName);
            taskStarted();

            UploadTask uploadTask = mNoteStorageReference.child(fileName).putFile(uri);

            mBuilder.setContentTitle(getString(R.string.notification_upload_title) + " " + fileName)
                    .setContentText(getString(R.string.notification_upload_message))
                    .setSmallIcon(R.drawable.ic_upload);

            mNotificationManager.notify(0, mBuilder.build());

            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    final int progress = (int) ((100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount());
                    //System.out.println("Upload is " + progress + "% done");
                    mBuilder.setProgress(100, progress, false);
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    mBuilder.setContentText(getString(R.string.upload_failure));
                    mNotificationManager.notify(0, mBuilder.build());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // Handle successful uploads on complete
                    taskSnapshot.getDownloadUrl();

                    String key = mDatabaseReference.child("notes").push().getKey();
                    mDatabaseReference.child("notes").child(key).setValue(note);

                    //System.out.println("Progress: " + taskSnapshot.);
                    mBuilder.setContentText(getString(R.string.upload_complete)).setProgress(0, 0, false);
                    mNotificationManager.notify(0, mBuilder.build());
                    taskCompleted();
                }
            });
        }

        return START_REDELIVER_INTENT;
    }

    private void taskStarted() {
        changeNumberOfTasks(1);
    }

    private void taskCompleted() {
        changeNumberOfTasks(-1);
    }

    private synchronized void changeNumberOfTasks(int delta) {
        Log.d(TAG, "changeNumberOfTasks:" + mNumTasks + ":" + delta);
        mNumTasks += delta;

        // If there are no tasks left, stop the service
        if (mNumTasks <= 0) {
            Log.d(TAG, "stopping");

            stopSelf();
        }
    }


}
