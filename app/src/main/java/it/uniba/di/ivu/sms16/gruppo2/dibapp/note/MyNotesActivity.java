package it.uniba.di.ivu.sms16.gruppo2.dibapp.note;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import it.uniba.di.ivu.sms16.gruppo2.dibapp.DrawerActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.R;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.ComplexObject;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.Note;

/**
 * Created by antoniolategano on 10/07/16.
 */
public class MyNotesActivity extends DrawerActivity implements SearchView.OnQueryTextListener, MyNotesFragment.OnDataPass {

    public static final String TYPE = "type";
    public static final String QUERY = "query";
    public static final String NOTE_LIST = "note_list";

    private List<Note> noteList;

    private static final String TAG = "MyNotesActivity";
    private Toolbar mToolbar;

    private FloatingActionButton mFabButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_notes);
        mToolbar = (Toolbar) findViewById(R.id.toolbar4);
        setSupportActionBar(mToolbar);

        mFabButton = (FloatingActionButton) findViewById(R.id.fab1);
        mFabButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MyNotesActivity.this, AddNoteActivity.class);
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
        getSupportFragmentManager().beginTransaction().replace(R.id.frame1, new MyNotesFragment()).commit();
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        Bundle bundle = new Bundle();
        bundle.putString(TYPE, "search_my_notes");
        bundle.putString(QUERY, query);
        ComplexObject<Note> notes = new ComplexObject<>(noteList);
        bundle.putSerializable(NOTE_LIST, notes);
        MyNotesFragment myNotesFragment = new MyNotesFragment();
        myNotesFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame1, myNotesFragment).commit();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    @Override
    public void onDataPass(List<Note> notes) {
        noteList = notes;
    }
}
