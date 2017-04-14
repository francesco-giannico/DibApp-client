package it.uniba.di.ivu.sms16.gruppo2.dibapp.utils;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.DrawerActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;

/**
 * Classe che si occupa principalmente dell'impostazione della comunicazione con google maps api
 */
public class ConnessoreGoogleApiLocation implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener, NavigationView.OnNavigationItemSelectedListener {
    protected static final String TAG = ConnessoreGoogleApiLocation.class.getSimpleName();
    protected final static int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    //GOOGLE API REFERENCES
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 204;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    private static int UPDATE_INTERVAL = 100000000;
    private static int FATEST_INTERVAL = 5000000;
    public GoogleMap mMap;
    //GeoFire
    private MyHashMap markers;
    protected Location mLastLocation;
    protected LocationRequest mLocationRequest;
    protected GoogleApiClient mGoogleApiClient;
    protected Marker markCurrentPosition;
    protected FirebaseMappa myfirebase;
    protected LatLng currentPosition;
    //actual coords
    protected boolean mRequestLocationUpdates = false;
    protected LatLng placeToGo;
    //indica quale elemento è stato selezionato nello spinner
    protected int LastSelected = 0;
    protected int selectedRange = 0;
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    //GOOGLE API REFERENCE
    protected GeoPosition sessionPosition;
    protected String sessioneSelezionataId;
    private Activity activityCaller;



    public void setLastSelected(int last){
        LastSelected=last;
    }
    public int getLastSelected(){
        return LastSelected;
    }
    public void setSelectedRange(int selectedRange){
        this.selectedRange=selectedRange;
    }
    public int getSelectedRange(){
        return selectedRange;
    }
    public FirebaseMappa getMyFirebase(){
        return myfirebase;
    }
    public void setSessionPosition(GeoPosition sessionPosition){
        this.sessionPosition=sessionPosition;
    }
    public ConnessoreGoogleApiLocation(Activity activityCaller){
        this.activityCaller=activityCaller;
        SharedPreferences sp = activityCaller.getPreferences(Context.MODE_PRIVATE);
        LastSelected = sp.getInt("LastSelectedSpinnerElement", 1);
        selectedRange = sp.getInt("LastRangeSelected", 1);
        myfirebase=new FirebaseMappa();
    }
    public void onStartOld(){
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }
    public void onResumeOld(){
        SharedPreferences sp = activityCaller.getPreferences(Context.MODE_PRIVATE);
        LastSelected = sp.getInt("LastSelectedSpinnerElement", 1);
        selectedRange=sp.getInt("LastRangeSelected",1);
        checkPlayServices();
        if (mGoogleApiClient.isConnected() && mRequestLocationUpdates) {
            startLocationUpdates();
        }
    }
    public void onStopOld(){
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    public void onPauseOld(){
        stopLocationUpdates();
        //scrivo sul sharedPreferences
        SharedPreferences shPref = activityCaller.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = shPref.edit();
        editor.putInt("LastSelectedSpinnerElement", LastSelected);
        editor.putInt("LastRangeSelected", selectedRange);
        editor.apply();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        //LISTENER MARKER
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                //se vuoi prendere un marker (che non sia la tua posizione) fai cosi : markers.get(marker).getDegreeCourse() per esempio
                //aggiorna la bottom Sheet Di regola.
                if(markers.get(marker)!=null)
                     sessioneSelezionataId= markers.get(marker).id;
                else
                        sessioneSelezionataId=null;
                placeToGo = new LatLng(marker.getPosition().latitude, marker.getPosition().longitude);
                //non hai cliccato sul marker che indica la tua posizione attuale
                if (!((currentPosition.latitude == placeToGo.latitude)
                        && (currentPosition.longitude == placeToGo.longitude))) {
                    settaDistanzaDelPostoDaRaggiungere();
                }
                else{
                    TextView txt = (TextView)activityCaller.findViewById(R.id.txtTempoPercorrenza);
                    if (txt!=null){ txt.setText(activityCaller.getResources().getString(R.string.seiQui));}
                }
                return false;
            }
        });
    }

    private void settaDistanzaDelPostoDaRaggiungere() {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(activityCaller, Locale.getDefault());
        try {
            // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            addresses = geocoder.getFromLocation(placeToGo.latitude, placeToGo.longitude, 1);
            new setDistanceInfoBackGround().execute(currentPosition.latitude, currentPosition.longitude,
                    addresses.get(0).getAddressLine(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private DistanzaTempo getDistanzaETempo(double lat1, double lng1, String destinationAddress) {
        StringBuilder stringBuilder = new StringBuilder();
        Double dist = 0.0;
        try {

            destinationAddress = destinationAddress.replaceAll(" ", "%20");
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + lat1 + "," + lng1 +
                    "&destination=" + destinationAddress + "&mode=walking&sensor=false&language=" +
                    activityCaller.getResources().getString(R.string.lingua);

            HttpPost httppost = new HttpPost(url);

            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            stringBuilder = new StringBuilder();


            response = client.execute(httppost);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JSONObject jsonObject;
        DistanzaTempo risultato = null;
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray array = jsonObject.getJSONArray("routes");
            JSONObject routes = array.getJSONObject(0);
            JSONArray legs = routes.getJSONArray("legs");
            JSONObject steps = legs.getJSONObject(0);
            JSONObject distance = steps.getJSONObject("distance");
            JSONObject duration = steps.getJSONObject("duration");
            risultato = new DistanzaTempo(distance.get("text").toString(), duration.get("text").toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return risultato;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }

    /**
     * Uso api google, creo la richiesta
     */
    public synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(activityCaller)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Creo la richiesta di localizzazione
     */
    public void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * con questo metodo posso sfruttare i controlli che google fa quando usi google maps, controlla se è acceso il gps ed è attiva
     * la localizzazione.
     */
    public void impostaLocationSettingRequestByGoogle() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        //inizio la richiesta.
                        displayLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(activityCaller, 1000);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        Toast.makeText(activityCaller, R.string.ErroreImpossibileStabilirePosizione, Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    /**
     * attivo la connessione mobile se disabilitatata
     */
    private void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass = Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

    /**
     * Controllo se è effettivaente connesso ad internet
     *
     * @param context
     * @return
     */
    private boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activityCaller);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode,activityCaller, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(activityCaller, R.string.dispositivoNonSupportato, Toast.LENGTH_LONG)
                        .show();
            }
            return false;
        }
        return true;
    }

 public void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(activityCaller, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activityCaller, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }

    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (ContextCompat.checkSelfPermission(activityCaller,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activityCaller,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);

        } else {
            if (mRequestLocationUpdates) {
                startLocationUpdates();
            }

            displayLocation();
        }
    }



    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(activityCaller, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GoogleApiAvailability.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
    }

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
    //    dialogFragment.show(activityCaller.getCogetFragmentManager(), "errordialog");
    }
// The rest of this code is all about building the error dialog

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
   /* public static void onDialogDismissed() {
        mResolvingError = false;
    }*/



    double lat;
    double lng;
   public void displayLocation() {
        if (ContextCompat.checkSelfPermission(activityCaller, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                //elimino la mia vecchia posizione in cui mi trovavo
                if (markCurrentPosition != null) {
                    markCurrentPosition.remove();
                }
                lat = mLastLocation.getLatitude();
                lng = mLastLocation.getLongitude();
                //QUI IMPOSTO IL MARKERff
                impostaMarkerInDeterminateParametri();
                int range=500;
                switch(selectedRange){
                    case 1: range=1000;
                }
                myfirebase.sessioniVicine(this, LastSelected,range);
            } else {
                Toast.makeText(activityCaller, R.string.ErroreLocationDisabilitata, Toast.LENGTH_LONG).show();
            }
        }
    }
    public void impostaMarkerInDeterminateParametri(){
        currentPosition = new LatLng(lat, lng);
        markCurrentPosition = mMap.addMarker(new MarkerOptions().position(currentPosition).title("Sei qui!").icon(
                BitmapDescriptorFactory.fromResource(R.drawable.marker)));
        if(sessionPosition==null)
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 18));
        else{
            LatLng sessioneCoords = new LatLng(sessionPosition.lat,sessionPosition.lng);
            Marker m=mMap.addMarker(new MarkerOptions().position(sessioneCoords).title(sessionPosition.placeName)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sessioneCoords, 18));
            placeToGo = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
            settaDistanzaDelPostoDaRaggiungere();
        }
    }

    public void clearMap() {
        mMap.clear();
    }
    public GoogleMap getMap(){return mMap;}

    public Marker getMarkCurrentPosition() {
        return markCurrentPosition;
    }

    public MyHashMap getMarkers() {
        return markers;
    }


    public void setCurrentMarkerPosition(MarkerOptions m) {
        markCurrentPosition = mMap.addMarker(m);
    }

    public void clearMarker() {
        markers.clear();
    }

    public void createNewHashMap() {
        markers = new MyHashMap();
    }

    public Marker addMarkerOnMap(MarkerOptions m) {
        return mMap.addMarker(m);
    }

    /* A fragment to display an error dialog */
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GoogleApiAvailability.getInstance().getErrorDialog(
                    this.getActivity(), errorCode, REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
           // onDialogDismissed();
        }
    }

    /**
     * Classe che mi permette di scaricare e non bloccare il thread principale
     */
    private class setDistanceInfoBackGround extends AsyncTask<Object, Double, DistanzaTempo> {
        @Override
        protected void onPostExecute(DistanzaTempo result) {

                TextView txt = (TextView)activityCaller.findViewById(R.id.txtTempoPercorrenza);
                if (txt != null && result != null) {
                    txt.setText(" ");
                    txt.setText(activityCaller.getResources().getString(R.string.aPiedi) + result.getTempo() + " (" + result.getDistanza() + ")");
                }

        }

        @Override
        protected DistanzaTempo doInBackground(Object... params) {
            return getDistanzaETempo((double) params[0], (double) params[1], (String) params[2]);
        }
    }

    public boolean getRequestLocationUpdates(){
        return mRequestLocationUpdates;
    }

    public LatLng getCurrentPosition(){
        return currentPosition;
    }
    public String getSessioneSelezionataId(){
        return sessioneSelezionataId;
    }
    public LatLng getPlaceToGo(){
        return placeToGo;
    }
    private class DistanzaTempo {
        private String distanza;
        private String tempo;

        DistanzaTempo(String distanza, String tempo) {
            this.distanza = distanza;
            this.tempo = tempo;
        }

        String getDistanza() {
            return distanza;
        }

        String getTempo() {
            return tempo;
        }
    }


}