package it.uniba.di.ivu.sms16.gruppo2.dibapp.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class DibAppFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "FMService";

    public void onMessageReceived(RemoteMessage remoteMessage) {
        // Handle data payload of FCM messages.
        Log.d(TAG, "FCM Message Id: " + remoteMessage.getMessageId());

        RemoteMessage.Notification notification = remoteMessage.getNotification();

        Log.d(TAG, "FCM Data Message: " + remoteMessage.getData());
    }

}