package it.uniba.di.ivu.sms16.gruppo2.dibapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Parcelable;
import android.widget.TextView;
import android.widget.Toast;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase.FirebaseHandler;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.User;


/**
 * Created by Salvatore on 06/07/2016.
 */
public class NFCHandler implements NfcAdapter.CreateNdefMessageCallback,
        NfcAdapter.OnNdefPushCompleteCallback {

    private NfcAdapter mNfcAdapter;
    private Context activityContext;
    private Activity activity;
    private String mioId;
    private String utenteSeguito;

    public NFCHandler(Context activityContext, Activity activity, String mioId) {
        this.activityContext = activityContext;
        this.activity = activity;
        this.mioId = mioId;
    }

    public void initializeNFCCommunication() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(activityContext);
        if(mNfcAdapter==null){
            Toast.makeText(activityContext,R.string.nfcNotAvailable, Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(activityContext, R.string.nfcAvailable, Toast.LENGTH_LONG).show();

            // this permette di passare come parametro i due metodi di Callback implementati
            // più in basso, rispettivamente onNdefPushComplete e createNdefMessage
            mNfcAdapter.setNdefPushMessageCallback(this, activity);
            mNfcAdapter.setOnNdefPushCompleteCallback(this, activity);
        }
    }

    public void listenNFCCommunication(Intent intent, User mioUser) {
        //Intent intent = activity.getIntent();
        String action = intent.getAction();
        if(action.equals(NfcAdapter.ACTION_NDEF_DISCOVERED)){
            Parcelable[] parcelables =
                    intent.getParcelableArrayExtra(
                            NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage inNdefMessage = (NdefMessage)parcelables[0];
            NdefRecord[] inNdefRecords = inNdefMessage.getRecords();
            NdefRecord NdefRecord_0 = inNdefRecords[0];
            String idUtenteToFollow = new String(NdefRecord_0.getPayload());
            //textInfo.setText(inMsg);
            utenteSeguito = idUtenteToFollow;
            Toast.makeText(activityContext, "Hai iniziato a seguire " + idUtenteToFollow, Toast.LENGTH_LONG);


            if(idUtenteToFollow != null) {
                FirebaseHandler firebaseHandler = new FirebaseHandler();
                firebaseHandler.followNewUserFromNfc(mioId, idUtenteToFollow, mioUser);
            }

        }
    }

    @Override
    public void onNdefPushComplete(NfcEvent event) {

        final String eventString = "onNdefPushComplete\n" + event.toString();
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity.getApplicationContext(),
                        "Hai iniziato a seguire " + utenteSeguito,
                        Toast.LENGTH_LONG).show();
            }
        });

        //activity.finish();
    }

    @Override
    public NdefMessage createNdefMessage(NfcEvent event) {

        String stringOut = mioId;
        byte[] bytesOut = stringOut.getBytes();

        NdefRecord ndefRecordOut = new NdefRecord(
                NdefRecord.TNF_MIME_MEDIA,
                "text/plain".getBytes(),
                new byte[] {},
                bytesOut);

        NdefMessage ndefMessageout = new NdefMessage(ndefRecordOut);
        return ndefMessageout;
    }

}
