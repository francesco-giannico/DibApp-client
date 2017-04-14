package it.uniba.di.ivu.sms16.gruppo2.dibapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class SplashActivity extends AppCompatActivity {

    private static final String FIRST_LAUNCH_KEY = "it.uniba.di.ivu.sms16.gruppo2.dibapp.FIRST_LAUNCH";
    private final int SPLASH_DISPLAY_LENGTH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doAfterDelay();
            }

        }, SPLASH_DISPLAY_LENGTH);
    }

    private void doAfterDelay() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPreferences.getBoolean(FIRST_LAUNCH_KEY, false)) {

            Intent intent = new Intent(this, SessionsBoardActivity.class);
            startActivity(intent);
            finish();

        } else {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(FIRST_LAUNCH_KEY, true);
            editor.apply();

            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);
            finish();
        }
    }

}