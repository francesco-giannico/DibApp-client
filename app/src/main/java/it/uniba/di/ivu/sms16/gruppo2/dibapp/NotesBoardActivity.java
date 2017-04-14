package it.uniba.di.ivu.sms16.gruppo2.dibapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnMenuTabClickListener;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.BestNotesFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.QueryNotesFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.RecentNotesFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.boardfragments.RecommendedNoteFragment;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.note.AddNoteActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.settings.SettingsActivity;


public class NotesBoardActivity extends DrawerActivity implements SearchView.OnQueryTextListener {

    private static final String TAG = "NotesBoardActivity";
    private BottomBar mBottomBar;
    private Toolbar mToolbar;
    private FloatingActionButton mFabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);
        mToolbar = (Toolbar) findViewById(R.id.sessionsNotesToolbar);
        setSupportActionBar(mToolbar);

        mFabButton = (FloatingActionButton) findViewById(R.id.sessionsNotesFab);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(NotesBoardActivity.this, AddNoteActivity.class);
                startActivity(i);
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //mBottomBar = BottomBar.attachShy((CoordinatorLayout) findViewById(R.id.my_coordinator), mRecyclerView, savedInstanceState);
        mBottomBar = BottomBar.attach(findViewById(R.id.sessionsNotesCoordinatorContainer), savedInstanceState);
        mBottomBar.setItems(R.menu.bottombar_menu_three_items);
        mBottomBar.setOnMenuTabClickListener(new OnMenuTabClickListener() {
            @Override
            public void onMenuTabSelected(@IdRes int menuItemId) {
                switch (menuItemId) {
                    case R.id.bb_menu_recents: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new RecentNotesFragment()).commit();
                        break;
                    }
                    case R.id.bb_menu_recommended: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new RecommendedNoteFragment()).commit();
                        break;
                    }
                    case R.id.bb_menu_bests: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new BestNotesFragment()).commit();
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
                    case R.id.bb_menu_bests: {
                        //getFragmentManager().beginTransaction().replace(R.id.frame, new BestNotesFragment()).commit();
                        break;
                    }
                }
            }
        });
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
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new RecentNotesFragment()).commit();
                        break;
                    }
                    case 1: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new RecommendedNoteFragment()).commit();
                        break;
                    }
                    case 2: {
                        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, new BestNotesFragment()).commit();
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
        bundle.putString("query", query);
        QueryNotesFragment queryNotesFragment = new QueryNotesFragment();
        queryNotesFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.sessionsNotesFrame, queryNotesFragment).commit();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}