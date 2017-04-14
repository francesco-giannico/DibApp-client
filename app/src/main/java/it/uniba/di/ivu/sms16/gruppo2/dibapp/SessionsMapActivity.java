package it.uniba.di.ivu.sms16.gruppo2.dibapp;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession.SessionDetailsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.utils.ConnessoreGoogleApiLocation;

public class SessionsMapActivity extends DrawerActivity{


    protected int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 204;

    // Request code to use when launching the resolution activity

    static Spinner spinner;
    //bottom sheet reference
    public static String CALLING_FROM_SESSION_DETAILS_ACTIVITY="Called_from_SessinoDetailsActivity";

    private ConnessoreGoogleApiLocation googleApiLocation;
    private Marker markToGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        googleApiLocation=new ConnessoreGoogleApiLocation(this);
        impostaToolbarDrawer();
        impostaButtonAvvioNavigatore();
        impostaButtonDettagliSessione();
        googleApiLocation.setSessionPosition(
                (GeoPosition)getIntent().getSerializableExtra(SessionsMapActivity.CALLING_FROM_SESSION_DETAILS_ACTIVITY));
        impostaMappa();

    }
    @Override
    protected void onPause(){

        super.onPause();
        googleApiLocation.onPauseOld();
    }
    @Override
    protected void onStop(){
        super.onStop();
        googleApiLocation.onStopOld();
    }
    @Override
    protected void onStart(){
        super.onStart();
        googleApiLocation.onStartOld();
    }
    @Override
    protected void onResume(){
        super.onResume();
        googleApiLocation.onResumeOld();
    }

    private void impostaListenerSpinner() {
        //listener per lo spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //refresh in base allo spinner
                ((TextView) spinner.getChildAt(0)).setTextColor(Color.WHITE);
                if (googleApiLocation.getMarkers() != null) {
                    googleApiLocation.setLastSelected(position);
                    HashMap<Marker,StudySession> markers = googleApiLocation.getMarkers();
                    //metto il nuovo marker sulla mappa solo se posso
                    switch (position) {

                        //mostro tutte le sessioni
                        case 0:
                            for (Marker m : markers.keySet()) {
                                m.setVisible(true);
                            }
                            break;
                        //mostro solo le leaderLess
                        case 1:
                            for (Marker m : markers.keySet()) {
                                if (markers.get(m).type.equals("leaderless")) {
                                    m.setVisible(true);
                                } else
                                    m.setVisible(false);
                            }
                            break;
                        //mostro solo le teaching
                        case 2:
                            for (Marker m : googleApiLocation.getMarkers().keySet()) {
                                if (markers.get(m).type.equals("teaching")) {
                                    m.setVisible(true);
                                } else
                                    m.setVisible(false);
                            }
                            break;
                        //mostro Solo quelle iniziate
                        case 3:
                            for (Marker m : markers.keySet()) {
                                StudySession sessione = markers.get(m);
                                String dataInizio = sessione.date;
                                String oraInizio = sessione.hourStart;
                                String DataEOraInizio = dataInizio + " " + oraInizio;
                                DateFormat format = new SimpleDateFormat("d MM HH:mm", Locale.ENGLISH);
                                Date dateSessionStart = null;
                                Date actualDate = new Date();//data attuale
                                try {
                                    dateSessionStart = format.parse(DataEOraInizio);
                                    if (actualDate.after(dateSessionStart) || actualDate.equals(dateSessionStart)) {
                                        m.setVisible(true);
                                    } else
                                        m.setVisible(false);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void impostaToolbarDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.activity_mappa);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }


    private void impostaButtonAvvioNavigatore() {
        ImageButton imgButton = (ImageButton) findViewById(R.id.imageButton);
        if (imgButton != null) {
            imgButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LatLng placeToGo= googleApiLocation.getPlaceToGo();
                    if (placeToGo != null && placeToGo.longitude !=
                    googleApiLocation.getCurrentPosition().longitude &&
                            placeToGo.latitude != googleApiLocation.getCurrentPosition().latitude) {
                        Intent mapintent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?daddr=" + placeToGo.latitude + "," + placeToGo.longitude));
                        // Make the Intent explicit by setting the Google Maps package
                        mapintent.setPackage("com.google.android.apps.maps");
                        if (mapintent.resolveActivity(getPackageManager()) != null) {
                            startActivity(mapintent);
                        }
                    } else {
                        Toast.makeText(SessionsMapActivity.this, R.string.impossibileAvviareNavigatoreDestinazioneNonScelta, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    private void impostaButtonDettagliSessione() {
        FrameLayout frmDettagli = (FrameLayout) findViewById(R.id.frmDettagliSessione);
        if (frmDettagli != null) {
            frmDettagli.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(googleApiLocation.getSessioneSelezionataId()!=null) {
                        Intent intent = new Intent(SessionsMapActivity.this,
                                SessionDetailsActivity.class);
                        intent.putExtra(SessionDetailsActivity.EXTRA_SESSION_ID, googleApiLocation.getSessioneSelezionataId());
                        intent.putExtra(SessionDetailsActivity.EXTRA_NOT_FROM_SESSION, false);
                        startActivity(intent);
                    }
                    else
                        Toast.makeText(v.getContext(),R.string.SelezionaSessione,Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void impostaMappa() {
        //MAPPA
        // Obtain the SupportMapFragment and gest notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(googleApiLocation);//on the fragment to register the callback
        //FINE MAPPA
        if (googleApiLocation.checkPlayServices()) {
            googleApiLocation.buildGoogleApiClient();
            googleApiLocation.createLocationRequest();
            googleApiLocation.impostaLocationSettingRequestByGoogle();
        }
    }


    //Mostro elementi in base allo spinner
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_settings_map, menu);
        //Spinner
        MenuItem item = menu.findItem(R.id.spinner);
        //imposto lo spinner
        spinner = (Spinner) MenuItemCompat.getActionView(item);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner_list_item_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(googleApiLocation.getLastSelected(), true);
        ((TextView) spinner.getChildAt(0)).setTextColor(Color.WHITE);
        impostaListenerSpinner();
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setTitle(getResources().getString(R.string.range)).setSingleChoiceItems(R.array.spinner_range, googleApiLocation.getSelectedRange(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        googleApiLocation.setSelectedRange(which);

                    }
                })
                        // Set the action buttons
                        .setPositiveButton(getResources().getString(R.string.conferma), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // User clicked OK, so save the mSelectedItems results somewhere
                                // or return them to the component that opened the dialog
                                int selectedRange= googleApiLocation.getSelectedRange();
                                switch (googleApiLocation.getSelectedRange()) {
                                    case 0:
                                        googleApiLocation.setSelectedRange(0);
                                        googleApiLocation.getMyFirebase().setRange(500,googleApiLocation);
                                        break;
                                    case 1:
                                        googleApiLocation.setSelectedRange(1);
                                        googleApiLocation.getMyFirebase().setRange(1000, googleApiLocation);
                                        break;
                                }

                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.cancella), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.show();
                break;
            case R.id.action_position:
                googleApiLocation.displayLocation();
                break;
            case R.id.action_search_bar:
                try {
                    //Qui viene chiamato l'intent di un app realizzata da Google per poter fare la ricerca.
                    Intent intent = new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY).build(this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    Log.e("GoogleSearch", e.getMessage());
                }
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Place_autocomplete risultato dell'intent che ha permesso di fare la ricerca
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place placeToGo = PlaceAutocomplete.getPlace(this, data);
                if (markToGo != null)
                    markToGo.remove();
                markToGo = googleApiLocation.getMap().addMarker(new MarkerOptions().position(placeToGo.getLatLng()).title(placeToGo.getAddress().toString()));
                googleApiLocation.getMap().animateCamera(CameraUpdateFactory.newLatLngZoom(placeToGo.getLatLng(), 18));
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_REQUEST_FINE_LOCATION) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (googleApiLocation.checkPlayServices()) {
                    googleApiLocation.buildGoogleApiClient();
                    googleApiLocation.createLocationRequest();
                }
                if (googleApiLocation.getRequestLocationUpdates()) {
                    googleApiLocation.startLocationUpdates();
                }
               googleApiLocation.displayLocation();
            }
        }
    }


}
