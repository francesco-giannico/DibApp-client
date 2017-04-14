package it.uniba.di.ivu.sms16.gruppo2.dibapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;

import java.util.ArrayList;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.BaseActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.firebase.FirebaseHandler;


/**
 * Created by Salvatore on 09/07/2016.
 */
public class SceltaInteressiActivity extends BaseActivity
    implements View.OnClickListener{

    public static String ARRAY_INTEREST_RESULT = "interest_array";
    private Button confermaInteressiButton;
    private ToggleButton interestMatematicaToggle;
    private ToggleButton interestFisicaToggle;
    private ToggleButton interestSicurezzaToggle;
    private ToggleButton interestProgrammazioneToggle;
    private ToggleButton interestAndroidToggle;
    private ToggleButton interestProgrammazioneWebToggle;
    private ToggleButton interestUxToggle;
    private ToggleButton interestDatabaseToggle;
    private ToggleButton interestMachineLearningToggle;
    private CoordinatorLayout snackbarLayout;
    private int contatoreInteressiScelti;
    ArrayList<String> interessi;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choice_interests);

        snackbarLayout = (CoordinatorLayout) findViewById(R.id.snackbar_layout);

        confermaInteressiButton = (Button) findViewById(R.id.confermaSceltaInteressi);
        interestMatematicaToggle = (ToggleButton) findViewById(R.id.interest_matematica);
        interestFisicaToggle = (ToggleButton) findViewById(R.id.interest_fisica);
        interestSicurezzaToggle = (ToggleButton) findViewById(R.id.interest_sicurezza_reti);
        interestProgrammazioneToggle = (ToggleButton) findViewById(R.id.interest_programmazione);
        interestAndroidToggle = (ToggleButton) findViewById(R.id.interest_programmazione_android);
        interestProgrammazioneWebToggle = (ToggleButton) findViewById(R.id.interest_web_developing);
        interestUxToggle = (ToggleButton) findViewById(R.id.interest_ux);
        interestDatabaseToggle = (ToggleButton) findViewById(R.id.interest_database);
        interestMachineLearningToggle = (ToggleButton) findViewById(R.id.interest_machine_learning);

        confermaInteressiButton.setOnClickListener(this);
        interestMatematicaToggle.setOnClickListener(this);
        interestFisicaToggle.setOnClickListener(this);
        interestSicurezzaToggle.setOnClickListener(this);
        interestProgrammazioneToggle.setOnClickListener(this);
        interestAndroidToggle.setOnClickListener(this);
        interestProgrammazioneWebToggle.setOnClickListener(this);
        interestUxToggle.setOnClickListener(this);
        interestDatabaseToggle.setOnClickListener(this);
        interestMachineLearningToggle.setOnClickListener(this);

        contatoreInteressiScelti = 0;
        interessi = new ArrayList<String>();

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.confermaSceltaInteressi) {
            saveInteressi();
        }

        else if(v.getId() == R.id.interest_matematica || v.getId() == R.id.interest_fisica ||
                v.getId() == R.id.interest_sicurezza_reti || v.getId() == R.id.interest_programmazione ||
                v.getId() == R.id.interest_programmazione_android || v.getId() == R.id.interest_web_developing ||
                v.getId() == R.id.interest_ux || v.getId() == R.id.interest_database ||
                v.getId() == R.id.interest_machine_learning) {

            ToggleButton interesse = (ToggleButton) v;
            setClickInterestBehaviour(interesse);
        }
    }

    private void saveInteressi() {
        String mioId = mCurrentUid;
        if(contatoreInteressiScelti == 4) {
            //Set interessi utente
            DatabaseReference refProfilo = new FirebaseHandler().getmUserReference(mioId);
            refProfilo.child("interests").setValue(interessi).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(snackbarLayout, R.string.error_add_interests, Snackbar.LENGTH_SHORT).show();
                }
            });

            /*
            Intent intent = getIntent();
            intent.putExtra(ARRAY_INTEREST_RESULT, interessi);
            setResult(ModificaProfiloActivity.ARRAY_INTERESTS_CODE);*/

            onBackPressed();

        } else {
            Toast.makeText(this, R.string.have_to_choice_interest, Toast.LENGTH_SHORT).show();
        }
    }

    private String getInteresseById(int idInteresse) {
        switch (idInteresse) {
            case R.id.interest_matematica: {
                return getResources().getString(R.string.matematica);
            }
            case R.id.interest_fisica: {
                return getResources().getString(R.string.fisica);
            }
            case R.id.interest_sicurezza_reti: {
                return getResources().getString(R.string.sicurezza);
            }
            case R.id.interest_programmazione: {
                return getResources().getString(R.string.programmazione);
            }
            case R.id.interest_programmazione_android: {
                return getResources().getString(R.string.programmazione_android);
            }
            case R.id.interest_web_developing: {
                return getResources().getString(R.string.programmazione_web);
            }
            case R.id.interest_ux: {
                return getResources().getString(R.string.ux);
            }
            case R.id.interest_database: {
                return getResources().getString(R.string.database);
            }
            case R.id.interest_machine_learning: {
                return getResources().getString(R.string.machine_learning);
            }
        }
        return "";
    }

    private void setClickInterestBehaviour(ToggleButton button) {

        if(button.isChecked()) {
            if (contatoreInteressiScelti <= 3) {
                button.setBackground(button.getContext().getResources().getDrawable(R.drawable.selector_interest_true));
                interessi.add(getInteresseById(button.getId()));
                contatoreInteressiScelti++;
            } else if(contatoreInteressiScelti == 4) {
                Toast.makeText(this, R.string.choice_interest, Toast.LENGTH_SHORT).show();
                button.setChecked(false);
            }
        }
        else {
            button.setBackground(button.getContext().getResources().getDrawable(R.drawable.selector_interest_false));
            interessi.remove(getInteresseById(button.getId()));
            contatoreInteressiScelti--;
        }
    }


}
