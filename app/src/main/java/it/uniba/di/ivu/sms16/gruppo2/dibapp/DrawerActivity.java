package it.uniba.di.ivu.sms16.gruppo2.dibapp;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import de.hdodenhof.circleimageview.CircleImageView;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.models.GeoPosition;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.note.MyNotesActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.profile.ProfiloUtenteActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.settings.SettingsActivity;
import it.uniba.di.ivu.sms16.gruppo2.dibapp.studysession.MySessionsActivity;


public class DrawerActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected DrawerLayout mDrawerLayout;
    private FrameLayout mFrameLayout;
    private static int mLastDrawerItemId = R.id.sessions_board;
    private NavigationView mNavigationView;

    private View mHeaderProfilo;
    private CircleImageView mImageProfileHeader;
    private TextView mEmailProfileHeader;
    private TextView mNomeCognomeProfileHeader;

    @Override
    public void setContentView(int layoutResID) {

        mDrawerLayout = (DrawerLayout) getLayoutInflater().inflate(R.layout.activity_drawer, null);
        mFrameLayout = (FrameLayout) mDrawerLayout.findViewById(R.id.contentDrawerActivity);

        getLayoutInflater().inflate(layoutResID, mFrameLayout, true);

        super.setContentView(mDrawerLayout);

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        mHeaderProfilo = mNavigationView.getHeaderView(0);
        initDrawerHeader(mHeaderProfilo);

    }

    @Override
    protected void onStart() {
        super.onStart();

        Menu navMenu = mNavigationView.getMenu();
        if (mCurrentUid == null) {
            navMenu.findItem(R.id.my_notes).setVisible(false);
            navMenu.findItem(R.id.my_sessions).setVisible(false);
        } else {
            setHeaderProfiloValues();
            navMenu.findItem(R.id.my_notes).setVisible(true);
            navMenu.findItem(R.id.my_sessions).setVisible(true);
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {

            mLastDrawerItemId = -1;
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.sessions_board:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(getBaseContext(), SessionsBoardActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.notes_board:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(getBaseContext(), NotesBoardActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.sessions_map:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;
                    GeoPosition g=null;
                    Intent intent = new Intent(getBaseContext(), SessionsMapActivity.class);
                    intent.putExtra(SessionsMapActivity.CALLING_FROM_SESSION_DETAILS_ACTIVITY,g);
                    startActivity(intent);
                }
                break;

            case R.id.my_sessions:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(getBaseContext(), MySessionsActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.my_notes:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(getBaseContext(), MyNotesActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.leaderboard:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(getBaseContext(), LeaderBoardActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.settings:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(getBaseContext(), SettingsActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.help:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(getBaseContext(), HelpActivity.class);
                    startActivity(intent);
                }
                break;

            case R.id.feedback:
                if (id != mLastDrawerItemId) {
                    mLastDrawerItemId = id;

                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:")); // only email apps should handle this
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"sms16@gmail.com"});
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                break;
        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initDrawerHeader(View view) {

        mImageProfileHeader = (CircleImageView) view.findViewById(R.id.imageViewProfileHeader);
        mEmailProfileHeader = (TextView) view.findViewById(R.id.textViewMailHeader);
        mNomeCognomeProfileHeader = (TextView) view.findViewById(R.id.textViewNomeCognomeHeader);
    }

    private void setHeaderProfiloValues() {

        if (mCurrentUser.photoUrl != null) {
            Glide.with(this).load(mCurrentUser.photoUrl).into(mImageProfileHeader);
        }

        mEmailProfileHeader.setText(mCurrentUser.email);
        mNomeCognomeProfileHeader.setText(mCurrentUser.name);

        mHeaderProfilo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLastDrawerItemId != -2) {
                    mLastDrawerItemId = -2;
                    Intent intent = new Intent(getBaseContext(), ProfiloUtenteActivity.class);
                    startActivity(intent);
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                }
            }
        });
    }


}
