package it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.util.List;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.DrawerActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.ComplexObject;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.StudySession;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.note.MyNotesFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.settings.SettingsActivity;

/**
 * Created by antoniolategano on 10/07/16.
 */
public class MySessionsActivity extends DrawerActivity
        implements SearchView.OnQueryTextListener, MySessionsFragment.OnDataPass {

    public static final String TYPE = "type";
    public static final String QUERY = "query";
    public static final String SESSION_LIST = "session_list";

    private List<StudySession> mStudySessionList;


    private static final String TAG = "MySessionsActivity";
    private static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 20;
    private Toolbar mToolbar;
    private FloatingActionButton mFabButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_sessions);

        mToolbar = (Toolbar) findViewById(R.id.my_sessions_toolbar);
        setSupportActionBar(mToolbar);

        mFabButton = (FloatingActionButton) findViewById(R.id.my_sessions_fab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MySessionsActivity.this, AddSessionActivity.class);
                i.putExtra(AddSessionActivity.EXTRA_USER_ID, mCurrentUid);
                startActivity(i);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

    }

    @Override
    protected void onStart() {
        super.onStart();
        getSupportFragmentManager().beginTransaction().replace(R.id.my_sessions_frame, new MySessionsFragment()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes_board_action_button, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        Bundle bundle = new Bundle();
        bundle.putString(TYPE, "search_my_sessions");
        bundle.putString(QUERY, query);
        ComplexObject<StudySession> sessions = new ComplexObject<>(mStudySessionList);
        bundle.putSerializable(SESSION_LIST, sessions);
        MySessionsFragment mySessionsFragment = new MySessionsFragment();
        mySessionsFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.my_sessions_frame, mySessionsFragment).commit();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onDataPass(List<StudySession> studySessions) {
        mStudySessionList = studySessions;
    }
}