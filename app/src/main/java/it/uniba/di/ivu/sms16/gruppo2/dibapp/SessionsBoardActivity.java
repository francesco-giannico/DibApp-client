package it.uniba.di.ivu.sms16.gruppo2.dibapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.authentication.AuthActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.BestNotesFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.NearSessionsFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.QueryNotesFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.QuerySessionFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.RecSessionsFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.RecentNotesFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.RecentSessionsFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.RecommendedNoteFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.SessionsListFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.settings.SettingsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession.AddSessionActivity;


public class SessionsBoardActivity extends DrawerActivity implements SessionsListFragment.SessionBoardCallBack, SearchView.OnQueryTextListener{

    public static final int AUTH_REQUEST_ACTIVITY = 902;
    public static final String QUERY = "query";

    private BottomBar mBottomBar;
    private Toolbar mToolbar;
    private FloatingActionButton mFabButton;
    private Snackbar mSnackBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        mToolbar = (Toolbar) findViewById(R.id.sessionsNotesToolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Bacheca Sessioni");

        mFabButton = (FloatingActionButton) findViewById(R.id.sessionsNotesFab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mCurrentUid != null) {
                    Intent intent = new Intent(SessionsBoardActivity.this, AddSessionActivity.class);
                    intent.putExtra(AddSessionActivity.EXTRA_USER_ID, mCurrentUid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(SessionsBoardActivity.this, AuthActivity.class);
                    startActivityForResult(intent, AUTH_REQUEST_ACTIVITY);
                }
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
        };

        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mBottomBar = BottomBar.attach(findViewById(R.id.sessionsNotesCoordinatorContainer), savedInstanceState);
        mBottomBar.setItems(R.menu.menu_bottombar_sessions);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                if (mSnackBar != null) {
                    mSnackBar.dismiss();
                }

                switch (menuItemId) {

                    case R.id.bb_menu_recents: {

                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame,
                                RecentSessionsFragment.newInstance(mCurrentUid)).commit();

                        break;
                    }
                    case R.id.bb_menu_recommended: {

                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame,
                                RecSessionsFragment.newInstance(mCurrentUid, mCurrentUser)).commit();
                        break;
                    }
                    case R.id.bb_menu_nearme: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame,
                                NearSessionsFragment.newInstance(mCurrentUid)).commit();
                        break;
                    }
                }
            }

            @Override
            public void onMenuTabReSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.bb_menu_recents: {

                        //getFragmentManager().beginTransaction().replace(R.id.frame, new RecentNotesFragment()).commit();
                        break;
                    }
                    case R.id.bb_menu_recommended: {

                        //getFragmentManager().beginTransaction().replace(R.id.frame, new RecommendedNoteFragment()).commit();
                        break;
                    }
                    case R.id.bb_menu_nearme: {
                        //getFragmentManager().beginTransaction().replace(R.id.frame, new BestNotesFragment()).commit();
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case SessionsListFragment.PERMISSIONS_REQUEST_FINE_LOCATION: {

                if (grantResults.length > 0) {
                        if(!(grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                            showSnackBar("You have to allow location permission before to get near sessions",null,null, Snackbar.LENGTH_LONG);
                        }
                }
                break;
            }

            default:
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == AUTH_REQUEST_ACTIVITY)
            if (resultCode == RESULT_OK) {

                Snackbar.make(findViewById(R.id.sessionsNotesCoordinatorContainer), "Authentication Success", Snackbar.LENGTH_SHORT).show();
                onStart();
            } else {
                Snackbar.make(findViewById(R.id.sessionsNotesCoordinatorContainer), "Authentication Failed", Snackbar.LENGTH_SHORT).show();
            }
    }

    @Override
    public void showSnackBar(String message, String actionText, View.OnClickListener actionListener, int length) {
        if (actionText != null) {
            mSnackBar = Snackbar.make(findViewById(R.id.sessionsNotesCoordinatorContainer), message, length)
                    .setAction(actionText, actionListener);
        } else {
            mSnackBar = Snackbar.make(findViewById(R.id.sessionsNotesCoordinatorContainer), message, length);
        }

        mSnackBar.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.notes_board_action_button, menu);

        MenuItem searchItem = menu.findItem(R.id.search);

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                mBottomBar.hide();
                mFabButton.hide();
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mBottomBar.show();
                mFabButton.show();
                switch (mBottomBar.getCurrentTabPosition()) {
                    case 0: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new RecentSessionsFragment()).commit();
                        break;
                    }
                    case 1: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new RecSessionsFragment()).commit();
                        break;
                    }
                    case 2: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new NearSessionsFragment()).commit();
                        break;
                    }
                }
                return true;
            }
        });

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
        bundle.putString(QUERY, query);
        QuerySessionFragment querySessionFragment = new QuerySessionFragment();
        querySessionFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, querySessionFragment).commit();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
