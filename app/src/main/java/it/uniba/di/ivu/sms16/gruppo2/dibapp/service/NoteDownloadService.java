package it.uniba.di.ivu.sms16.gruppo2.dibapp.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;

public class NoteDownloadService extends Service {

    /**
     * Actions
     **/
    public static final String ACTION_DOWNLOAD = "action_download";
    /**
     * Extras
     **/
    public static final String EXTRA_TITLE_PATH = "extra_title_path";
    public static final String EXTRA_FORMAT_PATH = "extra_format_path";
    public static final String EXTRA_REF_STORAGE_PATH = "extra_storage_path";
    private static final String TAG = "NoteDownloadService";
    private StorageReference mNoteStorageReference;

    private int mNumTasks = 0;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Storage
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

        if (ACTION_DOWNLOAD.equals(intent.getAction())) {
            // Get the path to download from the intent
            final String title = intent.getStringExtra(EXTRA_TITLE_PATH);
            final String format = intent.getStringExtra(EXTRA_FORMAT_PATH);
            final String refStorage = intent.getStringExtra(EXTRA_REF_STORAGE_PATH);

            // Mark task started
            Log.d(TAG, ACTION_DOWNLOAD + ":" + refStorage);
            taskStarted();

            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

            mBuilder.setContentTitle(getString(R.string.notification_download_title) + " " + title)
                    .setContentText(getString(R.string.notification_download_message))
                    .setSmallIcon(R.drawable.ic_download);

            mNotificationManager.notify(0, mBuilder.build());

            try {
                final File temp = File.createTempFile(title, "." + format, downloadFolder);

                mNoteStorageReference.child(refStorage).getFile(temp).
                        addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mBuilder.setContentText(getString(R.string.download_complete));
                                mNotificationManager.notify(0, mBuilder.build());
                                taskCompleted();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mBuilder.setContentText(getString(R.string.download_failure));
                        mNotificationManager.notify(0, mBuilder.build());
                        temp.delete();
                    }
                });
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
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
