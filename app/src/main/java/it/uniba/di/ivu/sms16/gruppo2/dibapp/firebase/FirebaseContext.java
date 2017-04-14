package it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase;

import com.firebase.client.Firebase;

/**
 * Created by frank on 17/06/2016.
 */
public class FirebaseContext extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }


}
