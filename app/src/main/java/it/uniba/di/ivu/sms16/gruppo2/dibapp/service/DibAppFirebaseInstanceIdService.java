package it.uniba.di.ivu.sms16.gruppo2.dibapp.service;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class DibAppFirebaseInstanceIdService extends FirebaseInstanceIdService {

    /**
     * The Application's current Instance ID token is no longer valid
     * and thus a new one must be requested.
     */
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();

        addRegistrationToFireDb(token);
    }

    private void addRegistrationToFireDb(String token) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference devicesRef = database.getReference("backend/devices");

        FirebaseAuth fireAuth = FirebaseAuth.getInstance();
        if (fireAuth.getCurrentUser() != null) {

            devicesRef.child(fireAuth.getCurrentUser().getUid()).setValue(token);

        }
    }


}