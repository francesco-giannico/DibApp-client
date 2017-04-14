package it.uniba.di.ivu.sms16.gruppo2.dibapp.utils;


import android.location.Location;
import android.util.Log;

import com.firebase.client.Firebase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;


public class FirebaseMappa extends android.app.Application{
    private static final String TAG = FirebaseMappa.class.getSimpleName();
    private static String MYDB="https://dibapp-dev.firebaseio.com";
    private static String STUDYSESSIONS = "/studySessions";
    private static String USERS = "/users";
    private static String GEOPOSITION = "/geoPosition";
    private DatabaseReference firebase=null;
    private int Range=500;
    private int selectionSpinner=0;
    @Override
    public void onCreate(){
        super.onCreate();
        Firebase.setAndroidContext(this);
    }

    /**
     * Vengono recuperate le sessioni che rientrano in una certa distanza con l'utente, in base al range che ha impostato
     *
     * @param connessoreGoogleApiLocation
     * @param tipo
     */
    public void sessioniVicine(final ConnessoreGoogleApiLocation connessoreGoogleApiLocation, int tipo, int range){

        selectionSpinner=tipo;
        Range=range;
        //Codice importante non cambiare mai.
        if (connessoreGoogleApiLocation.getMarkers()!=null) {
            connessoreGoogleApiLocation.clearMap();
            //rimetto il marker in base all'ultima posizione
            connessoreGoogleApiLocation.impostaMarkerInDeterminateParametri();
            connessoreGoogleApiLocation.clearMarker();
        } else
            connessoreGoogleApiLocation.createNewHashMap();
        firebase = FirebaseDatabase.getInstance().getReference("studySessions");
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    DatabaseReference x= FirebaseDatabase.getInstance().getReference(
                            STUDYSESSIONS+"/"+ds.getKey());
                    x.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            StudySession sessione= dataSnapshot.getValue(StudySession.class);
                            sessione.id=dataSnapshot.getKey();
                            aggiornaMarkers(sessione, connessoreGoogleApiLocation);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "getUser:onCancelled", databaseError.toException());
            }
        });

        //aggiorno posizione attuale utente
        updatePositionCurrentUser(connessoreGoogleApiLocation);

    }
    private void updatePositionCurrentUser(ConnessoreGoogleApiLocation connessoreGoogleApiLocation){
        FirebaseUser fireUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fireUser!=null){
            DatabaseReference fb =
                    FirebaseDatabase.getInstance().getReferenceFromUrl(MYDB+USERS+"/"+fireUser.getUid()+GEOPOSITION);
            fb.child("lat").setValue(connessoreGoogleApiLocation.getCurrentPosition().latitude);
            fb.child("lng").setValue(connessoreGoogleApiLocation.getCurrentPosition().longitude);
        }
    }


    private void aggiornaMarkers(StudySession sessione,ConnessoreGoogleApiLocation connessoreGoogleApiLocation){

        //metto il nuovo marker sulla mappa solo se posso
        LatLng sessCoords= new LatLng(sessione.geoPosition.lat,sessione.geoPosition.lng);

        switch (selectionSpinner) {
            case 0://metti il marker normalmente
                if (getDistance(connessoreGoogleApiLocation.getCurrentPosition(), sessCoords) <= Range) {
                    aggiungiMarker(connessoreGoogleApiLocation, sessione, true);
                }
                break;
            case 1:
                if (getDistance(connessoreGoogleApiLocation.getCurrentPosition(), sessCoords) <= Range) {
                    if (sessione.type.equals("leaderless"))
                        aggiungiMarker(connessoreGoogleApiLocation, sessione, true);
                    else
                        aggiungiMarker(connessoreGoogleApiLocation, sessione, false);
                }
                break;
            case 2:

                if (getDistance(connessoreGoogleApiLocation.getCurrentPosition(), sessCoords) <= Range) {
                    if (sessione.type.equals("teaching"))
                        aggiungiMarker(connessoreGoogleApiLocation, sessione, true);
                    else
                        aggiungiMarker(connessoreGoogleApiLocation, sessione, false);
                }
                break;
            case 3://già iniziate
                if (getDistance(connessoreGoogleApiLocation.getCurrentPosition(), sessCoords) <= Range) {
                    String dataInizio = sessione.date;
                    String oraInizio = sessione.hourStart;
                    String DataEOraInizio = dataInizio + " " + oraInizio;
                    DateFormat format = new SimpleDateFormat("d MM HH:mm", Locale.ENGLISH);
                    Date dateSessionStart = null;
                    Date actualDate = new Date();//data attuale
                    try {
                        dateSessionStart = format.parse(DataEOraInizio);
                        if (actualDate.after(dateSessionStart) || actualDate.equals(dateSessionStart))
                            aggiungiMarker(connessoreGoogleApiLocation, sessione, true);
                        else
                            aggiungiMarker(connessoreGoogleApiLocation, sessione, true);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }

    }
    /**distanza entro un certo raggio in base alla scelta utente corrente nelle impostazioni*/
    public double getDistance(LatLng LatLng1, LatLng LatLng2) {
        double distance = 0;
        Location locationA = new Location("A");
        locationA.setLatitude(LatLng1.latitude);
        locationA.setLongitude(LatLng1.longitude);
        Location locationB = new Location("B");
        locationB.setLatitude(LatLng2.latitude);
        locationB.setLongitude(LatLng2.longitude);
        distance = locationA.distanceTo(locationB);
        return distance;
    }

    private void aggiungiMarker(ConnessoreGoogleApiLocation connessoreGoogleApiLocation, StudySession sessione, boolean visibility) {
        //metto il nuovo marker sulla mappa

        MarkerOptions options= new MarkerOptions().position(new LatLng(sessione.geoPosition.lat,
                sessione.geoPosition.lng)).title(sessione.title).snippet(sessione.description);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

        Marker marker = connessoreGoogleApiLocation.addMarkerOnMap(options);
        marker.setVisible(visibility);
        //se la sessione non è già presente allora aggiungo il marker altrimenti lo rimpiazzo ( allo stesso tempo mi serve un contenitore per trovare un marker subito,
        //ecco xk sta fatta sta scelta.
        if (!connessoreGoogleApiLocation.getMarkers().containsValue(sessione)) {
            connessoreGoogleApiLocation.getMarkers().put(marker, sessione);
        } else {
            //rimuovo il vecchio marker di questa sessione
            Marker mark = connessoreGoogleApiLocation.getMarkers().getMarker(sessione);
            mark.remove();
            //metto quello aggiornato
            connessoreGoogleApiLocation.getMarkers().remove(mark);
            connessoreGoogleApiLocation.getMarkers().put(marker, sessione);
        }
    }




    public void setRange(int range,ConnessoreGoogleApiLocation connessoreGoogleApiLocation){
        Range=range;
        updatePositionCurrentUser(connessoreGoogleApiLocation);
    }
}